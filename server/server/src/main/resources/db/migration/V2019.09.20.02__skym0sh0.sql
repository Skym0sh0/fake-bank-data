ALTER TABLE BANK_STATEMENT
    ADD COLUMN CREATED_AT TIMESTAMP WITH TIME ZONE;
ALTER TABLE BANK_STATEMENT
    ALTER COLUMN CREATED_AT SET DEFAULT NOW();

UPDATE BANK_STATEMENT
SET CREATED_AT = NOW();

ALTER TABLE BANK_STATEMENT
    ALTER COLUMN CREATED_AT SET NOT NULL;

ALTER TABLE FINANCIAL_TRANSACTION
    ADD COLUMN CREATED_AT TIMESTAMP WITH TIME ZONE;
ALTER TABLE FINANCIAL_TRANSACTION
    ALTER COLUMN CREATED_AT SET DEFAULT NOW();

UPDATE FINANCIAL_TRANSACTION
SET CREATED_AT = NOW();

ALTER TABLE FINANCIAL_TRANSACTION
    ALTER COLUMN CREATED_AT SET NOT NULL;

ALTER TABLE BANK_STATEMENT
    ADD COLUMN UPDATED_AT TIMESTAMP WITH TIME ZONE;
ALTER TABLE BANK_STATEMENT
    ALTER COLUMN UPDATED_AT SET DEFAULT NOW();

UPDATE BANK_STATEMENT
SET UPDATED_AT = NOW();

ALTER TABLE BANK_STATEMENT
    ALTER COLUMN UPDATED_AT SET NOT NULL;
