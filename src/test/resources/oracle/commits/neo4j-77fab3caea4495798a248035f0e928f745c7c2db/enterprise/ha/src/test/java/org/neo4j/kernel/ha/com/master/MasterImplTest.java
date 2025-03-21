/*
 * Copyright (c) 2002-2015 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.kernel.ha.com.master;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import org.neo4j.cluster.ClusterSettings;
import org.neo4j.com.RequestContext;
import org.neo4j.com.ResourceReleaser;
import org.neo4j.com.Response;
import org.neo4j.com.TransactionNotPresentOnMasterException;
import org.neo4j.com.TransactionObligationResponse;
import org.neo4j.kernel.configuration.Config;
import org.neo4j.kernel.ha.HaSettings;
import org.neo4j.kernel.ha.com.master.MasterImpl.Monitor;
import org.neo4j.kernel.ha.com.master.MasterImpl.SPI;
import org.neo4j.kernel.impl.locking.Locks.Client;
import org.neo4j.kernel.impl.locking.Locks.ResourceType;
import org.neo4j.kernel.impl.locking.ResourceTypes;
import org.neo4j.kernel.impl.store.StoreId;
import org.neo4j.kernel.impl.transaction.TransactionRepresentation;
import org.neo4j.kernel.impl.transaction.log.TransactionIdStore;
import org.neo4j.test.OtherThreadExecutor.WorkerCommand;
import org.neo4j.test.OtherThreadRule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MasterImplTest
{
    @Test
    public void givenStartedAndInaccessibleWhenNewLockSessionThrowException() throws Throwable
    {
        // Given
        MasterImpl.SPI spi = mock( MasterImpl.SPI.class );
        Config config = config( 20 );

        when( spi.isAccessible() ).thenReturn( false );

        MasterImpl instance = new MasterImpl( spi, mock( MasterImpl.Monitor.class ), config );
        instance.start();

        // When
        try
        {
            instance.newLockSession( new RequestContext( 0, 1, 2, 0, 0 ) );
            fail();
        }
        catch ( org.neo4j.kernel.api.exceptions.TransactionFailureException e )
        {
            // Ok
        }
    }

    @Test
    public void givenStartedAndAccessibleWhenNewLockSessionThenSucceeds() throws Throwable
    {
        // Given
        MasterImpl.SPI spi = mockedSpi();
        Config config = config( 20 );

        when( spi.isAccessible() ).thenReturn( true );
        when( spi.getTransactionChecksum( anyLong() ) ).thenReturn( 1L );

        MasterImpl instance = new MasterImpl( spi, mock( MasterImpl.Monitor.class ), config );
        instance.start();
        HandshakeResult handshake = instance.handshake( 1, new StoreId() ).response();

        // When
        try
        {
            instance.newLockSession( new RequestContext( handshake.epoch(), 1, 2, 0, 0 ) );
        }
        catch ( Exception e )
        {
            fail( e.getMessage() );
        }
    }

    @Test
    public void failingToStartTxShouldNotLeadToNPE() throws Throwable
    {
        // Given
        MasterImpl.SPI spi = mockedSpi();
        Config config = config( 20 );

        when( spi.isAccessible() ).thenReturn( true );
        when( spi.acquireClient() ).thenThrow( new RuntimeException( "Nope" ) );
        when( spi.getTransactionChecksum( anyLong() ) ).thenReturn( 1L );
        mockEmptyResponse( spi );

        MasterImpl instance = new MasterImpl( spi, mock( MasterImpl.Monitor.class ), config );
        instance.start();
        Response<HandshakeResult> response = instance.handshake( 1, new StoreId() );
        HandshakeResult handshake = response.response();

        // When
        try
        {
            instance.newLockSession( new RequestContext( handshake.epoch(), 1, 2, 0, 0 ) );
            fail("Should have failed.");
        }
        catch ( Exception e )
        {
            // Then
            assertThat(e, instanceOf( RuntimeException.class ) );
            assertThat(e.getMessage(), equalTo( "Nope" ));
        }
    }

    private void mockEmptyResponse( SPI spi )
    {
        when( spi.packEmptyResponse( any() ) ).thenAnswer( new Answer()
        {
            @Override
            public Object answer( InvocationOnMock invocation ) throws Throwable
            {
                return new TransactionObligationResponse<>( invocation.getArguments()[0], StoreId.DEFAULT,
                        TransactionIdStore.BASE_TX_ID, ResourceReleaser.NO_OP );
            }
        } );
    }

    @Test
    public void shouldNotEndLockSessionWhereThereIsAnActiveLockAcquisition() throws Throwable
    {
        // GIVEN
        final CountDownLatch latch = new CountDownLatch( 1 );
        try
        {
            MasterImpl.SPI spi = mockedSpi();
            when( spi.isAccessible() ).thenReturn( true );
            Client client = mock( Client.class );
            doAnswer( new Answer<Void>()
            {
                @Override
                public Void answer( InvocationOnMock invocation ) throws Throwable
                {
                    latch.await();
                    return null;
                }
            } ).when( client ).acquireExclusive( any( ResourceType.class ), anyLong() );
            when( spi.acquireClient() ).thenReturn( client );
            Config config = config( 20 );
            final MasterImpl master = new MasterImpl( spi, mock( Monitor.class ), config, 20 );
            master.start();
            HandshakeResult handshake = master.handshake( 1, new StoreId() ).response();

            // WHEN
            final RequestContext context = new RequestContext( handshake.epoch(), 1, 2, 0, 0 );
            master.newLockSession( context );
            Future<Void> acquireFuture = otherThread.execute( new WorkerCommand<Void, Void>()
            {
                @Override
                public Void doWork( Void state ) throws Exception
                {
                    master.acquireExclusiveLock( context, ResourceTypes.NODE, 1L );
                    return null;
                }
            } );
            otherThread.get().waitUntilWaiting();
            master.endLockSession( context, false );
            verify( client, times( 0 ) ).close();
            latch.countDown();
            acquireFuture.get();

            // THEN
            verify( client, times( 1 ) ).close();
        }
        finally
        {
            latch.countDown();
        }
    }

    @Test
    public void shouldNotAllowCommitIfThereIsNoMatchingLockSession() throws Throwable
    {
        // Given
        MasterImpl.SPI spi = mock( MasterImpl.SPI.class );
        Config config = config( 20 );

        when( spi.isAccessible() ).thenReturn( true );
        when( spi.getTransactionChecksum( anyLong() ) ).thenReturn( 1L );
        mockEmptyResponse( spi );

        MasterImpl master = new MasterImpl( spi, mock( MasterImpl.Monitor.class ), config );
        master.start();
        HandshakeResult handshake = master.handshake( 1, new StoreId() ).response();

        RequestContext ctx = new RequestContext( handshake.epoch(), 1, 2, 0, 0 );

        // When
        try
        {
            master.commit( ctx, mock( TransactionRepresentation.class ) );
            fail("Should have failed.");
        }
        catch ( TransactionNotPresentOnMasterException e )
        {
            // Then
            assertThat(e.getMessage(), equalTo( new TransactionNotPresentOnMasterException( ctx ).getMessage() ));
        }
    }

    @Test
    public void shouldAllowCommitIfClientHoldsNoLocks() throws Throwable
    {
        // Given
        MasterImpl.SPI spi = mock( MasterImpl.SPI.class );
        Config config = config( 20 );

        Client locks = mock( Client.class );
        when(locks.trySharedLock( ResourceTypes.SCHEMA, ResourceTypes.schemaResource() ) ).thenReturn( true );

        when( spi.isAccessible() ).thenReturn( true );
        when( spi.getTransactionChecksum( anyLong() ) ).thenReturn( 1L );
        when( spi.acquireClient()).thenReturn( locks );
        mockEmptyResponse( spi );

        MasterImpl master = new MasterImpl( spi, mock( MasterImpl.Monitor.class ), config );
        master.start();
        HandshakeResult handshake = master.handshake( 1, new StoreId() ).response();

        int no_lock_session = -1;
        RequestContext ctx = new RequestContext( handshake.epoch(), 1, no_lock_session, 0, 0 );
        TransactionRepresentation tx = mock( TransactionRepresentation.class );

        // When
        master.commit( ctx, tx );

        // Then
        verify(spi).applyPreparedTransaction( tx );
    }

    public final @Rule OtherThreadRule<Void> otherThread = new OtherThreadRule<>();

    private Config config( int lockReadTimeout )
    {
        Map<String, String> params = new HashMap<>();
        params.put( HaSettings.lock_read_timeout.name(), lockReadTimeout + "s" );
        params.put( ClusterSettings.server_id.name(), "1" );
        return new Config( params, HaSettings.class );
    }

    public static SPI mockedSpi()
    {
        return mockedSpi( StoreId.DEFAULT );
    }

    public static SPI mockedSpi( final StoreId storeId )
    {
        MasterImpl.SPI mock = mock( MasterImpl.SPI.class );
        when( mock.storeId() ).thenReturn( storeId );
        when( mock.packEmptyResponse( any() ) ).thenAnswer( new Answer()
        {
            @Override
            public Object answer( InvocationOnMock invocation ) throws Throwable
            {
                return new TransactionObligationResponse<>( invocation.getArguments()[0], storeId,
                        TransactionIdStore.BASE_TX_ID, ResourceReleaser.NO_OP );
            }
        } );
        return mock;
    }
}
