INSERT INTO User (id, userName, email, password, createdTime, updatedTime)
VALUES ('4eb3b1d8-a369-4079-9ec3-c3b8a9fa02ed', 'john_doe', 'john_doe@example.com', 'encrypted_password', NOW(), NOW());

INSERT INTO Account (id, userId, balance, createdTime, updatedTime, currency)
VALUES ('0c91c7b6-0a54-4dc5-b9b0-d37e0ae81cf7', '4eb3b1d8-a369-4079-9ec3-c3b8a9fa02ed', '1000', NOW(), NOW(), 'USD');
