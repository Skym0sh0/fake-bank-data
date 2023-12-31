package de.sky.regular.income.importing.csv;

import de.sky.common.database.DatabaseConnection;
import de.sky.regular.income.api.Category;
import de.sky.regular.income.dao.CategoryDAO;
import de.sky.regular.income.database.DatabaseSupplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategorySuggester {

    private final DatabaseConnection db;
    private final CategoryDAO dao;

    @Autowired
    public CategorySuggester(DatabaseSupplier supplier, CategoryDAO dao) {
        this(supplier.getDatabase(), dao);
    }

    public CategoryBatchSuggester openForBatchSuggestion() {
        var allCategories = db.transactionWithResult(ctx -> dao.fetchAllCategoriesFlatted(ctx, false));

        log.info("Fetched {} categories for batch suggestion...", allCategories.size());

        return new CategoryBatchSuggester(allCategories);
    }

    @RequiredArgsConstructor
    public static class CategoryBatchSuggester implements AutoCloseable {
        private final List<Category> categories;

        public Optional<Category> findCategorySuggestion(String description, String category) {
            return Stream.of(category, description)
                    .filter(Objects::nonNull)
                    .flatMap(name -> {
                        return categories.stream()
                                .filter(cat -> Objects.equals(cat.getName(), name));
                    })
                    .findFirst();
        }

        @Override
        public void close() {
        }
    }
}
