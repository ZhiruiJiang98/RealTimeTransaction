CREATE TIME Transaction{
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    accountId VARCHAR(255),
    transactionType VARCHAR(255),
    amount VARCHAR(255),
    currency VARCHAR(255),
    updateTime VARCHAR(255),
    debitOrCredit VARCHAR(255),
    transactionStatus VARCHAR(255),
    createTime VARCHAR(255)
    FOREIGN KEY (accountId) REFERENCES Account(id)
};