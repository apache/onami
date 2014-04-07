package org.apache.onami.persist;

import com.google.inject.Provider;
import com.google.inject.Singleton;

import javax.transaction.UserTransaction;

import static org.apache.onami.persist.Preconditions.checkNotNull;

/**
 * Provider fro {@link UserTransaction} which retrieves the value from the JNDI context.
 */
@Singleton
public class UserTransactionProviderByJndiLookup
    implements Provider<UserTransaction>
{

    private final String jndiName;

    private final JndiLookupHelper jndiLookupHelper;

    /**
     * Constructor.
     *
     * @param jndiName jndi name of the entity manager factory. Must not be {@code null}.
     */
    UserTransactionProviderByJndiLookup( @UserTransactionJndiName String jndiName, JndiLookupHelper jndiLookupHelper )
    {
        this.jndiName = checkNotNull( jndiName, "jndiName is mandatory!" );
        this.jndiLookupHelper = checkNotNull( jndiLookupHelper, "jndiLookupHelper is mandatory!" );
    }

    /**
     * Gets a {@link UserTransaction} by looking it up in the JNDI context.
     *
     * @return the found entity user transaction
     * @throws RuntimeException when no user transaction was found.
     */
    //@Override
    public UserTransaction get()
    {
        return jndiLookupHelper.doJndiLookup( UserTransaction.class, jndiName );
    }

}
