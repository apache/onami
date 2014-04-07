package org.apache.onami.persist;

import com.google.inject.Provider;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManagerFactory;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class EntityManagerFactorySourceViaProviderTest
{
    private EntityManagerFactorySourceViaProvider sut;

    private Provider<EntityManagerFactory> emfProvider;

    @Before
    @SuppressWarnings( "unchecked" )
    public void setUp()
        throws Exception
    {
        emfProvider = mock(Provider.class);
        sut = new EntityManagerFactorySourceViaProvider( emfProvider );
    }

    @Test
    public void shouldReturnValueFromProvider() {
        // given
        EntityManagerFactory emfDummy = mock(EntityManagerFactory.class);
        doReturn( emfDummy ).when( emfProvider ).get();
        // when
        final EntityManagerFactory result = sut.getEntityManagerFactory();
        // then
        assertThat(result, sameInstance(emfDummy));
    }
}
