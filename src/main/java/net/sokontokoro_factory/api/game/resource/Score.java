package net.sokontokoro_factory.api.game.resource;

import java.sql.SQLException;
import java.util.ArrayList;

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

import net.sokontokoro_factory.api.game.dto.ScoreDto;
import net.sokontokoro_factory.api.game.dto.UserDto;
import net.sokontokoro_factory.api.game.form.ScoreForm;
import net.sokontokoro_factory.api.game.service.ScoreService;
import net.sokontokoro_factory.api.game.service.UserService;
import net.sokontokoro_factory.api.util.CacheManager;
import net.sokontokoro_factory.api.util.JSONResponse;
import net.sokontokoro_factory.api.util.validation.CompatibleGame;

import org.json.JSONArray;
import org.json.JSONObject;

@Path("scores")
public class Score {
	@Context
	HttpServletRequest request;
	
	
	/**
	 * 
	 * @param game_name
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
    @Path("me")
    @GET
    @Produces("text/plain")
    public Response getAllScoreData() throws ClassNotFoundException, SQLException {
		/* 認証確認 */
    	HttpSession session = request.getSession(false);
    	if (session == null) {
    		return Response
    				.status(Status.UNAUTHORIZED)
    				.entity(JSONResponse.message(JSONResponse.NOT_AUTHORIZED, "please login system"))
    				.build();
    	}

    	int userId = Integer.parseInt((String) session.getAttribute("user_id"));
    	UserDto user = new UserDto(userId);
		user = UserService.getDetail(user);

    	/* アカウント有効性確認 */
        if(!UserService.isValidId(userId)){
        	return Response
        			.status(Status.FORBIDDEN)
    				.entity(JSONResponse.message(JSONResponse.INVALID_ACCOUNT, "your account is not valid"))
        			.build();
        }
    	
    	/* エンティティ取得 */
        ArrayList<ScoreDto> scores = ScoreService.getScores(userId);

    	/* レスポンスデータ作成 */
        JSONObject scores_json = new JSONObject();
        for(ScoreDto score: scores){
        	JSONObject score_json = new JSONObject();
        	score_json.put("point", score.getPoint());
        	score_json.put("ranking", score.getRanking());
        	scores_json.put(score.getGameName(), score_json);
        }
        
        JSONObject response = new JSONObject();
        response.put("user_id", user.getId());
        response.put("user_name", user.getName());        
        response.put("scores", scores_json);

    	/* レスポンス */
    	return Response.ok()
    			.entity(response.toString())
    			.cacheControl(CacheManager.getNoCacheAndStoreControl())
   				.build();
    }


    @Path("{game_name}/me")
    @GET
    @Produces("application/json;charset=UTF-8")
    public Response getScoreData(@PathParam(value = "game_name") String gameName) throws ClassNotFoundException, SQLException {
		/* 認証確認 */
    	HttpSession session = request.getSession(false);
    	if (session == null) {
    		return Response
    				.status(Status.UNAUTHORIZED)
    				.entity(JSONResponse.message(JSONResponse.NOT_AUTHORIZED, "please login system"))
    				.build();
    	}

    	int userId = Integer.parseInt((String) session.getAttribute("user_id"));
    	UserDto user = new UserDto(userId);
		user = UserService.getDetail(user);

    	/* アカウント有効性確認 */
        if(!UserService.isValidId(userId)){
        	return Response
        			.status(Status.FORBIDDEN)
    				.entity(JSONResponse.message(JSONResponse.INVALID_ACCOUNT, "your account is not valid"))
        			.build();
        }
    	
    	/* エンティティ取得 */
        ScoreDto score = ScoreService.getScore(userId, gameName);

        if(score == null){
        	return Response
        			.status(Status.NOT_FOUND)
        			.entity(JSONResponse.message(JSONResponse.NOT_REGISTRATION, "please play the game."))
        			.build();        	
        }
        
    	/* レスポンスデータ作成 */
        score.setUserId(user.getId());
        score.setUserName(user.getName());

    	/* レスポンス */
    	return Response.ok()
    			.entity(score)
    			.cacheControl(CacheManager.getNoCacheAndStoreControl())
   				.build();
    }
    
    /**
     * スコア登録する
     * @param gameName
     * @param score
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @Path("{game_name}/me")
    @POST
    @Consumes("application/json;charset=UTF-8")
    @Produces("application/json;charset=UTF-8")
    public Response insertScore(
    						@PathParam(value = "game_name") String gameName,  
    						ScoreForm score) throws ClassNotFoundException, SQLException{
		/* 認証確認 */
    	HttpSession session = request.getSession(false);
    	if (session == null) {
    		return Response
    				.status(Status.UNAUTHORIZED)
    				.entity(JSONResponse.message(JSONResponse.NOT_AUTHORIZED, "please login system"))
    				.build();
    	}
    	/*tokenチェック*/
    	if(score.getSkntktToken().equals(session.getAttribute("skntkt_token"))){
    		return Response
    				.status(Status.BAD_REQUEST)
    				.entity(JSONResponse.message(JSONResponse.INVALID_TOKEN, "please get token again"))
    				.build();    		
    	}
    	/* DB書き込み */
    	int point = score.getPoint();
    	int userId = Integer.parseInt((String) session.getAttribute("user_id"));
		ScoreService.insertScore(gameName, userId, point);

    	/* レスポンスデータ作成 */
		ScoreDto response = new ScoreDto();
		response.setUserId(userId);
		response.setPoint(point);
		
    	/* レスポンス */
    	return Response.ok()
    			.entity(response)
    			.cacheControl(CacheManager.getNoCacheAndStoreControl())
   				.build();
    }

    /**
     * 上位(RANKING_TOP_NUMBER指定)のランキング情報を取得
     * @param game_name
     * @param request
     * @return
     * @throws SQLException 
     * @throws ClassNotFoundException 
     * @throws Exception 
     */
    @Path("/{game_name}/ranking")
    @GET
    @Produces("application/json;charset=UTF-8")
    public Response getRankingData(@PathParam(value = "game_name") @CompatibleGame String game_name) throws ClassNotFoundException, SQLException{

    	/* エンティティ取得 */
    	ArrayList<ScoreDto> scores = ScoreService.getHigher(game_name);
    	int total = ScoreService.getTotalNumber(game_name);

    	/* レスポンスデータ作成 */
    	JSONObject result = new JSONObject();
    	result.put("total", total);
    	result.put("game_name", game_name);
    	JSONArray JSONScores = new JSONArray();
    	for(ScoreDto score: scores){
    		JSONObject JSONScore = new JSONObject();
    		JSONScore.put("user_id", score.getUserId());    		
    		JSONScore.put("user_name", score.getUserName());
    		JSONScore.put("point", score.getPoint());
    		JSONScore.put("ranking", score.getRanking());
    		JSONScores.put(JSONScore);
    	}
    	result.put("ranking_table", JSONScores);
    	
    	/* レスポンス */
    	return Response.ok()
    			.entity(result.toString())
   				.cacheControl(CacheManager.getNoCacheAndStoreControl())
    			.build();

    }
}

