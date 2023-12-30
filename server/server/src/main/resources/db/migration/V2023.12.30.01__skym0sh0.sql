/*
 This migration script migrates all obsolete bank_statements with their transactions to the new turnovers
 */

-- map and insert bank_statements into turnover_files
INSERT INTO TURNOVER_FILE_IMPORT
WITH STMTS AS (SELECT ID
               FROM BANK_STATEMENT),
     DATA AS (SELECT S.ID                                                                AS STMT,
                     DATE_RECORD,
                     AMOUNT_VALUE_CENTS,
                     CASE WHEN IS_PERIODIC THEN 'is-periodic' ELSE 'is-not-periodic' END AS IS_PERIODIC,
                     REASON,
                     ';'                                                                 AS DIVIDER
              FROM FINANCIAL_TRANSACTION T
                       JOIN STMTS S ON S.ID = T.BANK_STATEMENT_ID),
     CSV_DATA AS (SELECT STMT,
                         DATE_RECORD::TEXT
                             || DIVIDER || AMOUNT_VALUE_CENTS::TEXT
                             || DIVIDER || IS_PERIODIC::TEXT
                             || DIVIDER || REASON
                             AS CSV
                  FROM DATA),
     CSV_DATA_ALL_TRANSACTIONS AS (SELECT STMT,
                                          STRING_AGG(CSV, E'\n') AS CSV
                                   FROM CSV_DATA
                                   GROUP BY STMT),
     DATA_FOR_FIRST_STATEMENTS AS (SELECT S.ID AS STMT,
                                          S.DATE_RECORD
                                              || ';' || S.AMOUNT_BALANCE_CENTS
                                              || ';' || 'is-not-periodic'
                                              || ';' || 'Start Guthaben'
                                               AS CSV
                                   FROM BANK_STATEMENT S
                                   WHERE S.PREVIOUS_STATEMENT_ID IS NULL),
     DATA_FOR_EMPTY_STATEMENTS AS (SELECT S.ID AS STMT,
                                          ''   AS CSV
                                   FROM BANK_STATEMENT S
                                   WHERE S.PREVIOUS_STATEMENT_ID IS NOT NULL
                                     AND NOT EXISTS(SELECT *
                                                    FROM FINANCIAL_TRANSACTION T
                                                    WHERE T.BANK_STATEMENT_ID = S.ID)),
     CSV_DATA_FULL AS (SELECT *
                       FROM CSV_DATA_ALL_TRANSACTIONS
                       UNION ALL
                       SELECT *
                       FROM DATA_FOR_EMPTY_STATEMENTS
                       UNION ALL
                       SELECT *
                       FROM DATA_FOR_FIRST_STATEMENTS)
SELECT ID                               AS ID,
       GREATEST(CREATED_AT, UPDATED_AT) AS IMPORTED_AT,
       DATE_RECORD                      AS FILENAME,
       LENGTH(CSV)                      AS FILE_SIZE,
       'text/csv'                       AS FILE_CONTENT_TYPE,
       convert_to(CSV, 'UTF-8')         AS FILE_CONTENT,
       MD5(CSV)                         AS CHECKSUM
FROM BANK_STATEMENT S
         JOIN CSV_DATA_FULL C ON S.ID = C.STMT;

-- map and insert Financial transactions into turnovers
INSERT INTO TURNOVER_ROW
WITH RANKED AS (SELECT *,
                       ROW_NUMBER() OVER (PARTITION BY DATE_RECORD, AMOUNT_VALUE_CENTS, REASON) AS RNK,
                       COUNT(*) OVER (PARTITION BY DATE_RECORD, AMOUNT_VALUE_CENTS, REASON)     AS CNT
                FROM FINANCIAL_TRANSACTION),
     WITH_SUFFIX AS (SELECT *,
                            CASE
                                WHEN CNT < 2
                                    THEN ''
                                ELSE ' ' || RNK
                                END AS SUFFIX
                     FROM RANKED),
     TRANSACTIONS_SIMPLE_AS_TURNOVERS AS (SELECT BANK_STATEMENT_ID  AS TURNOVER_FILE,
                                                 DATE_RECORD        AS DATE,
                                                 AMOUNT_VALUE_CENTS AS AMOUNT_VALUE_CENTS,
                                                 REASON || SUFFIX   AS DESCRIPTION,
                                                 REASON             AS SUGGESTED_CATEGORY,
                                                 REASON             AS RECIPIENT,
                                                 CATEGORY_ID        AS CATEGORY_ID,
                                                 CREATED_AT         AS LAST_UPDATED_AT,
                                                 ID                 AS ORIGINAL_ID
                                          FROM WITH_SUFFIX),
     TRANSACTIONS_AS_TURNOVERS AS (SELECT *,
                                          MD5(DATE || AMOUNT_VALUE_CENTS::TEXT || DESCRIPTION || RECIPIENT) AS ID,
                                          MD5(DATE || AMOUNT_VALUE_CENTS::TEXT || DESCRIPTION || RECIPIENT) AS CHECKSUM
                                   FROM TRANSACTIONS_SIMPLE_AS_TURNOVERS)
SELECT ID,
       TURNOVER_FILE,
       DATE,
       AMOUNT_VALUE_CENTS,
       DESCRIPTION,
       SUGGESTED_CATEGORY,
       RECIPIENT,
       CHECKSUM,
       CATEGORY_ID,
       LAST_UPDATED_AT
FROM TRANSACTIONS_AS_TURNOVERS;
