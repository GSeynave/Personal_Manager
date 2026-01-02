# Gamification of wallet.

## Overview

Like all the feature of the app, the wallet will also grant some essence / rewards.
The user need to feels rewarded the more he uses the wallet system.

## Events 

They're all async, and handle by spring events.

- WalletHasBeenCreated
- TransactionHasBeenImported (1 - 50 - 100 - 500 - 1000..)
- WalletHasBeenShared (once the tenant / social will be implemented)

## WalletHasBeenCreated

Once a wallet is create, the user will be granted some essence and a title.

## TransactionHasBeenImported

Once transactions are imported, the user will be granted some essence.
Here anti-cheat is important, the user could trick the system by importing one by one, or duplicated row.
So we need a reward per batch and per transaction that is limited and decreasing.

## WalletHasBeenShared

Once a user share a wallet with an other user, it will be granted some essence and a title.
This will be implemented when all the tenant / social system is ready.
