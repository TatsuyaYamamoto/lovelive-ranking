package net.sokontokoro_factory.api.game.resource;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.sokontokoro_factory.api.game.service.TokenService;

@Path("token")
public class Token {
	@Context
	HttpServletRequest request;
	
	@GET
	public Response getRegistrationToken(){
		// 認証確認
		HttpSession session = request.getSession(false);
    	if (session == null) {
			return Response.status(Status.UNAUTHORIZED).entity("UNAUTHORIZED!").build();
		}
		String skntkr_token = TokenService.generate();
		session.setAttribute("skntkr_token", skntkr_token);
    	return Response.ok().entity(skntkr_token).build();
	}
	
}

