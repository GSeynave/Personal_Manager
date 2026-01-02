# Wallet feature


## Overview
Allow the user to create wallet to his financial module.
A wallet is needed to begin importing transactions.

## Key Features
- **Wallet Creation** - The user can create 1 to n wallets to easily track and separate its transactions
- **Wallet tenant sharing** - A user can share a wallet to a tenant (family group)
- **Wallet role** - A user can have access to a wallet as read / write, this has to be define by the owner of the wallet.

## Architecture Decisions

### Rule Storage Format
**Choice:** Wallets are store in a PostgreSQL DB.
**Alternatives Considered:** 
- none.
**Reasoning:** As it is strictly relational, SQL DB make most sens here.
**Tradeoffs Accepted:** Will need to implement a HashRing when user base scale.

### Permission management
**Choice:** Owner / creator of the wallet has full right of adding / changing permission of a user.
**Alternatives Considered:**
- No role (Can lead to a mess)
**Reasoning:** One owner, lead them all.


## Technical Approach
- REST API for CRUD operations on wallet / permissions
- Tenant isolation via tenant_id foreign key on wallet table
- PostgreSQL Table with tenant indexation
- Role manage by a permission table link to each user

## Business Context
As we often have more than one bank wallet, it is important to introduce this feature.
The user will be able to create 1 to n wallet to manage its transactions easily.
As a wallet is sometimes shared between users, an option to share it to a tenant_id will then allow user to all access it.

## Future Considerations
- [ ] 

## Resources


