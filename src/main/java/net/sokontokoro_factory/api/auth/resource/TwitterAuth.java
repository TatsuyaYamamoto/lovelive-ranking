package net.sokontokoro_factory.api.auth.resource;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;

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
import net.sokontokoro_factory.api.game.dto.UserDto;
import net.sokontokoro_factory.api.game.service.UserService;
import net.sokontokoro_factory.api.util.Config;


@Path("twitter")
public class TwitterAuth {

	@Context
	HttpServletRequest request;
        
    /**
     * 
     * @param request
     * @return
     * @throws ConfigurationException 
     */
    @Path("login")
    @GET
    public Response login(@QueryParam("redirect")String redirect) throws URISyntaxException{
		HttpSession session = request.getSession();
		session.setAttribute("redirect", redirect);
    	
        Twitter twitter = new Twitter();
        Authorization authorization = twitter.getRequestToken(Config.getString("server.origin") + "/v1/auth/twitter/callback/");        
		session.setAttribute("request_token", authorization.getRequestToken());
		session.setAttribute("request_token_secret", authorization.getRequestTokenSecret());

		// twitter認証画面へリダイレクト
		URI uriRedirect = new URI("https://api.twitter.com/oauth/authorize?oauth_token=" + authorization.getRequestToken());
		return Response.seeOther(uriRedirect).build();
    }
    /**
     * twitterの認証完了後のエンドポイント
     * access_token, access_token_secret, user_id, screen_nameをセッションに登録する
     * @param oauth_token
     * @param oauth_verifier
     * @param request
     * @return
     * @throws ConfigurationException 
     * @throws SQLException 
     * @throws ClassNotFoundException 
     */
    @Path("callback")
    @GET
    public Response callback(
            @QueryParam("oauth_token") String request_token,
            @QueryParam("oauth_verifier") String oauth_verifier,
            @DefaultValue("") @QueryParam("denied") String denied
            ) throws ConfigurationException, URISyntaxException, ClassNotFoundException, SQLException{
        
        HttpSession session = request.getSession(false);
    	if(session == null){
    		return Response.status(Status.UNAUTHORIZED).entity("Unauthorized.Please try to login again, sorry.").build();
    	}
    	
    	// リダイレクト用URI
        URI uriRedirect = new URI(Config.getString("game.client.origin") +"/"+(String)session.getAttribute("redirect"));
        
        if(denied.equals("")){
        //認証許可の場合
        	Twitter twitter = new Twitter();
            Authorization authorization = twitter.getAccessToken(
            		(String)session.getAttribute("request_token"),
            		(String)session.getAttribute("request_token_secret"),
            		oauth_verifier);

            session.setAttribute("access_token", authorization.getAccessToken());
            session.setAttribute("access_token_secret", authorization.getAccessTokenSecret());
            session.setAttribute("user_id", authorization.getUserId());
            
            UserDto user = new UserDto();
            user.setId(Integer.parseInt(authorization.getUserId()));
            user.setName(authorization.getScreenName());

            if(!UserService.isValidId(Integer.parseInt(authorization.getUserId()))){
                // 未登録、削除済みユーザーの場合
            	UserService.registration(user);
			}        
            
        }else{
        // 認証不許可の場合
        	session.invalidate();
        }
        return Response.seeOther(uriRedirect).build();
    }
    /**
     * 
     * @param request
     * @return
     */
    @Path("logout")
    @GET
    public Response logout(@QueryParam("redirect") String redirect)throws URISyntaxException{

        HttpSession session = request.getSession(false);
        if(session != null){
            session.invalidate();
        }
        String destination = Config.getString("game.client.origin") + "/" + redirect;
        return Response.seeOther(new URI(destination)).build();
    }
}
