package de.sky.regular.income.importing.csv;

import com.google.common.io.BaseEncoding;
import de.sky.regular.income.importing.csv.parsers.TurnoverRecord;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Service
public class ChecksumComputer implements Function<TurnoverRecord, String> {
    private final Map<Long, MessageDigest> digesterMap = new ConcurrentHashMap<>();

    @Override
    public String apply(TurnoverRecord rec) {
        return computeChecksum(rec);
    }

    public String computeChecksum(TurnoverRecord rec) {
        String sb = String.valueOf(rec.getDate()) +
                rec.getAmountInCents() +
                rec.getDescription() +
                rec.getRecipient();

        return computeChecksum(sb.getBytes(StandardCharsets.UTF_8));
    }

    public String computeChecksum(byte[] data) {
        synchronized (this) {
            return BaseEncoding.base16().encode((getDigest().digest(data))).toUpperCase();
        }
    }

    private MessageDigest getDigest() {
        return digesterMap.computeIfAbsent(Thread.currentThread().threadId(), id -> {
            try {
                return MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e);
            }
        });
    }
}
