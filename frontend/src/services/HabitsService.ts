import axios from 'axios';
import HabitData, { type HabitType, type HabitFrequency, type DayOfWeek } from '@/model/HabitData';
import type HabitLog from '@/model/HabitLog';

const API_URL = `/api/habits`;

export default class HabitsService {

  async getHabits(): Promise<HabitData[]> {
    console.log(`Fetching habits from ${API_URL}`);
    const response = await axios.get(API_URL);
    return response.data;
  }

  async getHabitById(id: number): Promise<HabitData> {
    console.log(`Fetching habit ${id} from ${API_URL}/${id}`);
    const response = await axios.get(`${API_URL}/${id}`);
    return response.data;
  }

  async createHabit(
    title: string, 
    description: string, 
    category: string, 
    habitType: HabitType,
    frequency: HabitFrequency,
    scheduledDays: DayOfWeek[],
    numberOfTimes?: number,
    duration?: number
  ): Promise<HabitData> {
    // Prepare the payload for backend
    const payload: any = {
      title: title,
      description: description || undefined,
      category: category || undefined,
      habitType: habitType,
      frequency: frequency,
      scheduledDays: scheduledDays,
    };

    // Add type-specific fields
    if (habitType === 'NUMERIC' && numberOfTimes !== undefined) {
      payload.numberOfTimes = numberOfTimes;
    }
    if (habitType === 'DURATION' && duration !== undefined) {
      payload.duration = duration;
    }

    console.log('Creating Habit:', JSON.stringify(payload, null, 2));
    const response = await axios.post(API_URL, payload);
    return response.data;
  }

  async updateHabit(
    id: number,
    title: string,
    description?: string,
    category?: string,
    habitType?: HabitType,
    frequency?: HabitFrequency,
    scheduledDays?: DayOfWeek[],
    numberOfTimes?: number,
    duration?: number
  ): Promise<HabitData> {
    const payload: any = {
      title: title,
      description: description || undefined,
      category: category || undefined,
      habitType: habitType || undefined,
      frequency: frequency || undefined,
      scheduledDays: scheduledDays || undefined,
    };

    // Add type-specific fields based on habitType
    if (habitType === 'NUMERIC' && numberOfTimes !== undefined) {
      payload.numberOfTimes = numberOfTimes;
    }
    if (habitType === 'DURATION' && duration !== undefined) {
      payload.duration = duration;
    }

    console.log('Updating Habit:', JSON.stringify(payload, null, 2));
    const response = await axios.put(`${API_URL}/${id}`, payload);
    return response.data;
  }

  async deleteHabit(id: number): Promise<void> {
    console.log(`Deleting habit ${id}`);
    await axios.delete(`${API_URL}/${id}`);
  }

  // Habit Logs management
  async getHabitLogs(habitId: number): Promise<HabitLog[]> {
    console.log(`Fetching logs for habit ${habitId}`);
    const response = await axios.get(`${API_URL}/${habitId}/logs`);
    return response.data;
  }

  async createHabitLog(
    habitId: number,
    date: string, // ISO-8601 format (YYYY-MM-DD)
    completed?: boolean,
    numberOfTimes?: number,
    duration?: number
  ): Promise<HabitLog> {
    const payload: any = {
      date: date,
    };

    // Add type-specific fields
    if (completed !== undefined) {
      payload.completed = completed;
    }
    if (numberOfTimes !== undefined) {
      payload.numberOfTimes = numberOfTimes;
    }
    if (duration !== undefined) {
      payload.duration = duration;
    }

    console.log('Creating Habit Log:', JSON.stringify(payload, null, 2));
    const response = await axios.post(`${API_URL}/${habitId}/logs`, payload);
    return response.data;
  }

  async updateHabitLog(
    habitId: number,
    logId: number,
    date: string,
    completed?: boolean,
    numberOfTimes?: number,
    duration?: number
  ): Promise<HabitLog> {
    const payload: any = {
      date: date,
    };

    if (completed !== undefined) {
      payload.completed = completed;
    }
    if (numberOfTimes !== undefined) {
      payload.numberOfTimes = numberOfTimes;
    }
    if (duration !== undefined) {
      payload.duration = duration;
    }

    console.log('Updating Habit Log:', JSON.stringify(payload, null, 2));
    const response = await axios.put(`${API_URL}/${habitId}/logs/${logId}`, payload);
    return response.data;
  }

  async deleteHabitLog(habitId: number, logId: number): Promise<void> {
    console.log(`Deleting log ${logId} for habit ${habitId}`);
    await axios.delete(`${API_URL}/${habitId}/logs/${logId}`);
  }
}

