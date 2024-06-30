package de.sky.regular.income.backup;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Objects;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class BackupService implements InitializingBean {
    private static final Logger logger = getLogger(BackupService.class);

    private static final ZoneId UTC = ZoneId.of("UTC");

    private final TaskScheduler scheduler;
    private final BackupSender sender;

    @Value("${backup.process.time:#{null}}")
    private String backupTimeString;

    public BackupService(TaskScheduler scheduler, BackupSender sender) {
        this.scheduler = Objects.requireNonNull(scheduler);
        this.sender = Objects.requireNonNull(sender);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final var period = Duration.ofDays(1);
        final var backupTime = Optional.ofNullable(backupTimeString)
                .filter(StringUtils::isNotBlank)
                .map(LocalTime::parse)
                .orElseGet(() -> LocalTime.now(UTC).plus(Duration.ofSeconds(10)));

        var now = ZonedDateTime.now(UTC);
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
