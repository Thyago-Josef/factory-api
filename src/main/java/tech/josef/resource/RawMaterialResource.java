package tech.josef.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import tech.josef.dto.RawMaterialDTO;
import tech.josef.service.RawMaterialService;

@Path("/raw-materials")
@Produces("application/json")
@Consumes("application/json")
public class RawMaterialResource {

    @Inject
    RawMaterialService service;

    @GET
    public Response getAll() {
        return Response.ok(service.listAll()).build();
    }

    @POST
    public Response create(RawMaterialDTO dto) {
        return Response.status(201).entity(service.create(dto)).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, RawMaterialDTO dto) {
        return Response.ok(service.update(id, dto)).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        RawMaterialDTO dto = service.findById(id);
        return Response.ok(dto).build();
    }

    @PATCH
    @Path("/{id}")
    public Response patch(@PathParam("id") Long id, RawMaterialDTO dto) {
        RawMaterialDTO updated = service.patch(id, dto);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        service.delete(id);
        return Response.noContent().build();
    }
}