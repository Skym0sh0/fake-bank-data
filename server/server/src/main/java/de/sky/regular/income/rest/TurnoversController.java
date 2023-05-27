package de.sky.regular.income.rest;

import de.sky.regular.income.importing.excel.TurnoverCsvImporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/turnovers")
public class TurnoversController {
    private final TurnoverCsvImporter importer;

    @Autowired
    public TurnoversController(TurnoverCsvImporter importer) {
        this.importer = Objects.requireNonNull(importer);
    }

    @PostMapping("preview")
    public void processPreview(@RequestParam("file") MultipartFile file) throws Exception {
        log.info("Preview-processing file {} with {} bytes...", file.getOriginalFilename(), file.getSize());

        try (var is = new BufferedInputStream(file.getInputStream())) {
            importer.parseForPreview(is);
        }

        log.info("Preview processing finished");
    }
}
