ALTER TABLE BANK_STATEMENT
    DROP CONSTRAINT BANK_STATEMENT_PREVIOUS_STATEMENT_ID_KEY;

ALTER TABLE BANK_STATEMENT
    ADD CONSTRAINT BANK_STATEMENT_PREVIOUS_STATEMENT_ID_KEY UNIQUE (PREVIOUS_STATEMENT_ID)
        DEFERRABLE INITIALLY DEFERRED;
