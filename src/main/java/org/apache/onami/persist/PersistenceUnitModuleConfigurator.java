package org.apache.onami.persist;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import java.lang.annotation.Annotation;
import java.util.Properties;

class PersistenceUnitModuleConfigurator
    implements UnannotatedPersistenceUnitBuilder, AnnotatedPersistenceUnitBuilder, UnconfiguredPersistenceUnitBuilder
{
    private Class<? extends Annotation> annotation;

    private UserTransaction userTransaction;

    private String utJndiName;

    private Provider<UserTransaction> utProvider;

    private Key<? extends Provider<UserTransaction>> utProviderKey;

    private Properties properties;

    private String puName;

    private EntityManagerFactory emf;

    private String emfJndiName;

    private Provider<EntityManagerFactory> emfProvider;

    private Key<? extends Provider<EntityManagerFactory>> emfProviderKey;

    PersistenceUnitModule getPuModule()
    {
        return new PersistenceUnitModule( this );
    }

    public AnnotatedPersistenceUnitBuilder annotatedWith( Class<? extends Annotation> annotation )
    {
        this.annotation = annotation;
        return this;
    }

    public UnconfiguredPersistenceUnitBuilder useLocalTransaction()
    {
        // does nothing
        return this;
    }

    public UnconfiguredPersistenceUnitBuilder useGlobalTransaction( UserTransaction userTransaction )
    {
        this.userTransaction = userTransaction;
        return this;
    }

    public UnconfiguredPersistenceUnitBuilder useGlobalTransactionWithJndiName( String utJndiName )
    {
        this.utJndiName = utJndiName;
        return this;
    }

    public UnconfiguredPersistenceUnitBuilder useGlobalTransactionProvidedBy( Provider<UserTransaction> utProvider )
    {
        this.utProvider = utProvider;
        return this;
    }

    public UnconfiguredPersistenceUnitBuilder useGlobalTransactionProvidedBy(
        Class<? extends Provider<UserTransaction>> utProviderClass )
    {
        utProviderKey = Key.get( utProviderClass );
        return this;
    }

    public UnconfiguredPersistenceUnitBuilder useGlobalTransactionProvidedBy(
        TypeLiteral<? extends Provider<UserTransaction>> utProviderType )
    {
        utProviderKey = Key.get( utProviderType );
        return this;
    }

    public UnconfiguredPersistenceUnitBuilder useGlobalTransactionProvidedBy(
        Key<? extends Provider<UserTransaction>> utProviderKey )
    {
        this.utProviderKey = utProviderKey;
        return this;
    }

    public void addProperties( Properties properties )
    {
        this.properties = properties;
    }

    public void setPuName( String puName )
    {
        this.puName = puName;
    }

    public void setEmf( EntityManagerFactory emf )
    {
        this.emf = emf;
    }

    public void setEmfJndiName( String emfJndiName )
    {
        this.emfJndiName = emfJndiName;
    }

    public void setEmfProvider( Provider<EntityManagerFactory> emfProvider )
    {
        this.emfProvider = emfProvider;
    }

    public void setEmfProviderClass( Class<? extends Provider<EntityManagerFactory>> emfProviderClass )
    {
        this.emfProviderKey = Key.get( emfProviderClass );
    }

    public void setEmfProviderType( TypeLiteral<? extends Provider<EntityManagerFactory>> emfProviderType )
    {
        this.emfProviderKey = Key.get( emfProviderType );
    }

    public void setEmfProviderKey( Key<? extends Provider<EntityManagerFactory>> emfProviderKey )
    {
        this.emfProviderKey = emfProviderKey;
    }

    public boolean isApplicationManagedPersistenceUnit()
    {
        return puName != null;
    }


    UserTransaction getUserTransaction()
    {
        return userTransaction;
    }

    String getUtJndiName()
    {
        return utJndiName;
    }

    Provider<UserTransaction> getUtProvider()
    {
        return utProvider;
    }

    Key<? extends Provider<UserTransaction>> getUtProviderKey()
    {
        return utProviderKey;
    }

    Properties getProperties()
    {
        return properties;
    }

    String getPuName()
    {
        return puName;
    }

    EntityManagerFactory getEmf()
    {
        return emf;
    }

    String getEmfJndiName()
    {
        return emfJndiName;
    }

    Provider<EntityManagerFactory> getEmfProvider()
    {
        return emfProvider;
    }

    Key<? extends Provider<EntityManagerFactory>> getEmfProviderKey()
    {
        return emfProviderKey;
    }

    public boolean isEmfProvidedByJndiLookup()
    {
        return emfJndiName != null;
    }

    public boolean isEmfProvidedByInstance()
    {
        return emf != null;
    }

    public boolean isEmfProvidedByProvider()
    {
        return emfProvider != null;
    }

    public boolean isEmfProvidedByProviderKey()
    {
        return emfProviderKey != null;
    }

    public boolean isJta()
    {
        return utJndiName != null || userTransaction != null || utProvider != null || utProviderKey != null;
    }

    public boolean isUserTransactionProvidedByJndiLookup()
    {
        return utJndiName != null;
    }


    public boolean isUserTransactionProvidedByInstance()
    {
        return userTransaction != null;
    }

    public boolean isUserTransactionProvidedByProvider()
    {
        return utProvider != null;
    }

    public boolean isUserTransactionProvidedByProviderKey()
    {
        return utProviderKey != null;
    }

    public boolean isAnnotated()
    {
        return annotation != null;
    }

    public AnnotationHolder getAnnotationHolder()
    {
        return new AnnotationHolder( annotation );
    }

    Class<? extends Annotation> getAnnotation()
    {
        return annotation;
    }
}
