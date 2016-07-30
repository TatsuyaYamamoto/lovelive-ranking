package net.sokontokoro_factory.lovelive.persistence.facade;

import net.sokontokoro_factory.lovelive.persistence.entity.ScoreEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;

@RequestScoped
public class ScoreFacade extends AbstractFacade<ScoreEntity>{
    @Inject
    private EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    public ScoreFacade() {
        super(ScoreEntity.class);
    }

    /**
     * scoreEntityを取得する
     *
     * @param gameId
     * @param userId
     * @return
     */
    public ScoreEntity findById(int gameId, long userId){

        Query jpql = getEntityManager().createQuery(
                "SELECT s FROM ScoreEntity s WHERE s.gameId = :gameId AND s.userId = :userId",
                ScoreEntity.class)
                .setParameter("userId", userId)
                .setParameter("gameId", gameId);

        try{
            return (ScoreEntity) jpql.getSingleResult();
        }catch(NoResultException e){
            return null;
        }
    }

    public int getRanking(Integer gameId, int borderPoint){
        Query query = getEntityManager().createQuery(
                "SELECT count(s) FROM ScoreEntity s WHERE s.gameId = :gameId AND s.point > :borderPoint")
                .setParameter("borderPoint", borderPoint)
                .setParameter("gameId", gameId);


        try{
            return (int) query.getSingleResult() + 1;
        }catch(NoResultException e){
            // 該当ないは、指定ゲームの登録スコアが0件、または、指定pointが現在1位の場合
            // 前者はこのメソッドで考慮しない
            return 1;
        }
    }

    /**
     * 指定ゲームの降順のScoreEntityのarrayを返す。(指定ゲームの各ユーザーの最高スコアを取得する)
     *
     * @param gameId
     * @param limit
     * @return
     */

    public List<ScoreEntity> getByGameId(Integer gameId, int limit) {
        Query jpql = getEntityManager().createQuery(
                "SELECT s.point FROM ScoreEntity s WHERE s.gameId = :gameId GROUP BY s.point ORDER BY s.point DESC",
                Integer.class)
                .setParameter("gameId", gameId)
                .setMaxResults(limit);


        List<Integer> upperPointList = jpql.getResultList();

        int borderPoint = upperPointList.get(upperPointList.size() - 1);

        Query query = getEntityManager().createQuery(
                "SELECT S FROM ScoreEntity s WHERE s.gameId = :gameId AND s.point >= :point ORDER BY s.point DESC",
                ScoreEntity.class);

        query.setParameter("gameId", gameId).setParameter("point", borderPoint);

        return query.getResultList();
    }
}