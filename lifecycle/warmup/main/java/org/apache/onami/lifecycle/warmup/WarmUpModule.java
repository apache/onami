package org.apache.onami.lifecycle.warmup;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matcher;
import org.apache.onami.lifecycle.core.LifeCycleStageModule;

import java.lang.annotation.Annotation;

import static com.google.inject.matcher.Matchers.any;

public class WarmUpModule<A extends Annotation>
    extends AbstractModule
{
    private final LifeCycleStageModule<A> lifeCycleStageModule;

    public WarmUpModule( Class<A> stage )
    {
        this( stage, any() );
    }

    /**
     * Creates a new module which register methods annotated with input annotation on methods
     * in types filtered by the input matcher.
     *
     * @param stage       the <i>Dispose</i> annotation to be searched.
     * @param typeMatcher the filter for injectee types.
     */
    public WarmUpModule( Class<A> stage, Matcher<? super TypeLiteral<?>> typeMatcher )
    {
        WarmUper<A> stager = new WarmUper<A>( stage );
        lifeCycleStageModule =
            LifeCycleStageModule.builder( stage ).withTypeMatcher( typeMatcher ).withStager( stager ).withTypeMapper(
                stager ).build();
    }

    public static WarmUpModule<WarmUp> newWarmUpModule()
    {
        return new WarmUpModule<WarmUp>( WarmUp.class );
    }

    public static WarmUpModule<WarmUp> newWarmUpModule( Matcher<? super TypeLiteral<?>> typeMatcher )
    {
        return new WarmUpModule<WarmUp>( WarmUp.class, typeMatcher );
    }

    public static LifeCycleStageModule.Builder<WarmUp> builder()
    {
        WarmUper<WarmUp> stager = new WarmUper<WarmUp>( WarmUp.class );
        return LifeCycleStageModule.builder( WarmUp.class ).withStager( stager ).withTypeMapper( stager );
    }

    public static <A extends Annotation> LifeCycleStageModule.Builder<A> builder( Class<A> stage )
    {
        WarmUper<A> stager = new WarmUper<A>( stage );
        return LifeCycleStageModule.builder( stage ).withStager( stager ).withTypeMapper( stager );
    }

    @Override
    protected void configure()
    {
        binder().install( lifeCycleStageModule );
    }
}
