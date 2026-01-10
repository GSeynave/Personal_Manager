# Vars

# Transactions
POST http://localhost:8080/v1/transactions
Accept: application/json
Authorization: Bearer dev-user1

{
  "filter": {
    "toCategorize": true,
    "minDate": "2025-01-01",
    "maxDate": "2026-12-31",
    "walletId": 1
  },
  "pagination": {
    "page": 0,
    "size": 50
  }
}

# Wallets
GET http://localhost:8080/v1/wallets/1
Accept: application/json
Authorization: Bearer dev-user1
