package org.apache.onami.persist;

import java.lang.annotation.Annotation;

public class AnnotationHolder
{

    private final Class<? extends Annotation> annotation;

    AnnotationHolder( Class<? extends Annotation> annotation )
    {
        this.annotation = annotation;
    }

    Class<? extends Annotation> getAnnotation() {
        return annotation;
    }
}
