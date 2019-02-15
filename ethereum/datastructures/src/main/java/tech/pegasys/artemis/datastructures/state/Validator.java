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
import net.consensys.cava.bytes.Bytes48;
import net.consensys.cava.ssz.SSZ;
import net.consensys.cava.units.bigints.UInt64;

public class Validator {

  // BLS public key
  private Bytes48 pubkey;
  // Withdrawal credentials
  private Bytes32 withdrawal_credentials;
  // Epoch when validator activated
  private UInt64 activation_epoch;
  // Epoch when validator exited
  private UInt64 exit_epoch;
  // Epoch when validator withdrew
  private UInt64 withdrawal_epoch;
  // Epoch when validator was penalized
  private UInt64 penalized_epoch;
  // Status flags
  private UInt64 status_flags;

  public Validator(
      Bytes48 pubkey,
      Bytes32 withdrawal_credentials,
      UInt64 activation_epoch,
      UInt64 exit_epoch,
      UInt64 withdrawal_epoch,
      UInt64 penalized_epoch,
      UInt64 status_flags) {
    this.pubkey = pubkey;
    this.withdrawal_credentials = withdrawal_credentials;
    this.activation_epoch = activation_epoch;
    this.exit_epoch = exit_epoch;
    this.withdrawal_epoch = withdrawal_epoch;
    this.penalized_epoch = penalized_epoch;
    this.status_flags = status_flags;
  }

  public static Validator fromBytes(Bytes bytes) {
    return SSZ.decode(
        bytes,
        reader ->
            new Validator(
                Bytes48.wrap(reader.readBytes()),
                Bytes32.wrap(reader.readBytes()),
                UInt64.valueOf(reader.readUInt64()),
                UInt64.valueOf(reader.readUInt64()),
                UInt64.valueOf(reader.readUInt64()),
                UInt64.valueOf(reader.readUInt64()),
                UInt64.valueOf(reader.readUInt64())));
  }

  public Bytes toBytes() {
    return SSZ.encode(
        writer -> {
          writer.writeBytes(pubkey);
          writer.writeBytes(withdrawal_credentials);
          writer.writeUInt64(activation_epoch.toLong());
          writer.writeUInt64(exit_epoch.toLong());
          writer.writeUInt64(withdrawal_epoch.toLong());
          writer.writeUInt64(penalized_epoch.toLong());
          writer.writeUInt64(status_flags.toLong());
        });
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        pubkey,
        withdrawal_credentials,
        activation_epoch,
        exit_epoch,
        withdrawal_epoch,
        penalized_epoch,
        status_flags);
  }

  @Override
  public boolean equals(Object obj) {
    if (Objects.isNull(obj)) {
      return false;
    }

    if (this == obj) {
      return true;
    }

    if (!(obj instanceof Validator)) {
      return false;
    }

    Validator other = (Validator) obj;
    return Objects.equals(this.getPubkey(), other.getPubkey())
        && Objects.equals(this.getWithdrawal_credentials(), other.getWithdrawal_credentials())
        && Objects.equals(this.getActivation_epoch(), other.getActivation_epoch())
        && Objects.equals(this.getExit_epoch(), other.getExit_epoch())
        && Objects.equals(this.getWithdrawal_epoch(), other.getWithdrawal_epoch())
        && Objects.equals(this.getPenalized_epoch(), other.getPenalized_epoch())
        && Objects.equals(this.getStatus_flags(), other.getStatus_flags());
  }

  public Bytes48 getPubkey() {
    return pubkey;
  }

  public void setPubkey(Bytes48 pubkey) {
    this.pubkey = pubkey;
  }

  public Bytes32 getWithdrawal_credentials() {
    return withdrawal_credentials;
  }

  public void setWithdrawal_credentials(Bytes32 withdrawal_credentials) {
    this.withdrawal_credentials = withdrawal_credentials;
  }

  public UInt64 getActivation_epoch() {
    return activation_epoch;
  }

  public void setActivation_epoch(UInt64 activation_epoch) {
    this.activation_epoch = activation_epoch;
  }

  public UInt64 getExit_epoch() {
    return exit_epoch;
  }

  public void setExit_epoch(UInt64 exit_epoch) {
    this.exit_epoch = exit_epoch;
  }

  public UInt64 getWithdrawal_epoch() {
    return withdrawal_epoch;
  }

  public void setWithdrawal_epoch(UInt64 withdrawal_epoch) {
    this.withdrawal_epoch = withdrawal_epoch;
  }

  public UInt64 getPenalized_epoch() {
    return penalized_epoch;
  }

  public void setPenalized_epoch(UInt64 penalized_epoch) {
    this.penalized_epoch = penalized_epoch;
  }

  public UInt64 getStatus_flags() {
    return status_flags;
  }

  public void setStatus_flags(UInt64 status_flags) {
    this.status_flags = status_flags;
  }

  public boolean is_active_validator(UInt64 epoch) {
    // checks validator status against the validator status constants for whether the validator is
    // active
    return activation_epoch.compareTo(epoch) <= 0 && epoch.compareTo(exit_epoch) < 0;
  }
}
