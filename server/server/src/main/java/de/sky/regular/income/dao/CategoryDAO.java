package de.sky.regular.income.dao;

import de.sky.regular.income.api.Category;
import de.sky.regular.income.api.CategoryPatch;
import generated.sky.regular.income.tables.records.CategoryRecord;
import generated.sky.regular.income.tables.records.VCategoriesWithUsageCountRecord;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static generated.sky.regular.income.Tables.*;
import static org.jooq.impl.DSL.*;

@Component
public class CategoryDAO {
    public Category createCategory(DSLContext ctx, UUID parentId, CategoryPatch patch) {
        CategoryRecord rec = ctx.newRecord(CATEGORY);

        rec.setId(UUID.randomUUID());
        rec.setParentCategory(parentId);
        rec.setName(patch.name.trim());
        rec.setDescription(Optional.ofNullable(patch.description).map(String::trim).orElse(null));
        rec.setIsIncome(false);
        rec.setLastUpdatedAt(ZonedDateTime.now().toOffsetDateTime());

        rec.insert();

        return fetchById(ctx, rec.getId());
    }

    public Category updateCategory(DSLContext ctx, UUID id, CategoryPatch patch) {
        CategoryRecord rec = ctx.selectFrom(CATEGORY)
                .where(CATEGORY.ID.eq(id))
                .fetchOne();

        rec.setName(patch.name.trim());
        rec.setDescription(Optional.ofNullable(patch.description).map(String::trim).orElse(null));
        rec.setLastUpdatedAt(ZonedDateTime.now().toOffsetDateTime());

        rec.update();

        return fetchById(ctx, id);
    }

    public Category fetchById(DSLContext ctx, UUID id) {
        return ctx.selectFrom(V_CATEGORIES_WITH_USAGE_COUNT)
                .where(V_CATEGORIES_WITH_USAGE_COUNT.ID.eq(id))
                .fetchOne(rec -> mapRecursively(ctx, rec));
    }

    public List<Category> fetchChildrenOf(DSLContext ctx, UUID parent) {
        return ctx.selectFrom(V_CATEGORIES_WITH_USAGE_COUNT)
                .where(V_CATEGORIES_WITH_USAGE_COUNT.PARENT_CATEGORY.eq(parent))
                .orderBy(V_CATEGORIES_WITH_USAGE_COUNT.NAME)
                .fetch(rec -> mapRecursively(ctx, rec));
    }

    public List<Category> fetchAllCategoriesFlatted(DSLContext ctx, boolean deep) {
        return ctx.selectFrom(V_CATEGORIES_WITH_USAGE_COUNT)
                .fetchInto(V_CATEGORIES_WITH_USAGE_COUNT)
                .map(rec -> {
                    if (!deep)
                        return mapFlat(rec);

                    return mapRecursively(ctx, rec);
                });
    }

    public List<Category> fetchCategoryTree(DSLContext ctx) {
        return ctx.selectFrom(V_CATEGORIES_WITH_USAGE_COUNT)
                .where(V_CATEGORIES_WITH_USAGE_COUNT.PARENT_CATEGORY.isNull())
                .orderBy(V_CATEGORIES_WITH_USAGE_COUNT.NAME)
                .fetch()
                .map(rec -> mapRecursively(ctx, rec));
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
        ctx.update(CATEGORY)
                .set(CATEGORY.LAST_UPDATED_AT, ZonedDateTime.now().toOffsetDateTime())
                .where(CATEGORY.ID.eq(childId))
                .or(CATEGORY.ID.eq(newParentId))
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

    private Category mapFlat(VCategoriesWithUsageCountRecord rec) {
        var c = new Category();

        c.setId(rec.getId());
        c.setParentId(rec.getParentCategory());

        c.setName(rec.getName());
        c.setDescription(rec.getDescription());

        c.setSubCategories(null);

        c.setUsageCount(rec.getUseCount());

        c.setCreatedAt(ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault()));
        c.setUpdatedAt(rec.getLastUpdatedAt().toZonedDateTime());

        return c;
    }

    private Category mapRecursively(DSLContext ctx, VCategoriesWithUsageCountRecord rec) {
        var c = mapFlat(rec);

        c.setSubCategories(fetchChildrenOf(ctx, rec.getId()));

        return c;
    }
}
