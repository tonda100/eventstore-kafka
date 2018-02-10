package net.osomahe.todolist.query.boundary;

import java.util.Collection;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.osomahe.todolist.query.entity.TodoAggregate;


/**
 * @author Antonin Stoklasek
 */
@Path("todo")
public class TodoQueryResource {

    @Inject
    private TodoQueryService qsTodo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTodos() {
        Collection<TodoAggregate> todos = this.qsTodo.getAllTodos();
        return Response.ok().entity(todos).build();
    }

}
