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

package tech.pegasys.artemis.services.chainstorage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.charset.StandardCharsets;
import net.consensys.cava.bytes.Bytes;
import net.consensys.cava.bytes.Bytes32;
import net.consensys.cava.bytes.Bytes48;
import net.consensys.cava.concurrent.AsyncCompletion;
import net.consensys.cava.concurrent.AsyncResult;
import net.consensys.cava.kv.KeyValueStore;
import net.consensys.cava.kv.MapKeyValueStore;
import tech.pegasys.artemis.datastructures.beaconchainblocks.BeaconBlock;
import tech.pegasys.artemis.state.InterfaceAdapter;

public class BlockRepository {

  private static final Gson gson =
      new GsonBuilder()
          .registerTypeAdapter(Bytes32.class, new InterfaceAdapter<Bytes32>())
          .registerTypeAdapter(Bytes48.class, new InterfaceAdapter<Bytes48>())
          .create();

  private final KeyValueStore keyValueStore;

  public BlockRepository() {
    keyValueStore = MapKeyValueStore.open();
  }

  public AsyncCompletion store(BeaconBlock beaconBlock) {
    String bytes = gson.toJson(beaconBlock);

    return keyValueStore.putAsync(
        Bytes.concatenate(beaconBlock.getSignature()),
        Bytes.wrap(bytes.getBytes(StandardCharsets.UTF_8)));
  }

  public AsyncResult<BeaconBlock> retrieve(Bytes48[] beaconBlockSignature) {
    return keyValueStore
        .getAsync(Bytes.concatenate(beaconBlockSignature))
        .thenApply(
            bytes ->
                gson.fromJson(
                    new String(bytes.toArrayUnsafe(), StandardCharsets.UTF_8), BeaconBlock.class));
  }
}
