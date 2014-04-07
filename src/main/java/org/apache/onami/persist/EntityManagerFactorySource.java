package org.apache.onami.persist;

import javax.persistence.EntityManagerFactory;

public interface EntityManagerFactorySource
{
    EntityManagerFactory getEntityManagerFactory();
}
