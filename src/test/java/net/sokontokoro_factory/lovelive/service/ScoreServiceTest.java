package net.sokontokoro_factory.lovelive.service;

import net.sokontokoro_factory.lovelive.TestUtil;
import net.sokontokoro_factory.lovelive.WeldJUnit4Runner;
import net.sokontokoro_factory.lovelive.exception.InvalidArgumentException;
import net.sokontokoro_factory.lovelive.exception.NoResourceException;
import net.sokontokoro_factory.lovelive.persistence.entity.ScoreEntity;
import net.sokontokoro_factory.lovelive.persistence.master.MasterGame;
import net.sokontokoro_factory.lovelive.persistence.facade.ScoreFacade;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.*;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(CdiTestRunner.class)
public class ScoreServiceTest {
    @Inject
    private ScoreService targetClass;

    @Inject
    private ScoreFacade scoreFacade;

    @Test
    public void test_getScore_スコア情報を取得できる()throws Exception{
        long userId = 111111;
        ScoreEntity actualScore = targetClass.getScore(MasterGame.HONOCAR, userId);

        assertThat(actualScore.getUserId(),is(userId));
    }

    @Test
    public void test_getScore_ユーザーIDが存在しない場合例外が発生する()throws Exception{
        long notExistUserId = 25252;
        try{
            targetClass.getScore(MasterGame.HONOCAR, notExistUserId);
            fail();
        }catch (InvalidArgumentException ok){

        }
    }

    @Test
    public void test_getScore_スコアが未登録の場合例外が発生する()throws Exception{
        long notExistUserId = 999999;
        try{
            targetClass.getScore(MasterGame.SHAKARIN, notExistUserId);
            fail();
        }catch (NoResourceException ok){

        }
    }


    @Test
    public void test_getTop_ランキング上位のスコアリストを降順で取得できる()throws Exception{

        List<ScoreEntity> actualList = targetClass.getTops(MasterGame.HONOCAR);

        // 降順のリストである
        int preventPoint = Integer.MAX_VALUE;
        for (ScoreEntity score: actualList) {
            assertTrue(preventPoint >= score.getPoint());
            preventPoint = score.getPoint();
        }

        // 決められたリスト数のデータを返却している
        assertThat(actualList.size(), is(TestUtil.getPrivateField(ScoreService.class, "PRODUCE_NUMBER_OF_RANKING")));
    }

    @Test
    public void test_getRanking_順位を取得できる()throws Exception{

        List<ScoreEntity> scores = targetClass.getTops(MasterGame.HONOCAR);

        // １位
        assertTrue(targetClass.getRanking(MasterGame.HONOCAR, scores.get(0).getPoint()) == 1);

        // 最下位
        assertTrue(targetClass.getRanking(MasterGame.HONOCAR, scores.get(scores.size() -1 ).getPoint()) == scores.size());
    }


    /******************************************************
     * test management method
     */
    /** データベースのバックアップ */
    private static File backupFile;

    @BeforeClass
    public static void setUpClass() throws Exception{
        String[] backupTargetTables = {
                "USER",
                "SCORE",
                "GAME_LOG"
        };
        backupFile = TestUtil.createDatabaseBackupFile(backupTargetTables);
    }

    @Before
    public void setUp() throws Exception {
        TestUtil.importTestDataSet();
    }

    @After
    public void tearDown() throws Exception {

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        TestUtil.importBackupFileToDatabase(backupFile);
    }
}