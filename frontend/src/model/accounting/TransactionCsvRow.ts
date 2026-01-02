export default class TransactionCsvRow {
  date: Date;
  amount: number;
  description: string;
  currentBalance: number;

  constructor(description: string, amount: number, date: Date, currentBalance: number) {
    this.description = description;
    this.amount = amount;
    this.date = date;
    this.currentBalance = currentBalance;
  }
}
