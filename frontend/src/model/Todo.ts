export default class Todo {
    id: string;
    title: string;
    due_date: Date | null = null;
    assigned_to: string | null = null;
    completed: boolean;


    constructor(id: string, title: string, due_date: Date | null, assigned_to: string | null, completed: boolean) {
        this.id = id;
        this.title = title;
        this.due_date = due_date;
        this.assigned_to = assigned_to;
        this.completed = completed;
    }
}