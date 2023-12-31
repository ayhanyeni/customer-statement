create table CUSTOMER_STATEMENT_EVALUATION (
     ID bigserial NOT NULL CHECK ( ID > 0 )
         CONSTRAINT PK_CUSTOMER_STATEMENT_EVALUATION PRIMARY KEY,
     UUID uuid not null,
     RESULT varchar(10000000),
     STATUS varchar(20) NOT NULL,
     CREATED_AT timestamp without time zone NOT NULL
);

create index UX_CUSTOMER_STATEMENT_PROCESS_RESULT__UUID ON CUSTOMER_STATEMENT_EVALUATION (UUID);
