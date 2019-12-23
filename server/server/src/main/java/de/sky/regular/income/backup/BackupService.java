package de.sky.regular.income.backup;

import de.sky.common.database.DatabaseConnection;
import de.sky.regular.income.database.DatabaseSupplier;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.io.ByteArrayInputStream;
import java.util.Objects;

import static org.slf4j.LoggerFactory.getLogger;

@EnableScheduling
@Configuration
@Controller
public class BackupService {
    private static final Logger logger = getLogger(BackupService.class);

    private static final String CRON_PART_SECOND = "0";
    private static final String CRON_PART_MINUTE = "0";
    private static final String CRON_PART_HOUR = "5";
    private static final String CRON_PART_DAY_OF_MONTH = "*";
    private static final String CRON_PART_MONTH = "*";
    private static final String CRON_PART_DAY_OF_WEEK = "*";

    private static final String CRON_FULL = CRON_PART_SECOND + " " + CRON_PART_MINUTE + " " + CRON_PART_HOUR + " " + CRON_PART_DAY_OF_MONTH + " " + CRON_PART_MONTH + " " + CRON_PART_DAY_OF_WEEK;

    //@Scheduled(cron = CRON_FULL)
    @Scheduled(cron = "* * * * * *")
    public void doBackup() {
        sender.doBackupSend(() -> {
            return new ByteArrayInputStream("bla bla bla".getBytes());
        });
    }

    private final DatabaseConnection db;
    private final BackupSender sender;

    public BackupService(DatabaseConnection db, BackupSender sender) {
        this.sender = Objects.requireNonNull(sender);
        this.db = Objects.requireNonNull(db);
    }

    @Autowired
    public BackupService(DatabaseSupplier supplier, BackupSender sender) {
        this(supplier.getDatabase(), sender);
    }
}
