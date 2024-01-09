package de.sky.regular.income.importing.csv.parsers;

import com.univocity.parsers.conversions.ObjectConversion;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CsvProcessorConverters {
    public static class DateConverter extends ObjectConversion<LocalDate> {
        private static final DateTimeFormatter FRMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        @Override
        protected LocalDate fromString(String input) {
            return LocalDate.parse(input, FRMT);
        }

        @Override
        public String revert(LocalDate input) {
            return input.format(FRMT);
        }
    }

    public static class MoneyAmountConverter extends ObjectConversion<Integer> {
        @Override
        protected Integer fromString(String input) {
            double tmp = Double.parseDouble(input.replace(',', '.'));
            return (int) (tmp * 100);
        }

        @Override
        public String revert(Integer input) {
            double tmp = input / 100.0;
            return String.format("%f.2", tmp);
        }
    }
}
