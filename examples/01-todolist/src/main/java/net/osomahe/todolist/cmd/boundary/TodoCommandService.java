package net.osomahe.todolist.cmd.boundary;

import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import net.osomahe.esk.eventstore.boundary.EventStorePublisher;
import net.osomahe.todolist.cmd.entity.TodoCompletedEvent;
import net.osomahe.todolist.cmd.entity.TodoCreatedEvent;
import net.osomahe.todolist.cmd.entity.TodoDeletedEvent;
import net.osomahe.todolist.cmd.entity.TodoInfo;


/**
 * @author Antonin Stoklasek
 */
@Stateless
public class TodoCommandService {

    @Inject
    private EventStorePublisher esPublisher;

    public void createTodo(TodoInfo todoInfo) {
        TodoCreatedEvent event = new TodoCreatedEvent();
        event.setId(UUID.randomUUID().toString());
        event.setName(todoInfo.getName());
        this.esPublisher.publish(event);
    }

    public void setTodoCompleted(String id) {
        TodoCompletedEvent event = new TodoCompletedEvent();
        event.setId(id);
        this.esPublisher.publish(event);
    }

    public void deleteTodo(String id) {
        TodoDeletedEvent event = new TodoDeletedEvent();
        event.setId(id);
        this.esPublisher.publish(event);
    }
}
