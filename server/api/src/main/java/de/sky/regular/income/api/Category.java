package de.sky.regular.income.api;

import de.sky.regular.income.api.detail.UpdatedMetaInformation;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class Category extends UpdatedMetaInformation {
    public String name;
    public String description;

    public boolean isNew;

    public UUID parentId;
    public List<Category> subCategories;

    public Long usageCount;
}
