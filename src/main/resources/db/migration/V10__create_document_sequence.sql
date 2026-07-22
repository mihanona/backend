CREATE TABLE document_sequence (
                                   id             UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                                   tenant_id      UUID        NOT NULL REFERENCES tenant(id),
                                   document_type  VARCHAR(30) NOT NULL, -- QUOTE | INVOICE | (future: CREDIT_NOTE, RECEIPT...)
                                   year           INT         NOT NULL,
                                   prefix         VARCHAR(20) NOT NULL,
                                   next_number    INT         NOT NULL DEFAULT 1
);

CREATE UNIQUE INDEX uq_document_sequence ON document_sequence(tenant_id, document_type, year);