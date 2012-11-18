package org.nnsoft.guice.junice;

/*
 *    Copyright 2010-2012 The 99 Software Foundation
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.nnsoft.guice.junice.annotation.GuiceModules;
import org.nnsoft.guice.junice.annotation.GuiceProvidedModules;
import org.nnsoft.guice.junice.annotation.Mock;
import org.nnsoft.guice.junice.annotation.MockFramework;
import org.nnsoft.guice.junice.annotation.MockType;
import org.nnsoft.guice.junice.handler.GuiceInjectableClassHandler;
import org.nnsoft.guice.junice.handler.GuiceModuleHandler;
import org.nnsoft.guice.junice.handler.GuiceProvidedModuleHandler;
import org.nnsoft.guice.junice.handler.MockFrameworkHandler;
import org.nnsoft.guice.junice.handler.MockHandler;
import org.nnsoft.guice.junice.mock.MockEngine;
import org.nnsoft.guice.junice.mock.guice.MockTypeListener;
import org.nnsoft.guice.junice.reflection.ClassVisitor;
import org.nnsoft.guice.junice.reflection.HandleException;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.matcher.Matchers;
import com.google.inject.util.Modules;

/**
 * <p>
 * It's a {@link BlockJUnit4ClassRunner} runner.
 * </p>
 * <p>
 * This class creates a Google Guice {@link Injector} configured by {@link GuiceModules} annotation (only fr modules
 * with default constructor) and {@link GuiceProvidedModules} annotation and {@link Mock}.
 * </p>
 * <p>
 * <b>Example #1:</b> <br>
 * 
 * <pre>
 * 
 * &#064;RunWith(JUniceRunner.class)
 * &#064;GuiceModules(modules=SimpleModule.class)
 * public class AcmeTestCase {
 * 
 *     &#064;GuiceProvidedModules
 *     static public Module getProperties() {
 *         ...
 *         return Modules.combine(new ComplexModule(loadProperies()), ...  );
 *     }
 * 
 * </pre>
 * 
 * </p>
 * <p>
 * <b>Example #2:</b> <br>
 * 
 * <pre>
 * 
 * &#064;RunWith(JUniceRunner.class)
 * public class AcmeTestCase extends com.google.inject.AbstractModule {
 * 
 *     public void configure() {
 *         //Configure your proper modules
 *         ...
 *         bind(Service.class).annotatedWith(TestAnnotation.class).to(ServiceTestImpl.class);
 *         ...
 *     }
 * 
 *     &#064;Mock
 *     private AnotherService serviceMock;
 * 
 *     &#064;Inject
 *     private Service serviceTest;
 * 
 *     &#064;org.junit.Test
 *     public void test() {
 *         assertNotNull(serviceMock);
 *         assertNotNull(serviceTest);
 *     }
 * </pre>
 * 
 * </p>
 * 
 * @see GuiceMockModule
 */
