package net.sokontokoro_factory.api.auth.resource;

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

import net.sokontokoro_factory.lib.twitter.Twitter;
import net.sokontokoro_factory.lib.twitter.oauth.v1.Authorization;
import net.sokontokoro_factory.api.util.Config;


@Path("twitter")
public class TwitterAuth {

    
    @Path("")// getUserProfile
    @GET
    @Produces("application/json;charset=UTF-8")
    public Response test() {
        return Response.ok().entity("Resource is ok.").build();
    }
    
    
    @Path("check")
    @GET
    public Response checkLogin(
            @Context HttpServletRequest request){
        HttpSession session = request.getSession(false);

        if(session == null){
            return Response.status(Status.REQUEST_TIMEOUT).entity("Your session has timed out.").build();
        }
        return Response
        		.ok()
        		.entity("Your session is available.")
        		.build();
    }
    
    /**
     * 
     * @param request
     * @return
     * @throws ConfigurationException 
     */
    @Path("login")
    @GET
    public Response login(
            @QueryParam("game_name") String game_name, 
            @Context HttpServletRequest request) throws ConfigurationException {

        Twitter twitter = new Twitter();
        Authorization authorization = twitter.getRequestToken(Config.getString("server.origin") + "/v1/auth/twitter/callback");
        
		HttpSession session = request.getSession();
		session.setAttribute("request_token", authorization.getRequestToken());
		session.setAttribute("request_token_secret", authorization.getRequestTokenSecret());
		session.setAttribute("logingGame", game_name);

		URI uriRedirect = null;
		
		
		try {
			uriRedirect = new URI("https://api.twitter.com/oauth/authorize?oauth_token=" + authorization.getRequestToken());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// twitter認証画面へリダイレクト
		return Response
				.seeOther(uriRedirect)
				.build();

    }
    /**
     * twitterの認証完了後のエンドポイント
     * access_token, access_token_secret, user_id, screen_nameをセッションに登録する
     * @param oauth_token
     * @param oauth_verifier
     * @param request
     * @return
     * @throws ConfigurationException 
     */
    @Path("callback")
    @GET
    public Response callback(
            @QueryParam("oauth_token") String request_token,
            @QueryParam("oauth_verifier") String oauth_verifier,
            @DefaultValue("default") @QueryParam("denied") String denied,
            @Context HttpServletRequest request) throws ConfigurationException{
        
        HttpSession session = request.getSession(false);
        
    	if(session == null){
    		return Response.status(Status.UNAUTHORIZED).entity("Unauthorized.Please try to login again, sorry.").build();
    	}
        
        if("default".equals(denied)){
            Twitter twitter = new Twitter();
            Authorization authorization = twitter.getAccessToken(
            		(String)session.getAttribute("request_token"),
            		(String)session.getAttribute("request_token_secret"),
            		oauth_verifier);

            session.setAttribute("access_token", authorization.getAccessToken());
            session.setAttribute("access_token_secret", authorization.getAccessTokenSecret());
            session.setAttribute("user_id", authorization.getUserId());
            session.setAttribute("screen_name", authorization.getScreenName());
            
        }else{
            session.invalidate();
        }

        URI uriRedirect = null;
        try {
            String destination = Config.getString("game.client.origin") + "/" + session.getAttribute("logingGame");
            uriRedirect = new URI(destination);
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Response
        		.seeOther(uriRedirect)
        		.build();
    }
    /**
     * 
     * @param request
     * @return
     */
    @Path("logout")
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
            String destination = Config.getString("game.client.origin") + "/" + game_name;
            uriRedirect = new URI(destination);
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Response
        		.seeOther(uriRedirect)
                .header("Access-Control-Allow-Credentials", true)
                .build();
    }
}
