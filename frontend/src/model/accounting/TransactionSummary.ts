import type Transaction from "./Transaction";

export default class TransactionSummary {

  category: string; // category of the transaction
  expense: number; // amount spent in this category
  percent: number; // percentage of total spend in this category
  maxExpected: number; // maximum expected to be spent for this category
  transactions: Transaction[]; // list of transactions in this category

  constructor(category: string, expense: number, percent: number, maxExpected: number, transactions: Transaction[]) {
    this.category = category;
    this.expense = expense;
    this.percent = percent;
    this.maxExpected = maxExpected;
    this.transactions = transactions;
  }
}
