export default class Transaction {

  id: number;
  description: string;
  amount: number;
  date: Date;
  category: TransactionCategory; //create enum Category { Income, Expense, Transfer }
  subCategory: TransactionSubCategory;
  type: TransactionType;
  customCategory: string;

  constructor(id: number, description: string, amount: number, date: Date, category: TransactionCategory = TransactionCategory.NONE,
    subCategory: TransactionSubCategory = TransactionSubCategory.NONE, type: TransactionType = TransactionType.DEBIT,
    customCategory: string = "") {
    this.id = id;
    this.description = description;
    this.amount = amount;
    this.date = date;
    this.category = category;
    this.subCategory = subCategory;
    this.type = type;
    this.customCategory = customCategory;
  }

}

export enum TransactionCategory {
  NONE,
  BILL,
  DEBT,
  SALARY,
  INSURANCE,
  SUBSCRIPTIONS,
  HEALTH,
  CAR,
  GROSSERY,
  SAVING,
  HELP,
  REFUND,
  PET,
  CHILDREN,
  TRANSFER,
  MISC
}

export enum TransactionSubCategory {
  NONE,
  HOUSE,
  SCHOOL,
  DAD
}

export enum TransactionType {
  CREDIT = 'CREDIT',
  DEBIT = 'DEBIT'
}
