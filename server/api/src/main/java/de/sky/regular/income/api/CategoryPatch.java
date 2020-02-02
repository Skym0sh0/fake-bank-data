package de.sky.regular.income.api;

import de.sky.regular.income.api.detail.PatchInformation;
import lombok.Data;

@Data
public class CategoryPatch extends PatchInformation {
    public String name;
    public String description;
}
