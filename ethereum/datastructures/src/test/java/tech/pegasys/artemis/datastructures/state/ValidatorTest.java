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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static tech.pegasys.artemis.datastructures.util.DataStructureUtil.randomUInt64;

import net.consensys.cava.bytes.Bytes;
import net.consensys.cava.bytes.Bytes32;
import net.consensys.cava.bytes.Bytes48;
import net.consensys.cava.units.bigints.UInt64;
import org.junit.jupiter.api.Test;

class ValidatorTest {

  private Bytes48 pubkey = Bytes48.random();
  private Bytes32 withdrawalCredentials = Bytes32.random();
  private UInt64 activationEpoch = randomUInt64();
  private UInt64 exitEpoch = randomUInt64();
  private UInt64 withdrawalEpoch = randomUInt64();
  private UInt64 penalizedEpoch = randomUInt64();
  private UInt64 statusFlags = randomUInt64();

  private Validator validator =
      new Validator(
          pubkey,
          withdrawalCredentials,
          activationEpoch,
          exitEpoch,
          withdrawalEpoch,
          penalizedEpoch,
          statusFlags);

  @Test
  void equalsReturnsTrueWhenObjectAreSame() {
    Validator testValidator = validator;

    assertEquals(validator, testValidator);
  }

  @Test
  void equalsReturnsTrueWhenObjectFieldsAreEqual() {
    Validator testValidator =
        new Validator(
            pubkey,
            withdrawalCredentials,
            activationEpoch,
            exitEpoch,
            withdrawalEpoch,
            penalizedEpoch,
            statusFlags);

    assertEquals(validator, testValidator);
  }

  @Test
  void equalsReturnsFalseWhenPubkeysAreDifferent() {
    Validator testValidator =
        new Validator(
            pubkey.not(),
            withdrawalCredentials,
            activationEpoch,
            exitEpoch,
            withdrawalEpoch,
            penalizedEpoch,
            statusFlags);

    assertNotEquals(validator, testValidator);
  }

  @Test
  void equalsReturnsFalseWhenWithdrawalCredentialsAreDifferent() {
    Validator testValidator =
        new Validator(
            pubkey,
            withdrawalCredentials.not(),
            activationEpoch,
            exitEpoch,
            withdrawalEpoch,
            penalizedEpoch,
            statusFlags);

    assertNotEquals(validator, testValidator);
  }

  @Test
  void equalsReturnsFalseWhenActivationEpochsAreDifferent() {
    Validator testValidator =
        new Validator(
            pubkey,
            withdrawalCredentials,
            activationEpoch.add(randomUInt64()),
            exitEpoch,
            withdrawalEpoch,
            penalizedEpoch,
            statusFlags);

    assertNotEquals(validator, testValidator);
  }

  @Test
  void equalsReturnsFalseWhenExitEpochsAreDifferent() {
    Validator testValidator =
        new Validator(
            pubkey,
            withdrawalCredentials,
            activationEpoch,
            exitEpoch.add(randomUInt64()),
            withdrawalEpoch,
            penalizedEpoch,
            statusFlags);

    assertNotEquals(validator, testValidator);
  }

  @Test
  void equalsReturnsFalseWhenWithdrawalEpochsAreDifferent() {
    Validator testValidator =
        new Validator(
            pubkey,
            withdrawalCredentials,
            activationEpoch,
            exitEpoch,
            withdrawalEpoch.add(randomUInt64()),
            penalizedEpoch,
            statusFlags);

    assertNotEquals(validator, testValidator);
  }

  @Test
  void equalsReturnsFalseWhenPenalizedEpochsAreDifferent() {
    Validator testValidator =
        new Validator(
            pubkey,
            withdrawalCredentials,
            activationEpoch,
            exitEpoch,
            withdrawalEpoch,
            penalizedEpoch.add(randomUInt64()),
            statusFlags);

    assertNotEquals(validator, testValidator);
  }

  @Test
  void equalsReturnsFalseWhenStatusFlagsaAreDifferent() {
    Validator testValidator =
        new Validator(
            pubkey,
            withdrawalCredentials,
            activationEpoch,
            exitEpoch,
            withdrawalEpoch,
            penalizedEpoch,
            statusFlags.add(randomUInt64()));

    assertNotEquals(validator, testValidator);
  }

  @Test
  void rountripSSZ() {
    Bytes sszValidatorBytes = validator.toBytes();
    assertEquals(validator, Validator.fromBytes(sszValidatorBytes));
  }
}
