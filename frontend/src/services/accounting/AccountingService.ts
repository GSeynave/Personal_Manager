import axios from 'axios';
import TransactionSummary from '@/model/accounting/TransactionSummary';
import type AccountingSummary from '@/model/accounting/AccountingSummary';
import type TransactionCsvRow from '@/model/accounting/TransactionCsvRow';
import type Transaction from '@/model/accounting/Transaction';
import type UncategorizedTransactions from '@/model/accounting/UncategorizedTransactions';

const API_URL = `/api/transactions`;

export default class AccountingService {

  async getAccountingSummary(params: URLSearchParams, walletId?: number): Promise<AccountingSummary> {
    if (walletId) {
      params.append('walletId', walletId.toString());
    }
    const response = await axios.get(API_URL + "/summary", { params });
    return response.data;
  }

  async getTransactionsSummary(params: URLSearchParams, walletId?: number): Promise<TransactionSummary> {
    if (walletId) {
      params.append('walletId', walletId.toString());
    }
    const response = await axios.get(API_URL, { params });
    return response.data;
  }

  async importTransactionsFromCsv(csvLines: TransactionCsvRow[], walletId?: number): Promise<void> {
    const params = new URLSearchParams();
    if (walletId) {
      params.append('walletId', walletId.toString());
    }
    await axios.post(`${API_URL}/csv`, csvLines, { params });
  }

  async getTransactionsToCategorize(walletId?: number): Promise<UncategorizedTransactions> {
    console.log("Fetching transactions to categorize...");

    const page = 0;
    const size = 100; // You can adjust the size as needed

    const params = new URLSearchParams({ page: page.toString(), size: size.toString() });
    if (walletId) {
      params.append('walletId', walletId.toString());
    }
    const response = await axios.get(`${API_URL}/to-categorize`, { params });
    console.log("Transactions to categorize retrieved:", response.data);
    return response.data;
  }

  async updateTransaction(transactions: Transaction[]): Promise<void> {
    await axios.put(`${API_URL}/categorize`, transactions);
  }

}
