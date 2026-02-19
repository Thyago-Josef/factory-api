package tech.josef.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import tech.josef.dto.ProductDTO;
import tech.josef.service.ProductService;

import java.util.List;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    ProductService productService;

    @GET
    public List<ProductDTO> getAll() {
        return productService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        ProductDTO dto = productService.findById(id);
        return dto != null ? Response.ok(dto).build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    public Response create(@Valid ProductDTO dto) {
        // Cria o produto e já vincula as matérias-primas enviadas no JSON
        ProductDTO created = productService.create(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }


    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, ProductDTO dto) {
        try {
            ProductDTO updated = productService.update(id, dto);
            return Response.ok(updated).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @PATCH
    @Path("/{id}")
    public Response patch(@PathParam("id") Long id, ProductDTO dto) {
        try {
            ProductDTO updated = productService.patch(id, dto);
            return Response.ok(updated).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        productService.delete(id);
        return Response.noContent().build();
    }
}