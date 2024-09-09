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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

	private final DatabaseConnection db;
	private final CategoryDAO dao;
	private final UserProvider user;

	@Autowired
	public CategoryController(DatabaseSupplier supplier, CategoryDAO dao, UserProvider user) {
		this(supplier.getDatabase(), dao, user);
	}

	@PostMapping
	public Category createCategory(@RequestBody CategoryPatch cat) {
		return db.transactionWithResult(ctx -> dao.createCategory(ctx, user.getCurrentUser(ctx).getId(), null, cat));
	}

	@PostMapping("{parent-id}/children")
	public Category createCategoryAsChild(@PathVariable("parent-id") UUID parentId, @RequestBody CategoryPatch cat) {
		return db.transactionWithResult(ctx -> dao.createCategory(ctx, user.getCurrentUser(ctx).getId(), parentId, cat));
	}

	@PatchMapping("{id}")
	public Category updateCategory(@PathVariable("id") UUID id, @RequestBody CategoryPatch cat) {
		return db.transactionWithResult(ctx -> dao.updateCategory(ctx, user.getCurrentUser(ctx).getId(), id, cat));
	}

	@GetMapping
	public List<Category> getFlatCategories(@RequestParam(value = "deep", required = false) Boolean deep) {
		return db.transactionWithResult(
				ctx -> dao.fetchAllCategoriesFlatted(ctx, user.getCurrentUser(ctx).getId(), Optional.ofNullable(deep).orElse(false)));
	}

	@GetMapping("tree")
	public List<Category> getCategoriesAsTree() {
		return db.transactionWithResult((DSLContext ctx) -> dao.fetchCategoryTree(ctx, user.getCurrentUser(ctx).getId()));
	}

	@PatchMapping("{parent-id}/children/{child-id}")
	public Category reallocateCategory(@PathVariable("parent-id") UUID newParentId, @PathVariable("child-id") UUID childId) {
		return db.transactionWithResult(ctx -> dao.reassignParent(ctx, user.getCurrentUser(ctx).getId(), childId, newParentId));
	}

	@DeleteMapping("{id}")
	public void deleteCategory(@PathVariable("id") UUID id) {
		db.transactionWithoutResult(ctx -> dao.deleteCategory(ctx, user.getCurrentUser(ctx).getId(), id));
	}
}
