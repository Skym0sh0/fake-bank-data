package de.sky.regular.income.importing.csv.parsers.common;

import de.sky.regular.income.importing.csv.parsers.TurnoverRecord;

import java.util.function.BiFunction;
import java.util.function.Function;

public record CsvRecordWithMetadata<T>(T bean, RawMetaDataProcessor.RowMetaData metaData) {
    public TurnoverRecord toTurnOverRecord(BiFunction<T, TurnoverRecord.TurnoverRecordBuilder, TurnoverRecord> mapper) {
        var builder = TurnoverRecord.builder()
                .lineNumber(metaData().lineNumber())
                .rawValuePairs(metaData.row());

        return mapper.apply(bean, builder);
    }

    public static <R> Function<CsvRecordWithMetadata<R>, TurnoverRecord> map(BiFunction<R, TurnoverRecord.TurnoverRecordBuilder, TurnoverRecord> mapper) {
        return tmp -> tmp.toTurnOverRecord(mapper);
    }
}
