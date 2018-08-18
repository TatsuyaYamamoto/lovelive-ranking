package net.sokontokoro_factory.lovelive.persistence.facade;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import net.sokontokoro_factory.lovelive.persistence.entity.GameLogEntity;

@RequestScoped
public class GameLogFacade extends AbstractFacade<GameLogEntity> {
  @Inject private EntityManager entityManager;

  @Override
  public EntityManager getEntityManager() {
    return entityManager;
  }

  public GameLogFacade() {
    super(GameLogEntity.class);
  }
}
