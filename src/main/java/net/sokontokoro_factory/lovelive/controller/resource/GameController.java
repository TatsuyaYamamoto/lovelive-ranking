package net.sokontokoro_factory.lovelive.controller.resource;

import net.sokontokoro_factory.lovelive.controller.form.PostScoreForm;
import net.sokontokoro_factory.lovelive.domain.score.GameType;
import net.sokontokoro_factory.lovelive.domain.types.Member;
import net.sokontokoro_factory.lovelive.service.LogService;
import net.sokontokoro_factory.lovelive.service.LoginSession;
import net.sokontokoro_factory.lovelive.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("games")
public class GameController {
  private LoginSession loginSession;
  private LogService logService;
  private ScoreService scoreService;

  @Autowired
  public GameController(
      LoginSession loginSession, LogService logService, ScoreService scoreService) {
    this.loginSession = loginSession;
    this.logService = logService;
    this.scoreService = scoreService;
  }

  /**
   * スコア情報を登録する。 スコア情報はプレイログとランキング用スコアに別れ、ランキング用スコアはログイン中のユーザーのもののみ保存される。
   *
   * @return
   */
  @RequestMapping(
      path = "{game_name}/scores",
      method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity postScore(
      @PathVariable("game_name") String gameName, @RequestBody @Valid PostScoreForm form) {

    GameType game = null;
    Member member = null;
    try {
      game = GameType.valueOf(gameName.toUpperCase());
      member = Member.valueOf(form.getMember().toUpperCase());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build();
    }

    // プレイログ登録
    logService.addGameLog(game, member, loginSession.getUserId(), form.getPoint());

    if (loginSession.isLogin()) {
      // ランキング用スコア登録

      scoreService.insertScore(game, member, loginSession.getUserId(), form.getPoint());
    }

    return ResponseEntity.ok().build();
  }
}
