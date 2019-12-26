package de.sky.regular.income.api.detail;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public abstract class CreatedMetaInformation {
    public UUID id;

    public ZonedDateTime createdAt;
//    public UserInfo createdBy;
}