public class JUniceRunner
    extends BlockJUnit4ClassRunner
{

    final private static Logger logger = Logger.getLogger( JUniceRunner.class.getName() );

    private Injector injector;

    final private List<Module> allModules;

    final private Map<Field, Object> mocked = new HashMap<Field, Object>( 1 );

    private MockType mockFramework = MockType.EASY_MOCK;

    /**
     * JUniceRunner constructor to create the core JUnice class.
     * 
     * @see RunWith
     * @param klass The test case class to run.
     * @throws org.junit.runners.model.InitializationError if any error occurs.
     */
    public JUniceRunner( Class<?> klass )
        throws InitializationError
    {
        super( klass );

        try
        {
            if ( logger.isLoggable( Level.FINER ) )
            {
                logger.finer( "Inizializing JUniceRunner for class: " + klass.getSimpleName() );
            }

            this.allModules = inizializeInjector( klass );

            if ( logger.isLoggable( Level.FINER ) )
            {
                logger.finer( "done..." );
            }
        }
        catch ( Exception e )
        {
            final List<Throwable> throwables = new ArrayList<Throwable>( 1 );
            throwables.add( e );
            throw new InitializationError( throwables );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void run( final RunNotifier notifier )
    {
        if ( logger.isLoggable( Level.FINER ) )
        {
            logger.finer( " ### Run test case: " + getTestClass().getJavaClass() + " ### " );
            logger.finer( " #### Creating injector ####" );
        }

        this.injector = createInjector( allModules );
        super.run( notifier );
        this.flush();

        if ( logger.isLoggable( Level.FINER ) )
        {
            logger.finer( " ### End test case: " + getTestClass().getJavaClass().getName() + " ### " );
        }
    }

    /**
     * {@inheritDoc}
     */
    private void flush()
    {
        this.injector = null;
        this.allModules.clear();
        this.mocked.clear();
    }

    @Override
    protected void runChild( FrameworkMethod method, RunNotifier notifier )
    {
        if ( logger.isLoggable( Level.FINER ) )
        {
            logger.finer( " +++ invoke test method: " + method.getName() + " +++ " );
        }

        super.runChild( method, notifier );
        resetAllResetAfterMocks();

        if ( logger.isLoggable( Level.FINER ) )
        {
            logger.finer( " --- end test method: " + method.getName() + " --- " );
        }

    }

    // create test class via Google-Guice to inject all not-static dependencies.
    protected Object createTest()
        throws Exception
    {
        if ( logger.isLoggable( Level.FINER ) )
        {
            logger.finer( " Create and inject test class: " + getTestClass().getJavaClass() );
        }
        return this.injector.getInstance( getTestClass().getJavaClass() );
    }

    protected Injector createInjector( List<Module> modules )
    {

        return Guice.createInjector( modules );
    }

    /**
     * <p>
     * Initialize the main Injector.
     * </p>
     * <p>
     * This methot collects modules from {@link GuiceModules}, {@link GuiceProvidedModules}, {@link Mock}, creates a
     * Google-Guice Injector and than inject static members into callings class.
     * </p>
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws HandleException
     */
    protected <T> List<Module> inizializeInjector( Class<T> clazz )
        throws HandleException, InstantiationException, IllegalAccessException
    {
        final List<Module> modules = new ArrayList<Module>();
        Module m = visitClass( clazz );
        if ( m != null )
        {
            modules.add( m );
        }
        return modules;
    }

    private void resetAllResetAfterMocks()
    {
        for ( Entry<Field, Object> entry : mocked.entrySet() )
        {
            final Mock mockAnnotation = entry.getKey().getAnnotation( Mock.class );
            if ( mockAnnotation.resetAfter() )
            {
                MockEngine mockEngine = MockEngineFactory.getMockEngine( mockFramework );
                mockEngine.resetMock( entry.getValue() );
            }
        }
    }

    /**
     * @throws HandleException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private <T> Module visitClass( final Class<T> clazz )
        throws HandleException, InstantiationException, IllegalAccessException
    {
        try
        {
            if ( logger.isLoggable( Level.FINER ) )
            {
                logger.finer( "  Start introspecting class: " + clazz.getName() );
            }
            final List<Module> allModules = new ArrayList<Module>( 1 );

            // Setup the handlers
            final GuiceProvidedModuleHandler guiceProvidedModuleHandler = new GuiceProvidedModuleHandler();
            final GuiceModuleHandler guiceModuleHandler = new GuiceModuleHandler();
            final GuiceInjectableClassHandler<Inject> guiceInjectableClassHandler = new GuiceInjectableClassHandler<Inject>();
            final GuiceInjectableClassHandler<javax.inject.Inject> jsr330InjectableClassHandler = new GuiceInjectableClassHandler<javax.inject.Inject>();

            final MockHandler mockHandler = new MockHandler();
            final MockFrameworkHandler mockFrameworkHandler = new MockFrameworkHandler();

            // Visit class and super-classes
            new ClassVisitor()
            .registerHandler( GuiceProvidedModules.class, guiceProvidedModuleHandler )
            .registerHandler( GuiceModules.class, guiceModuleHandler )
            .registerHandler( Mock.class, mockHandler )
            .registerHandler( MockFramework.class, mockFrameworkHandler )
            .registerHandler( Inject.class, guiceInjectableClassHandler )
            .registerHandler( javax.inject.Inject.class, jsr330InjectableClassHandler )
            .visit( clazz );

            // Retrieve mock framework
            if ( mockFrameworkHandler.getMockType() != null )
            {
                this.mockFramework = mockFrameworkHandler.getMockType();
            }

            // retrieve the modules founded
            allModules.addAll( guiceProvidedModuleHandler.getModules() );
            allModules.addAll( guiceModuleHandler.getModules() );
            MockEngine engine = MockEngineFactory.getMockEngine( this.mockFramework );
            this.mocked.putAll( mockHandler.getMockedObject( engine ) );
            if ( !this.mocked.isEmpty() )
            {
                // Replace all real module binding with Mocked moduled.
                Module m = Modules.override( allModules ).with( new GuiceMockModule( this.mocked ) );
                allModules.clear();
                allModules.add( m );
            }

            // Add only clasess that have got the Inject annotation
             final Class<?>[] guiceInjectableClasses = guiceInjectableClassHandler.getClasses();
             final Class<?>[] jsr330InjectableClasses = jsr330InjectableClassHandler.getClasses();

            final AbstractModule statcInjector = new AbstractModule()
            {
                @Override
                protected void configure()
                {
                    // inject all STATIC dependencies
                    if ( guiceInjectableClasses.length != 0 )
                    {
                        requestStaticInjection( guiceInjectableClasses );
                    }
                    
                    if ( jsr330InjectableClasses.length != 0 )
                    {
                        requestStaticInjection( jsr330InjectableClasses );
                    }

                    
                }
            };
            if ( guiceInjectableClasses.length != 0 || jsr330InjectableClasses.length != 0 )
            {
                allModules.add( statcInjector );
            }

            // Check if the class is itself a Google Module.
            if ( Module.class.isAssignableFrom( getTestClass().getJavaClass() ) )
            {
                if ( logger.isLoggable( Level.FINER ) )
                {
                    logger.finer( "   creating module from test class " + getTestClass().getJavaClass() );
                }
                final Module classModule = (Module) getTestClass().getJavaClass().newInstance();
                allModules.add( classModule );
            }

            // create MockTypeListenerModule
            if ( this.mocked.size() != 0 )
            {
                final AbstractModule mockTypeListenerModule = new AbstractModule()
                {
                    @Override
                    protected void configure()
                    {
                        bindListener( Matchers.any(), new MockTypeListener( mocked ) );
                    }
                };

                // BEGIN patch for issue: google-guice: #452
                for ( Entry<Field, Object> entry : mocked.entrySet() )
                {
                    final Field field = entry.getKey();
                    final Object mock = entry.getValue();
                    if ( Modifier.isStatic( field.getModifiers() ) )
                    {
                        if ( logger.isLoggable( Level.FINER ) )
                        {
                            logger.finer( "   inject static mock field: " + field.getName() );
                        }

                        field.setAccessible( true );
                        field.set( field.getDeclaringClass(), mock );
                    }
                }
                // END patch for issue: google-guice: #452

                allModules.add( mockTypeListenerModule );
            }

            if ( allModules.size() != 0 )
            {
                if ( logger.isLoggable( Level.FINER ) )
                {
                    StringBuilder builder = new StringBuilder();
                    builder.append( " Collected modules: " );
                    builder.append( "\n" );
                    for ( Module module : allModules )
                    {
                        builder.append( "    " + module );
                        builder.append( "\n" );
                    }
                    logger.finer( builder.toString() );
                }
                return Modules.combine( allModules );
            }
            return null;
        }
        finally
        {
            if ( logger.isLoggable( Level.FINER ) )
            {
                logger.finer( " ...done" );
            }
        }
    }

}
