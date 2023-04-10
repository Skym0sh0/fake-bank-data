package de.sky.regular.income.utils;

import de.sky.regular.income.api.TransactionPatch;

import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class TransactionChecksumCalculator {
    public String calculateChecksum(TransactionPatch transaction) {
        byte[] checksum = Digester.createMD5()
                .append(transaction.getDate())
                .append(transaction.getAmountInCents())
                .append(transaction.getCategoryId().toString())
                .create();

        return bytesToHex(checksum);
    }

    private static class Digester {
        private final MessageDigest digest;

        private Digester(MessageDigest digest) {
            this.digest = Objects.requireNonNull(digest);
        }

        public static Digester createMD5() {
            try {
                return new Digester(MessageDigest.getInstance("MD5"));
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        public byte[] create() {
            return digest.digest();
        }

        public Digester append(LocalDate d) {
            return append(d.toEpochDay());
        }

        public Digester append(Long l) {
            digest.update(ByteBuffer.allocate(Long.BYTES).putLong(l));

            return this;
        }

        public Digester append(Integer i) {
            digest.update(ByteBuffer.allocate(Integer.BYTES).putInt(i));

            return this;
        }

        public Digester append(String s) {
            digest.update(s.getBytes(StandardCharsets.UTF_8));

            return this;
        }

        public Digester append(List<String> list) {
            list.forEach(this::append);

            return this;
        }
    }

    private static String bytesToHex(byte[] hash) {
        return DatatypeConverter.printHexBinary(hash).toLowerCase();
    }
}
