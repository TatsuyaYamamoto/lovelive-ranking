package net.sokontokoro_factory.lovelive.persistence;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerProducer {

    @Produces
    @RequestScoped
    public EntityManager create() {
        EntityManagerFactory fac = Persistence.createEntityManagerFactory("lovelive_PU");
        return fac.createEntityManager();
    }

    protected void closeEntityManager(@Disposes EntityManager entityManager) {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }
}