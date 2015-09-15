package net.sokontokoro_factory.games.oauth;


import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;


@Path("")

public class LoginServlet {


	final String HOME = "http://diary.sokontokoro-factory.net/honocar/";
	final String ORIGIN = "http://diary.sokontokoro-factory.net";

	final String ENDPOINT_TWITTER_OAUTH_TOKEN = "https://api.twitter.com/oauth/authenticate";
	final String QUERY_KEY_TWITTER_OAUTH_TOKEN = "oauth_token=";
	
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	@Path("/login")
	@GET
	public Response login(@Context HttpServletRequest request) {

		String response = TokenService.getRequestToken();

		// 取得文字列の分割
		// &を=に置き換えて、書く文字列要素に分割
		String paramaters = response.replaceAll("&", "=");
		String[] paramater = paramaters.split("=");
		String oauth_token = paramater[1];
		String oauth_token_secret = paramater[3];

		HttpSession session = request.getSession();
		session.setAttribute("oauth_token", oauth_token);
		session.setAttribute("oauth_token_secret", oauth_token_secret);

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
		if(session != null){
			session.invalidate();			
		}

		URI uriRedirect = null;
		try {
			uriRedirect = new URI(HOME);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.seeOther(uriRedirect)
				.header("Access-Control-Allow-Origin", ORIGIN)
				.header("Access-Control-Allow-Headers", "*")
				.header("Access-Control-Allow-Methods", "*")
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
	@Path("/redirect")
	@GET
	public Response redirect(@QueryParam("oauth_token") String oauth_token,
			@QueryParam("oauth_verifier") String oauth_verifier,
			@Context HttpServletRequest request) {
		String response = TokenService.getAccessToken(oauth_token, oauth_verifier);
		HttpSession session = request.getSession(false);

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
		session.setAttribute("user_id", paramater[5]);

		// 認証が終わったら、homeに戻る
		URI uriRedirect = null;
		try {
			uriRedirect = new URI(HOME);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.seeOther(uriRedirect).build();
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@Path("/check")// getUserProfile
	@GET
	@Produces("application/json;charset=UTF-8")
	public Response checkLogin(@Context HttpServletRequest request) throws Exception {


		final CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setMustRevalidate(true);
        cacheControl.setNoStore(true);
		
		HttpSession session = request.getSession(false);
		String user_id = null;        
        
		if (session == null) {
			return Response.status(Status.UNAUTHORIZED).entity("UNAUTHORIZED!")
					.header("Access-Control-Allow-Origin", ORIGIN)
					.header("Access-Control-Allow-Headers", "*")
					.header("Access-Control-Allow-Methods", "*")
					.header("Access-Control-Allow-Credentials", true)
					.cacheControl(cacheControl)
					.build();
		} else {
			user_id = (String) session.getAttribute("user_id");
		}
		if(user_id == null){
			return Response.status(Status.UNAUTHORIZED).entity("UNAUTHORIZED!!")
					.header("Access-Control-Allow-Origin", ORIGIN)
					.header("Access-Control-Allow-Headers", "*")
					.header("Access-Control-Allow-Methods", "*")
					.header("Access-Control-Allow-Credentials", true)
					.cacheControl(cacheControl)
					.build();	
		}
		
		String profile = TokenService.getProfile((String) session.getAttribute("user_id"), 
				(String) session.getAttribute("access_token"),
				(String) session.getAttribute("access_token_secret"));
		
		
		return Response.ok()
					.header("Access-Control-Allow-Origin", ORIGIN)
					.header("Access-Control-Allow-Headers", "*")
					.header("Access-Control-Allow-Methods", "*")
					.header("Access-Control-Allow-Credentials", true)
					.entity(profile)
					.cacheControl(cacheControl)
					.build();
		
	}
}
