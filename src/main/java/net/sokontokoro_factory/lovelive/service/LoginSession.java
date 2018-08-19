package net.sokontokoro_factory.lovelive.service;

import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class LoginSession implements Serializable {

  @Autowired @Getter HttpServletRequest request;

  /* field */
  @Getter @Setter OAuth1RequestToken requestToken;

  @Getter @Setter OAuth1AccessToken accessToken;

  @Getter @Setter String redirectUriAfterLogging;

  @Getter @Setter Long userId;

  @Getter @Setter String userName;

  @Getter @Setter String skntkrToken;

  public boolean isLogin() {
    return accessToken != null ? true : false;
  }

  public void invalidate() {
    // 変数初期化
    requestToken = null;
    accessToken = null;
    redirectUriAfterLogging = null;

    // セッションID再作成
    request.getSession(false).invalidate();
  }
}
