package de.sky.common.database.converters;

import org.jooq.Converter;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

public class TimestampConverter implements Converter<Timestamp, ZonedDateTime> {
    @Override
    public ZonedDateTime from(Timestamp databaseObject) {
        return Optional.ofNullable(databaseObject)
                .map(Timestamp::toInstant)
                .map(ts -> ts.atZone(ZoneId.of("UTC")))
                .orElse(null);
    }

    @Override
    public Timestamp to(ZonedDateTime userObject) {
        return Optional.ofNullable(userObject)
                .map(ZonedDateTime::toInstant)
                .map(Timestamp::from)
                .orElse(null);
    }

    @Override
    public Class<Timestamp> fromType() {
        return Timestamp.class;
    }

    @Override
    public Class<ZonedDateTime> toType() {
        return ZonedDateTime.class;
    }
}
