import Todo from './Todo'
import TodoGroup from './TodoGroup'

export default class TodosView {
    ungroupedTodos: Todo[]
    groupedTodos: TodoGroup[]

    constructor(ungroupedTodos: Todo[] = [], groupedTodos: TodoGroup[] = []) {
        this.ungroupedTodos = ungroupedTodos
        this.groupedTodos = groupedTodos
    }
}
