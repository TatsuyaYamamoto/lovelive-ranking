package net.sokontokoro_factory.lovelive.service;

import javax.transaction.Transactional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.sokontokoro_factory.lovelive.domain.log.GameLog;
import net.sokontokoro_factory.lovelive.domain.log.GameLogRepository;
import net.sokontokoro_factory.lovelive.domain.score.GameType;
import net.sokontokoro_factory.lovelive.domain.types.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LogService {
  private final GameLogRepository gameLogRepository;

  private final LoginSession loginSession;

  @Autowired
  public LogService(GameLogRepository gameLogRepository, LoginSession loginSession) {
    this.gameLogRepository = gameLogRepository;
    this.loginSession = loginSession;
  }

  /**
   * ゲームログを登録する
   *
   * @param game
   * @param userId
   * @param point
   */
  @Transactional
  public void addGameLog(
      @NonNull GameType game, Member member, Long userId, @NonNull Integer point) {
    GameLog.add(
        gameLogRepository,
        game,
        member,
        userId,
        point,
        loginSession.getRequest().getRequestedSessionId(),
        loginSession.getRequest().getSession().getCreationTime(),
        loginSession.getRequest().getRemoteAddr(),
        loginSession.getRequest().getHeader("user-agent"),
        loginSession.getRequest().getLocale().toString());
  }
}
