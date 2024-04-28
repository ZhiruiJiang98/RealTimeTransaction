CREATE TABLE Transaction
(
    id                VARCHAR(255),
    accountId         VARCHAR(255),
    messageId         VARCHAR(255),
    amount            VARCHAR(255),
    currency          VARCHAR(255),
    updatedTime        VARCHAR(255),
    debitOrCredit     VARCHAR(255),
    transactionStatus VARCHAR(255),
    createdTime        VARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY (accountId) REFERENCES Account (id)
);