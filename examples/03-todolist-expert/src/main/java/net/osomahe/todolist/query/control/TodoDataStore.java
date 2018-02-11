package net.osomahe.todolist.query.control;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.Singleton;

import net.osomahe.todolist.query.entity.TodoAggregate;


/**
 * @author Antonin Stoklasek
 */
@Singleton
public class TodoDataStore {

    private Map<String, TodoAggregate> mapTodos = new ConcurrentHashMap<>();

    public void addTodo(TodoAggregate todo) {
        this.mapTodos.put(todo.getId(), todo);
    }

    public TodoAggregate getTodo(String id) {
        return this.mapTodos.get(id);
    }

    public Collection<TodoAggregate> getAllTodos() {
        return mapTodos.values();
    }

    public void removeTodo(String id) {
        mapTodos.remove(id);
    }
}
