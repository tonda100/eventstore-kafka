package net.osomahe.todolist.query.control;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import net.osomahe.todolist.cmd.entity.TodoCompletedEvent;
import net.osomahe.todolist.cmd.entity.TodoCreatedEvent;
import net.osomahe.todolist.cmd.entity.TodoDeletedEvent;
import net.osomahe.todolist.query.entity.TodoAggregate;


/**
 * @author Antonin Stoklasek
 */
@Stateless
public class TodoEventHandler {

    @Inject
    private TodoDataStore ds;

    public void handleCreate(@Observes TodoCreatedEvent event) {
        TodoAggregate todo = new TodoAggregate();
        todo.setId(event.getId());
        todo.setName(event.getName());
        todo.setCompleted(false);
        ds.addTodo(todo);
    }

    public void handleCompleted(@Observes TodoCompletedEvent event) {
        TodoAggregate todo = ds.getTodo(event.getId());
        todo.setCompleted(true);
    }

    public void handleDelete(@Observes TodoDeletedEvent event) {
        ds.removeTodo(event.getId());
    }

}
