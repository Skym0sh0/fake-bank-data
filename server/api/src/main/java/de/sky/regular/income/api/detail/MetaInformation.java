package de.sky.regular.income.api.detail;

import de.sky.regular.income.api.UserInfo;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public abstract class MetaInformation {
    public UUID id;

    public ZonedDateTime createdAt;
    public UserInfo createdBy;

    public ZonedDateTime updatedAt;
    public UserInfo updatedBy;
}
