ALTER TABLE CATEGORY
    ADD COLUMN OWNER_ID UUID;

UPDATE CATEGORY
SET OWNER_ID = (SELECT ID FROM USERS LIMIT 1);

ALTER TABLE CATEGORY
    ALTER OWNER_ID SET NOT NULL;

ALTER TABLE CATEGORY
    ADD CONSTRAINT FK_CATEGORY_OWNER
        FOREIGN KEY (OWNER_ID)
            REFERENCES USERS (ID);

ALTER TABLE category
DROP CONSTRAINT CATEGORY_NAME_KEY;

CREATE UNIQUE INDEX IDX_CATEGORY_NAME_KEY
    ON CATEGORY (OWNER_ID, NAME);
