export default class HabitLog {
    id?: number;  // Optional for creation, required for updates
    habitId: number;
    userId?: string;
    date?: string; // ISO-8601 date format (YYYY-MM-DD) - for requests
    completed?: boolean; // For YES_NO habits
    numberOfTimes?: number; // For NUMERIC habits
    duration?: number; // For DURATION habits (in minutes)
    createdAt?: string; // Backend returns this as the date
    updatedAt?: string;

    constructor(
        habitId: number,
        userId?: string,
        date?: string,
        id?: number,
        completed?: boolean,
        numberOfTimes?: number,
        duration?: number,
        createdAt?: string,
        updatedAt?: string
    ) {
        this.id = id;
        this.habitId = habitId;
        this.userId = userId;
        this.date = date;
        this.completed = completed;
        this.numberOfTimes = numberOfTimes;
        this.duration = duration;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}