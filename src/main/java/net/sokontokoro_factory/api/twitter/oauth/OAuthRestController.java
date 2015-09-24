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
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;


@Path("")
public class OAuthRestController {

	final String ORIGIN = "http://diary.sokontokoro-factory.net";

	final String ENDPOINT_TWITTER_OAUTH_TOKEN = "https://api.twitter.com/oauth/authenticate";
	final String QUERY_KEY_TWITTER_OAUTH_TOKEN = "oauth_token=";

	
	
	@Path("/")// getUserProfile
	@GET
	@Produces("application/json;charset=UTF-8")
	public Response test() {
		return Response.ok().entity("test is ok.").build();
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	@Path("/login")
	@GET
	public Response login(
			@QueryParam("game") String game, 
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
		session.setAttribute("logingGame", game);

		URI uriRedirect = null;
		
		
		try {
			uriRedirect = new URI(ENDPOINT_TWITTER_OAUTH_TOKEN + "?" + QUERY_KEY_TWITTER_OAUTH_TOKEN + oauth_token);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	public Response logout(@Context HttpServletRequest request){

		HttpSession session = request.getSession(false);
		String destination = ORIGIN + "/" + session.getAttribute("logingGame");
		if(session != null){
			session.invalidate();			
		}

		URI uriRedirect = null;
		
		try {
			uriRedirect = new URI(destination);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			@Context HttpServletRequest request) {
		
		HttpSession session = request.getSession(false);
		
		if("default".equals(denied)){
			String response = OAuthService.getAccessToken(oauth_token, oauth_verifier);
			/*
			 * レスポンスボディは
			 * 「access_token」「access_token_secret」「user_id」「screen_name」「x_auth_expires
			 * 」 のkeyとvalue
			String s1 = "Stringクラスは文字列を表します。";
			String s4 = "クラス";
	 		if (s1.indexOf(s4) != -1) {
	 			// 部分一致です
			}else {
				// 部分一致ではありません
			}
			 */
			String paramaters = response.replaceAll("&", "=");
			String[] paramater = paramaters.split("=");

			session.setAttribute("access_token", paramater[1]);
			session.setAttribute("access_token_secret", paramater[3]);
			session.setAttribute("user_id", String.valueOf(paramater[5]));	// 数字列なのでStringにキャストする

		}
		
		
		String destination = ORIGIN + "/" + session.getAttribute("logingGame");
		URI uriRedirect = null;
		try {
			uriRedirect = new URI(destination);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.seeOther(uriRedirect).build();
	}
}
