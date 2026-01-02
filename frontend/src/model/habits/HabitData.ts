export type HabitType = 'YES_NO' | 'NUMERIC' | 'DURATION'
export type HabitFrequency = 'DAILY' | 'WEEKLY' | 'CUSTOM'
export type DayOfWeek = 'MONDAY' | 'TUESDAY' | 'WEDNESDAY' | 'THURSDAY' | 'FRIDAY' | 'SATURDAY' | 'SUNDAY'

export default class HabitData {
    id?: number;  // Optional for creation, required for updates
    title: string;
    description?: string;
    category?: string; // Qualitative type like "Health", "Productivity", etc.
    habitType: HabitType; // Backend uses habitType: YES_NO, NUMERIC, or DURATION
    frequency: HabitFrequency; // DAILY or WEEKLY
    scheduledDays: DayOfWeek[]; // Days of the week when the habit can be logged
    numberOfTimes?: number; // Target count for NUMERIC habits
    duration?: number; // Target minutes for DURATION habits
    userId?: string;
    createdAt?: string;
    updatedAt?: string;

    constructor(
        title: string,
        habitType: HabitType,
        frequency: HabitFrequency = 'DAILY',
        scheduledDays: DayOfWeek[] = [],
        id?: number,
        description?: string,
        category?: string,
        numberOfTimes?: number,
        duration?: number,
        userId?: string,
        createdAt?: string,
        updatedAt?: string
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.habitType = habitType;
        this.frequency = frequency;
        this.scheduledDays = scheduledDays;
        this.numberOfTimes = numberOfTimes;
        this.duration = duration;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}