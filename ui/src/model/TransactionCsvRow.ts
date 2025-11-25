export default class TransactionCsvRow {
  date: Date;
  amount: number;
  description: string;

  constructor(description: string, amount: number, date: Date) {
    this.description = description;
    this.amount = amount;
    this.date = date;
  }
}
