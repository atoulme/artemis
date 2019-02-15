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

package tech.pegasys.artemis.datastructures.blocks;

import java.util.Objects;
import net.consensys.cava.bytes.Bytes;
import net.consensys.cava.bytes.Bytes32;
import net.consensys.cava.ssz.SSZ;
import net.consensys.cava.units.bigints.UInt64;

public class ProposalSignedData {

  private UInt64 slot;
  private UInt64 shard;
  private Bytes32 block_root;

  public ProposalSignedData(UInt64 slot, UInt64 shard, Bytes32 block_root) {
    this.slot = slot;
    this.shard = shard;
    this.block_root = block_root;
  }

  public static ProposalSignedData fromBytes(Bytes bytes) {
    return SSZ.decode(
        bytes,
        reader ->
            new ProposalSignedData(
                UInt64.valueOf(reader.readUInt64()),
                UInt64.valueOf(reader.readUInt64()),
                Bytes32.wrap(reader.readBytes())));
  }

  public Bytes toBytes() {
    return SSZ.encode(
        writer -> {
          writer.writeUInt64(slot.toLong());
          writer.writeUInt64(shard.toLong());
          writer.writeBytes(block_root);
        });
  }

  @Override
  public int hashCode() {
    return Objects.hash(slot, shard, block_root);
  }

  @Override
  public boolean equals(Object obj) {
    if (Objects.isNull(obj)) {
      return false;
    }

    if (this == obj) {
      return true;
    }

    if (!(obj instanceof ProposalSignedData)) {
      return false;
    }

    ProposalSignedData other = (ProposalSignedData) obj;
    return Objects.equals(this.getSlot(), other.getSlot())
        && Objects.equals(this.getShard(), other.getShard())
        && Objects.equals(this.getBlock_root(), other.getBlock_root());
  }

  /** ******************* * GETTERS & SETTERS * * ******************* */
  public UInt64 getSlot() {
    return slot;
  }

  public void setSlot(UInt64 slot) {
    this.slot = slot;
  }

  public UInt64 getShard() {
    return shard;
  }

  public void setShard(UInt64 shard) {
    this.shard = shard;
  }

  public Bytes32 getBlock_root() {
    return block_root;
  }

  public void setBlock_root(Bytes32 block_root) {
    this.block_root = block_root;
  }
}
