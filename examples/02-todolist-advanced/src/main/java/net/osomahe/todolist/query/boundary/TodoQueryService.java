package net.osomahe.todolist.query.boundary;

import java.util.Collection;

import javax.ejb.Stateless;
import javax.inject.Inject;

import net.osomahe.todolist.query.control.TodoDataStore;
import net.osomahe.todolist.query.entity.TodoAggregate;


/**
 * @author Antonin Stoklasek
 */
@Stateless
public class TodoQueryService {

    @Inject
    private TodoDataStore ds;

    public Collection<TodoAggregate> getAllTodos() {
        return ds.getAllTodos();
    }
}
