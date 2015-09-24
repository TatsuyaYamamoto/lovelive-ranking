package net.sokontokoro_factory.api.game.ranking;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;

import net.sokontokoro_factory.api.game.util.DBManager;
import net.sokontokoro_factory.api.game.util.Property;


@Path("")
public class RankingRestController {

	
	final static int NUMBER_OF_TOP = 10;
    private @Context
    HttpServletRequest req;

    // ゲームの指定なしでランキング情報は取得できない
    @Path("")
    @GET
    @Produces("application/json;charset=UTF-8")
    public Response getScoresButError() throws Exception{
    	return Response
    			.status(Status.BAD_REQUEST)
    			.entity("PLEASE SELECT GAME")
    			.build();
    }
    
    
    // 上位"NUMBER_OF_TOP"のランキング情報を取得
    @Path("/{game_name}")
    @GET
    @Produces("application/json;charset=UTF-8")
    public Response getHigher(
                            @PathParam(value = "game_name") String game_name, 
                            @Context HttpServletRequest request) 
                            throws Exception{

		HttpSession session = request.getSession(false);
    	if(session == null){
    		return Response.status(Status.UNAUTHORIZED).entity("認証されていません。").build();
    	}
    	JSONArray scores =  RankingService.getHigher(game_name, NUMBER_OF_TOP);
    	return Response.ok().entity(scores).build();

    }
    
    // アクセス者ののランキング情報を取得
    @Path("/{game_name}/me")
    @GET
    @Produces("application/json;charset=UTF-8")
    public Response getMine(
                  @PathParam(value = "game_name") String game_name, 
                            @Context HttpServletRequest request) 
                            throws Exception{

		HttpSession session = request.getSession(false);
    	if(session == null){
    		return Response.status(Status.UNAUTHORIZED).entity("認証されていません。").build();
    	}
    	String scores = RankingService.getMine(game_name);
    	return Response.ok().entity(scores).build();

    }
}

