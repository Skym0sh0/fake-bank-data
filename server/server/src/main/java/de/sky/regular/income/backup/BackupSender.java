package de.sky.regular.income.backup;

import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import de.sky.common.database.DatabaseConnection;
import de.sky.regular.income.database.DatabaseSupplier;
import generated.sky.regular.income.RegularIncome;
import generated.sky.regular.income.Tables;
import generated.sky.regular.income.tables.records.BackupHistoryRecord;
import org.apache.commons.lang3.time.StopWatch;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static generated.sky.regular.income.Tables.*;
import static org.jooq.impl.DSL.*;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class BackupSender {
    private static final Logger logger = getLogger(BackupSender.class);

    private static final DateTimeFormatter FRMT_SUBJECT = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.of("UTC"));
    private static final DateTimeFormatter FRMT_BODY = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.of("UTC"));
    private static final DateTimeFormatter FRMT_FILE = DateTimeFormatter.ofPattern("yyyy_MM_dd").withZone(ZoneId.of("UTC"));

    private final DatabaseConnection db;
    private final JavaMailSender javaMailSender;

    @Value("${backup.mail.recipient}")
    private String recipientAddress;
    @Value("${backup.mail.sender}")
    private String senderAddress;

    public BackupSender(DatabaseConnection db, JavaMailSender sender) {
        this.db = Objects.requireNonNull(db);
        this.javaMailSender = Objects.requireNonNull(sender);
    }

    @Autowired
    public BackupSender(DatabaseSupplier supplier, JavaMailSender sender) {
        this(supplier.getDatabase(), sender);
    }

    public void fetchDataAndBackup() {
        ZonedDateTime now = ZonedDateTime.now();

        synchronized (javaMailSender) {
            db.transactionWithoutResult(ctx -> {
                boolean backupNeeded = isBackupNeeded(ctx);

                BackupHistoryRecord entry = ctx.newRecord(BACKUP_HISTORY);
                entry.setId(UUID.randomUUID());
                entry.setLastCheck(now.toOffsetDateTime());
                entry.setHadWorkToDo(backupNeeded);

                doBackupIfNeeded(ctx, now, backupNeeded)
                        .ifPresentOrElse(error -> {
                            entry.setSuccess(false);
                            entry.setErrorDetails(error);
                        }, () -> {
                            entry.setSuccess(true);
                            entry.setErrorDetails(null);
                        });

                entry.insert();
            });
        }
    }

    private boolean isBackupNeeded(DSLContext ctx) {
        if (ctx.fetchCount(BACKUP_HISTORY) == 0)
            return true;

        var tsField = field(name("LAST_TS"), OffsetDateTime.class);

        BackupHistoryRecord lastBackup = ctx.select()
                .from(BACKUP_HISTORY)
                .where(BACKUP_HISTORY.SUCCESS.isTrue())
                .and(BACKUP_HISTORY.LAST_CHECK.lessThan(
                        select(max(tsField))
                                .from(
                                        select(BANK_STATEMENT.CREATED_AT.as(tsField)).from(BANK_STATEMENT)
                                                .unionAll(select(BANK_STATEMENT.UPDATED_AT.as(tsField)).from(BANK_STATEMENT))
                                                .unionAll(select(FINANCIAL_TRANSACTION.CREATED_AT.as(tsField)).from(FINANCIAL_TRANSACTION))
                                )
                                .limit(1)
                ))
                .orderBy(BACKUP_HISTORY.LAST_CHECK.desc())
                .limit(1)
                .fetchOneInto(BACKUP_HISTORY);

        return lastBackup != null;
    }

    private Optional<String> doBackupIfNeeded(DSLContext ctx, ZonedDateTime timestamp, boolean backupNeeded) {
        if (!backupNeeded) {
            logger.info("No Backup was necessary since no new data was written");
            return Optional.empty();
        }

        return sendMail(timestamp, () -> {
            Set<Table<?>> excludes = Sets.newHashSet(Tables.V_ORDERED_BANK_STATEMENTS, BACKUP_HISTORY);

            logger.info("Fetching data from Database");

            return RegularIncome.REGULAR_INCOME.tableStream()
                    .filter(t -> !excludes.contains(t))
                    .filter(t -> !t.getName().toUpperCase().startsWith("V_"))
                    .collect(Collectors.toMap(Table::getName, table -> ctx.fetch(table).formatCSV()));
        });
    }

    private Optional<String> sendMail(ZonedDateTime timestamp, Supplier<Map<String, String>> dataFileSupplier) {
        logger.info("Sending Backup...");

        var sw = StopWatch.createStarted();

        try {
            var msg = javaMailSender.createMimeMessage();

            var helper = new MimeMessageHelper(msg, true);

            helper.setTo(recipientAddress);
            helper.setFrom(senderAddress);

            helper.setSubject(String.format("[%s] Regular Income Backup", FRMT_SUBJECT.format(timestamp)));
            helper.setText(String.format("Regular Income Backup at %s", FRMT_BODY.format(timestamp)));

            var data = dataFileSupplier.get();
            for (Map.Entry<String, String> entry : data.entrySet()) {
                String name = entry.getKey();
                String dataChunk = entry.getValue();

                helper.addAttachment(String.format("regular_income_backup_%s_[%s].csv", FRMT_FILE.format(timestamp), name), () -> new ByteArrayInputStream(dataChunk.getBytes()));
            }

            logger.info("Execute actual Email sending...");

            javaMailSender.send(msg);

            logger.info("Backup sent in {}", sw);

            return Optional.empty();
        } catch (Exception e) {
            logger.error("Failed sending mail", e);

            return Optional.of(Throwables.getStackTraceAsString(e));
        }
    }
}
