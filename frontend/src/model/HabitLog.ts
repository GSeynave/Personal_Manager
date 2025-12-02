export default class HabitLog {
    id: number;  // Optional for creation, required for updates
    completion_date: Date;

    constructor(
        id: number,
        completion_date: Date, 
    ) {
        this.id = id;
        this.completion_date = completion_date;
    }
}