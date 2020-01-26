BEGIN;

INSERT INTO CATEGORY(ID, NAME, IS_INCOME)
SELECT UUID_GENERATE_V4(), REASON, amount_value_cents >= 0
FROM FINANCIAL_TRANSACTION
ON CONFLICT DO NOTHING;

UPDATE FINANCIAL_TRANSACTION AS T
SET CATEGORY_ID = C.ID
FROM CATEGORY AS C
WHERE C.NAME = T.REASON;

COMMIT;

ALTER TABLE FINANCIAL_TRANSACTION
    ALTER COLUMN CATEGORY_ID SET NOT NULL;