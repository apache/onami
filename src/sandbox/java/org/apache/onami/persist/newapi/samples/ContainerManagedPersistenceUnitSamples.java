package org.apache.onami.persist.newapi.samples;

import com.google.inject.Module;
import org.apache.onami.persist.newapi.PersistenceModule;
import org.apache.onami.persist.newapi.PersistenceUnitModuleConfiguration;

public class ContainerManagedPersistenceUnitSamples
{
    public static Module createPersistenceModule()
    {
        return new SimplesPersistenceModule();
    }

    public static Module createAnnotatedPersistenceModuleUsing()
    {
        return new AnnotatedPersistenceModule();
    }

    public static Module createAnnotatedPersistenceModuleUsingUsingGlobalTransactions()
    {
        return new AnnotatedPersistenceModuleUsingGlobalTransactions();
    }

    public static Module createAnnotatedAndConfiguredPersistenceModule()
    {
        return new AnnotatedAndConfiguredPersistenceModule();
    }

    public static Module createAnnotatedAndConfiguredPersistenceModuleUsingGlobalTransactions()
    {
        return new AnnotatedAndConfiguredPersistenceModule();
    }

    private static class SimplesPersistenceModule
        extends PersistenceModule
    {
        @Override
        protected void configurePersistence()
        {
            addContainerManagedPersistenceUnitWithJndiName( "sampleJndiName" );
        }
    }

    private static class AnnotatedPersistenceModule
        extends PersistenceModule
    {
        @Override
        protected void configurePersistence()
        {
            addContainerManagedPersistenceUnitWithJndiName( "samplePU" ).annotatedWith( PU1.class );
        }
    }

    private static class AnnotatedPersistenceModuleUsingGlobalTransactions
        extends PersistenceModule
    {
        @Override
        protected void configurePersistence()
        {
            addContainerManagedPersistenceUnitWithJndiName( "samplePU" ).annotatedWith(
                PU1.class ).useGlobalTransactionWithJndiName( "sampleUtJndiName" );
        }
    }

    private static class AnnotatedAndConfiguredPersistenceModule
        extends PersistenceModule
    {
        @Override
        protected void configurePersistence()
        {
            addContainerManagedPersistenceUnitWithJndiName( "samplePU" ).annotatedWith( PU1.class ).configuredWith(
                new PersistenceConfiguration() );
        }
    }

    private static class AnnotatedAndConfiguredPersistenceModuleUsingGlobalTransactions
        extends PersistenceModule
    {
        @Override
        protected void configurePersistence()
        {
            addContainerManagedPersistenceUnitWithJndiName( "samplePU" ).annotatedWith(
                PU1.class ).useGlobalTransactionWithJndiName( "sampleUtJndiName" ).configuredWith(
                new PersistenceConfiguration() );
        }
    }

    private static class PersistenceConfiguration
        extends PersistenceUnitModuleConfiguration
    {
        @Override
        protected void configure()
        {
            bindAndExpose( Object.class ).toInstance( new Object() );
        }
    }
}
