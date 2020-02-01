package de.sky.regular.income.api;

import de.sky.regular.income.api.detail.UpdatedMetaInformation;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class Category extends UpdatedMetaInformation {
    public String name;
    public String description;

    public UUID parentId;
    public List<Category> children;
}
