package de.sky.regular.income.importing.excel;

import de.sky.regular.income.api.CategoryPatch;
import de.sky.regular.income.api.StatementPatch;
import lombok.Data;

import java.util.Collection;
import java.util.List;

@Data
public class ImportResult {
    public Collection<CategoryPatch> categories;
    public List<StatementPatch> statements;
}
