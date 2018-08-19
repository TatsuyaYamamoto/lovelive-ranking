package net.sokontokoro_factory.lovelive.persistence;

import net.sokontokoro_factory.lovelive.persistence.entity.GameLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameLogRepository extends JpaRepository<GameLogEntity, Integer> {
}
