package de.sky.regular.income.dao;

import de.sky.regular.income.api.Category;
import de.sky.regular.income.api.CategoryPatch;
import generated.sky.regular.income.tables.records.CategoryRecord;
import generated.sky.regular.income.tables.records.VCategoriesWithUsageCountRecord;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static generated.sky.regular.income.Tables.*;
import static org.jooq.impl.DSL.*;

@Component
public class CategoryDAO {

    public Category createCategory(DSLContext ctx, UUID userId, UUID parentId, CategoryPatch patch) {
        var now = ZonedDateTime.now().toOffsetDateTime();

        CategoryRecord rec = ctx.newRecord(CATEGORY);

        rec.setId(UUID.randomUUID());
        rec.setOwnerId(userId);
        rec.setParentCategory(parentId);
        rec.setName(patch.name.trim());
        rec.setDescription(Optional.ofNullable(patch.description).map(String::trim).orElse(null));
        rec.setIsIncome(false);
        rec.setCreatedAt(now);
        rec.setLastUpdatedAt(now);

        rec.insert();

        return fetchById(ctx, userId, rec.getId());
    }

    public Category updateCategory(DSLContext ctx, UUID userId, UUID id, CategoryPatch patch) {
        CategoryRecord rec = ctx.selectFrom(CATEGORY)
                .where(CATEGORY.ID.eq(id))
                .and(CATEGORY.OWNER_ID.eq(userId))
                .fetchOne();

        rec.setName(patch.name.trim());
        rec.setDescription(Optional.ofNullable(patch.description).map(String::trim).orElse(null));
        rec.setLastUpdatedAt(ZonedDateTime.now().toOffsetDateTime());

        rec.update();

        return fetchById(ctx, userId, id);
    }

    public Category fetchById(DSLContext ctx, UUID userId, UUID id) {
        return fetchAllCategoriesFlatted(ctx, userId, true)
                .stream()
                .filter(cat -> Objects.equals(id, cat.getId()))
                .findAny()
                .orElseThrow();
    }

    public void deleteCategory(DSLContext ctx, UUID userId, UUID id) {
        var turnovers = TURNOVER_ROW.as("turnovers");
        var parent = CATEGORY.as("parent");
        var child = CATEGORY.as("child");

        ctx.update(turnovers)
                .set(turnovers.CATEGORY_ID, child.PARENT_CATEGORY)
                .from(child)
                .where(DSL.and(
                        child.ID.eq(turnovers.CATEGORY_ID),
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

    public Category reassignParent(DSLContext ctx, UUID userId, UUID childId, UUID newParentId) {
        var now = ZonedDateTime.now().toOffsetDateTime();

        ctx.update(CATEGORY)
                .set(CATEGORY.PARENT_CATEGORY, newParentId)
                .set(CATEGORY.LAST_UPDATED_AT, now)
                .where(CATEGORY.ID.eq(childId))
                .and(CATEGORY.OWNER_ID.eq(userId))
                .execute();
        ctx.update(CATEGORY)
                .set(CATEGORY.LAST_UPDATED_AT, now)
                .where(CATEGORY.OWNER_ID.eq(userId).and(
                        CATEGORY.ID.eq(childId).or(CATEGORY.ID.eq(newParentId))
                ))
                .execute();

        var parent = CATEGORY.as("parent");
        var cteName = name("cte");
        var cte = CATEGORY.as(cteName);

        UUID rootParentId = ctx.withRecursive(cte.getName())
                .as(
                        select(CATEGORY.ID, CATEGORY.PARENT_CATEGORY)
                                .from(CATEGORY)
                                .where(CATEGORY.ID.eq(childId))
                                .and(CATEGORY.OWNER_ID.eq(userId))
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

        return fetchById(ctx, userId, rootParentId);
    }

    public List<Category> fetchAllCategoriesFlatted(DSLContext ctx, UUID userId, boolean deep) {
        var allCategories = ctx.selectFrom(V_CATEGORIES_WITH_USAGE_COUNT)
                .where(V_CATEGORIES_WITH_USAGE_COUNT.OWNER_ID.eq(userId))
                .orderBy(V_CATEGORIES_WITH_USAGE_COUNT.NAME)
                .limit(10_000)
                .fetchInto(V_CATEGORIES_WITH_USAGE_COUNT);

        var categoriesByParentId = allCategories
                .stream()
                .filter(cat -> cat.getParentCategory() != null)
                .collect(Collectors.groupingBy(VCategoriesWithUsageCountRecord::getParentCategory));

        return allCategories.map(rec -> {
            if (!deep) {
                return mapFlat(rec);
            }

            return mapRecursively(categoriesByParentId, rec);
        });
    }

    public List<Category> fetchCategoryTree(DSLContext ctx, UUID userId) {
        return fetchAllCategoriesFlatted(ctx, userId, true)
                .stream()
                .filter(cat -> cat.getParentId() == null)
                .toList();
    }

    private Category mapRecursively(Map<UUID, List<VCategoriesWithUsageCountRecord>> categoriesByParentId, VCategoriesWithUsageCountRecord rec) {
        var c = mapFlat(rec);

        c.setSubCategories(
                Optional.ofNullable(categoriesByParentId.get(rec.getId()))
                        .stream()
                        .flatMap(Collection::stream)
                        .map(child -> mapRecursively(categoriesByParentId, child))
                        .toList()
        );

        return c;
    }

    private Category mapFlat(VCategoriesWithUsageCountRecord rec) {
        var c = new Category();

        c.setId(rec.getId());
        c.setParentId(rec.getParentCategory());

        c.setName(rec.getName());
        c.setDescription(rec.getDescription());
        c.setNew(Objects.equals(rec.getCreatedAt(), rec.getLastUpdatedAt()));

        c.setSubCategories(null);

        c.setUsageCount(rec.getUseCount());

        c.setCreatedAt(rec.getCreatedAt().toZonedDateTime());
        c.setUpdatedAt(rec.getLastUpdatedAt().toZonedDateTime());

        return c;
    }
}
