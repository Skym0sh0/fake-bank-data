package de.sky.regular.income.api;

import de.sky.regular.income.api.detail.UpdatedMetaInformation;
import lombok.Data;

import java.util.List;

@Data
public class Category extends UpdatedMetaInformation {
    public String name;
    public String description;

    public List<Category> children;
}
