import type HabitLog from "./HabitLog";

export default class HabitData {
    id: number;  // Optional for creation, required for updates
    title: string;
    description: string;
    category: string; // Qualitative type like "Health", "Productivity", etc.
    logs: HabitLog[];
    type: string; // e.g., "Action", "Quantity", "Duration"
    unit?: string; // e.g., "times", "minutes", etc.

    constructor(
        id: number,
        title: string, 
        description: string,
        category: string,
        logs: HabitLog[],
        type: string,
        unit?: string,
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.type = type;
        this.unit = unit;
        this.logs = logs;
    }
}