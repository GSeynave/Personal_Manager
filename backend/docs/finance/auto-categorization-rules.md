# Categorizing Transactions

## Rules

A user can define rules, that will help fasten the categorization and automate it.
Based on [[Wallet#Category|User's categories]]


### Basic rule

```
```text
IF [field] [comparator] [value]
THEN [action]
```

### Complex rule

```text
IF [field] [comparator] [value]
AND/OR [field] [comparator] [value]
AND/OR [field] [comparator] [value]
...
THEN [action]
```


**Example**

```text
IF importLabel CONTAINS "AMAZON"
THEN category = "Shopping"

IF importLabel CONTAINS "SALARY"
AND amount > 1000
THEN category = "Income" AND customLabel = "Monthly Salary"

IF importLabel STARTS_WITH "SHELL"
OR importLabel STARTS_WITH "BP"
OR importLabel STARTS_WITH "ESSO"
THEN category = "Transport" 
AND subCategory = "Car"
AND customLabel = "Gas Station"
```
