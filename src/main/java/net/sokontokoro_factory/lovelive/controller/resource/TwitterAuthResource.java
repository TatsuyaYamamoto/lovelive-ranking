package net.sokontokoro_factory.lovelive.controller.resource;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import net.sokontokoro_factory.lovelive.controller.dto.ErrorDto;
import net.sokontokoro_factory.lovelive.exception.NoResourceException;
import net.sokontokoro_factory.lovelive.service.LoginSession;
import net.sokontokoro_factory.lovelive.service.UserService;
import net.sokontokoro_factory.yoshinani.file.config.Config;
import net.sokontokoro_factory.yoshinani.file.config.ConfigLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;


@Path("auth/twitter")
@RequestScoped
public class TwitterAuthResource {
    private static final Logger logger = LogManager.getLogger(TwitterAuthResource.class.getSimpleName());

    private static final Config config = ConfigLoader.getProperties();
    private static final String GAME_CLIENT_ORIGIN = config.getString("game.client.origin");
    private static final String TWITTER_APIKEY = config.getString("twitter.apikey");
    private static final String TWITTER_SECRET = config.getString("twitter.secret");

    @Inject
    LoginSession loginSession;

    @Inject
    UserService userService;

    @Context
    UriInfo uriInfo;

    /**
     * ログイン処理を実行する
     *
     * @param redirectPath 　ログイン後のリダイレクト先
     * @return
     */
    @Path("login")
    @GET
    public Response login(
            @QueryParam("redirect")
            @DefaultValue("/")
                    String redirectPath) {

        String callbackUri = uriInfo.getBaseUriBuilder()
                .path(TwitterAuthResource.class)
                .path("/callback")
                .build().toString();
        logger.info("callback URL after logging:" + callbackUri);

        final OAuth10aService service = new ServiceBuilder(TWITTER_APIKEY)
                .apiSecret(TWITTER_SECRET)
                .callback(callbackUri)
                .build(TwitterApi.instance());

        final OAuth1RequestToken requestToken;
        try {
            requestToken = service.getRequestToken();
        } catch (InterruptedException | ExecutionException | IOException e) {
            logger.catching(e);
            ErrorDto error = new ErrorDto();
            error.setMessage("faild to access twitter auth providing server");
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }

        loginSession.setRedirectPathAfterLogging(redirectPath);
        loginSession.setRequestToken(requestToken);

        // twitter認証画面へリダイレクト
        URI redirect = UriBuilder
                .fromUri("https://api.twitter.com")
                .path("oauth")
                .path("authorize")
                .queryParam("oauth_token", requestToken.getToken()).build();

        return Response.seeOther(redirect).build();
    }

    /**
     * twitterの認証完了後のエンドポイント
     * access_token, access_token_secret, user_id, screen_nameをセッションに登録する
     *
     * @param oauthToken
     * @param oauthVerifier
     * @param denied
     * @return
     */
    @Path("callback")
    @GET
    public Response callback(
            @QueryParam("oauth_token") String oauthToken,
            @QueryParam("oauth_verifier") String oauthVerifier,
            @QueryParam("denied") @DefaultValue("admit") String denied) {

        final OAuth1RequestToken requestToken = loginSession.getRequestToken();

        // リクエストトークン取得済み確認
        if (requestToken == null) {
            return Response.status(Status.UNAUTHORIZED).entity("Unauthorized.Please try to login again, sorry.").build();
        }

        URI redirect = UriBuilder
                .fromUri(GAME_CLIENT_ORIGIN)
                .path(loginSession.getRedirectPathAfterLogging())
                .build();

        try {
            // ユーザーが認証許可したか
            if (denied.equals("admit")) {
                //認証許可の場合
                final OAuth10aService service = new ServiceBuilder(TWITTER_APIKEY)
                        .apiSecret(TWITTER_SECRET)
                        .build(TwitterApi.instance());
                final OAuth1AccessToken accessToken = service.getAccessToken(requestToken, oauthVerifier);


                final OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/account/verify_credentials.json");
                service.signRequest(accessToken, request);

                JSONObject credentialsJson = new JSONObject(service.execute(request).getBody());

                Long userId = Long.valueOf((String) credentialsJson.get("id_str"));
                String screenName = (String) credentialsJson.get("screen_name");

                loginSession.setAccessToken(accessToken);
                loginSession.setUserId(userId);
                loginSession.setUserName(screenName);

                logger.info("loging requesting user admit. user id: %s", userId);
                try {
                    userService.getById(loginSession.getUserId());
                } catch (NoResourceException notRegisterd) {
                    logger.info("loging requesting user not exist in DB. register!");
                    userService.create(loginSession.getUserId(), loginSession.getUserName());
                }

            } else {
                // 認証不許可の場合
                loginSession.invalidate();
                logger.info("loging requesting user deny.");
            }
        } catch (ExecutionException | InterruptedException | IOException e) {
            logger.catching(e);
            ErrorDto error = new ErrorDto();
            error.setMessage("faild to access twitter auth providing server");
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }

        return Response.seeOther(redirect).build();
    }

    /**
     * ログアウトする。
     * ログインセッションを削除し、指定pathへリダイレクトする。
     *
     * @param redirectPath
     * @return
     */
    @Path("logout")
    @GET
    public Response logout(
            @QueryParam("redirect")
            @DefaultValue("/")
                    String redirectPath) {

        URI redirect = UriBuilder
                .fromUri(GAME_CLIENT_ORIGIN)
                .path(redirectPath)
                .build();

        loginSession.invalidate();

        return Response.seeOther(redirect).build();
    }
}
