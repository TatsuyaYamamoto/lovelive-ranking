package net.sokontokoro_factory.lovelive.controller.resource;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sokontokoro_factory.lovelive.controller.dto.ErrorDto;
import net.sokontokoro_factory.lovelive.controller.dto.ScoreDto;
import net.sokontokoro_factory.lovelive.controller.form.InsertScoreForm;
import net.sokontokoro_factory.lovelive.exception.InvalidArgumentException;
import net.sokontokoro_factory.lovelive.exception.NoResourceException;
import net.sokontokoro_factory.lovelive.persistence.entity.ScoreEntity;
import net.sokontokoro_factory.lovelive.service.LogService;
import net.sokontokoro_factory.lovelive.service.LoginSession;
import net.sokontokoro_factory.lovelive.service.ScoreService;
import net.sokontokoro_factory.lovelive.type.GameType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("scores")
public class ScoreResource {
  private final LoginSession loginSession;

  private final ScoreService scoreService;

  private final LogService logService;

  @Autowired
  public ScoreResource(
      LoginSession loginSession, ScoreService scoreService, LogService logService) {
    this.loginSession = loginSession;
    this.scoreService = scoreService;
    this.logService = logService;
  }

  /**
   * ログイン中のUserIDのスコア情報を取得する
   *
   * @param gameName
   * @return
   * @throws NoResourceException
   * @throws InvalidArgumentException
   */
  @RequestMapping(
      path = "{game_name}/me",
      method = RequestMethod.GET,
      produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
  public ResponseEntity getScore(@PathVariable("game_name") String gameName)
      throws NoResourceException, InvalidArgumentException {

    if (!loginSession.isLogin()) {
      ErrorDto response = new ErrorDto();
      response.setMessage("unauthorized. please request after logging.");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    String upperCaseGameName = gameName.toUpperCase();

    // ゲーム名の入力チェック
    if (!GameType.contains(upperCaseGameName)) {
      ErrorDto errorDto = new ErrorDto();
      errorDto.setMessage("正しいゲーム名を指定して下さい");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDto);
    }

    /* エンティティ取得 */
    GameType game = GameType.valueOf(upperCaseGameName);
    ScoreEntity scoreEntity = scoreService.getScore(game, loginSession.getUserId());
    long ranking = scoreService.getRankingNumber(game, scoreEntity.getPoint());

    /* レスポンスデータ作成 */
    ScoreDto score = new ScoreDto();
    score.setGame(game);
    score.setUserId(scoreEntity.getUserId());
    score.setUserName(scoreEntity.getUserEntity().getName());
    score.setPoint(scoreEntity.getPoint());
    score.setCount(scoreEntity.getCount());
    score.setRanking(ranking);

    /* レスポンス */
    return ResponseEntity.ok(score);
  }

  /**
   * スコア登録する
   *
   * @param gameName
   * @param insertScoreForm
   * @return
   * @throws InvalidArgumentException
   */
  @RequestMapping(
      path = "{game_name}/me",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity insertScore(
      @PathVariable("game_name") String gameName,
      InsertScoreForm insertScoreForm,
      UriComponentsBuilder uriBuilder)
      throws InvalidArgumentException {

    if (!loginSession.isLogin()) {
      ErrorDto response = new ErrorDto();
      response.setMessage("unauthorized. please request after logging.");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // 入力チェック
    if (insertScoreForm.getPoint() == null) {
      ErrorDto errorDto = new ErrorDto("点数を入力して下さい");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    // ゲーム名の入力チェック
    String upperCaseGameName = gameName.toUpperCase();
    if (!GameType.contains(upperCaseGameName)) {
      ErrorDto errorDto = new ErrorDto();
      errorDto.setMessage("正しいゲーム名を指定して下さい");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDto);
    }

    /* DB書き込み */
    GameType game = GameType.valueOf(upperCaseGameName);
    try {
      scoreService.insertScore(game, loginSession.getUserId(), insertScoreForm.getPoint());
    } catch (NoResourceException e) {
      ErrorDto errorDto = new ErrorDto();
      errorDto.setMessage("正しいゲーム名を指定して下さい");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDto);
    }

    /* レスポンス */
    URI uri =
        uriBuilder
            .path("lovelive/scores/{game}/me")
            .buildAndExpand(game.name().toLowerCase())
            .toUri();

    return ResponseEntity.created(uri).build();
  }

  @RequestMapping(
      path = "{game_name}/playlog",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity postPlayLog(
      @PathVariable("game_name") String gameName, InsertScoreForm insertScoreForm) {
    // ゲーム名の入力チェック
    String upperCaseGameName = gameName.toUpperCase();
    if (!GameType.contains(upperCaseGameName)) {
      ErrorDto errorDto = new ErrorDto();
      errorDto.setMessage("正しいゲーム名を指定して下さい");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDto);
    }

    /* ロギング */
    GameType game = GameType.valueOf(upperCaseGameName);
    logService.addGameLog(
        game, loginSession != null ? loginSession.getUserId() : null, insertScoreForm.getPoint());

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  /**
   * 指定順位以下のランキングスコアリストを取得する
   *
   * @param gameName
   * @param offset
   * @return
   */
  @RequestMapping(
      path = "/{game_name}/ranking",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity getRankingData(
      @PathVariable("game_name") String gameName,
      @RequestParam(value = "offset", defaultValue = "1") Integer offset,
      @RequestParam(value = "offset", defaultValue = "10") Integer limit) {

    // ゲーム名の入力チェック
    String upperCaseGameName = gameName.toUpperCase();
    if (!GameType.contains(upperCaseGameName)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto("正しいゲーム名を指定して下さい"));
    }

    // offset値の入力チェック
    if (offset <= 0) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new ErrorDto("ランキングのoffset値は1以上を指定して下さい。"));
    }

    /* エンティティ取得 */
    GameType game = GameType.valueOf(upperCaseGameName);
    List<ScoreEntity> scoreEntities = scoreService.getList(game, offset, limit);

    List<ScoreDto> scores = new ArrayList();
    long ranking = 0;
    int upperUserPoint = Integer.MAX_VALUE;
    for (ScoreEntity scoreEntity : scoreEntities) {
      int targetUserPoint = scoreEntity.getPoint();
      if (targetUserPoint < upperUserPoint) {
        ranking++;
      }

      ScoreDto score = new ScoreDto();
      score.setUserId(scoreEntity.getUserId());
      score.setPoint(targetUserPoint);
      score.setUserName(scoreEntity.getUserEntity().getName());
      score.setHighScoreDate(scoreEntity.getUpdateDate());
      score.setRanking(ranking);

      scores.add(score);

      upperUserPoint = targetUserPoint;
    }

    /* レスポンスデータ作成 */
    Map<String, Object> response = new HashMap();
    response.put("game_name", gameName);
    response.put("ranking_table", scores);

    /* レスポンス */
    return ResponseEntity.ok(response);
  }
}
