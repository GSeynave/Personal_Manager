import type Transaction from "./Transaction";

export default class UncategorizedTransactions {

  transactions: Transaction[];
  page: number;
  totalPage: number;
  totalElements: number;

  constructor(transactions: Transaction[], page: number, totalPage: number, totalElements: number) {
    this.transactions = transactions;
    this.page = page;
    this.totalPage = totalPage;
    this.totalElements = totalElements;
  }

}
