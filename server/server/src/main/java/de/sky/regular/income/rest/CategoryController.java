package de.sky.regular.income.rest;

import de.sky.common.database.DatabaseConnection;
import de.sky.regular.income.api.Category;
import de.sky.regular.income.dao.CategoryDAO;
import de.sky.regular.income.database.DatabaseSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

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

    @GetMapping
    public List<Category> getAllCategories() {
        return db.transactionWithResult(dao::fetchAllCategories);
    }
}
