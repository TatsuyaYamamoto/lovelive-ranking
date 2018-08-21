package net.sokontokoro_factory.lovelive.domain.log;

import javax.persistence.*;
import lombok.Data;
import lombok.NonNull;
import net.sokontokoro_factory.lovelive.domain.score.GameType;

@Entity
@Table(name = "game_log")
@Data
public class GameLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "game", nullable = false)
  @Enumerated(EnumType.STRING)
  private GameType game;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "point", nullable = false)
  private Integer point;

  @Column(name = "play_date", nullable = false)
  private Long playDate;

  @Column(name = "session_id")
  private String sessionId;

  @Column(name = "start_session_date")
  private Long startSessionDate;

  @Column(name = "client_ip")
  private String clientIp;

  @Column(name = "user_agent")
  private String userAgent;

  @Column(name = "locale")
  private String locale;

  public static GameLog add(
      @NonNull GameLogRepository repository,
      @NonNull GameType game,
      Long userId,
      @NonNull Integer point,
      String sessionId,
      Long startSessionDate,
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
