package net.osomahe.realapp.price.boundary;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * TODO write JavaDoc
 *
 * @author Antonin Stoklasek
 */
@Path("prices")
public class PriceResource {

    @Inject
    private PriceBoundary priceBoundary;

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAllPrices() {
        return Response.ok().entity(priceBoundary.getPrices()).build();
    }

    @GET
    @Path("counters")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCounters() {
        return Response.ok().entity(priceBoundary.getCounters()).build();
    }
}
