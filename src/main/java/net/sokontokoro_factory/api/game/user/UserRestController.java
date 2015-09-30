package net.sokontokoro_factory.api.game.user;


import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONObject;

@Path("")
public class UserRestController{
	
	@Path("/me")
	@GET
    @Produces("application/json;charset=UTF-8")
	public Response getUserName(
					@Context HttpServletRequest request)
					throws Exception {
		
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response.status(Status.UNAUTHORIZED).entity("UNAUTHORIZED!")
					.build();
		}
    	int user_id = Integer.parseInt((String) session.getAttribute("user_id"));
		String screen_name = (String) session.getAttribute("screen_name");
		JSONObject result = null;

		try{
			result = UserService.getUserName(user_id);
			if(!result.has("user_id")){
				UserService.registration(user_id, "@"+screen_name);
				result = UserService.getUserName(user_id);
			}			
		}catch(SQLException e){
			return Response
					.status(Status.BAD_REQUEST).entity("SQLみす")
					.header("Access-Control-Allow-Credentials", true)
					.build();
		}

		
		
		final CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setMustRevalidate(true);
        cacheControl.setNoStore(true);
		
		return Response.ok()
				.entity(result.toString())
				.cacheControl(cacheControl)
				.build();
		
	}

	@Path("/me")
	@POST
    @Consumes("application/json;charset=UTF-8")
    @Produces("text/plain;charset=UTF-8")
	public Response registration(
					@Context HttpServletRequest request, 
					UserResource user)
					throws Exception {

		final CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setMustRevalidate(true);
        cacheControl.setNoStore(true);
		
		HttpSession session = request.getSession(false);
            
		if (session == null) {
			return Response.status(Status.UNAUTHORIZED).entity("UNAUTHORIZED!")
					.build();
		}
    	int user_id = Integer.parseInt((String) session.getAttribute("user_id"));
		
		try{
			UserService.registration(user_id, user.user_name);
		}catch(SQLException e){
			return Response
					.status(Status.BAD_REQUEST).entity("SQLみす")
					.header("Access-Control-Allow-Credentials", true)
					.build();
		}
		
		return Response.ok()
					.entity("user_id=" + user_id + ", user_name=" + user.user_name + " で登録中です。")
					.cacheControl(cacheControl)
					.build();
		
	}
}
