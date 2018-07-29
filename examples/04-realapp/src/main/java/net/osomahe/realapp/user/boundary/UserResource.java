package net.osomahe.realapp.user.boundary;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.osomahe.esk.eventstore.boundary.EventStorePublisher;
import net.osomahe.realapp.user.entity.User;
import net.osomahe.realapp.user.entity.UserCreatedEvent;
import net.osomahe.realapp.user.entity.UserDeleteEvent;
import net.osomahe.realapp.user.entity.UserRenameEvent;


/**
 * TODO write JavaDoc
 *
 * @author Antonin Stoklasek
 */
@Path("users")
public class UserResource {

    @Inject
    private EventStorePublisher esPublisher;

    @Inject
    private UserBoundary userBoundary;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers() {
        return Response.ok().entity(userBoundary.getUsers()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(User user) {
        UserCreatedEvent event = new UserCreatedEvent();
        event.setId(user.getId());
        event.setName(user.getName());
        esPublisher.publish(event);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response renameUser(User user) {
        UserRenameEvent event = new UserRenameEvent();
        event.setId(user.getId());
        event.setName(user.getName());
        esPublisher.publish(event);
        return Response.ok().build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteUser(@PathParam("id") String id) {
        UserDeleteEvent event = new UserDeleteEvent();
        event.setId(id);
        esPublisher.publish(event);
        return Response.accepted().build();
    }

}
