ALTER TABLE TURNOVER_FILE_IMPORT
    DROP CONSTRAINT TURNOVER_FILE_IMPORT_TURNOVER_FILE_FORMAT_CHECK;

ALTER TABLE TURNOVER_FILE_IMPORT
    ADD CONSTRAINT TURNOVER_FILE_IMPORT_TURNOVER_FILE_FORMAT_CHECK
        CHECK ( TURNOVER_FILE_FORMAT IN (
                                         'VR_BANK_CSV'::TEXT,
                                         'DKB'::TEXT,
                                         'NEW_DKB'::TEXT,
                                         'PAYPAL'::TEXT)
            );
