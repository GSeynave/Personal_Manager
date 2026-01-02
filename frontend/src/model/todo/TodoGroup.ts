import Todo from './Todo'

export default class TodoGroup {
    id: number
    title: string
    description: string
    todos: Todo[]

    constructor(id: number, title: string, description: string, todos: Todo[] = []) {
        this.id = id
        this.title = title
        this.description = description
        this.todos = todos
    }
}
