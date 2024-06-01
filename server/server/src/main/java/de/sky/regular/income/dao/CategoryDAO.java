package de.sky.regular.income.dao;

import de.sky.regular.income.api.Category;
import de.sky.regular.income.api.CategoryBudget;
import de.sky.regular.income.api.CategoryPatch;
import generated.sky.regular.income.tables.records.CategoryBudgetRecord;
import generated.sky.regular.income.tables.records.CategoryRecord;
import generated.sky.regular.income.tables.records.VCategoriesWithUsageCountRecord;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.impl.TableRecordImpl;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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

        insertBudget(ctx, userId, rec.getId(), patch.budget);

        if (parentId != null) {
            var updated = ctx.update(CATEGORY)
                    .set(CATEGORY.LAST_UPDATED_AT, now)
                    .where(CATEGORY.ID.eq(parentId))
                    .and(CATEGORY.OWNER_ID.eq(userId))
                    .execute();
            if (updated != 1)
                throw new RuntimeException("Creating child category failed due to updates " + updated);
        }

        return fetchById(ctx, userId, rec.getId());
    }

    public Category updateCategory(DSLContext ctx, UUID userId, UUID id, CategoryPatch patch) {
        CategoryRecord rec = ctx.selectFrom(CATEGORY)
                .where(CATEGORY.ID.eq(id))
                .and(CATEGORY.OWNER_ID.eq(userId))
                .forUpdate()
                .fetchOptional()
                .orElseThrow(() -> new RuntimeException("Not category found: " + id));

        rec.setName(patch.name.trim());
        rec.setDescription(Optional.ofNullable(patch.description).map(String::trim).orElse(null));
        rec.setLastUpdatedAt(ZonedDateTime.now().toOffsetDateTime());

        rec.update();

        insertBudget(ctx, userId, id, patch.budget);

        return fetchById(ctx, userId, id);
    }

    private static void insertBudget(DSLContext ctx, UUID userId, UUID categoryId, CategoryBudget categoryBudget) {
        ctx.deleteFrom(CATEGORY_BUDGET)
                .where(CATEGORY_BUDGET.OWNER_ID.eq(userId))
                .and(CATEGORY_BUDGET.CATEGORY_ID.eq(categoryId))
                .execute();

        Optional.ofNullable(categoryBudget)
                .map(b -> {
                    var budget = ctx.newRecord(CATEGORY_BUDGET);

                    budget.setOwnerId(userId);
                    budget.setCategoryId(categoryId);
                    budget.setId(UUID.randomUUID());

                    budget.setMonthlyBudgetAmountValueCents(b.budgetInCents);
                    budget.setWarningThresholdFraction(Optional.ofNullable(b.exceedingThreshold).map(BigDecimal::valueOf).orElse(BigDecimal.ZERO));

                    return budget;
                })
                .ifPresent(TableRecordImpl::insert);
    }

    public Category fetchById(DSLContext ctx, UUID userId, UUID id) {
        return fetchAllCategoriesFlatted(ctx, userId, true)
                .stream()
                .filter(cat -> Objects.equals(id, cat.getId()))
                .findAny()
                .orElseThrow();
    }

    public void deleteCategory(DSLContext ctx, UUID userId, UUID id) {
        var now = ZonedDateTime.now().toOffsetDateTime();

        var turnovers = TURNOVER_ROW.as("turnovers");
        var parent = CATEGORY.as("parent");
        var child = CATEGORY.as("child");

        ctx.update(turnovers)
                .set(turnovers.CATEGORY_ID, child.PARENT_CATEGORY)
                .set(turnovers.LAST_UPDATED_AT, now)
                .from(child)
                .where(DSL.and(
                        child.ID.eq(turnovers.CATEGORY_ID),
                        child.ID.eq(id),
                        child.PARENT_CATEGORY.isNotNull()
                ))
                .and(turnovers.OWNER_ID.eq(userId))
                .and(child.OWNER_ID.eq(userId))
                .execute();

        ctx.update(child)
                .set(CATEGORY.PARENT_CATEGORY, parent.PARENT_CATEGORY)
                .set(CATEGORY.LAST_UPDATED_AT, now)
                .from(parent)
                .where(and(
                        child.PARENT_CATEGORY.eq(parent.ID),
                        parent.ID.eq(id)
                ))
                .and(child.OWNER_ID.eq(userId))
                .and(parent.OWNER_ID.eq(userId))
                .execute();

        ctx.update(parent)
                .set(CATEGORY.LAST_UPDATED_AT, now)
                .from(child)
                .where(parent.ID.eq(child.PARENT_CATEGORY))
                .and(child.ID.eq(id))
                .and(child.OWNER_ID.eq(userId))
                .and(parent.OWNER_ID.eq(userId))
                .execute();

        ctx.deleteFrom(CATEGORY_BUDGET)
                .where(CATEGORY_BUDGET.CATEGORY_ID.eq(id))
                .and(CATEGORY_BUDGET.OWNER_ID.eq(userId))
                .execute();

        ctx.deleteFrom(CATEGORY)
                .where(CATEGORY.ID.eq(id))
                .and(CATEGORY.OWNER_ID.eq(userId))
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
                .where(CATEGORY.OWNER_ID.eq(userId))
                .and(CATEGORY.ID.eq(childId).or(CATEGORY.ID.eq(newParentId)))
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

        var categoriesByParentId = allCategories.stream()
                .filter(cat -> cat.getParentCategory() != null)
                .collect(Collectors.groupingBy(VCategoriesWithUsageCountRecord::getParentCategory));

        var budgetsByCategoryId = ctx.selectFrom(CATEGORY_BUDGET)
                .where(CATEGORY_BUDGET.OWNER_ID.eq(userId))
                .fetch()
                .intoMap(CATEGORY_BUDGET.CATEGORY_ID);

        return allCategories.map(rec -> {
            if (!deep) {
                return mapFlat(rec, budgetsByCategoryId);
            }

            return mapRecursively(categoriesByParentId, rec, budgetsByCategoryId);
        });
    }

    public List<Category> fetchCategoryTree(DSLContext ctx, UUID userId) {
        return fetchAllCategoriesFlatted(ctx, userId, true)
                .stream()
                .filter(cat -> cat.getParentId() == null)
                .toList();
    }

    private Category mapRecursively(Map<UUID, List<VCategoriesWithUsageCountRecord>> categoriesByParentId, VCategoriesWithUsageCountRecord rec, Map<UUID, CategoryBudgetRecord> budgetsByCategoryId) {
        var c = mapFlat(rec, budgetsByCategoryId);

        c.setSubCategories(
                Optional.ofNullable(categoriesByParentId.get(rec.getId()))
                        .stream()
                        .flatMap(Collection::stream)
                        .map(child -> mapRecursively(categoriesByParentId, child, budgetsByCategoryId))
                        .toList()
        );

        return c;
    }

    private Category mapFlat(VCategoriesWithUsageCountRecord rec, Map<UUID, CategoryBudgetRecord> budgetsByCategoryId) {
        var c = new Category();

        c.setId(rec.getId());
        c.setParentId(rec.getParentCategory());

        c.setName(rec.getName());
        c.setDescription(rec.getDescription());
        c.setNew(Objects.equals(rec.getCreatedAt(), rec.getLastUpdatedAt()));

        c.setBudget(
                Optional.ofNullable(budgetsByCategoryId.get(rec.getId()))
                        .map(CategoryDAO::mapCategoryBudgetToAPi)
                        .orElse(null)
        );

        c.setSubCategories(null);

        c.setUsageCount(rec.getUseCount());

        c.setCreatedAt(rec.getCreatedAt().toZonedDateTime());
        c.setUpdatedAt(rec.getLastUpdatedAt().toZonedDateTime());

        return c;
    }

    private static CategoryBudget mapCategoryBudgetToAPi(CategoryBudgetRecord b) {
        var budget = new CategoryBudget();
        budget.setBudgetInCents(b.getMonthlyBudgetAmountValueCents());
        budget.setExceedingThreshold(b.getWarningThresholdFraction().doubleValue());
        return budget;
    }
}
