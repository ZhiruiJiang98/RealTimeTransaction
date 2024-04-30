## Feature Statement:
Develop a robust and scalable real-time transaction service 
that enable users to securely load funds to their account and authorize 
transactions based on their current balances. The system must support real-time balance updates with currency exchange
and maintain a high level of availability and reliability. The system will build base on **AWS SAM** by using AWS Lambda,
CDK, APIGateWay, CloudWatch, CLI, IAM, and RDS(MySQL).

## Problem Statement:
Current's core banking engine requires a real-time transaction service that can handle
two types of transactions:
1) Load: Add money to a user with different currency (credit)
2) Authorization: Conditionally remove money from a user with different currency (debit)

## Detail Design:
### Database Design:

![img.png](Images/dbdesign.png)

#### User
* id: (PK, VARCHAR): Uniquely identifies a user
* username: (VARCHAR): User's username
* email: (VARCHAR): User's email
* createTime: (VARCHAR): Unix Timestamp of the user was created
* updatedTime: (VARCHAR): Unix Timestamp of update with the user profile
* PASSWORD: (VARCHAR): User's password

#### Account
* id (PK, VARCHAR): Uniquely identifies an account
* userId (FK, VARCHAR): User's id
* balance (VARCHAR): Account balance
* createTime (VARCHAR): Unix Timestamp of the account was created
* updateTime (VARCHAR): Unix Timestamp of the account was last updated
* currency (VARCHAR): The currency of the account balances.

#### Transaction
* id: (PK, VARCHAR): Uniquely identifies a transaction
* accountId (FK, VARCHAR): Account's id
* messageId (VARCHAR): UUID of message Id
* amount (VARCHAR): The amount of the transaction
* currency (VARCHAR): The currency of the transaction
* entryStatus (VARCHAR) : Debit or Credit
* createTime (VARCHAR): Unix Timestamp of the transaction was created
* updateTime (VARCHAR): Unix Timestamp of the transaction was last updated
* status (VARCHAR): The status of the transaction (PENDING, APPROVED, DECLINED)

### API Design:

#### Currency Exchange
The transaction service supports currency exchange for transactions involving different currencies. The currency exchange functionality is integrated with the `/load/{messageId}` and `/authorization/{messageId}` endpoints.

1. Exchange Rates:
  - The system integrates with an external currency exchange rate provider to fetch real-time exchange rates.
  - Exchange rates are periodically updated to reflect the current market rates.
  - The fetched exchange rates are cached in the system to minimize the need for frequent external API calls.

2. Currency Conversion:
  - When a transaction is initiated with a currency different from the user's account currency, the system performs currency conversion.
  - The conversion is based on the real-time exchange rate fetched from the external provider.
  - The converted amount is used for updating the account balance.

3. API Integration:
  - The `/load/{messageId}` and `/authorization/{messageId}` endpoints accept transactions in different currencies.
  - The request payload includes the transaction currency and amount.
  - The response payload includes the updated account balance in the account currency.


#### GET /ping
* Response Body:
```json
{
  "serverTime": "string"
}
```

example:
```json
{
  "serverTime": "2021-10-10T10:00:00Z"
}
```

#### PUT /load/{messageId}
* Parameter: `messageId: String`
* Request Body:
```json
{
  "messageId": "string",
  "userId": "string",
  "transactionAmount": {
    "amount": "string",
    "currency": "string",
    "debitOrCredit": "string"
  }
}
```
example:
<br>`messageId: 50e70c62-e480-49fc-bc1b-e991ac672173`
```json 

{
  "messageId": "50e70c62-e480-49fc-bc1b-e991ac672173",
  "userId": "8786e2f9-d472-46a8-958f-d659880e723d",
  "transactionAmount": {
      "amount": "100.23",
      "currency": "USD",
      "debitOrCredit": "CREDIT"
  }
}
```

* Response Body:

```json
 
{
  "messageId": "string",
  "userId": "string",
  "responseCode": "string",
  "balance": {
    "amount": "string",
    "currency": "string",
    "debitOrCredit": "string"
  }
}
```
example: 
```json
{
  "messageId": "55210c62-e480-asdf-bc1b-e991ac67FSAC",
  "userId": "2226e2f9-ih09-46a8-958f-d659880asdfD",
  "responseCode": "APPROVED",
  "balance": {
    "amount": "100.23",
    "currency": "USD",
    "debitOrCredit": "CREDIT"
  }
}
```

#### PUT /authorize/{messageId}

* Parameters: 'messageId'
* Request Body:
    
    ```json
    {
    "messageId": "string",
    "userId": "string",
    "balance": {
        "amount": "string",
        "currency": "string",
        "debitOrCredit": "string"
    }
    }
    ```
  example: 
<br>`messageId: 50e70c62-e480-49fc-bc1b-e991ac672173`
  ```json
    {
    "messageId": "50e70c62-e480-49fc-bc1b-e991ac672173",
    "userId": "8786e2f9-d472-46a8-958f-d659880e723d",
    "balance": {
        "amount": "0",
        "currency": "USD",
        "debitOrCredit": "CREDIT"
    }
    }
    ```

* Response Body:
    ```json
  { 
  "messageId": "string",
    "userId": "string",
    "responseCode": "string",
    "balance": {
        "amount": "string",
        "currency": "string",
        "debitOrCredit": "string"
      }
  }
    ```
  
  example:
  ```json
  { "messageId": "50e70c62-e480-49fc-bc1b-e991ac672173",
    "userId": "8786e2f9-d472-46a8-958f-d659880e723d",
      "responseCode": "APPROVED",
      "balance": {
        "amount": "0",
        "currency": "USD",
        "debitOrCredit": "DEBIT"
    }
  }
  ```
#### Server Error
```json
{
  "message": "string",
  "error": "string"
}
```
example:
```json
{
  "message": "Internal Server Error",
  "error": "500"
}
```

### Open Question

