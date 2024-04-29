CREATE TABLE Account
(
    id         VARCHAR(255),
    userId     VARCHAR(255),
    balance    VARCHAR(255),
    createdTime VARCHAR(255),
    updatedTime VARCHAR(255),
    currency VARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY (userId) REFERENCES User (id)
);