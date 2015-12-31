package net.sokontokoro_factory.api.game.exception;

import javax.validation.ValidationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import net.sokontokoro_factory.api.util.JSONResponse;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException>{

	private static final String MESSAGE = "不正な文字列、数値を入力しています";
	
    @Override
    public Response toResponse(ValidationException exception) {
    	return Response
    			.status(Status.BAD_REQUEST)
    			.entity(JSONResponse.message(JSONResponse.INVALID_FORM, MESSAGE))
    			.build();
    }
}