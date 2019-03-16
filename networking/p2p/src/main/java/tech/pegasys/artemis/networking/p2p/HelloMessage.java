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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.consensys.cava.bytes.Bytes32;

final class HelloMessage {

  private final long networkId;

  private final long chainId;

  private final Bytes32 latestFinalizedRoot;

  private final long latestFinalizedEpoch;

  private final Bytes32 bestRoot;

  private final long bestSlot;

  @JsonCreator
  public HelloMessage(
      @JsonProperty("networkId") long networkId,
      @JsonProperty("chainId") long chainId,
      @JsonProperty("latestFinalizedRoot") Bytes32 latestFinalizedRoot,
      @JsonProperty("latestFinalizedEpoch") long latestFinalizedEpoch,
      @JsonProperty("bestRoot") Bytes32 bestRoot,
      @JsonProperty("bestSlot") long bestSlot) {
    this.networkId = networkId;
    this.chainId = chainId;
    this.latestFinalizedRoot = latestFinalizedRoot;
    this.latestFinalizedEpoch = latestFinalizedEpoch;
    this.bestRoot = bestRoot;
    this.bestSlot = bestSlot;
  }

  public long networkId() {
    return networkId;
  }

  public long chainId() {
    return chainId;
  }

  public Bytes32 latestFinalizedRoot() {
    return latestFinalizedRoot;
  }

  public long latestFinalizedEpoch() {
    return latestFinalizedEpoch;
  }

  public Bytes32 bestRoot() {
    return bestRoot;
  }

  public long bestSlot() {
    return bestSlot;
  }
}
