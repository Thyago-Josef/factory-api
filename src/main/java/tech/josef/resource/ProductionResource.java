package tech.josef.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import tech.josef.dto.ProductionSuggestionDTO;
import tech.josef.service.ProductionService;

import java.util.List;

@Path("/production")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductionResource {

    @Inject
    ProductionService productionService;

    @GET
    @Path("/suggestions")
    public List<ProductionSuggestionDTO> getSuggestions() {
        // RF004: Retorna a lista de produtos que podem ser fabricados
        // priorizando os de maior lucro.
        return productionService.suggestOptimizedProduction();
    }

    @POST
    @Path("/execute/{productId}")
    public Response executeProduction(@PathParam("productId") Long productId, @QueryParam("quantity") Integer quantity) {
        try {
            productionService.executeProduction(productId, quantity);
            return Response.ok().entity("Produção executada e estoque atualizado com sucesso!").build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}