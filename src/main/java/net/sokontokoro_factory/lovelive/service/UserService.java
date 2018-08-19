package net.sokontokoro_factory.lovelive.service;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import javax.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import net.sokontokoro_factory.lovelive.ApplicationConfig;
import net.sokontokoro_factory.lovelive.domain.user.FavoriteType;
import net.sokontokoro_factory.lovelive.domain.user.User;
import net.sokontokoro_factory.lovelive.domain.user.UserRepository;
import net.sokontokoro_factory.lovelive.exception.NoResourceException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserService {
  private ApplicationConfig config;
  private final UserRepository userRepos;

  @Autowired
  public UserService(ApplicationConfig config, UserRepository userRepos) {
    this.config = config;
    this.userRepos = userRepos;
  }

  public String getProfileImageUrl(long userId, OAuth1AccessToken accessToken)
      throws InterruptedException, ExecutionException, IOException {
    /* twitterサーバーへの問い合わせ */
    final OAuth10aService service =
        new ServiceBuilder(config.getCredential().getTwitterKey())
            .apiSecret(config.getCredential().getTwitterSecret())
            .build(TwitterApi.instance());
    final OAuthRequest request =
        new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/users/show.json?user_id=" + userId);

    service.signRequest(accessToken, request);
    String body = service.execute(request).getBody();
    JSONObject usersShowJson = new JSONObject(body);

    String profileImageUrl = usersShowJson.get("profile_image_url").toString();
    return profileImageUrl;
  }

  /**
   * 論理削除されていないユーザーのID検索を行う
   *
   * @param userId
   * @return
   * @throws NoResourceException 存在しない、または論理削除済みの場合
   */
  public User getById(long userId) throws NoResourceException {
    Optional<User> user = User.get(userRepos, userId);

    if (user.isPresent()) {
      return user.get();
    }

    throw new NoResourceException("Provided ID is not registered.");
  }

  /**
   * userを新規作成する。 論理削除済みのユーザーの場合、deleteフラグの削除とuserNameの更新を行う
   *
   * @param userId
   * @param name
   */
  @Transactional
  public void create(long userId, String name) {
    User.create(userRepos, userId, name);
  }

  /**
   * user情報を更新する。nullまたは空文字の項目は更新しない 更新対象：ユーザー名、論理削除フラグ
   *
   * @param userId ユーザーID
   * @param name
   * @param favorite
   * @throws NoResourceException ユーザーIDが存在しない場合
   */
  @Transactional
  public void update(long userId, String name, FavoriteType favorite) throws NoResourceException {

    /* 更新対象のuser objectを取得 */
    User updateUser = getById(userId);

    /* 更新 */
    if (name != null) {
      updateUser.setName(name);
    }
    if (favorite != null) {
      updateUser.setFavorite(favorite);
    }
  }

  /**
   * userを論理削除する
   *
   * @param userId ユーザーID
   * @throws NoResourceException ユーザーIDが存在しない場合
   */
  @Transactional
  public void delete(long userId) throws NoResourceException {
    User user = getById(userId);
    user.delete();
  }
}
