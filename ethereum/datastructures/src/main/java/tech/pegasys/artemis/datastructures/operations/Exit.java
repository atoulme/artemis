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

package tech.pegasys.artemis.datastructures.operations;

import java.util.Objects;
import net.consensys.cava.bytes.Bytes;
import net.consensys.cava.ssz.SSZ;
import net.consensys.cava.units.bigints.UInt64;

public class Exit {

  private UInt64 epoch;
  private UInt64 validator_index;
  private BLSSignature signature;

  public Exit(UInt64 epoch, UInt64 validator_index, BLSSignature signature) {
    this.epoch = epoch;
    this.validator_index = validator_index;
    this.signature = signature;
  }

  public static Exit fromBytes(Bytes bytes) {
    return SSZ.decode(
        bytes,
        reader ->
            new Exit(
                UInt64.valueOf(reader.readUInt64()),
                UInt64.valueOf(reader.readUInt64()),
                BLSSignature.fromBytes(reader.readBytes())));
  }

  public Bytes toBytes() {
    return SSZ.encode(
        writer -> {
          writer.writeUInt64(epoch.toLong());
          writer.writeUInt64(validator_index.toLong());
          writer.writeBytes(signature.toBytes());
        });
  }

  @Override
  public int hashCode() {
    return Objects.hash(epoch, validator_index, signature);
  }

  @Override
  public boolean equals(Object obj) {
    if (Objects.isNull(obj)) {
      return false;
    }

    if (this == obj) {
      return true;
    }

    if (!(obj instanceof Exit)) {
      return false;
    }

    Exit other = (Exit) obj;
    return Objects.equals(this.getEpoch(), other.getEpoch())
        && Objects.equals(this.getValidator_index(), other.getValidator_index())
        && Objects.equals(this.getSignature(), other.getSignature());
  }

  /** ******************* * GETTERS & SETTERS * * ******************* */
  public UInt64 getEpoch() {
    return epoch;
  }

  public void setEpoch(UInt64 epoch) {
    this.epoch = epoch;
  }

  public UInt64 getValidator_index() {
    return validator_index;
  }

  public void setValidator_index(UInt64 validator_index) {
    this.validator_index = validator_index;
  }

  public BLSSignature getSignature() {
    return signature;
  }

  public void setSignature(BLSSignature signature) {
    this.signature = signature;
  }
}
