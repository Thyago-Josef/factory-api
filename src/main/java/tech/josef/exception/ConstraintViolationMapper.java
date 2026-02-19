package tech.josef.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Provider
public class ConstraintViolationMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", 400);

        // Coleta todas as violações (ex: nome repetido, campo nulo)
        String messages = exception.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining(", "));

        body.put("error", "Erro de validação");
        body.put("details", messages);

        return Response.status(Response.Status.BAD_REQUEST).entity(body).build();
    }
}