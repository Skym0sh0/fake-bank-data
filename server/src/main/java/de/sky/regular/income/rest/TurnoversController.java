package de.sky.regular.income.rest;

import com.google.common.base.Stopwatch;
import de.sky.regular.income.api.CategoryTurnoverReport;
import de.sky.regular.income.api.RawCsvTable;
import de.sky.regular.income.api.TurnoverImport;
import de.sky.regular.income.api.TurnoverImportFormat;
import de.sky.regular.income.api.TurnoverImportPatch;
import de.sky.regular.income.api.TurnoverImportRowsPatch;
import de.sky.regular.income.api.TurnoverPreview;
import de.sky.regular.income.importing.csv.TurnoverCsvImporter;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DatePart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.*;

@Slf4j
@RestController
public class TurnoversController implements generated.sky.regular.income.api.rest.TurnoversApi {
    private final TurnoverCsvImporter importer;

    @Autowired
    public TurnoversController(TurnoverCsvImporter importer) {
        this.importer = Objects.requireNonNull(importer);
    }

    @Override
    public ResponseEntity<List<TurnoverImportFormat>> getSupportedFormats() {
        return ResponseEntity.ok(List.of(TurnoverImportFormat.values()));
    }

    @Override
    public ResponseEntity<RawCsvTable> processCsvPreview(MultipartFile file, String encoding) {
        log.info("CSV Preview table processing file {} with {} bytes...", file.getOriginalFilename(), file.getSize());

        var sw = Stopwatch.createStarted();

        try (var reader = new BufferedReader(new InputStreamReader(file.getInputStream(), Charset.forName(encoding)))) {
            return ResponseEntity.ok(importer.parseCsvAsTablePreview(reader));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            log.info("Preview processing finished in {}", sw.stop());
        }
    }

    @Override
    public ResponseEntity<TurnoverPreview> processPreview(TurnoverImportFormat format, MultipartFile file, String encoding) {
        log.info("Preview-processing {} file {} with {} bytes...", format, file.getOriginalFilename(), file.getSize());
        var sw = Stopwatch.createStarted();

        try (var reader = new BufferedReader(new InputStreamReader(file.getInputStream(), Charset.forName(encoding)))) {
            var rows = importer.parseForPreview(format, reader);

            return ResponseEntity.ok(
                    TurnoverPreview.builder()
                            .filename(file.getOriginalFilename())
                            .format(format)
                            .encoding(encoding)
                            .uploadTime(OffsetDateTime.now())
                            .rows(rows)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            log.info("Preview processing finished in {}", sw.stop());
        }
    }

    @Override
    public ResponseEntity<TurnoverImport> createTurnoverImport(MultipartFile file, TurnoverImportPatch patch) {
        log.info("Importing {} file {} with {} bytes and {} data rows...", patch.getFormat(), file.getOriginalFilename(), file.getSize(), patch.getRows().size());

        try {
            return ResponseEntity.ok(importer.createImport(ZonedDateTime.now(), file, patch));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<List<TurnoverImport>> fetchTurnoverImports() {
        return ResponseEntity.ok(importer.fetchTurnoverImports());
    }

    @Override
    public ResponseEntity<TurnoverImport> fetchTurnoverImport(UUID id) {
        return ResponseEntity.ok(importer.fetchTurnoverImport(id));
    }

    @Override
    public ResponseEntity<TurnoverImport> patchTurnoverImport(UUID id, TurnoverImportRowsPatch patch) {
        return ResponseEntity.ok(importer.patchTurnoverImport(id, patch));
    }

    @Override
    public ResponseEntity<Void> deleteTurnoverImport(UUID id) {
        importer.deleteImport(id);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> batchPatchTurnoverImports(TurnoverImportRowsPatch patch) {
        importer.batchPatchTurnoverImport(patch);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<de.sky.regular.income.api.TurnoverRow>> fetchTurnoversForCategory(UUID categoryId) {
        return ResponseEntity.ok(importer.fetchTurnoversForImport(categoryId));
    }

    @Override
    public ResponseEntity<CategoryTurnoverReport> fetchTurnoversReportForCategory(UUID categoryId, String iDateGrouping, Integer iRecursionLevel) {
        var recursionLevel = Optional.ofNullable(iRecursionLevel);

        var dateGrouping = DatePart.valueOf(iDateGrouping.toUpperCase());

        if (!Arrays.asList(DatePart.DAY, DatePart.MONTH, DatePart.YEAR).contains(dateGrouping))
            throw new IllegalArgumentException("Unsupported date grouping: " + dateGrouping);

        return ResponseEntity.ok(importer.fetchTurnoversReportForImport(categoryId, dateGrouping, recursionLevel.orElse(1)));
    }
}
