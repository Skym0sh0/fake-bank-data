package de.sky.regular.income.api.detail;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public abstract class UpdatedMetaInformation extends CreatedMetaInformation {
    public ZonedDateTime updatedAt;
//    public UserInfo updatedBy;
}
