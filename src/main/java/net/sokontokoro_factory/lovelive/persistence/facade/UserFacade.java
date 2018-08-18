package net.sokontokoro_factory.lovelive.persistence.facade;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import net.sokontokoro_factory.lovelive.persistence.entity.UserEntity;

@RequestScoped
public class UserFacade extends AbstractFacade<UserEntity> {
  @Inject private EntityManager entityManager;

  @Override
  public EntityManager getEntityManager() {
    return entityManager;
  }

  public UserFacade() {
    super(UserEntity.class);
  }

  /** IDに対応したEntityが存在する場合、trueを返す レコードにない、論理削除済みの場合はfalse */
  @Override
  public boolean isExist(Object id) {
    UserEntity result = getEntityManager().find(UserEntity.class, id);

    if (result == null || result.isDeleted()) {
      return false;
    } else {
      return true;
    }
  }
}
