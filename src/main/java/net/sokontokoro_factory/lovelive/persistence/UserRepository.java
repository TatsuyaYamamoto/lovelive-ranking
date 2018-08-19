package net.sokontokoro_factory.lovelive.persistence;

import net.sokontokoro_factory.lovelive.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
