CREATE TABLE CATEGORY
(
    ID              UUID    NOT NULL UNIQUE,
    PARENT_CATEGORY UUID,
    NAME            TEXT    NOT NULL UNIQUE,
    IS_INCOME       BOOLEAN NOT NULL,
    DESCRIPTION     TEXT,

    PRIMARY KEY (ID),
    FOREIGN KEY (PARENT_CATEGORY) REFERENCES CATEGORY (ID)
);

ALTER TABLE FINANCIAL_TRANSACTION
    ADD COLUMN CATEGORY_ID UUID;

ALTER TABLE FINANCIAL_TRANSACTION
    ADD FOREIGN KEY (CATEGORY_ID) REFERENCES CATEGORY (ID);
