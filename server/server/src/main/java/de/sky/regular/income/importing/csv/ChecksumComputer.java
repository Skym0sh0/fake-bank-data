package de.sky.regular.income.importing.csv;

import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.function.Function;

@Service
public class ChecksumComputer implements Function<TurnoverCsvParser.TurnoverRecord, String> {
    private final MessageDigest digester;

    @SneakyThrows
    public ChecksumComputer() {
        this.digester = MessageDigest.getInstance("MD5");
    }

    @Override
    public String apply(TurnoverCsvParser.TurnoverRecord rec) {
        return computeChecksum(rec);
    }

    public String computeChecksum(TurnoverCsvParser.TurnoverRecord rec) {
        String sb = String.valueOf(rec.getDate()) +
                rec.getAmountInCents() +
                rec.getDescription() +
                rec.getRecipient();

        return computeChecksum(sb.getBytes(StandardCharsets.UTF_8));
    }

    public String computeChecksum(byte[] data) {
        return Hex.encodeHexString(digester.digest(data));
    }
}
