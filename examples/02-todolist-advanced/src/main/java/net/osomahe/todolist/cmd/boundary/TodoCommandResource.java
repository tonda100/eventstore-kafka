package net.osomahe.todolist.cmd.boundary;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.osomahe.todolist.cmd.entity.TodoInfo;


/**
 * @author Antonin Stoklasek
 */
@Path("todo")
public class TodoCommandResource {

    @Inject
    private TodoCommandService cmdTodo;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTodo(TodoInfo todoInfo) {
        this.cmdTodo.createTodo(todoInfo);
        return Response.accepted().build();
    }

    @PATCH
    @Path("{id}/complete")
    public Response setTodoCompleted(@PathParam("id") String id) {
        this.cmdTodo.setTodoCompleted(id);
        return Response.accepted().build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteTodo(@PathParam("id") String id) {
        this.cmdTodo.deleteTodo(id);
        return Response.accepted().build();
    }
}
