package de.sky.regular.income.backup;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Supplier;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class BackupSender {
    private static final Logger logger = getLogger(BackupSender.class);

    private static final DateTimeFormatter FRMT_SUBJECT = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.of("UTC"));
    private static final DateTimeFormatter FRMT_BODY = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.of("UTC"));
    private static final DateTimeFormatter FRMT_FILE = DateTimeFormatter.ofPattern("yyyy_MM_dd").withZone(ZoneId.of("UTC"));

    private final JavaMailSender javaMailSender;

    @Value("${backup.mail.recipient}")
    private String recipientAddress;
    @Value("${backup.mail.sender}")
    private String senderAddress;

    public BackupSender(JavaMailSender sender) {
        this.javaMailSender = Objects.requireNonNull(sender);
    }

    public void doBackupSend(Supplier<InputStream> jsonSupplier) {
        logger.info("Sending Backup...");

        synchronized (javaMailSender) {
            sendMail(jsonSupplier);
        }
    }

    private void sendMail(Supplier<InputStream> jsonSupplier) {
        var now = ZonedDateTime.now();

        var sw = StopWatch.createStarted();

        try {
            var msg = javaMailSender.createMimeMessage();

            var helper = new MimeMessageHelper(msg, true);

            helper.setTo(recipientAddress);
            helper.setFrom(senderAddress);

            helper.setSubject(String.format("[%s] Regular Income Backup", FRMT_SUBJECT.format(now)));
            helper.setText(String.format("Regular Income Backup at %s", FRMT_BODY.format(now)));

            helper.addAttachment(String.format("regular_income_backup_%s.json", FRMT_FILE.format(now)), jsonSupplier::get);

            javaMailSender.send(msg);

            logger.info("Backup sent in {}", sw);
        } catch (Exception e) {
            logger.error("Failed sending mail", e);
        }
    }
}
