package de.sky.regular.income.api.detail;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.ZonedDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class UpdatedMetaInformation extends CreatedMetaInformation {
    public ZonedDateTime updatedAt;
//    public UserInfo updatedBy;
}
