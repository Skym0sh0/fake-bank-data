package de.sky.regular.income.rest;

import com.google.common.base.Stopwatch;
import de.sky.regular.income.api.turnovers.*;
import de.sky.regular.income.importing.csv.TurnoverCsvImporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
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

    @GetMapping("/formats")
    public List<TurnoverImportFormat> getSupportedFormats() {
        return List.of(TurnoverImportFormat.values());
    }

    @PostMapping("preview/csv")
    public RawCsvTable processPreview(@RequestParam("file") MultipartFile file, @RequestParam(value = "encoding", defaultValue = "UTF-8") String encoding) throws Exception {
        log.info("CSV Preview table processing file {} with {} bytes...", file.getOriginalFilename(), file.getSize());

        var sw = Stopwatch.createStarted();

        try (var reader = new BufferedReader(new InputStreamReader(file.getInputStream(), Charset.forName(encoding)))) {
            return importer.parseCsvAsTablePreview(reader);
        } finally {
            log.info("Preview processing finished in {}", sw.stop());
        }
    }

    @PostMapping("preview")
    public TurnOverPreview processPreview(@RequestParam("file") MultipartFile file, @RequestParam("format") TurnoverImportFormat format, @RequestParam(value = "encoding", defaultValue = "UTF-8") String encoding) throws Exception {
        log.info("Preview-processing {} file {} with {} bytes...", format, file.getOriginalFilename(), file.getSize());

        var sw = Stopwatch.createStarted();

        try (var reader = new BufferedReader(new InputStreamReader(file.getInputStream(), Charset.forName(encoding)))) {
            var rows = importer.parseForPreview(format, reader);

            return TurnOverPreview.builder()
                    .filename(file.getOriginalFilename())
                    .format(format)
                    .encoding(encoding)
                    .uploadTime(ZonedDateTime.now())
                    .rows(rows)
                    .build();
        } finally {
            log.info("Preview processing finished in {}", sw.stop());
        }
    }

    @PostMapping(consumes = "multipart/form-data")
    public TurnoverImport createTurnoverImport(@RequestPart("file") MultipartFile file, @RequestPart("data") TurnoverImportPatch patch) throws Exception {
        log.info("Importing {} file {} with {} bytes and {} data rows...", patch.format, file.getOriginalFilename(), file.getSize(), patch.getRows().size());

        return importer.createImport(ZonedDateTime.now(), file, patch);
    }

    @GetMapping
    public List<TurnoverImport> fetchTurnoverImports() {
        return importer.fetchTurnoverImports();
    }

    @GetMapping("/{id}")
    public TurnoverImport fetchTurnoverImport(@PathVariable("id") UUID id) {
        return importer.fetchTurnoverImport(id);
    }

    @DeleteMapping("/{id}")
    public void deleteTurnoverImport(@PathVariable("id") UUID id) {
        importer.deleteImport(id);
    }
}
