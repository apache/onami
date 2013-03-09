package org.apache.onami.lifecycle.core;

import com.google.inject.TypeLiteral;

import java.lang.annotation.Annotation;

class NoOpStageableTypeMapper<A extends Annotation> implements StageableTypeMapper<A>
{
    public <I> void registerType( Stageable stageable, TypeLiteral<I> parentType )
    {
        // NOP
    }
}
