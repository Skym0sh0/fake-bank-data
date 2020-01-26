package de.sky.regular.income.dao;

import de.sky.regular.income.api.Category;
import generated.sky.regular.income.tables.records.CategoryRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import static generated.sky.regular.income.Tables.CATEGORY;

@Component
public class CategoryDAO {
    public List<Category> fetchAllCategories(DSLContext ctx) {
        var categories = ctx.selectFrom(CATEGORY)
                .fetchInto(CATEGORY);

        return categories.stream()
                .filter(rec -> Objects.isNull(rec.getParentCategory()))
                .map(rec -> mapRecursively(rec, categories))
                .sorted((a, b) -> {
                    ToIntFunction<Category> size = c -> Optional.ofNullable(c).map(Category::getChildren).map(List::size).orElse(0);

                    return -(size.applyAsInt(a) - size.applyAsInt(b));
                })
//                .limit(5)
                .collect(Collectors.toList());
    }

    private Category mapRecursively(CategoryRecord rec, Collection<CategoryRecord> records) {
        Category c = new Category();

        c.setId(rec.getId());
        c.setName(rec.getName());
        c.setDescription(rec.getDescription());

        c.setChildren(findChildren(rec.getId(), records));

        c.setCreatedAt(ZonedDateTime.now());
        if (ThreadLocalRandom.current().nextBoolean())
            c.setUpdatedAt(ZonedDateTime.now());

        return c;
    }

    private List<Category> findChildren(UUID id, Collection<CategoryRecord> records) {
        return records.stream()
                .filter(rec -> Objects.equals(id, rec.getParentCategory()))
                .map(rec -> mapRecursively(rec, records))
                .collect(Collectors.toList());
    }
}
