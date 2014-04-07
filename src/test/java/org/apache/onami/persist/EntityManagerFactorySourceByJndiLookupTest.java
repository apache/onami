package org.apache.onami.persist;

import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManagerFactory;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class EntityManagerFactorySourceByJndiLookupTest
{

    public static final String JNDI_NAME = "jndiName";

    private EntityManagerFactorySourceByJndiLookup sut;

    private JndiLookupHelper jndiLookupHelper;

    @Before
    public void setUp()
        throws Exception
    {
        jndiLookupHelper = mock( JndiLookupHelper.class );
        sut = new EntityManagerFactorySourceByJndiLookup( JNDI_NAME, jndiLookupHelper );
    }


    @Test
    public void shouldLookupEmfByJndiName()
        throws Exception
    {
        // given
        final EntityManagerFactory emf = mock( EntityManagerFactory.class );
        doReturn( emf ).when( jndiLookupHelper ).doJndiLookup( EntityManagerFactory.class, JNDI_NAME );
        // when
        final EntityManagerFactory result = sut.getEntityManagerFactory();
        // then
        assertThat( result, sameInstance( emf ) );
    }

    @Test(expected = NullPointerException.class)
    public void jndiNameIsMandatory()
    {
        new EntityManagerFactorySourceByJndiLookup( null, jndiLookupHelper );
    }

    @Test(expected = NullPointerException.class)
    public void jndiLookupHelperIsMandatory()
    {
        new EntityManagerFactorySourceByJndiLookup( JNDI_NAME, null );
    }

}
