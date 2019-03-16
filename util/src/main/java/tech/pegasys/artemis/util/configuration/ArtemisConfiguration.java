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

package tech.pegasys.artemis;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import net.consensys.cava.config.Configuration;
import net.consensys.cava.config.PropertyValidator;
import net.consensys.cava.config.Schema;
import net.consensys.cava.config.SchemaBuilder;

final class ArtemisConfiguration {

  private final Configuration config;

  ArtemisConfiguration(Configuration config) {
    this.config = config;
  }

  static final Schema createSchema() {
    SchemaBuilder builder =
        SchemaBuilder.create()
            .addString("identity", null, "Identity of the peer", PropertyValidator.isPresent());
    builder.addString("networkInterface", "0.0.0.0", "Peer to peer network interface", null);
    builder.addInteger("port", 9000, "Peer to peer port", PropertyValidator.inRange(0, 65535));
    builder.addInteger(
        "advertisedPort",
        9000,
        "Peer to peer advertised port",
        PropertyValidator.inRange(0, 65535));
    builder.addInteger(
        "numValidators",
        128,
        "represents the total number of validators in the network",
        PropertyValidator.inRange(1, 16384));
    builder.addInteger(
        "numNodes",
        1,
        "represents the total number of nodes on the network",
        PropertyValidator.inRange(1, 16384));
    builder.addListOfMap("peers", Collections.emptyList(), "Static peers", null);
    return builder.toSchema();
  }

  private static final Schema schema = createSchema();

  static ArtemisConfiguration fromFile(String path) {
    Path configPath = Paths.get(path);
    try {
      return new ArtemisConfiguration(Configuration.fromToml(configPath, schema));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public String getIdentity() {
    return config.getString("identity");
  }

  public int getPort() {
    return config.getInteger("port");
  }

  public int getAdvertisedPort() {
    return config.getInteger("advertisedPort");
  }

  public String getNetworkInterface() {
    return config.getString("networkInterface");
  }

  public int getNumValidators() {
    return config.getInteger("numValidators");
  }

  public int getNumNodes() {
    return config.getInteger("numNodes");
  }
}

