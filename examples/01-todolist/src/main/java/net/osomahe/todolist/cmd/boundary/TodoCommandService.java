package net.osomahe.todolist.cmd.boundary;

import javax.inject.Inject;

import net.osomahe.esk.boundary.EventStorePublisher;
import net.osomahe.todolist.cmd.entity.TodoCompletedEvent;
import net.osomahe.todolist.cmd.entity.TodoCreatedEvent;
import net.osomahe.todolist.cmd.entity.TodoDeletedEvent;
import net.osomahe.todolist.cmd.entity.TodoInfo;


/**
 * @author Antonin Stoklasek
 */
public class TodoCommandService {

    @Inject
    private EventStorePublisher esPublisher;

    public void createTodo(TodoInfo todoInfo) {
        TodoCreatedEvent event = new TodoCreatedEvent();
        event.setName(todoInfo.getName());
        this.esPublisher.publish(event);
    }

    public void setTodoCompleted(String id) {
        TodoCompletedEvent event = new TodoCompletedEvent();
        event.setAggregateId(id);
        this.esPublisher.publish(event);
    }

    public void deleteTodo(String id) {
        TodoDeletedEvent event = new TodoDeletedEvent();
        event.setAggregateId(id);
        this.esPublisher.publish(event);
    }
}
