import axios from 'axios';
import Todo from '@/model/todo/Todo';
import TodoGroup from '@/model/todo/TodoGroup';
import TodosView from '@/model/todo/TodosView';

const API_URL = `/api/todos`;
const GROUP_API_URL = `/api/todo-groups`;

export default class TodoService {

  async getTodos(): Promise<TodosView> {
    console.log(`Fetching todos from ${API_URL}`);
    const response = await axios.get(API_URL);
    return response.data;
  }

  async addTodo(todo: Todo, groupId?: number) {
    // Prepare the payload for backend
    const payload = {
      title: todo.title,
      enhancedTitle: todo.enhancedTitle || null,
      due_date: todo.due_date,
      completed: todo.completed,
      assigned_to: todo.assigned_to,
      todoGroupId: groupId || null
    };
    console.log('Adding todo:', JSON.stringify(payload, null, 2));
    const response = await axios.post(API_URL, payload);
    return response.data;
  }

  async updateTodo(todo: Todo, groupId?: number) {
    if (!todo.id) {
      throw new Error('Todo ID is required for update');
    }
    const payload = {
      id: todo.id,
      title: todo.title,
      enhancedTitle: todo.enhancedTitle || null,
      due_date: todo.due_date,
      completed: todo.completed,
      assigned_to: todo.assigned_to,
      todoGroupId: groupId !== undefined ? groupId : null
    };
    const response = await axios.put(`${API_URL}/${todo.id}`, payload);
    return response.data;
  }

  async deleteTodo(id: string) {
    const response = await axios.delete(`${API_URL}/${id}`);
    return response.data;
  }

  // Group management
  async createGroup(title: string, description: string): Promise<TodoGroup> {
    const response = await axios.post(GROUP_API_URL, { title, description });
    return response.data;
  }

  async deleteGroup(groupId: number): Promise<void> {
    const response = await axios.delete(`${GROUP_API_URL}/${groupId}`);
    return response.data;
  }

  async moveTodoToGroup(todo: Todo, groupId: number | null) {
    const payload = {
      id: todo.id,
      title: todo.title,
      enhancedTitle: todo.enhancedTitle || null,
      due_date: todo.due_date,
      completed: todo.completed,
      assigned_to: todo.assigned_to,
      todoGroupId: groupId
    };
    
    const response = await axios.put(`${API_URL}/${todo.id}`, payload);
    return response.data;
  }
}
