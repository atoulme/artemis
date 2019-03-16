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

package tech.pegasys.artemis.services;

import com.google.common.eventbus.EventBus;
import io.vertx.core.Vertx;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import tech.pegasys.artemis.networking.p2p.RLPxP2PNetwork;
import tech.pegasys.artemis.util.cli.CommandLineArguments;
import tech.pegasys.artemis.util.configuration.ArtemisConfiguration;

public class ServiceController {

  private ServiceInterface beaconChainService;
  private ServiceInterface powchainService;
  private ServiceInterface chainStorageService;

  private final ExecutorService beaconChainExecuterService = Executors.newSingleThreadExecutor();
  private final ExecutorService powchainExecuterService = Executors.newSingleThreadExecutor();
  private final ExecutorService chainStorageExecutorService = Executors.newSingleThreadExecutor();
  private RLPxP2PNetwork network;

  // initialize/register all services
  public <U extends ServiceInterface, V extends ServiceInterface, W extends ServiceInterface>
      void initAll(
          EventBus eventBus,
          CommandLineArguments cliArgs,
          ArtemisConfiguration config,
          Vertx vertx,
          Class<U> beaconChainServiceType,
          Class<V> powchainServiceType,
          Class<W> chainStorageServiceType) {
    try {
      beaconChainService = beaconChainServiceType.getDeclaredConstructor().newInstance();
      powchainService = powchainServiceType.getDeclaredConstructor().newInstance();
      chainStorageService = chainStorageServiceType.getDeclaredConstructor().newInstance();
      network =
          new RLPxP2PNetwork(
              eventBus,
              vertx,
              config.getKeyPair(),
              config.getPort(),
              config.getAdvertisedPort(),
              config.getNetworkInterface(),
              config.getNetworkID(),
              config.getStaticPeers());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    beaconChainService.init(eventBus, config);
    powchainService.init(eventBus);
    chainStorageService.init(eventBus);
  }

  public void startAll(CommandLineArguments cliArgs) {
    // start all services
    beaconChainExecuterService.execute(beaconChainService);
    powchainExecuterService.execute(powchainService);
    chainStorageExecutorService.execute(chainStorageService);
  }

  public void stopAll(CommandLineArguments cliArgs) {
    // stop all services
    beaconChainExecuterService.shutdown();
    beaconChainService.stop();
    powchainExecuterService.shutdown();
    powchainService.stop();
    chainStorageExecutorService.shutdown();
    chainStorageService.stop();
  }
}
