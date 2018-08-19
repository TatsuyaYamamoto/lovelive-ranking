package net.sokontokoro_factory.lovelive.domain.score;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Integer> {
  Optional<Score> findByGameAndUserId(GameType game, long userId);
}
