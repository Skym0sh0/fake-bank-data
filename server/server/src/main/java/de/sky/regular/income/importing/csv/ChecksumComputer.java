package de.sky.regular.income.importing.csv;

import com.google.common.io.BaseEncoding;
import de.sky.regular.income.importing.csv.parsers.TurnoverRecord;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.function.Function;

@Service
public class ChecksumComputer implements Function<TurnoverRecord, String> {
    private final MessageDigest digester;

    @SneakyThrows
    public ChecksumComputer() {
        this.digester = MessageDigest.getInstance("MD5");
    }

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
        return BaseEncoding.base16().encode((digester.digest(data)));
    }
}
