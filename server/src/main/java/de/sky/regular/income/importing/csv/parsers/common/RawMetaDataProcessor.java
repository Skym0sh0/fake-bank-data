package de.sky.regular.income.importing.csv.parsers.common;

import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.AbstractRowProcessor;

import java.util.*;

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

        var map = new LinkedHashMap<String, String>();
        for (int i = 0; i < headers.length; i++) {
            map.put(headers[i], row[i]);
        }

        rows.add(new RowMetaData(lineNumberOffset + context.currentLine(), map));
    }

    public record RowMetaData(long lineNumber, Map<String, String> row) {
    }

    public List<RowMetaData> getRows() {
        return Collections.unmodifiableList(rows);
    }
}
