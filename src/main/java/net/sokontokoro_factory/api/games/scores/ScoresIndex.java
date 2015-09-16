package net.sokontokoro_factory.api.games.scores;

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

import net.sokontokoro_factory.api.games.db.DBManager;
import net.sokontokoro_factory.api.games.util.Property;


@Path("")
public class ScoresIndex {

    private @Context
    HttpServletRequest req;

    @Path("/test")
    @GET
    @Produces("text/plain;charset=UTF-8")
    public String getItem() {
      String message = "";
      try{
      message = Property.testText() + " main test";

      } catch (Exception e) {}
      return message;
    }

    /*
    * スコア取得
    */
    @Path("/{game_name}")
    @GET
    @Produces("application/json;charset=UTF-8")
    public Response getScores(
    		@PathParam(value = "game_name") String game_name, 
    		@Context HttpServletRequest request) throws Exception{

		HttpSession session = request.getSession(false);
    	
    	if(session == null){
    		return Response.status(Status.UNAUTHORIZED).entity("認証されていません。").build();
    	}
    	String scores = DBManager.getScores(game_name).toString();

    	return Response.ok().entity(scores).build();

    }

    @Path("/{game_name}")
    @POST
    @Consumes("application/json;charset=UTF-8")
    public Response insertScore(@Context HttpServletRequest request){

		HttpSession session = request.getSession(false);
    	
    	if(session == null){
    		return Response.status(Status.UNAUTHORIZED).entity("認証されていません。").build();
    	}
    	
    	try {
			DBManager.insertScore(null, 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return Response.ok().entity("スコア登録が完了しました。").build();
    }
    
  //   /*
  //   * スコア登録
  //   */
  //   @Path("/recordScore")
  //   @POST
  //   @Produces("text/plain;charset=UTF-8")
  //   public Response recordScore(ScoreInfo scoreInfo) throws Exception{

		// HttpSession session = req.getSession(false);

  //       if (session != null) {

  //           try {
  //               JSONObject tmpUser = DBManager.getUserById(userinfo.getUserId());
  //               DBManager.insertUser(userinfo);
  //               return Response.ok().entity("ユーザ登録成功しました。").build();
  //           } catch (Exception e) {
  //               LOGGER.error(e);
  //               throw e;
  //           }
  //       } else {
  //           return Response.status(Status.UNAUTHORIZED).entity("認証権限がありません。")
  //                   .build();
  //       }

  //   }
}

