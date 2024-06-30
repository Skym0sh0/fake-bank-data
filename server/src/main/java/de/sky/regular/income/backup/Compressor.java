package de.sky.regular.income.backup;

import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Compressor {
    @Getter
    private final String filename;

    private OpenCompressedFile openFile;
    private byte[] compressedContent;

    public Compressor(String filename) {
        this.filename = Objects.requireNonNull(filename);
    }

    public InputStream getRawInputStream() {
        if (compressedContent == null)
            throw new IllegalStateException("Compressing was not yet done or finished");

        return new ByteArrayInputStream(compressedContent);
    }

    @SneakyThrows
    public Map<String, byte[]> readFiles() {
        Map<String, byte[]> result = new HashMap<>();

        try (var zip = new ZipInputStream(this.getRawInputStream())) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                var content = StreamUtils.copyToByteArray(zip);

                result.put(entry.getName(), content);
            }

            return result;
        }
    }

    public Map<String, String> readFilesAsString() {
        return readFiles()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new String(e.getValue(), StandardCharsets.UTF_8)));
    }

    public OpenCompressedFile open() {
        if (openFile != null)
            throw new IllegalStateException("Is already opened");

        if (compressedContent != null)
            throw new IllegalStateException("Is already closed");

        openFile = new OpenCompressedFile(this);

        return openFile;
    }

    private void finishCompression(byte[] compressed) {
        if (openFile == null)
            throw new IllegalStateException("Compressing was not yet started");
        if (compressedContent != null)
            throw new IllegalStateException("Compression was already done");

        this.compressedContent = compressed;
        this.openFile = null;
    }

    public static class OpenCompressedFile implements AutoCloseable {
        private final ByteArrayOutputStream os = new ByteArrayOutputStream();
        private final ZipOutputStream zip = new ZipOutputStream(os);

        private final Compressor opener;

        public OpenCompressedFile(Compressor opener) {
            this.opener = Objects.requireNonNull(opener);

            zip.setLevel(Deflater.BEST_COMPRESSION);
        }

        @SneakyThrows
        @Override
        public void close() {
            zip.close();

            opener.finishCompression(os.toByteArray());
        }

        @SneakyThrows
        public void addFile(String name, String content) {
            addFile(name, content.getBytes(StandardCharsets.UTF_8));
        }

        @SneakyThrows
        public void addFile(String name, byte[] content) {
            zip.putNextEntry(new ZipEntry(name));

            StreamUtils.copy(content, zip);

            zip.closeEntry();
        }
    }
}
