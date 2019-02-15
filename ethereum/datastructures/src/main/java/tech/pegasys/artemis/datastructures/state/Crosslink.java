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

package tech.pegasys.artemis.datastructures.state;

import java.util.Objects;
import net.consensys.cava.bytes.Bytes;
import net.consensys.cava.bytes.Bytes32;
import net.consensys.cava.ssz.SSZ;
import net.consensys.cava.units.bigints.UInt64;

public class Crosslink {

  private UInt64 epoch;
  private Bytes32 shard_block_root;

  public Crosslink(UInt64 epoch, Bytes32 shard_block_root) {
    this.epoch = epoch;
    this.shard_block_root = shard_block_root;
  }

  public static Crosslink fromBytes(Bytes bytes) {
    return SSZ.decode(
        bytes,
        reader ->
            new Crosslink(UInt64.valueOf(reader.readUInt64()), Bytes32.wrap(reader.readBytes())));
  }

  public Bytes toBytes() {
    return SSZ.encode(
        writer -> {
          writer.writeUInt64(epoch.toLong());
          writer.writeBytes(shard_block_root);
        });
  }

  @Override
  public int hashCode() {
    return Objects.hash(epoch, shard_block_root);
  }

  @Override
  public boolean equals(Object obj) {
    if (Objects.isNull(obj)) {
      return false;
    }

    if (this == obj) {
      return true;
    }

    if (!(obj instanceof Crosslink)) {
      return false;
    }

    Crosslink other = (Crosslink) obj;
    return Objects.equals(this.getEpoch(), other.getEpoch())
        && Objects.equals(this.getShard_block_root(), other.getShard_block_root());
  }

  /** ******************* * GETTERS & SETTERS * * ******************* */
  public Bytes32 getShard_block_root() {
    return shard_block_root;
  }

  public void setShard_block_root(Bytes32 shard_block_root) {
    this.shard_block_root = shard_block_root;
  }

  public UInt64 getEpoch() {
    return epoch;
  }

  public void setEpoch(UInt64 epoch) {
    this.epoch = epoch;
  }
}
