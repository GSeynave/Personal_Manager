export default class AccountingSummary {
  income: number;
  expense: number;
  saving: number;
  balance?: number;

  constructor(income: number, expense: number, saving: number, balance?: number) {
    this.income = income;
    this.expense = expense;
    this.saving = saving;
    this.balance = balance;
  }
}
