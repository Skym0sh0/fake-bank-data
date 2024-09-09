package de.sky.regular.income.rest;

import de.sky.regular.income.api.Category;
import de.sky.regular.income.api.CategoryPatch;
import de.sky.regular.income.dao.CategoryDAO;
import de.sky.regular.income.database.DatabaseConnection;
import de.sky.regular.income.database.DatabaseSupplier;
import de.sky.regular.income.users.UserProvider;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CategoryController implements generated.sky.regular.income.api.rest.CategoryApi {

    private final DatabaseConnection db;
    private final CategoryDAO dao;
    private final UserProvider user;

    @Autowired
    public CategoryController(DatabaseSupplier supplier, CategoryDAO dao, UserProvider user) {
        this(supplier.getDatabase(), dao, user);
    }

    @Override
    public ResponseEntity<Category> createCategory(CategoryPatch cat) {
        return ResponseEntity.ok(
                db.transactionWithResult(ctx -> dao.createCategory(ctx, user.getCurrentUser(ctx).getId(), null, cat))
        );
    }

    @Override
    public ResponseEntity<Category> createCategoryAsChild(UUID parentId, CategoryPatch cat) {
        return ResponseEntity.ok(
                db.transactionWithResult(ctx -> dao.createCategory(ctx, user.getCurrentUser(ctx).getId(), parentId, cat))
        );
    }

    @Override
    public ResponseEntity<Category> updateCategory(UUID id, CategoryPatch cat) {
        return ResponseEntity.ok(
                db.transactionWithResult(ctx -> dao.updateCategory(ctx, user.getCurrentUser(ctx).getId(), id, cat))
        );
    }

    @Override
    public ResponseEntity<List<Category>> getFlatCategories(Boolean deep) {
        return ResponseEntity.ok(
                db.transactionWithResult(
                        ctx -> dao.fetchAllCategoriesFlatted(ctx, user.getCurrentUser(ctx).getId(), Optional.ofNullable(deep).orElse(false)))
        );
    }

    @Override
    public ResponseEntity<List<Category>> getCategoriesAsTree() {
        return ResponseEntity.ok(
                db.transactionWithResult((DSLContext ctx) -> dao.fetchCategoryTree(ctx, user.getCurrentUser(ctx).getId()))
        );
    }

    @Override
    public ResponseEntity<Category> reallocateCategory(UUID newParentId, UUID childId) {
        return ResponseEntity.ok(
                db.transactionWithResult(ctx -> dao.reassignParent(ctx, user.getCurrentUser(ctx).getId(), childId, newParentId))
        );
    }

    @Override
    public ResponseEntity<Void> deleteCategory(UUID id) {
        db.transactionWithoutResult(ctx -> dao.deleteCategory(ctx, user.getCurrentUser(ctx).getId(), id));

        return ResponseEntity.ok().build();
    }
}
