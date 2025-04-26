package de.sky.regular.income.importing.csv.parsers.common;

import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.AbstractRowProcessor;
import de.sky.regular.income.importing.csv.parsers.TurnoverRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

class RawMetaDataProcessor extends AbstractRowProcessor {
    private final long lineNumberOffset;

    private List<RowMetaData> rows;

    public RawMetaDataProcessor(long lineNumberOffset) {
        if (lineNumberOffset < 0)
            throw new IllegalArgumentException("Line number offset cannot be negative");

        this.lineNumberOffset = lineNumberOffset;
    }

    public RawMetaDataProcessor() {
        this(0);
    }

    @Override
    public void processStarted(ParsingContext context) {
        rows = new ArrayList<>();
    }

    @Override
    public void rowProcessed(String[] row, ParsingContext context) {
        String[] headers = context.headers();

        if (headers.length != row.length)
            throw new IllegalStateException("Headers do not match row length " + Arrays.toString(headers) + " vs " + Arrays.toString(row));

        var cells = IntStream.range(0, headers.length)
                .mapToObj(i -> new TurnoverRecord.TurnoverRawRecordValues(headers[i], row[i]))
                .toList();

        rows.add(new RowMetaData(lineNumberOffset + context.currentLine(), cells));
    }

    public record RowMetaData(long lineNumber, List<TurnoverRecord.TurnoverRawRecordValues> cellValue) {
    }


    public List<RowMetaData> getRows() {
        return Collections.unmodifiableList(rows);
    }
}
