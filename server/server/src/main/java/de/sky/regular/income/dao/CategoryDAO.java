package de.sky.regular.income.dao;

import de.sky.regular.income.api.Category;
import de.sky.regular.income.api.CategoryPatch;
import generated.sky.regular.income.tables.records.CategoryRecord;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.*;

import static generated.sky.regular.income.Tables.CATEGORY;
import static generated.sky.regular.income.Tables.FINANCIAL_TRANSACTION;
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
        var transaction = FINANCIAL_TRANSACTION.as("transaction");
        var parent = CATEGORY.as("parent");
        var child = CATEGORY.as("child");

        ctx.update(transaction)
                .set(transaction.CATEGORY_ID, child.PARENT_CATEGORY)
                .from(child)
                .where(DSL.and(
                        child.ID.eq(transaction.CATEGORY_ID),
                        child.ID.eq(id),
                        child.PARENT_CATEGORY.isNotNull()
                ))
                .execute();

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
