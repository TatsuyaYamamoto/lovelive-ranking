package net.sokontokoro_factory.lovelive.controller.resource;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import lombok.extern.log4j.Log4j2;
import net.sokontokoro_factory.lovelive.ApplicationConfig;
import net.sokontokoro_factory.lovelive.controller.dto.ErrorDto;
import net.sokontokoro_factory.lovelive.exception.NoResourceException;
import net.sokontokoro_factory.lovelive.service.LoginSession;
import net.sokontokoro_factory.lovelive.service.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("auth/twitter")
@Log4j2
public class TwitterAuthResource {
  private ApplicationConfig config;

  private final LoginSession loginSession;

  private final UserService userService;

  @Autowired
  public TwitterAuthResource(
      ApplicationConfig config, LoginSession loginSession, UserService userService) {
    this.config = config;
    this.loginSession = loginSession;
    this.userService = userService;
  }

  /**
   * ログイン処理を実行する
   *
   * @param redirect 　ログイン後のリダイレクト先
   * @return
   */
  @RequestMapping(value = "login", method = RequestMethod.GET)
  public ResponseEntity<ErrorDto> login(
      @RequestParam(value = "redirect", defaultValue = "/") String redirect,
      UriComponentsBuilder uriBuilder) {

    String callbackUri = uriBuilder.path("/auth/twitter/callback").build().toString();
    log.info("callback URL after logging:" + callbackUri);

    final OAuth10aService service =
        new ServiceBuilder(config.credential.getTwitterKey())
            .apiSecret(config.credential.getTwitterSecret())
            .callback(callbackUri)
            .build(TwitterApi.instance());

    final OAuth1RequestToken requestToken;
    try {
      requestToken = service.getRequestToken();
    } catch (InterruptedException | ExecutionException | IOException e) {
      log.catching(e);
      ErrorDto error = new ErrorDto();
      error.setMessage("faild to access twitter auth providing server");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    loginSession.setRedirectUriAfterLogging(
        redirect.contains("://") ? redirect : "http://games.sokontokoro-factory.net" + redirect);
    loginSession.setRequestToken(requestToken);

    // twitter認証画面へリダイレクト
    URI toTwitterUri =
        URI.create(
            "https://api.twitter.com/oauth/authenticate?oauth_token=" + requestToken.getToken());
    return ResponseEntity.status(HttpStatus.SEE_OTHER).location(toTwitterUri).build();
  }

  /**
   * twitterの認証完了後のエンドポイント access_token, access_token_secret, user_id, screen_nameをセッションに登録する
   *
   * @param oauthToken
   * @param oauthVerifier
   * @param denied
   * @return
   */
  @RequestMapping(value = "callback", method = RequestMethod.GET)
  public ResponseEntity callback(
      @RequestParam("oauth_token") String oauthToken,
      @RequestParam("oauth_verifier") String oauthVerifier,
      @RequestParam(value = "denied", defaultValue = "admit") String denied) {

    final OAuth1RequestToken requestToken = loginSession.getRequestToken();

    // リクエストトークン取得済み確認
    if (requestToken == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Unauthorized.Please try to login again, sorry.");
    }

    URI redirect = URI.create(loginSession.getRedirectUriAfterLogging());

    try {
      // ユーザーが認証許可したか
      if (denied.equals("admit")) {
        // 認証許可の場合
        final OAuth10aService service =
            new ServiceBuilder(config.credential.getTwitterKey())
                .apiSecret(config.credential.getTwitterSecret())
                .build(TwitterApi.instance());
        final OAuth1AccessToken accessToken = service.getAccessToken(requestToken, oauthVerifier);

        final OAuthRequest request =
            new OAuthRequest(
                Verb.GET, "https://api.twitter.com/1.1/account/verify_credentials.json");
        service.signRequest(accessToken, request);

        JSONObject credentialsJson = new JSONObject(service.execute(request).getBody());

        Long userId = Long.valueOf((String) credentialsJson.get("id_str"));
        String screenName = (String) credentialsJson.get("screen_name");

        loginSession.setAccessToken(accessToken);
        loginSession.setUserId(userId);
        loginSession.setUserName(screenName);

        log.info("loging requesting user admit. user id: %s", userId);
        try {
          userService.getById(loginSession.getUserId());
        } catch (NoResourceException notRegisterd) {
          log.info("loging requesting user not exist in DB. register!");
          userService.create(loginSession.getUserId(), loginSession.getUserName());
        }

      } else {
        // 認証不許可の場合
        loginSession.invalidate();
        log.info("loging requesting user deny.");
      }
    } catch (ExecutionException | InterruptedException | IOException e) {
      log.catching(e);
      ErrorDto error = new ErrorDto();
      error.setMessage("faild to access twitter auth providing server");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    return ResponseEntity.status(HttpStatus.SEE_OTHER).location(redirect).build();
  }

  /**
   * ログアウトする。 ログインセッションを削除し、指定pathへリダイレクトする。
   *
   * @param redirect
   * @return
   */
  @RequestMapping(value = "logout", method = RequestMethod.GET)
  public ResponseEntity logout(
      @RequestParam(value = "redirect", defaultValue = "/") String redirect,
      UriComponentsBuilder uriBuilder) {

    String uri =
        redirect.contains("://") ? redirect : "http://games.sokontokoro-factory.net" + redirect;

    loginSession.invalidate();

    return ResponseEntity.status(HttpStatus.SEE_OTHER).location(URI.create(uri)).build();
  }
}
