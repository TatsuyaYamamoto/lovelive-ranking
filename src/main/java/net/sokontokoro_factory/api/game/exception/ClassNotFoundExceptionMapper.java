package net.sokontokoro_factory.api.game.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ClassNotFoundExceptionMapper implements ExceptionMapper<ClassNotFoundException>{

    @Override
    public Response toResponse(ClassNotFoundException exception) {
    	return Response.status(Status.INTERNAL_SERVER_ERROR).entity("設定ファイル関連のエラー" + exception.getMessage()).build();
    }
}