package net.sokontokoro_factory.api.twitter.user;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("")
public class UserRestController{

	final String ORIGIN = "http://diary.sokontokoro-factory.net";
	/**
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */

	@Path("/")// getUserProfile
	@GET
	@Produces("application/json;charset=UTF-8")
	public Response testsura() {
		return Response.ok().entity("test/ is ok.").build();
	}
	
	@Path("/test")// getUserProfile
	@GET
	@Produces("application/json;charset=UTF-8")
	public Response test() {
		return Response.ok().entity("users is ok.").build();
	}
	
	/**
	 * セッション内のuser_idを使って自身のTwitterユーザー情報を取得する
	 * https://api.twitter.com/1.1/users/show.json?user_id=
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@Path("/me")
	@GET
	@Produces("application/json;charset=UTF-8")
	public Response getMe(@Context HttpServletRequest request) throws Exception {

		final CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setMustRevalidate(true);
        cacheControl.setNoStore(true);
		
		HttpSession session = request.getSession(false);
            
		if (session == null) {
			return Response.status(Status.UNAUTHORIZED).entity("UNAUTHORIZED!")
					.header("Access-Control-Allow-Origin", ORIGIN)
					.header("Access-Control-Allow-Headers", "*")
					.header("Access-Control-Allow-Methods", "*")
					.header("Access-Control-Allow-Credentials", true)
					.cacheControl(cacheControl)
					.build();
		}
		
		
		Map<String, String> param_query = new HashMap<String, String>();
		param_query.put("user_id", (String) session.getAttribute("user_id"));
		
		// twitter api 実行
		String data = UserService.execute(
				(String) session.getAttribute("access_token"),
				(String) session.getAttribute("access_token_secret"),
				"GET",
				"https://api.twitter.com/1.1/users/show.json", 
				param_query);
		
		
		return Response.ok().entity(data)
					.header("Access-Control-Allow-Origin", ORIGIN)
					.header("Access-Control-Allow-Headers", "*")
					.header("Access-Control-Allow-Methods", "*")
					.header("Access-Control-Allow-Credentials", true)
					.cacheControl(cacheControl)
					.build();
		
	}

}