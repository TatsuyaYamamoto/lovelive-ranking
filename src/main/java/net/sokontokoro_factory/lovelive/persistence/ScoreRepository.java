package net.sokontokoro_factory.lovelive.persistence;

import net.sokontokoro_factory.lovelive.persistence.entity.ScoreEntity;
import net.sokontokoro_factory.lovelive.type.GameType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreRepository extends JpaRepository<ScoreEntity, Integer> {
  ScoreEntity findByGameAndUserId(GameType game, long userId);
}
