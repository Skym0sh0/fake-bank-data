package de.sky.regular.income.rest;

import de.sky.common.database.DatabaseConnection;
import de.sky.regular.income.api.Category;
import de.sky.regular.income.api.CategoryPatch;
import de.sky.regular.income.dao.CategoryDAO;
import de.sky.regular.income.database.DatabaseSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final DatabaseConnection db;
    private final CategoryDAO dao;

    public CategoryController(DatabaseConnection db, CategoryDAO dao) {
        this.db = Objects.requireNonNull(db);
        this.dao = Objects.requireNonNull(dao);
    }

    @Autowired
    public CategoryController(DatabaseSupplier supplier, CategoryDAO dao) {
        this(supplier.getDatabase(), dao);
    }

    @PostMapping
    public Category createCategory(@RequestBody CategoryPatch cat) {
        return db.transactionWithResult(ctx -> dao.createCategory(ctx, null, cat));
    }

    @PostMapping("{parent-id}/children")
    public Category createCategoryAsChild(@PathVariable("parent-id") UUID parentId, @RequestBody CategoryPatch cat) {
        return db.transactionWithResult(ctx -> dao.createCategory(ctx, parentId, cat));
    }

    @PatchMapping("{id}")
    public Category updateCategory(@PathVariable("id") UUID id, @RequestBody CategoryPatch cat) {
        return db.transactionWithResult(ctx -> dao.updateCategory(ctx, id, cat));
    }

    @GetMapping
    public List<Category> getFlatCategories(@RequestParam(value = "deep", required = false) Boolean deep) {
        return db.transactionWithResult(ctx -> dao.fetchAllCategoriesFlatted(ctx, Optional.ofNullable(deep).orElse(false)));
    }

    @GetMapping("tree")
    public List<Category> getCategoriesAsTree() {
        return db.transactionWithResult(dao::fetchCategoryTree);
    }

    @PatchMapping("{parent-id}/children/{child-id}")
    public Category reallocateCategory(@PathVariable("parent-id") UUID newParentId, @PathVariable("child-id") UUID childId) {
        return db.transactionWithResult(ctx -> dao.reassignParent(ctx, childId, newParentId));
    }

    @DeleteMapping("{id}")
    public void deleteCategory(@PathVariable("id") UUID id) {
        db.transactionWithoutResult(ctx -> dao.deleteCategory(ctx, id));
    }
}
