package net.sokontokoro_factory.lovelive.persistence.facade;

import net.sokontokoro_factory.lovelive.persistence.entity.UserEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

@RequestScoped
public class UserFacade extends AbstractFacade<UserEntity>{
    @Inject
    private EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    public UserFacade() {
        super(UserEntity.class);
    }

}