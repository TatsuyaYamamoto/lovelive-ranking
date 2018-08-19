package net.sokontokoro_factory.lovelive.service;

import javax.transaction.Transactional;

import net.sokontokoro_factory.lovelive.domain.log.GameLogRepository;
import net.sokontokoro_factory.lovelive.domain.log.GameLog;
import net.sokontokoro_factory.lovelive.domain.score.GameType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LogService {
  private static final Logger logger = LogManager.getLogger(LogService.class);

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
  public void addGameLog(GameType game, Long userId, int point) {
    logger.entry("add()", game, userId);

    GameLog.add(
        gameLogRepository,
        game,
        userId,
        point,
        loginSession.getRequest().getRequestedSessionId(),
        loginSession.getRequest().getSession().getCreationTime(),
        loginSession.getRequest().getRemoteAddr(),
        loginSession.getRequest().getHeader("user-agent"),
        loginSession.getRequest().getLocale().toString());

    logger.traceExit();
  }
}
