CREATE TABLE Account{
    id VARCHAR(255) PRIMARY KEY,
    userId VARCHAR(255),
    balance VARCHAR(255),
    createTime VARCHAR(255),
    updateTime VARCHAR(255),
    FOREIGN KEY (userId) REFERENCES User(id)
}