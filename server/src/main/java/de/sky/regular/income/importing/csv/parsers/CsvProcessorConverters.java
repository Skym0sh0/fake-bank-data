package de.sky.regular.income.importing.csv.parsers;

import com.univocity.parsers.conversions.ObjectConversion;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CsvProcessorConverters {
    @RequiredArgsConstructor
    public static abstract class BaseDateConverter extends ObjectConversion<LocalDate> {
        private final DateTimeFormatter frmt;

        @Override
        protected LocalDate fromString(String input) {
            return LocalDate.parse(input, frmt);
        }

        @Override
        public String revert(LocalDate input) {
            return input.format(frmt);
        }
    }

    public static class FullGermanDateConverter extends BaseDateConverter {
        public FullGermanDateConverter() {
            super(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        }
    }

    public static class ShortGermanDateConverter extends BaseDateConverter {
        public ShortGermanDateConverter() {
            super(DateTimeFormatter.ofPattern("dd.MM.yy"));
        }
    }

    public static class MoneyAmountConverter extends ObjectConversion<Integer> {
        @Override
        protected Integer fromString(String input) {
            var inputWithoutThousandsSeparator = StringUtils.remove(input, '.'); // 1.000,000 -> 1000,000
            var inputInEnglishStyle = inputWithoutThousandsSeparator.replace(',', '.'); // 1000,000 -> 1000.000

            double tmp = Double.parseDouble(inputInEnglishStyle);
            return (int) (tmp * 100);
        }

        @Override
        public String revert(Integer input) {
            double tmp = input / 100.0;
            return String.format("%f.2", tmp);
        }
    }

    public static class TimeConverter extends ObjectConversion<LocalTime> {
        private static final DateTimeFormatter FRMT = DateTimeFormatter.ofPattern("HH:mm:ss");

        @Override
        protected LocalTime fromString(String input) {
            return LocalTime.parse(input, FRMT);
        }

        @Override
        public String revert(LocalTime input) {
            return input.format(FRMT);
        }
    }

    public static class TimeZoneConverter extends ObjectConversion<ZoneId> {
        @Override
        protected ZoneId fromString(String input) {
            return ZoneId.of(input);
        }

        @Override
        public String revert(ZoneId input) {
            return input.getId();
        }
    }
}
