package net.sokontokoro_factory.lovelive.domain.log;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameLogRepository extends JpaRepository<GameLog, Integer> {
}
