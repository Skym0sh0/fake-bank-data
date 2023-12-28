ALTER TABLE TURNOVER_ROW
    ADD COLUMN LAST_UPDATED_AT timestamp with time zone;

update turnover_row
set LAST_UPDATED_AT = now();

ALTER TABLE turnover_row
    ALTER LAST_UPDATED_AT SET NOT NULL;
