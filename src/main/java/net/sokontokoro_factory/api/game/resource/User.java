package net.sokontokoro_factory.api.game.resource;


import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONObject;

import net.sokontokoro_factory.api.game.form.UserForm;
import net.sokontokoro_factory.api.game.service.UserService;
import net.sokontokoro_factory.api.util.CacheManager;

@Path("/users")
public class User{
	@Context
	HttpServletRequest request;
		
	/**
	 * ログインアカウントの情報を返す
	 * @param request
	 * @return　{user_id:***, user_name: ***}
	 * @throws Exception
	 */
	@Path("/me")
	@GET
    @Produces("application/json;charset=UTF-8")
	public Response getUserName() throws Exception {
		
		// 認証確認
		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response
					.status(Status.UNAUTHORIZED)
					.entity("UNAUTHORIZED!")
					.build();
		}
		
    	int user_id = Integer.parseInt((String) session.getAttribute("user_id"));
		String screen_name = (String) session.getAttribute("screen_name");
		JSONObject result = null;

		try{
			result = UserService.getUserName(user_id);
			if(!result.has("user_id")){
				// user_
				UserService.registration(user_id, "@"+screen_name);
				result = UserService.getUserName(user_id);
			}			
		}catch(SQLException e){
			return Response
					.status(Status.BAD_REQUEST).entity("SQLみす")
					.header("Access-Control-Allow-Credentials", true)
					.build();
		}
		
		return Response.ok()
				.entity(result.toString())
				.cacheControl(CacheManager.getNoCacheAndStoreControl())
				.build();
		
	}
	
	/**
	 * 
	 * @param request
	 * @param user
	 * @return
	 * @throws Exception
	 */
	@Path("/me")
	@POST
    @Consumes("application/json;charset=UTF-8")
    @Produces("text/plain;charset=UTF-8")
	public Response registration(UserForm user) throws Exception {

		HttpSession session = request.getSession(false);
		if (session == null) {
			return Response
					.status(Status.UNAUTHORIZED)
					.entity("UNAUTHORIZED!")
					.build();
		}
    	int user_id = Integer.parseInt((String) session.getAttribute("user_id"));
    	JSONObject result = null;
		try{
			result = UserService.registration(user_id, user.user_name);
		}catch(SQLException e){
			return Response
					.status(Status.BAD_REQUEST).entity("SQLみす")
					.header("Access-Control-Allow-Credentials", true)
					.build();
		}
		
		return Response.ok()
					.entity(result.toString())
					.cacheControl(CacheManager.getNoCacheAndStoreControl())
					.build();
		
	}
}
