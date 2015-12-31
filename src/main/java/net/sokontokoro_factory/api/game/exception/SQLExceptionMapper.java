package net.sokontokoro_factory.api.game.exception;

import java.sql.SQLException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import net.sokontokoro_factory.api.util.JSONResponse;

@Provider
public class SQLExceptionMapper implements ExceptionMapper<SQLException>{

	private static final String MESSAGE = "データーベースにエラーが発生しました";
	
    @Override
    public Response toResponse(SQLException exception) {
    	return Response
    			.status(Status.INTERNAL_SERVER_ERROR)
    			.entity(JSONResponse.message(JSONResponse.INTERNAL_SERVER_ERROR, MESSAGE))
    			.build();
    }
}