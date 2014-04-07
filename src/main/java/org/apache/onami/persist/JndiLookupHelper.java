package org.apache.onami.persist;

import com.google.inject.Singleton;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Helper class which does a JNDI lookup and handles exceptions.
 */
@Singleton
public class JndiLookupHelper
{

    /**
     * Does the actual JNDI lookup.
     *
     * @param type     type of the object to lookup
     * @param jndiName name of the object to lookup
     * @param <T>      type of the object to lookup
     * @return the object provided by the JNDI context.
     */
    @SuppressWarnings( "unchecked" )
    <T> T doJndiLookup( Class<T> type, String jndiName )
    {
        try
        {
            final InitialContext ctx = new InitialContext();
            final T emf = (T) ctx.lookup( jndiName );

            Preconditions.checkNotNull( emf, "lookup for " + type.getSimpleName() + " with JNDI name '" + jndiName
                + "' returned null" );

            return emf;
        }
        catch ( NamingException e )
        {
            throw new RuntimeException(
                "lookup for " + type.getSimpleName() + " with JNDI name '" + jndiName + "' failed", e );
        }
    }
}
