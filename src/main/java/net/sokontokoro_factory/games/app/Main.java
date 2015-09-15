package net.sokontokoro_factory.games.app;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;



import org.apache.log4j.Logger;

@Path("")
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class);

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
    @Path("/scores/{game_name}")
    @GET
    @Produces("application/json;charset=UTF-8")
    public Response getScores(@PathParam(value = "game_name") String game_name) throws Exception{

		// HttpSession session = req.getSession(false);

		// if (session != null) {
			try{
				return Response.ok().entity(DBManager.getScores(game_name).toString()).build();

			} catch (Exception e) {
				LOGGER.error("大変だ！"+e);
				throw e;
			}
		// } else {
		// 	return Response.status(Status.UNAUTHORIZED).entity("認証権限がありません。").build();
		// }

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

