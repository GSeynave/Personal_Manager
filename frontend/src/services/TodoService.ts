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
    const response = await axios.post(API_URL, todo);
    return response.data;
  }

  async updateTodo(todo: Todo) {
    const response = await axios.put(`${API_URL}/${todo.id}`, todo);
    return response.data;
  }

  async deleteTodo(id: string) {
    const response = await axios.delete(`${API_URL}/${id}`);
    return response.data;
  }
}
