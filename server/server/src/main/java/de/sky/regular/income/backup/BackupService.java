package de.sky.regular.income.backup;

import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Objects;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class BackupService implements InitializingBean {
    private static final Logger logger = getLogger(BackupService.class);

    private final TaskScheduler scheduler;
    private final BackupSender sender;

    @Value("${backup.process.time}")
    private LocalTime backupTime;

    public BackupService(TaskScheduler scheduler, BackupSender sender) {
        this.scheduler = Objects.requireNonNull(scheduler);
        this.sender = Objects.requireNonNull(sender);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Duration period = Duration.ofDays(1);

        var now = ZonedDateTime.now(ZoneId.of("UTC"));
        var scheduled = ZonedDateTime.of(LocalDate.now(), backupTime, now.getZone());

        while (scheduled.isBefore(now))
            scheduled = scheduled.plus(period);

        scheduler.scheduleAtFixedRate(() -> {
            doBackup();

            logger.info("Next scheduled execution is in {} at {}", period, ZonedDateTime.now().plus(period));
        }, scheduled.toInstant(), period);

        logger.info("Scheduling set up successfully. Next execution is in {} at {}", Duration.between(now, scheduled), scheduled);
    }

    public void doBackup() {
        logger.info("Starting scheduled Backup...");

        try {
            sender.fetchDataAndBackup();
        } catch (Exception e) {
            logger.error("Scheduled Backup encountered an Error", e);
        }

        logger.info("Scheduled Backup finished");
    }
}
