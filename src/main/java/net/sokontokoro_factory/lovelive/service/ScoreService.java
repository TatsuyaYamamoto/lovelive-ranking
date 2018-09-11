package net.sokontokoro_factory.lovelive.service;

import static java.util.Comparator.comparing;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.sokontokoro_factory.lovelive.domain.score.GameType;
import net.sokontokoro_factory.lovelive.domain.score.Score;
import net.sokontokoro_factory.lovelive.domain.score.ScoreRepository;
import net.sokontokoro_factory.lovelive.domain.types.Member;
import net.sokontokoro_factory.lovelive.exception.InvalidArgumentException;
import net.sokontokoro_factory.lovelive.exception.NoResourceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScoreService {
  private final ScoreRepository scoreRepo;

  @Autowired
  public ScoreService(ScoreRepository scoreRepo) {
    this.scoreRepo = scoreRepo;
  }

  /**
   * スコア情報を返す
   *
   * @param game
   * @param userId
   * @return
   * @throws NoResourceException スコアが存在しないとき
   * @throws InvalidArgumentException ユーザーIDが存在しないとき
   */
  public Score getScore(GameType game, long userId)
      throws NoResourceException, InvalidArgumentException {
    Optional<Score> score = Score.get(scoreRepo, game, userId);
    if (!score.isPresent()) {
      throw new NoResourceException("スコア未登録です。");
    } else {
      return score.get();
    }
  }

  /**
   * スコア情報をupsertする
   *
   * @param game
   * @param userId
   * @param point
   * @throws InvalidArgumentException
   */
  @Transactional
  public void insertScore(GameType game, long userId, int point)
      throws InvalidArgumentException, NoResourceException {

    Optional<Score> score = Score.get(scoreRepo, game, userId);
    if (!score.isPresent()) {
      Score.create(scoreRepo, game, userId, point);
    } else {
      score.get().updatePoint(point, null);
    }
  }

  /**
   * スコア情報をupsertする
   *
   * @param game
   * @param userId
   * @param point
   * @throws InvalidArgumentException
   */
  @Transactional
  public void insertScore(@NonNull GameType game, @NonNull Member member, long userId, int point) {

    Optional<Score> score = Score.get(scoreRepo, game, userId);
    if (!score.isPresent()) {
      Score created = Score.create(scoreRepo, game, userId, point);
      created.setMember(member);
    } else {
      score.get().updatePoint(point, member);
    }
  }

  /**
   * 順位を取得する
   *
   * @param targetGame 対象のゲーム
   * @param targetPoint 順位を取得する点数
   * @return
   */
  public Long getRankingNumber(GameType targetGame, int targetPoint) {
    log.debug("getRanking()", targetGame, targetPoint);

    List<Score> allScore = scoreRepo.findAll();
    long ranking =
        allScore
                .stream()
                .filter(score -> !score.getUser().isDeleted())
                .filter(score -> score.getGame() == targetGame)
                .filter(score -> score.getPoint() > targetPoint)
                .count()
            + 1;

    log.debug("result: " + ranking);

    return ranking;
  }

  /**
   * offsetRankingNumber以下の順位のScoreEntityのリストを取得する
   *
   * @param targetGame 対象のゲーム
   * @param offsetRankingNumber 検索するリストのスコアの上限値
   * @param range 検索するリストの数
   * @return
   */
  public List<Score> getList(GameType targetGame, int offsetRankingNumber, int range) {
    int offsetBorderPoint = getBorderPoint(targetGame, offsetRankingNumber);
    int limitBorderPoint = getBorderPoint(targetGame, offsetRankingNumber + range);

    List<Score> allScore = scoreRepo.findAll();
    List<Score> topScores =
        allScore
            .stream()
            .filter(score -> !score.getUser().isDeleted())
            .filter(score -> score.getGame() == targetGame)
            .filter(score -> score.getPoint() <= offsetBorderPoint)
            .filter(score -> score.getPoint() > limitBorderPoint)
            .sorted(comparing(Score::getPoint).reversed())
            .collect(Collectors.toList());

    return topScores;
  }

  /**
   * 指定のランキング順位に入るための最低ポイントを取得する
   *
   * @param targetGame
   * @param targetRankingNumber
   * @return
   */
  public int getBorderPoint(GameType targetGame, int targetRankingNumber) {
    List<Score> allScore = scoreRepo.findAll();

    List<Integer> descPoints =
        allScore
            .stream()
            .filter(score -> score.getGame() == targetGame)
            .map(score -> score.getPoint())
            .distinct()
            .sorted(Comparator.reverseOrder())
            .collect(Collectors.toList());

    // スコア未登録の場合
    if (descPoints.size() == 0) {
      return 0;
    }

    int borderPoint;
    if (descPoints.size() < targetRankingNumber) {
      borderPoint = descPoints.get(descPoints.size() - 1);
    } else {
      borderPoint = descPoints.get(targetRankingNumber - 1);
    }
    log.info("game: " + targetGame + ", borderpoint: " + borderPoint);
    return borderPoint;
  }
}
