CREATE VIEW V_CATEGORIES_WITH_USAGE_COUNT
AS (
   WITH FIN_TRANSACTION_COUNT AS (SELECT CATEGORY_ID,
                                         COUNT(*) AS FINANCIAL_TRANSACTION_COUNT
                                  FROM FINANCIAL_TRANSACTION
                                  GROUP BY CATEGORY_ID),
        TURNOVERS_COUNT AS (SELECT CATEGORY_ID,
                                   COUNT(*) AS TURNOVER_COUNT
                            FROM TURNOVER_ROW
                            GROUP BY CATEGORY_ID),
        OVERALL_COUNT AS (SELECT COALESCE(c.CATEGORY_ID, t.CATEGORY_ID) AS CATEGORY_ID,
                                 COALESCE(c.FINANCIAL_TRANSACTION_COUNT, 0) +
                                 COALESCE(t.TURNOVER_COUNT, 0)          AS USE_COUNT
                          FROM FIN_TRANSACTION_COUNT c
                                   FULL JOIN TURNOVERS_COUNT t ON c.CATEGORY_ID = t.CATEGORY_ID),
        COMPLETED_CATEGORIES AS (SELECT c.*, coalesce(o.USE_COUNT, 0) as USE_COUNT
                                 FROM CATEGORY c
                                          LEFT JOIN OVERALL_COUNT o ON c.ID = o.CATEGORY_ID)
   SELECT *
   FROM COMPLETED_CATEGORIES
);
