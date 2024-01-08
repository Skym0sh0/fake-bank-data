ALTER TABLE TURNOVER_ROW
    ADD COLUMN OWNER_ID UUID;

UPDATE TURNOVER_ROW
SET OWNER_ID = (SELECT ID FROM USERS LIMIT 1);

ALTER TABLE TURNOVER_ROW
    ALTER OWNER_ID SET NOT NULL;

CREATE INDEX IDX_TURNOVER_ROW_OWNER
    ON TURNOVER_ROW (OWNER_ID);

-- row belongs to an owner
ALTER TABLE TURNOVER_ROW
    ADD CONSTRAINT FK_TURNOVER_ROW_OWNER
        FOREIGN KEY (OWNER_ID)
            REFERENCES USERS (ID);
-- row belongs to an file of the same owner
ALTER TABLE TURNOVER_ROW
    ADD CONSTRAINT FK_TURNOVER_ROW_FILE_FKEY
        FOREIGN KEY (TURNOVER_FILE, OWNER_ID)
            REFERENCES TURNOVER_FILE_IMPORT (ID, OWNER_ID);
-- row belongs to category of the same owner
ALTER TABLE TURNOVER_ROW
    ADD CONSTRAINT FK_TURNOVER_ROW_CATEGORY_FKEY
        FOREIGN KEY (CATEGORY_ID, OWNER_ID)
            REFERENCES CATEGORY (ID, OWNER_ID);
