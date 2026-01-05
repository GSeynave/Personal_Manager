import axios from 'axios';
import type {
  TransactionCategoryDTO,
  CreateTransactionCategoryRequest,
  UpdateTransactionCategoryRequest
} from '@/model/accounting/TransactionCategory';

const API_URL = '/api/transaction-categories';

export default class TransactionCategoryService {
  
  /**
   * Get all categories as a flat list
   * GET /v1/transaction-categories
   */
  async getAllCategories(): Promise<TransactionCategoryDTO[]> {
    const response = await axios.get<TransactionCategoryDTO[]>(API_URL);
    return response.data;
  }

  /**
   * Get root categories with their full hierarchy (subcategories included)
   * GET /v1/transaction-categories/root
   */
  async getRootCategories(): Promise<TransactionCategoryDTO[]> {
    const response = await axios.get<TransactionCategoryDTO[]>(`${API_URL}/root`);
    return response.data;
  }

  /**
   * Get a specific category by ID with its subcategories
   * GET /v1/transaction-categories/{id}
   * @param id - Category ID
   * @throws 400 Bad Request if category not found
   */
  async getCategoryById(id: number): Promise<TransactionCategoryDTO> {
    const response = await axios.get<TransactionCategoryDTO>(`${API_URL}/${id}`);
    return response.data;
  }

  /**
   * Create a new category
   * POST /v1/transaction-categories
   * @param request - Category creation data
   * @throws 400 Bad Request if validation fails or title already exists
   */
  async createCategory(request: CreateTransactionCategoryRequest): Promise<TransactionCategoryDTO> {
    const response = await axios.post<TransactionCategoryDTO>(API_URL, request);
    return response.data;
  }

  /**
   * Update an existing category
   * PUT /v1/transaction-categories/{id}
   * @param id - Category ID to update
   * @param request - Updated category data (all fields optional)
   * @throws 400 Bad Request if category not found, circular reference, or validation fails
   */
  async updateCategory(id: number, request: UpdateTransactionCategoryRequest): Promise<TransactionCategoryDTO> {
    const response = await axios.put<TransactionCategoryDTO>(`${API_URL}/${id}`, request);
    return response.data;
  }

  /**
   * Delete a category
   * DELETE /v1/transaction-categories/{id}
   * @param id - Category ID to delete
   * @throws 400 Bad Request if category not found or has subcategories
   */
  async deleteCategory(id: number): Promise<void> {
    await axios.delete(`${API_URL}/${id}`);
  }

  /**
   * Reorder categories
   * PUT /v1/transaction-categories/reorder
   * @param categoryIds - Array of category IDs in the desired order
   */
  async reorderCategories(categoryIds: number[]): Promise<void> {
    await axios.put(`${API_URL}/reorder`, { categoryIds });
  }
}
