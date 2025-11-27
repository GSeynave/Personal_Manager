import axios from 'axios';
import Todo from '@/model/Todo';

const API_URL = `/api/todos`;

export default class TodoService {

  async getTodos(): Promise<Todo[]> {
    console.log(`Fetching todos from ${API_URL}`);
    const response = await axios.get(API_URL);
    return response.data;
  }

  async addTodo(todo: Todo) {
    // Prepare the payload for backend
    const payload = {
      title: todo.title,
      enhancedTitle: todo.enhancedTitle || null,
      due_date: todo.due_date,
      completed: todo.completed,
      assigned_to: todo.assigned_to
    };
    // Don't send id for creation
    console.log('Adding todo:', JSON.stringify(payload, null, 2));
    const response = await axios.post(API_URL, payload);
    return response.data;
  }

  async updateTodo(todo: Todo) {
    if (!todo.id) {
      throw new Error('Todo ID is required for update');
    }
    const payload = {
      id: todo.id,
      title: todo.title,
      enhancedTitle: todo.enhancedTitle || null,
      due_date: todo.due_date,
      completed: todo.completed,
      assigned_to: todo.assigned_to
    };
    const response = await axios.put(`${API_URL}/${todo.id}`, payload);
    return response.data;
  }

  async deleteTodo(id: string) {
    const response = await axios.delete(`${API_URL}/${id}`);
    return response.data;
  }
}
