export default class Transaction {

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
