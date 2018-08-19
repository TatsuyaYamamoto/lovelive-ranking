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
import net.sokontokoro_factory.lovelive.exception.NoResourceException;
import net.sokontokoro_factory.lovelive.domain.user.UserRepository;
import net.sokontokoro_factory.lovelive.domain.user.User;
import net.sokontokoro_factory.lovelive.domain.user.FavoriteType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserService {
  private static final Logger logger = LogManager.getLogger(UserService.class);

  @Value("${app.credential.twitter-key}")
  private String twitterApikey;

  @Value("${app.credential.twitter-secret}")
  private String twitterSecret;

  private final UserRepository userRepos;

  @Autowired
  public UserService(UserRepository userRepos) {
    this.userRepos = userRepos;
  }

  public String getProfileImageUrl(long userId, OAuth1AccessToken accessToken)
      throws InterruptedException, ExecutionException, IOException {
    logger.entry(userId, accessToken);

    /* twitterサーバーへの問い合わせ */
    final OAuth10aService service =
        new ServiceBuilder(twitterApikey).apiSecret(twitterSecret).build(TwitterApi.instance());
    final OAuthRequest request =
        new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/users/show.json?user_id=" + userId);

    service.signRequest(accessToken, request);
    String body = service.execute(request).getBody();
    JSONObject usersShowJson = new JSONObject(body);

    String profileImageUrl = usersShowJson.get("profile_image_url").toString();
    return logger.traceExit(profileImageUrl);
  }

  /**
   * 論理削除されていないユーザーのID検索を行う
   *
   * @param userId
   * @return
   * @throws NoResourceException 存在しない、または論理削除済みの場合
   */
  public User getById(long userId) throws NoResourceException {
    logger.entry(userId);

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
    logger.entry(userId, name);
    User.create(userRepos, userId, name);
    logger.traceExit();
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

    logger.entry(userId, name, favorite);

    /* 更新対象のuser objectを取得 */
    User updateUser = getById(userId);

    /* 更新 */
    if (name != null) {
      updateUser.setName(name);
    }
    if (favorite != null) {
      updateUser.setFavorite(favorite);
    }

    logger.traceExit();
  }

  /**
   * userを論理削除する
   *
   * @param userId ユーザーID
   * @throws NoResourceException ユーザーIDが存在しない場合
   */
  @Transactional
  public void delete(long userId) throws NoResourceException {
    logger.entry(userId);

    User user = getById(userId);
    user.delete();

    logger.traceExit();
  }
}
