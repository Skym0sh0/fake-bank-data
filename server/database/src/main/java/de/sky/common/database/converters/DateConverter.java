package de.sky.common.database.converters;

import org.jooq.Converter;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

public class DateConverter implements Converter<Date, LocalDate> {

    @Override
    public LocalDate from(Date databaseObject) {
        return Optional.ofNullable(databaseObject)
                .map(Date::toLocalDate)
                .orElse(null);
    }

    @Override
    public Date to(LocalDate userObject) {
        return Optional.ofNullable(userObject)
                .map(Date::valueOf)
                .orElse(null);
    }

    @Override
    public Class<Date> fromType() {
        return Date.class;
    }

    @Override
    public Class<LocalDate> toType() {
        return LocalDate.class;
    }
}
