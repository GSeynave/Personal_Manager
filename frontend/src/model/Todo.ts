export default class Todo {
    id?: number;  // Optional for creation, required for updates
    title: string;
    enhancedTitle?: string;
    due_date: string | null = null;  // ISO date string (YYYY-MM-DD)
    assigned_to: string | null = null;
    completed: boolean;

    constructor(
        title: string, 
        completed: boolean = false,
        id?: number,
        due_date: string | null = null, 
        assigned_to: string | null = null,
        enhancedTitle?: string
    ) {
        this.id = id;
        this.title = title;
        this.enhancedTitle = enhancedTitle;
        this.due_date = due_date;
        this.assigned_to = assigned_to;
        this.completed = completed;
    }
}