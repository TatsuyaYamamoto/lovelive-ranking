package net.sokontokoro_factory.api.game.resource;


import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONObject;

import net.sokontokoro_factory.api.game.dto.UserDto;
import net.sokontokoro_factory.api.game.form.UserForm;
import net.sokontokoro_factory.api.game.service.UserService;
import net.sokontokoro_factory.api.util.CacheManager;
import net.sokontokoro_factory.api.util.JSONResponse;
import net.sokontokoro_factory.lib.twitter.Twitter;

@Path("/users")
public class User{
	@Context
	HttpServletRequest request;
		
	/**
	 * ログインアカウントの情報を返す。
	 * user_id, user_nameはsokontokoro-server, iconURLはtwitter server RestAPIへ問い合わせる。
	 * @param request
	 * @return　{user_id:***, user_name: ***, iconURL: ***}
	 * @throws SQLException 
	 * @throws Exception
	 */
	@Path("/me")
	@GET
    @Produces("application/json;charset=UTF-8")
	public Response getMyInfo() throws SQLException{
		
		/* 認証確認 */
    	HttpSession session = request.getSession(false);
    	if (session == null) {
    		return Response
    				.status(Status.UNAUTHORIZED)
    				.entity(JSONResponse.message(JSONResponse.NOT_AUTHORIZED, "please login system"))
    				.build();
    	}
		
    	int userId = Integer.parseInt((String) session.getAttribute("user_id"));

    	/* アカウント有効性確認 */
        if(!UserService.isValidId(userId)){
        	return Response
        			.status(Status.FORBIDDEN)
    				.entity(JSONResponse.message(JSONResponse.INVALID_ACCOUNT, "your account is not valid"))
        			.build();
        }

        /* execute */
    	UserDto user = new UserDto(userId);
		user = UserService.getDetail(user);
        
		/* twitterサーバーへの問い合わせ */
		HashMap<String, String> parameterQuery = new HashMap<String, String>();
		parameterQuery.put("user_id", String.valueOf(userId));
		Twitter twitter = new Twitter((String)session.getAttribute("access_token"),(String)session.getAttribute("access_token_secret"));
		JSONObject twitterAccountData = twitter.getResource(
													"https://api.twitter.com/1.1/users/show.json",
													parameterQuery);
		user.setIconURL((String) twitterAccountData.get("profile_image_url"));

    	/* レスポンス */
		return Response.ok()
				.entity(user)
				.cacheControl(CacheManager.getNoCacheAndStoreControl())
				.build();
	}

	/**
	 * ユーザー情報をformにしたがって更新する
	 * session取得時にユーザー登録、有効化が実施されている前提
	 * @param request
	 * @param user
	 * @return
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws Exception
	 */
	@Path("/me")
	@POST
    @Consumes("application/json;charset=UTF-8")
    @Produces("application/json;charset=UTF-8")
	public Response registrationMyInfo(@Valid UserForm userForm) throws ClassNotFoundException, SQLException{
		
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
    	user.setName(userForm.getUserName());

    	/* アカウント有効性確認 */
        if(!UserService.isValidId(userId)){
        	return Response
        			.status(Status.FORBIDDEN)
    				.entity(JSONResponse.message(JSONResponse.INVALID_ACCOUNT, "your account is not valid"))
        			.build();
        }
    	
    	/* execute */
    	user = UserService.registration(user);

    	/* レスポンス */
		return Response.ok()
				.status(Status.CREATED)
				.entity(user)
				.cacheControl(CacheManager.getNoCacheAndStoreControl())
				.build();
	}
	
	@Path("/me")
	@DELETE
    @Produces("application/json;charset=UTF-8")
	public Response deleteMyInfo() throws ClassNotFoundException, SQLException{

		/* 認証確認 */
    	HttpSession session = request.getSession(false);
    	if (session == null) {
    		return Response
    				.status(Status.UNAUTHORIZED)
    				.entity(JSONResponse.message(JSONResponse.NOT_AUTHORIZED, "please login system"))
    				.build();
    	}
		
		UserDto deleteUser = new UserDto();
		deleteUser.setId(Integer.parseInt((String)session.getAttribute("user_id")));

    	/* execute */
		UserService.delete(deleteUser);
		
    	/* レスポンス */
		return Response.ok().status(Status.NO_CONTENT).build();
	}
	
}
