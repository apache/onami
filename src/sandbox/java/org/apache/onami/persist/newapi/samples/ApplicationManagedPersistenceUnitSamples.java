package org.apache.onami.persist.newapi.samples;

import com.google.inject.Module;
import org.apache.onami.persist.newapi.PersistenceModule;
import org.apache.onami.persist.newapi.PersistenceUnitModuleConfiguration;

public class ApplicationManagedPersistenceUnitSamples
{
    public static Module createPersistenceModule()
    {
        return new SimplesPersistenceModule();
    }

    public static Module createAnnotatedPersistenceModule()
    {
        return new AnnotatedPersistenceModule();
    }

    public static Module createAnnotatedAndConfiguredPersistenceModule()
    {
        return new AnnotatedAndConfiguredPersistenceModule();
    }

    private static class SimplesPersistenceModule
        extends PersistenceModule
    {
        @Override
        protected void configurePersistence()
        {
            addApplicationManagedPersistenceUnit( "samplePU" );
        }
    }

    private static class AnnotatedPersistenceModule
        extends PersistenceModule
    {
        @Override
        protected void configurePersistence()
        {
            addApplicationManagedPersistenceUnit( "samplePU" ).annotatedWith( PU1.class );
        }
    }

    private static class AnnotatedAndConfiguredPersistenceModule
        extends PersistenceModule
    {
        @Override
        protected void configurePersistence()
        {
            addApplicationManagedPersistenceUnit( "samplePU" ).annotatedWith( PU1.class ).configuredWith(
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
