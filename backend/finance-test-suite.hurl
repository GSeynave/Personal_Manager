# Test-Suite for Finance Application APIs
#
# User and Tenant are assumed to be pre-created in the system.
# User: dev-user1
# Tenant ID: 1
# ==========================================
# SETUP - Verify seed data exists
# ==========================================

### Verify user1 exists
GET http://localhost:8080/v1/wallets
Authorization: Bearer dev-user1
HTTP 200

# 1 - Create a new wallet
#
POST http://localhost:8080/v1/wallets
Accept: application/json
Authorization: Bearer dev-user1
{
    "name": "My Wallet",
    "description": "Personal wallet",
    "tenantId": 1
}
# Expected Response status:
HTTP 201
[Asserts]
jsonpath "$.id" > 0
jsonpath "$.name" == "My Wallet"
jsonpath "$.description" == "Personal wallet"
jsonpath "$.ownerId" == 1
jsonpath "$.tenantId" == 1
jsonpath "$.balance" == null
jsonpath "$.ownerEmail" exists
jsonpath "$.createdAt" exists
jsonpath "$.updatedAt" exists
[Captures]
walletId: jsonpath "$.id"


# 2 - Get the newly created wallet
#
GET http://localhost:8080/v1/wallets/{{walletId}}
Accept: application/json
Authorization: Bearer dev-user1
# Expected Response status:
HTTP 200
[Asserts]
jsonpath "$.id" == {{walletId}}
jsonpath "$.name" == "My Wallet"
jsonpath "$.description" == "Personal wallet"
jsonpath "$.ownerId" == 1
jsonpath "$.tenantId" == 1
jsonpath "$.balance" == null
jsonpath "$.ownerEmail" exists
jsonpath "$.createdAt" exists
jsonpath "$.updatedAt" exists

# 3 - Create a new transaction in the wallet

POST http://localhost:8080/v1/transactions/csv?walletId={{walletId}}
Accept: application/json
Authorization: Bearer dev-user1
[
  {
    "amount": 10.00,
    "date": "15/06/2025",
    "description": "Grocery Shopping",
    "currentBalance": 100.50
  },
  {
    "amount": 52.10,
    "date": "15/06/2025",
    "description": "Grocery Shopping",
    "currentBalance": 100.50
  },
  {
    "amount": 60.00,
    "date": "15/06/2025",
    "description": "Grocery Shopping",
    "currentBalance": 40.50
  }
]
# Expected Response status:
HTTP 200
[Asserts]
body == "3"

# 4.1 - Create parent categories
POST http://localhost:8080/v1/transaction-categories
Accept: application/json
Authorization: Bearer dev-user1
{
    "title": "Groceries",
    "description": "Expenses for groceries",
    "icon": "shopping-cart",
    "expectedAmount": 400.00,
    "parentCategoryId": null
}
# Expected Response status:
HTTP 201
[Asserts]
jsonpath "$.id" > 0
jsonpath "$.title" == "Groceries"
jsonpath "$.description" == "Expenses for groceries"
jsonpath "$.icon" == "shopping-cart"
jsonpath "$.expectedAmount" == 400.00
jsonpath "$.parentCategoryId" == null
[Captures]
categoryId: jsonpath "$.id"

# 4.2 - Create sub categories
POST http://localhost:8080/v1/transaction-categories
Accept: application/json
Authorization: Bearer dev-user1
{
    "title": "Food",
    "description": "Expenses for food",
    "icon": "utensils",
    "expectedAmount": 0.00,
    "parentCategoryId": {{categoryId}}
}
# Expected Response status:
HTTP 201
[Asserts]
jsonpath "$.id" > 0
jsonpath "$.title" == "Food"
jsonpath "$.description" == "Expenses for food"
jsonpath "$.icon" == "utensils"
jsonpath "$.expectedAmount" == 0.00
jsonpath "$.parentCategoryId" == {{categoryId}}
[Captures]
subCategory1Id: jsonpath "$.id"

POST http://localhost:8080/v1/transaction-categories
Accept: application/json
Authorization: Bearer dev-user1
{
    "title": "Baby Products",
    "description": "Expenses for baby products",
    "icon": "baby-carriage",
    "expectedAmount": 0.00,
    "parentCategoryId": {{categoryId}}
}
# Expected Response status:
HTTP 201
[Asserts]
jsonpath "$.id" > 0
jsonpath "$.title" == "Baby Products"
jsonpath "$.description" == "Expenses for baby products"
jsonpath "$.icon" == "baby-carriage"
jsonpath "$.expectedAmount" == 0.00
jsonpath "$.parentCategoryId" == {{categoryId}}
[Captures]
subCategory2Id: jsonpath "$.id"

# 5 - Get uncategorized transactions
# 6 - Categorize transactions
# 7 - Get categorized transactions (overview)
# 8 - Get transaction summary
# 9 - Clean all created data


# ==========================================
# CLEANUP - Delete test data
# ==========================================

### Delete wallet - Cascade delete transactions when deleting wallet
DELETE http://localhost:8080/v1/wallets/{{walletId}}
Authorization: Bearer dev-user1
HTTP 204

### Verify wallet deletion
GET http://localhost:8080/v1/wallets/{{walletId}}
Authorization: Bearer dev-user1
HTTP 404

### Delete categories (if you have delete endpoint) - cascade delete sub-categories when deleting parent category
DELETE http://localhost:8080/v1/transaction-categories/{{subCategory1Id}}
Authorization: Bearer dev-user1
HTTP 204
DELETE http://localhost:8080/v1/transaction-categories/{{subCategory2Id}}
Authorization: Bearer dev-user1
HTTP 204
DELETE http://localhost:8080/v1/transaction-categories/{{categoryId}}
Authorization: Bearer dev-user1
HTTP 204
