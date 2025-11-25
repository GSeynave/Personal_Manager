import axios from 'axios';
import TransactionSummary from '@/model/TransactionSummary';
import type AccountingSummary from '@/model/AccountingSummary';
import type TransactionCsvRow from '@/model/TransactionCsvRow';
import type Transaction from '@/model/Transaction';

const API_URL = `/api/transactions`;

export default class AccountingService {

  async getAccountingSummary(params: URLSearchParams): Promise<AccountingSummary> {
    const response = await axios.get(API_URL + "/summary", { params });
    return response.data;
  }

  async getTransactionsSummary(params: URLSearchParams): Promise<TransactionSummary> {
    const response = await axios.get(API_URL, { params });
    return response.data;
  }

  async importTransactionsFromCsv(csvLines: TransactionCsvRow[]): Promise<void> {
    await axios.post(`${API_URL}/csv`, csvLines);
  }

  async getTransactionsToCategorize(): Promise<Transaction[]> {
    console.log("Fetching transactions to categorize...");
    const response = await axios.get(`${API_URL}/to-categorize`);
    return response.data;
  }

  async updateTransaction(transactions: Transaction[]): Promise<void> {
    await axios.put(`${API_URL}/categorize`, transactions);
  }

}
