package net.sokontokoro_factory.api.game.exception;

import java.sql.SQLException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SQLExceptionMapper implements ExceptionMapper<SQLException>{

    @Override
    public Response toResponse(SQLException exception) {
        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("DB関連のエラー" + exception.getMessage()).build();
    }
}