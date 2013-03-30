package org.apache.onami.lifecycle.standard;

import org.apache.onami.lifecycle.core.StageHandler;
import org.apache.onami.lifecycle.core.Stageable;
import org.apache.onami.lifecycle.core.Stager;

import java.lang.annotation.Annotation;

class StagerWrapper<A extends Annotation> implements Stager<A>
{
    private final Disposer disposer;

    private final Class<A> stage;

    StagerWrapper( Disposer disposer, Class<A> stage )
    {
        this.disposer = disposer;
        this.stage = stage;
    }

    public void register( final Stageable stageable )
    {
        Disposable disposable = new Disposable()
        {
            public void dispose( final DisposeHandler disposeHandler )
            {
                StageHandler stageHandler = new StageHandler()
                {
                    public <I> void onSuccess( I injectee )
                    {
                        disposeHandler.onSuccess( injectee );
                    }

                    public <I, E extends Throwable> void onError( I injectee, E error )
                    {
                        disposeHandler.onError( injectee, error );
                    }
                };
                stageable.stage( stageHandler );
            }
        };
        disposer.register( disposable );
    }

    public void stage()
    {
        disposer.dispose();
    }

    public void stage( final StageHandler stageHandler )
    {
        DisposeHandler disposeHandler = new DisposeHandler()
        {
            public <I> void onSuccess( I injectee )
            {
                stageHandler.onSuccess( injectee );
            }

            public <I, E extends Throwable> void onError( I injectee, E error )
            {
                stageHandler.onError( injectee, error );
            }
        };
        disposer.dispose( disposeHandler );
    }

    public Class<A> getStage()
    {
        return stage;
    }
}
