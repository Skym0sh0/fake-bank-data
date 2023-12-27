package de.sky.regular.income.rest;

import com.google.common.base.Stopwatch;
import de.sky.regular.income.api.turnovers.TurnOverPreview;
import de.sky.regular.income.api.turnovers.TurnoverImport;
import de.sky.regular.income.api.turnovers.TurnoverImportPatch;
import de.sky.regular.income.importing.csv.TurnoverCsvImporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/turnover-import")
public class TurnoversController {
    private final TurnoverCsvImporter importer;

    @Autowired
    public TurnoversController(TurnoverCsvImporter importer) {
        this.importer = Objects.requireNonNull(importer);
    }

    @PostMapping("preview")
    public TurnOverPreview processPreview(@RequestParam("file") MultipartFile file) throws Exception {
        log.info("Preview-processing file {} with {} bytes...", file.getOriginalFilename(), file.getSize());

        var sw = Stopwatch.createStarted();

        try (var is = new BufferedInputStream(file.getInputStream())) {
            var rows = importer.parseForPreview(is);

            return TurnOverPreview.builder()
                    .filename(file.getOriginalFilename())
                    .uploadTime(ZonedDateTime.now())
                    .rows(rows)
                    .build();
        } finally {
            log.info("Preview processing finished in {}", sw.stop());
        }
    }

    @PostMapping(consumes = "multipart/form-data")
    public TurnoverImport createTurnoverImport(@RequestPart("file") MultipartFile file, @RequestPart("data") TurnoverImportPatch patch) {
        log.info(
                "Importing file: {} {} {} {} with data:",
                file.getOriginalFilename(),
                file.getSize(),
                file.getName(),
                file.getContentType()
        );

        patch.getRows().forEach(r -> log.info("\t - {}", r));

        return TurnoverImport.builder()
                .id(UUID.randomUUID())
                .turnovers(List.of())
                .date(LocalDate.now())
                .build();
    }

    @GetMapping
    public List<TurnoverImport> createTurnoverImport() {
        return List.of();
    }
}
