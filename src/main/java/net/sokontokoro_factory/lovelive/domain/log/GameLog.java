package net.sokontokoro_factory.lovelive.domain.log;

import javax.persistence.*;
import lombok.Data;
import net.sokontokoro_factory.lovelive.domain.score.GameType;

@Entity
@Table(name = "GAME_LOG")
@Data
public class GameLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "GAME", nullable = false)
  @Enumerated(EnumType.STRING)
  private GameType game;

  @Column(name = "USER_ID")
  private Long userId;

  @Column(name = "POINT", nullable = false)
  private Integer point;

  @Column(name = "PLAY_DATE", nullable = false)
  private Long playDate;

  @Column(name = "SESSION_ID")
  private String sessionId;

  @Column(name = "START_SESSION_DATE")
  private Long startSessionDate;

  @Column(name = "CLIENT_IP")
  private String clientIp;

  @Column(name = "USER_AGENT")
  private String userAgent;

  @Column(name = "LOCALE")
  private String locale;

  public static GameLog add(
      GameLogRepository repository,
      GameType game,
      long userId,
      int point,
      String sessionId,
      long startSessionDate,
      String clientIp,
      String userAgent,
      String locale) {
    GameLog log = new GameLog();
    log.setGame(game);
    log.setUserId(userId);
    log.setPoint(point);
    log.setPlayDate(System.currentTimeMillis());
    log.setSessionId(sessionId);
    log.setStartSessionDate(startSessionDate);
    log.setClientIp(clientIp);
    log.setUserAgent(userAgent);
    log.setLocale(locale);

    return repository.save(log);
  }
}
