TRUNCATE TABLE CUSTOMER_STATEMENT_EVALUATION RESTART IDENTITY;

INSERT INTO CUSTOMER_STATEMENT_EVALUATION (UUID, RESULT, STATUS, CREATED_AT)
VALUES ('46badd6f-647d-4c0d-9fdc-e9acdb2b0303', NULL, 'INITIALIZED', '2030-04-01 11:11:11'),
       ('46badd6f-647d-4c0d-9fdc-e9acdb2b0304',
        '[{"transactionReference": 1000, "description": "Test transaction description."}, {"transactionReference": 2000, "description": "2. Test transaction description."}]',
        'COMPLETED', '2030-04-01 11:11:11');
