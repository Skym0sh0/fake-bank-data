DROP VIEW IF EXISTS V_ORDERED_BANK_STATEMENTS;

CREATE VIEW V_ORDERED_BANK_STATEMENTS
AS (
   WITH RECURSIVE CTE AS (
       SELECT *, 0 AS RANK
       FROM BANK_STATEMENT
       WHERE PREVIOUS_STATEMENT_ID IS NULL

       UNION ALL
       SELECT e.*, C.RANK + 1
       FROM CTE c
                JOIN BANK_STATEMENT e ON e.PREVIOUS_STATEMENT_ID = c.ID
   )
   SELECT *
   FROM CTE
);

DROP VIEW IF EXISTS V_CATEGORIES_WITH_USAGE_COUNT;

CREATE VIEW V_CATEGORIES_WITH_USAGE_COUNT
AS
(
WITH TURNOVERS_COUNT AS (SELECT CATEGORY_ID,
                                COUNT(*) AS USE_COUNT
                         FROM TURNOVER_ROW
                         GROUP BY CATEGORY_ID),
     COMPLETED_CATEGORIES AS (SELECT c.*, COALESCE(o.USE_COUNT, 0) as USE_COUNT
                              FROM CATEGORY c
                                       LEFT JOIN TURNOVERS_COUNT o ON c.ID = o.CATEGORY_ID)
SELECT *
FROM COMPLETED_CATEGORIES
);

