package net.sokontokoro_factory.lovelive.service;

import javax.transaction.Transactional;

import net.sokontokoro_factory.lovelive.persistence.GameLogRepository;
import net.sokontokoro_factory.lovelive.persistence.entity.GameLogEntity;
import net.sokontokoro_factory.lovelive.type.GameType;
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

    GameLogEntity log = new GameLogEntity();
    log.setGame(game);
    log.setUserId(userId);
    log.setPoint(point);
    log.setPlayDate(System.currentTimeMillis());
    log.setSessionId(loginSession.getRequest().getRequestedSessionId());
    log.setStartSessionDate(loginSession.getRequest().getSession().getCreationTime());
    log.setClientIp(loginSession.getRequest().getRemoteAddr());
    log.setUserAgent(loginSession.getRequest().getHeader("user-agent"));
    log.setLocale(loginSession.getRequest().getLocale().toString());

    gameLogRepository.save(log);

    logger.traceExit();
  }
}
