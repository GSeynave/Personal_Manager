import type TransactionCategory from "./TransactionCategory";

export default class Transaction {

  id: number;
  importLabel: string;
  customLabel: string;
  amount: number;
  date: Date;
  category: TransactionCategory; //create enum Category { Income, Expense, Transfer }
  type: TransactionType;
  relatedTransactionId: number;
  accountId: number;

  constructor(id: number, importLabel: string, customLabel: string, amount: number,
    date: Date, category: TransactionCategory, type: TransactionType, relatedTransactionId: number,
    accountId: number) {
    this.id = id;
    this.importLabel = importLabel;
    this.customLabel = customLabel;
    this.amount = amount;
    this.date = date;
    this.category = category;
    this.type = type;
    this.relatedTransactionId = relatedTransactionId;
    this.accountId = accountId;
  }
}

export enum TransactionType {
  CREDIT = 'CREDIT',
  DEBIT = 'DEBIT'
}
