package net.sokontokoro_factory.lovelive.controller.resource;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import net.sokontokoro_factory.lovelive.controller.dto.ErrorDto;
import net.sokontokoro_factory.lovelive.controller.dto.UserDto;
import net.sokontokoro_factory.lovelive.controller.form.UpdateUserForm;
import net.sokontokoro_factory.lovelive.exception.NoResourceException;
import net.sokontokoro_factory.lovelive.domain.user.User;
import net.sokontokoro_factory.lovelive.service.LoginSession;
import net.sokontokoro_factory.lovelive.service.UserService;
import net.sokontokoro_factory.lovelive.domain.user.FavoriteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
public class UserResource {

  private final UserService userService;

  private final LoginSession loginSession;

  @Autowired
  public UserResource(UserService userService, LoginSession loginSession) {
    this.userService = userService;
    this.loginSession = loginSession;
  }

  /**
   * ログインアカウントの情報を返す。 user_id, user_nameはsokontokoro-server, iconURLはtwitter server RestAPIへ問い合わせる。
   *
   * @return　{user_id:***, user_name: ***, iconURL: ***}
   * @throws NoResourceException
   * @throws TweetlyOAuthException
   */
  @RequestMapping(
      path = "me",
      method = RequestMethod.GET,
      produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
  public ResponseEntity getMyInfo() throws NoResourceException {

    if (!loginSession.isLogin()) {
      ErrorDto response = new ErrorDto();
      response.setMessage("unauthorized. please request after logging.");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /* execute */
    User user = userService.getById(loginSession.getUserId());

    /* twitterサーバーへの問い合わせ */
    String imageUrl = null;
    try {
      imageUrl = userService.getProfileImageUrl(user.getId(), loginSession.getAccessToken());
    } catch (IOException | InterruptedException | ExecutionException ignore) {
    }

    /* レスポンス */
    UserDto response = new UserDto();
    response.setId(user.getId());
    response.setName(user.getName());
    if (user != null) {
      response.setIconURL(imageUrl);
    }
    return ResponseEntity.ok(response);
  }

  /**
   * ユーザー情報をformにしたがって更新する session取得時にユーザー登録、有効化が実施されている前提
   *
   * @param updateUserForm
   * @return
   * @throws NoResourceException
   */
  @RequestMapping(path = "me", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity update(UpdateUserForm updateUserForm) throws NoResourceException {

    if (!loginSession.isLogin()) {
      ErrorDto response = new ErrorDto();
      response.setMessage("unauthorized. please request after logging.");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    FavoriteType favorite = null;
    // キャラ名の入力チェック
    if (updateUserForm.getFavorite() != null) {
      favorite = FavoriteType.codeOf(updateUserForm.getFavorite());
      if (favorite == null) {
        ErrorDto errorDto = new ErrorDto("正しいキャラクター名を指定して下さい");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
      }
    }

    /* execute */
    userService.update(loginSession.getUserId(), updateUserForm.getUserName(), favorite);

    return ResponseEntity.ok().build();
  }

  /**
   * ユーザーを削除する
   *
   * @return
   * @throws NoResourceException
   */
  @RequestMapping(path = "me", method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
  public ResponseEntity deleteMyInfo() throws NoResourceException {

    if (!loginSession.isLogin()) {
      ErrorDto response = new ErrorDto();
      response.setMessage("unauthorized. please request after logging.");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /* execute */
    userService.delete(loginSession.getUserId());
    loginSession.invalidate();

    /* レスポンス */
    return ResponseEntity.noContent().build();
  }
}
