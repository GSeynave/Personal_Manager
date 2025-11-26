export default class AccountingSummary {
  income: number;
  expense: number;
  saving: number;

  constructor(income: number, expense: number, saving: number) {
    this.income = income;
    this.expense = expense;
    this.saving = saving;
  }
}
