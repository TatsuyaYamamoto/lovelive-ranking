package net.sokontokoro_factory.lovelive.domain.score;

import java.util.Optional;
import javax.persistence.*;
import lombok.Data;
import lombok.NonNull;
import net.sokontokoro_factory.lovelive.domain.types.Member;
import net.sokontokoro_factory.lovelive.domain.user.User;

/**
 * TODO: スコアの持ち方の再検討 IntegerのPointカラムしかないため、少数、文字列の評価ができない。
 * おいものみきりから、レベル、x人抜きの点数以外の概念が出たため、汎用的に格納できるデータの持ち方にする必要がある。
 *
 * <p>おいものみきりの点数について ベストタイム最大五桁、レベル1桁、人抜き1桁の 3-7桁でスコアを格納する 1: 易しい 2: 普通 3: 難しい
 * 9999910の場合、ベストタイムは99999、レベルは易しい、0人抜き 23335の場合、ベストタイムは233、レベルは難しい、5人抜き
 */
@Entity
@Table(name = "score")
@Data
public class Score {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "game", nullable = false)
  @Enumerated(EnumType.STRING)
  private GameType game;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "point", nullable = false)
  private Integer point;

  // TODO migration to be non nullable.
  @Column(name = "member", nullable = true)
  @Enumerated(EnumType.STRING)
  private Member member;

  @Column(name = "create_date", nullable = false)
  private Long createDate;

  @Column(name = "update_date")
  private Long updateDate;

  @Column(name = "final_date", nullable = false)
  private Long finalDate;

  @Column(name = "count", nullable = false)
  private Integer count;

  /** *********************************** relation */
  @JoinColumn(name = "user_id", referencedColumnName = "ID", insertable = false, updatable = false)
  @ManyToOne
  private User user;

  protected Score() {}

  public Score(@NonNull GameType game, long userId, int point) {
    this.game = game;
    this.userId = userId;
    this.point = point;
    this.count = 1;
  }

  public static Optional<Score> get(ScoreRepository repo, @NonNull GameType game, long userId) {
    return repo.findByGameAndUserId(game, userId);
  }

  public static Score create(ScoreRepository repo, @NonNull GameType game, long userId, int point) {
    Score score = new Score(game, userId, point);
    return repo.save(score);
  }

  public boolean updatePoint(int newPoint, Member member) {
    boolean isUpdated = false;
    if (this.point < newPoint) {
      this.point = newPoint;
      if (member != null) {
        this.member = member;
      }
      isUpdated = true;
    }
    this.count += 1;
    this.finalDate = System.currentTimeMillis();

    return isUpdated;
  }

  @PrePersist
  protected void prePersist() {
    this.createDate = System.currentTimeMillis();
  }

  @PreUpdate
  protected void preUpdate() {
    this.updateDate = System.currentTimeMillis();
  }
}
