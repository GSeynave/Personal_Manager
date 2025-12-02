import axios from 'axios';
import Todo from '@/model/Todo';
import TodoGroup from '@/model/TodoGroup';
import TodosView from '@/model/TodosView';
import type HabitsData from '@/model/HabitData';
import type HabitLog from '@/model/HabitLog';

const API_URL = `/api/habits`;
const GROUP_API_URL = `/api/habit-logs`;

export default class HabitsService {

  async getHabits(): Promise<HabitsData[]> {
    console.log(`Fetching habits from ${API_URL}`);
    const response = await axios.get(API_URL);
    return response.data;
  }

  async createHabit(title: string, description: string, category: string, type: string, unit?: string): Promise<HabitsData> {
    // Prepare the payload for backend
    const payload = {
      title: title,
      description: description || null,
      type: type || null,
      category: category || null,
      unit: unit || null,
    };
    console.log('Creating Habit:', JSON.stringify(payload, null, 2));
    const response = await axios.post(API_URL, payload);
    return response.data;
  }

  async updateHabit(habit: HabitsData) {
    if (!habit.id) {
      throw new Error('Habit ID is required for update');
    }
    const payload = {
      id: habit.id,
      title: habit.title,
      description: habit.description || null,
      type: habit.type || null,
    };
    const response = await axios.put(`${API_URL}/${habit.id}`, payload);
    return response.data;
  }

  async deleteHabit(id: number): Promise<void> {
    const response = await axios.delete(`${API_URL}/${id}`);
    return response.data;
  }

  // Habit Logs management
  async addHabitLog(habitId: number, completion_date: Date): Promise<HabitLog> {
    const response = await axios.post(`${GROUP_API_URL}/${habitId}`, { completion_date: completion_date });
    return response.data;
  }

}
