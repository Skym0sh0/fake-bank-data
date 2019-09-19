package de.sky.regular.income.importing.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public enum ExcelColumns {
    DATE(0, "Datum", LocalDate.class, cell -> {
        return Optional.ofNullable(cell.getDateCellValue())
                .map(Date::toInstant)
                .map(i -> i.atZone(ZoneId.systemDefault()))
                .map(ZonedDateTime::toLocalDate);
    }),
    BALANCE(1, "Haben", Double.class, ExcelColumns::parseNumeric),
    INCOME(2, "Einkommen", Double.class, ExcelColumns::parseNumeric),
    EXPENSE(3, "Ausgaben", Double.class, ExcelColumns::parseNumeric),
    REASON(4, "Grund", String.class, cell -> Optional.of(cell.getStringCellValue())),
    CONTROL(5, "Kontrolle", Double.class, cell -> Optional.empty()),
    CONTROL_DIFFERENCE(6, "KontrolleDifferenz", Double.class, cell -> Optional.empty()),
    PERIODIC(7, "Regelmässig", Boolean.class, cell -> Optional.of(!StringUtils.isBlank(cell.getStringCellValue()))),
    ;

    public static final String SHEET_NAME = "Konto";

    private final int index;
    private final String name;
    private final Class<?> type;
    private final Function<Cell, ? extends Optional<?>> parser;

    <T> ExcelColumns(int index, String name, Class<T> type, Function<Cell, Optional<T>> parser) {
        this.index = index;
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
        this.parser = Objects.requireNonNull(parser);
    }

    public <T> Optional<T> parseFrom(Row row, Class<T> type) {
        if (!Objects.equals(type, this.type))
            throw new IllegalArgumentException();

        Cell cell = row.getCell(this.index);

        if (cell == null)
            return Optional.empty();

        try {
            return this.parser.apply(cell)
                    .map(type::cast);
        } catch (Exception e) {
            if (cell.getCellType() == CellType.STRING && this.name.equals(cell.getStringCellValue()))
                return Optional.empty();

            String msg = String.format("Cell %d of line %d could not be parsed to %s: '%s'",
                    cell.getColumnIndex(),
                    cell.getRowIndex(),
                    this.type.getSimpleName(),
                    new DataFormatter().formatCellValue(cell)
            );

            throw new RuntimeException(msg, e);
        }
    }

    private static Optional<Double> parseNumeric(Cell c) {
        if (c.getCellType() == CellType.BLANK)
            return Optional.empty();

        return Optional.of(c.getNumericCellValue());
    }
}
