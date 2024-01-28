package de.sky.regular.income.backup;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class CompressorTest {
    private final Compressor compressor = new Compressor("filename.zip");

    @Test
    void showHowUsageIsDone() {
        try (var cmp = compressor.open()) {
            cmp.addFile("filename.txt", "some-file-content");
            cmp.addFile("file2.csv", "some,file,content");
        }

        assertThat(compressor.getFilename())
                .isEqualTo("filename.zip");

        assertThat(compressor.getRawInputStream())
                .isNotNull();

        assertThat(compressor.readFilesAsString())
                .containsEntry("filename.txt", "some-file-content")
                .containsEntry("file2.csv", "some,file,content");
    }

    @Test
    void checkDoubleOpening() {
        assertThat(compressor.open())
                .isNotNull();

        assertThatCode(compressor::open)
                .isNotNull();
    }

    @Test
    void checkOpenRawStreamYieldsAnError() {
        assertThatCode(compressor::getRawInputStream)
                .isNotNull();
    }
}
