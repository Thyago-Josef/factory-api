package tech.josef.exception;



import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException exception) {
        return Response.status(exception.getResponse().getStatus())
                .entity(Map.of(
                        "status", exception.getResponse().getStatus(),
                        "message", exception.getMessage()
                ))
                .build();
    }
}