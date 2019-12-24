package de.sky.regular.income.backup;

import org.slf4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

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

    private final BackupSender sender;

    public BackupService(BackupSender sender) {
        this.sender = Objects.requireNonNull(sender);
    }

    @Scheduled(cron = CRON_FULL, zone = "UTC")
    public void doBackup() {
        logger.info("Starting scheduled [{}] Backup...", CRON_FULL);

        try {
            sender.fetchDataAndBackup();
        } catch (Exception e) {
            logger.error("Scheduled Backup encountered an Error", e);
        }

        logger.info("Scheduled Backup finished");
    }
}
