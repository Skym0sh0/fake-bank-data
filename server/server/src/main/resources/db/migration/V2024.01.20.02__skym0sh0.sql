DROP VIEW V_CATEGORIES_WITH_USAGE_COUNT;

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

ALTER TABLE FINANCIAL_TRANSACTION
    DROP COLUMN CATEGORY_ID;
