package de.sky.regular.income.dao;

import de.sky.regular.income.api.Category;
import de.sky.regular.income.api.CategoryPatch;
import generated.sky.regular.income.tables.records.CategoryRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import static generated.sky.regular.income.Tables.CATEGORY;
import static org.jooq.impl.DSL.*;

@Component
public class CategoryDAO {
    private final Map<UUID, ZonedDateTime> creationTs = new HashMap<>();
    private final Map<UUID, ZonedDateTime> updateTs = new HashMap<>();

    public Category createCategory(DSLContext ctx, UUID parentId, CategoryPatch patch) {
        CategoryRecord rec = ctx.newRecord(CATEGORY);

        rec.setId(UUID.randomUUID());
        rec.setParentCategory(parentId);
        rec.setName(patch.name.trim());
        rec.setDescription(Optional.ofNullable(patch.description).map(String::trim).orElse(null));
        rec.setIsIncome(false);

        rec.insert();

        creationTs.put(rec.getId(), ZonedDateTime.now());

        return fetchById(ctx, rec.getId());
    }

    public Category updateCategory(DSLContext ctx, UUID id, CategoryPatch patch) {
        CategoryRecord rec = ctx.selectFrom(CATEGORY)
                .where(CATEGORY.ID.eq(id))
                .fetchOne();

        rec.setName(patch.name.trim());
        rec.setDescription(Optional.ofNullable(patch.description).map(String::trim).orElse(null));

        rec.update();

        updateTs.put(id, ZonedDateTime.now());

        return fetchById(ctx, id);
    }

    public Category fetchById(DSLContext ctx, UUID id) {
        return ctx.selectFrom(CATEGORY)
                .where(CATEGORY.ID.eq(id))
                .fetchOne(rec -> mapRecursively(ctx, rec));
    }

    public List<Category> fetchChildrenOf(DSLContext ctx, UUID parent) {
        return ctx.selectFrom(CATEGORY)
                .where(CATEGORY.PARENT_CATEGORY.eq(parent))
                .fetch(rec -> mapRecursively(ctx, rec));
    }

    public List<Category> fetchAllCategoriesFlatted(DSLContext ctx, boolean deep) {
        return ctx.selectFrom(CATEGORY)
                .fetchInto(CATEGORY)
                .map(rec -> {
                    if (!deep)
                        return mapFlat(rec);

                    return mapRecursively(ctx, rec);
                });
    }

    public void deleteCategory(DSLContext ctx, UUID id) {
        var child = CATEGORY.as("child");
        var parent = CATEGORY.as("parent");

        ctx.update(child)
                .set(CATEGORY.PARENT_CATEGORY, parent.PARENT_CATEGORY)
                .from(parent)
                .where(and(
                        child.PARENT_CATEGORY.eq(parent.ID),
                        parent.ID.eq(id)
                ))
                .execute();

        ctx.deleteFrom(CATEGORY)
                .where(CATEGORY.ID.eq(id))
                .execute();
    }

    public Category reassignParent(DSLContext ctx, UUID childId, UUID newParentId) {
        ctx.update(CATEGORY)
                .set(CATEGORY.PARENT_CATEGORY, newParentId)
                .where(CATEGORY.ID.eq(childId))
                .execute();

        var parent = CATEGORY.as("parent");
        var cteName = name("cte");
        var cte = CATEGORY.as(cteName);

        UUID rootParentId = ctx.withRecursive(cte.getName())
                .as(
                        select(CATEGORY.ID, CATEGORY.PARENT_CATEGORY)
                                .from(CATEGORY)
                                .where(CATEGORY.ID.eq(childId))
                                .unionAll(
                                        select(parent.ID, parent.PARENT_CATEGORY)
                                                .from(cteName)
                                                .join(parent)
                                                .on(parent.ID.eq(cte.PARENT_CATEGORY))
                                )
                )
                .select(cte.ID)
                .from(cteName)
                .where(cte.PARENT_CATEGORY.isNull())
                .fetchOneInto(CATEGORY.ID.getType());

        return fetchById(ctx, rootParentId);
    }

    @Deprecated
    public List<Category> fetchAllCategoriesAsHierarchy(DSLContext ctx) {
        var categories = ctx.selectFrom(CATEGORY)
                .fetchInto(CATEGORY);

        return categories.stream()
                .filter(rec -> Objects.isNull(rec.getParentCategory()))
                .map(rec -> mapRecursively(rec, null, categories))
                .sorted((a, b) -> {
                    ToIntFunction<Category> size = c -> Optional.ofNullable(c).map(Category::getSubCategories).map(List::size).orElse(0);

                    return -(size.applyAsInt(a) - size.applyAsInt(b));
                })
                .collect(Collectors.toList());
    }

    @Deprecated
    private Category mapRecursively(CategoryRecord rec, UUID parentId, Collection<CategoryRecord> records) {
        Category c = new Category();

        c.setId(rec.getId());
        c.setName(rec.getName());
        c.setDescription(rec.getDescription());

        c.setParentId(parentId);
        c.setSubCategories(findChildren(rec.getId(), records));

        c.setCreatedAt(ZonedDateTime.now());
        if (ThreadLocalRandom.current().nextBoolean())
            c.setUpdatedAt(ZonedDateTime.now());

        return c;
    }

    @Deprecated
    private List<Category> findChildren(UUID id, Collection<CategoryRecord> records) {
        return records.stream()
                .filter(rec -> Objects.equals(id, rec.getParentCategory()))
                .map(rec -> mapRecursively(rec, id, records))
                .collect(Collectors.toList());
    }

    private Category mapFlat(CategoryRecord rec) {
        Category c = new Category();

        c.setId(rec.getId());
        c.setParentId(rec.getParentCategory());

        c.setName(rec.getName());
        c.setDescription(rec.getDescription());

        c.setSubCategories(null);

        c.setCreatedAt(creationTs.computeIfAbsent(rec.getId(), id -> ZonedDateTime.now()));
        c.setUpdatedAt(updateTs.get(rec.getId()));

        return c;
    }

    private Category mapRecursively(DSLContext ctx, CategoryRecord rec) {
        Category c = new Category();

        c.setId(rec.getId());
        c.setParentId(rec.getParentCategory());

        c.setName(rec.getName());
        c.setDescription(rec.getDescription());

        c.setSubCategories(fetchChildrenOf(ctx, rec.getId()));

        c.setCreatedAt(creationTs.computeIfAbsent(rec.getId(), id -> ZonedDateTime.now()));
        c.setUpdatedAt(updateTs.get(rec.getId()));

        return c;
    }
}
