/*
 * Copyright 2019 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package tech.pegasys.artemis.networking.p2p;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import io.vertx.core.Vertx;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import net.consensys.cava.concurrent.AsyncCompletion;
import net.consensys.cava.crypto.SECP256K1;
import net.consensys.cava.rlpx.MemoryWireConnectionsRepository;
import net.consensys.cava.rlpx.WireConnectionRepository;
import net.consensys.cava.rlpx.vertx.VertxRLPxService;
import net.consensys.cava.rlpx.wire.WireConnection;
import org.logl.log4j2.Log4j2LoggerProvider;
import tech.pegasys.artemis.networking.p2p.api.P2PNetwork;
import tech.pegasys.artemis.storage.ChainStorageClient;

/**
 * Peer to peer network for beacon nodes, over a RLPx connection.
 *
 * <p>This service exposes services over the subprotocol "bea".
 *
 * @see BeaconSubprotocolHandler
 */
public final class RLPxP2PNetwork implements P2PNetwork {

  private static final long CHAIN_ID = 1;

  private final AtomicBoolean started = new AtomicBoolean(false);
  private final EventBus eventBus;
  private final Vertx vertx;
  private final SECP256K1.KeyPair keyPair;
  private final int port;
  private final int advertisedPort;
  private final String networkInterface;
  private final Log4j2LoggerProvider loggerProvider;
  private final long networkId;
  private final List<URI> peers;

  private ChainStorageClient client;
  private WireConnectionRepository wireConnectionRepository;
  private VertxRLPxService service;

  /**
   * Default constructor.
   *
   * @param eventBus the event bus
   * @param vertx Vert.x instance to manage network
   * @param client the client storage
   * @param keyPair the identity of the node on network
   * @param port the port to bind to
   * @param advertisedPort the port to advertise
   * @param networkInterface the network interface to bind to
   * @param networkId the network ID to advertise to other peers
   * @param staticPeers the list of peers to connect to on startup
   */
  public RLPxP2PNetwork(
      EventBus eventBus,
      Vertx vertx,
      SECP256K1.KeyPair keyPair,
      int port,
      int advertisedPort,
      String networkInterface,
      long networkId,
      List<URI> staticPeers) {
    this.eventBus = eventBus;
    this.vertx = vertx;
    this.keyPair = keyPair;
    this.port = port;
    this.advertisedPort = advertisedPort;
    this.networkInterface = networkInterface;
    this.loggerProvider = new Log4j2LoggerProvider();
    this.networkId = networkId;
    this.peers = staticPeers;
    eventBus.register(this);
  }

  @Subscribe
  public void setStorage(ChainStorageClient client) {
    this.client = client;
  }

  @Override
  public void run() {
    if (started.compareAndSet(false, true)) {
      wireConnectionRepository = new MemoryWireConnectionsRepository();
      service =
          new VertxRLPxService(
              vertx,
              loggerProvider,
              port,
              networkInterface,
              advertisedPort,
              keyPair,
              Collections.singletonList(new BeaconSubprotocol(client, networkId, CHAIN_ID)),
              "Artemis 0.1");
      try {
        service.start().join(10, TimeUnit.SECONDS);
        for (URI peer : peers) {
          SECP256K1.PublicKey publicKey = SECP256K1.PublicKey.fromHexString(peer.getUserInfo());
          service.connectTo(publicKey, new InetSocketAddress(peer.getHost(), peer.getPort()));
        }
      } catch (TimeoutException | InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public void stop() {
    if (started.compareAndSet(true, false)) {
      try {
        service.stop().join(10, TimeUnit.SECONDS);
      } catch (TimeoutException | InterruptedException e) {
        throw new RuntimeException(e);
      } finally {
        vertx.close();
        eventBus.unregister(this);
      }
    }
  }

  @Override
  public void close() throws IOException {
    stop();
  }

  @Override
  public Collection<?> getPeers() {
    if (!started.get()) {
      throw new IllegalStateException();
    }
    List<String> peers = new ArrayList<>();
    for (WireConnection conn : wireConnectionRepository.asIterable()) {
      peers.add(conn.id());
    }
    return peers;
  }

  @Override
  public CompletableFuture<?> connect(String peer) {
    if (!started.get()) {
      throw new IllegalStateException();
    }
    URI enode = URI.create(peer);

    CompletableFuture<?> completableFuture = new CompletableFuture<>();

    AsyncCompletion completion =
        service.connectTo(
            SECP256K1.PublicKey.fromHexString(enode.getRawUserInfo()),
            new InetSocketAddress(enode.getHost(), enode.getPort()));
    completion.whenComplete(
        exception -> {
          if (exception != null) {
            completableFuture.completeExceptionally(exception);
          } else {
            completableFuture.complete(null);
          }
        });

    return completableFuture;
  }

  @Override
  public void subscribe(String event) {
    if (!started.get()) {
      throw new IllegalStateException();
    }
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isListening() {
    return started.get();
  }
}
