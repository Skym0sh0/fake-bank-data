package de.sky.regular.income.api;

import de.sky.regular.income.api.detail.UpdatedMetaInformation;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class Category extends UpdatedMetaInformation {
    public UUID id;
    public String name;
    public String description;

    public List<Category> children;
}
