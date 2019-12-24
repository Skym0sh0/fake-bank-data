package de.sky.regular.income.backup;

import com.google.common.collect.Sets;
import de.sky.common.database.DatabaseConnection;
import de.sky.regular.income.database.DatabaseSupplier;
import generated.sky.regular.income.RegularIncome;
import generated.sky.regular.income.Tables;
import org.apache.commons.lang3.time.StopWatch;
import org.jooq.Table;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
        synchronized (javaMailSender) {
            Set<Table<?>> excludes = Sets.newHashSet(Tables.V_ORDERED_BANK_STATEMENTS);

            sendMail(() -> {
                logger.info("Fetching data from Database");

                try (var ctx = db.getContext()) {
                    return RegularIncome.REGULAR_INCOME.tableStream()
                            .filter(t -> !excludes.contains(t))
                            .collect(Collectors.toMap(Table::getName, table -> ctx.fetch(table).formatCSV()));
                }
            });
        }
    }

    private void sendMail(Supplier<Map<String, String>> dataFileSupplier) {
        logger.info("Sending Backup...");

        var now = ZonedDateTime.now();
        var sw = StopWatch.createStarted();

        try {
            var msg = javaMailSender.createMimeMessage();

            var helper = new MimeMessageHelper(msg, true);

            helper.setTo(recipientAddress);
            helper.setFrom(senderAddress);

            helper.setSubject(String.format("[%s] Regular Income Backup", FRMT_SUBJECT.format(now)));
            helper.setText(String.format("Regular Income Backup at %s", FRMT_BODY.format(now)));

            var data = dataFileSupplier.get();
            for (Map.Entry<String, String> entry : data.entrySet()) {
                String name = entry.getKey();
                String dataChunk = entry.getValue();

                helper.addAttachment(String.format("regular_income_backup_%s_[%s].csv", FRMT_FILE.format(now), name), () -> new ByteArrayInputStream(dataChunk.getBytes()));
            }

            logger.info("Execute actual Email sending...");

            javaMailSender.send(msg);

            logger.info("Backup sent in {}", sw);
        } catch (Exception e) {
            logger.error("Failed sending mail", e);
        }
    }
}
