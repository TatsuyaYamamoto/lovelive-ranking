package net.sokontokoro_factory.api.twitter.oauth;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.configuration.ConfigurationException;

import net.sokontokoro_factory.api.util.Property;


@Path("oauth")
public class OAuthRestController {

	
	@Path("/")// getUserProfile
	@GET
	@Produces("application/json;charset=UTF-8")
	public Response test() {
		return Response.ok().entity("test is ok.").build();
	}
	
	
	@Path("/check")
	@GET
	public Response checkIsLogin(
			@Context HttpServletRequest request){
		HttpSession session = request.getSession(false);

		if(session == null){
			return Response.status(Status.REQUEST_TIMEOUT).entity("Your session has timed out.").build();
		}
		return Response.ok().entity("Your session is available.").build();
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	@Path("/login")
	@GET
	public Response login(
			@QueryParam("game_name") String game_name, 
			@Context HttpServletRequest request) {

		String response = OAuthService.getRequestToken();

		// 取得文字列の分割
		// &を=に置き換えて、書く文字列要素に分割
		String paramaters = response.replaceAll("&", "=");
		String[] paramater = paramaters.split("=");
		String oauth_token = paramater[1];
		String oauth_token_secret = paramater[3];

		HttpSession session = request.getSession();
		session.setAttribute("oauth_token", oauth_token);
		session.setAttribute("oauth_token_secret", oauth_token_secret);
		session.setAttribute("logingGame", game_name);

		URI uriRedirect = null;
		
		
		try {
			uriRedirect = new URI(Property.AUTHENTICATE_URL() + "?oauth_token=" + oauth_token);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(ConfigurationException e){
			
		}
		// twitter認証画面へリダイレクト
		return Response.seeOther(uriRedirect).build();

	}
	/**
	 * 
	 * @param request
	 * @return
	 */
	@Path("/logout")
	@GET
	public Response logout(
			@QueryParam("game_name") String game_name,
			@Context HttpServletRequest request){

		HttpSession session = request.getSession(false);
		if(session != null){
			session.invalidate();
		}

		URI uriRedirect = null;
		
		try {
			String destination = Property.GAME_CLIENT_ORIGIN() + "/" + game_name;
			uriRedirect = new URI(destination);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigurationException e){
			
		}
		return Response.seeOther(uriRedirect)
				.header("Access-Control-Allow-Credentials", true)
				.build();
	}

	/**
	 * 
	 * @param oauth_token
	 * @param oauth_verifier
	 * @param request
	 * @return
	 */
	@Path("/callback")
	@GET
	public Response callback(
			@QueryParam("oauth_token") String oauth_token,
			@QueryParam("oauth_verifier") String oauth_verifier,
			@DefaultValue("default") @QueryParam("denied") String denied,
			@Context HttpServletRequest request){
		
		HttpSession session = request.getSession(false);
		
		if("default".equals(denied)){
			String response = OAuthService.getAccessToken(oauth_token, oauth_verifier);
			/*
			 * レスポンスボディは
			 * 「access_token」「access_token_secret」「user_id」「screen_name」「x_auth_expires
			 * 」 のkeyとvalue
			 */
			String paramaters = response.replaceAll("&", "=");
			String[] paramater = paramaters.split("=");

			session.setAttribute("access_token", paramater[1]);
			session.setAttribute("access_token_secret", paramater[3]);
			session.setAttribute("user_id", paramater[5]);
			session.setAttribute("screen_name", paramater[7]);
		}else{
			session.invalidate();
		}

		URI uriRedirect = null;
		try {
			String destination = Property.GAME_CLIENT_ORIGIN() + "/" + session.getAttribute("logingGame");
			uriRedirect = new URI(destination);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(ConfigurationException e){
			
		}
		return Response.seeOther(uriRedirect).build();
	}
}
