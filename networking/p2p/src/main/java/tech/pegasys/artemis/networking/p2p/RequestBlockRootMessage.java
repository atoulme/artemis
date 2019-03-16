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

import net.consensys.cava.bytes.Bytes32;

final class RequestBlockRootMessage {

  private Bytes32 startRoot;

  private long startSlot;

  private long max;

  private long skip;

  private boolean direction;

  public Bytes32 getStartRoot() {
    return startRoot;
  }

  public void setStartRoot(Bytes32 startRoot) {
    this.startRoot = startRoot;
  }

  public long getStartSlot() {
    return startSlot;
  }

  public void setStartSlot(long startSlot) {
    this.startSlot = startSlot;
  }

  public long getMax() {
    return max;
  }

  public void setMax(long max) {
    this.max = max;
  }

  public long getSkip() {
    return skip;
  }

  public void setSkip(long skip) {
    this.skip = skip;
  }

  public boolean isDirection() {
    return direction;
  }

  public void setDirection(boolean direction) {
    this.direction = direction;
  }
}
