package net.osomahe.todolist.cmd.entity;

import net.osomahe.esk.eventstore.entity.AbstractEvent;
import net.osomahe.esk.eventstore.entity.TopicName;


/**
 * Sets for all ToDo events topic name
 *
 * @author Antonin Stoklasek
 */
@TopicName("todo")
public abstract class TodoEvent extends AbstractEvent {
}
