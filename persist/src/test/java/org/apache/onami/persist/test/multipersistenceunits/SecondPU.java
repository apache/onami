package org.apache.onami.persist.test.multipersistenceunits;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

@Retention( RetentionPolicy.RUNTIME )
@Target( { FIELD, PARAMETER, METHOD } )
@Qualifier
public @interface SecondPU
{
}
