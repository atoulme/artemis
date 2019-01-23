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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.google.common.eventbus.EventBus;
import net.consensys.cava.bytes.Bytes48;
import org.junit.Test;
import tech.pegasys.artemis.datastructures.beaconchainblocks.BeaconBlock;

public class ChainStorageServiceTest {

  @Test
  public void testStartTwice() {
    ChainStorageService service = new ChainStorageService();
    service.init(new EventBus());
    service.run();
    service.run();
  }

  @Test
  public void testStopBeforeStart() {
    ChainStorageService service = new ChainStorageService();
    service.init(new EventBus());
    service.stop();
    service.run();
  }

  @Test
  public void testAccessAfterClose() {
    ChainStorageService service = new ChainStorageService();
    service.init(new EventBus());
    service.run();
    service.stop();
    try {
      service.getBlockRepository();
      fail("Should have thrown an IllegalStateException");
    } catch (IllegalStateException e) {

    }
  }

  @Test
  public void testStoreAndRetrieveBlock() throws Exception {
    ChainStorageService service = new ChainStorageService();
    service.init(new EventBus());
    service.run();
    BeaconBlock block = new BeaconBlock();
    block.setSignature(new Bytes48[] {Bytes48.random(), Bytes48.random(), Bytes48.random()});
    service.getBlockRepository().store(block).join();
    BeaconBlock read = service.getBlockRepository().retrieve(block.getSignature()).get();
    assertEquals(block.getSignature().length, read.getSignature().length);
    for (int i = 0; i < block.getSignature().length; i++) {
      assertEquals(block.getSignature()[i], read.getSignature()[i]);
    }
  }
}
