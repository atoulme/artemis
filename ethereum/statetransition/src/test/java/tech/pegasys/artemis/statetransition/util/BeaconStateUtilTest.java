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

package tech.pegasys.artemis.statetransition.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.pegasys.artemis.datastructures.util.DataStructureUtil.randomUInt64;
import static tech.pegasys.artemis.datastructures.util.DataStructureUtil.randomValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.consensys.cava.bytes.Bytes32;
import net.consensys.cava.bytes.Bytes48;
import net.consensys.cava.junit.BouncyCastleExtension;
import net.consensys.cava.units.bigints.UInt64;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tech.pegasys.artemis.datastructures.Constants;
import tech.pegasys.artemis.datastructures.operations.BLSSignature;
import tech.pegasys.artemis.datastructures.state.Fork;
import tech.pegasys.artemis.datastructures.state.Validator;
import tech.pegasys.artemis.statetransition.BeaconState;

@ExtendWith(BouncyCastleExtension.class)
class BeaconStateUtilTest {
  @Test
  void minReturnsMin() {
    UInt64 actual = BeaconStateUtil.min(UInt64.valueOf(13L), UInt64.valueOf(12L));
    UInt64 expected = UInt64.valueOf(12L);
    assertEquals(expected, actual);
  }

  @Test
  void minReturnsMinWhenEqual() {
    UInt64 actual = BeaconStateUtil.min(UInt64.valueOf(12L), UInt64.valueOf(12L));
    UInt64 expected = UInt64.valueOf(12L);
    assertEquals(expected, actual);
  }

  @Test
  void maxReturnsMax() {
    UInt64 actual = BeaconStateUtil.max(UInt64.valueOf(13L), UInt64.valueOf(12L));
    UInt64 expected = UInt64.valueOf(13L);
    assertEquals(expected, actual);
  }

  @Test
  void maxReturnsMaxWhenEqual() {
    UInt64 actual = BeaconStateUtil.max(UInt64.valueOf(13L), UInt64.valueOf(13L));
    UInt64 expected = UInt64.valueOf(13L);
    assertEquals(expected, actual);
  }

  @Test
  void sqrtOfSquareNumber() {
    UInt64 actual = BeaconStateUtil.integer_squareroot(UInt64.valueOf(3481L));
    UInt64 expected = UInt64.valueOf(59L);
    assertEquals(expected, actual);
  }

  @Test
  void sqrtOfANonSquareNumber() {
    UInt64 actual = BeaconStateUtil.integer_squareroot(UInt64.valueOf(27L));
    UInt64 expected = UInt64.valueOf(5L);
    assertEquals(expected, actual);
  }

  @Test
  void sqrtOfANegativeNumber() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          BeaconStateUtil.integer_squareroot(UInt64.valueOf(-1L));
        });
  }

  // TODO It may make sense to move these tests to a Fork specific test class in the future.
  // *************** START Fork Tests ***************
  @Test
  void getForkVersionReturnsPreviousVersionWhenGivenEpochIsLessThanForkEpoch() {
    // Setup Fork Versions
    // The values of these don't really matter, it just makes sense that
    // previous version is less than current version.
    UInt64 previousVersion = UInt64.ZERO;
    UInt64 currentVersion = previousVersion.add(UInt64.valueOf(1L));

    // Setup Epochs
    // It is necessary for this test that givenEpoch is less than forkEpoch.
    UInt64 givenEpoch = UInt64.valueOf(100L);
    UInt64 forkEpoch = givenEpoch.add(UInt64.valueOf(1L));

    // Setup Fork
    Fork fork = new Fork(previousVersion, currentVersion, forkEpoch);

    assertEquals(BeaconStateUtil.get_fork_version(fork, givenEpoch), previousVersion);
  }

  @Test
  void getForkVersionReturnsCurrentVersionWhenGivenEpochIsGreaterThanForkEpoch() {
    // Setup Fork Versions
    // The values of these don't really matter, it just makes sense that
    // previous version is less than current version.
    UInt64 previousVersion = UInt64.ZERO;
    UInt64 currentVersion = previousVersion.add(UInt64.valueOf(1L));

    // Setup Epochs
    // It is necessary for this test that givenEpoch is greater than forkEpoch.
    UInt64 forkEpoch = UInt64.valueOf(100L);
    UInt64 givenEpoch = forkEpoch.add(UInt64.valueOf(1L));

    // Setup Fork
    Fork fork = new Fork(previousVersion, currentVersion, forkEpoch);

    assertEquals(BeaconStateUtil.get_fork_version(fork, givenEpoch), currentVersion);
  }

  @Test
  void getDomainReturnsAsExpectedForAllSignatureDomainTypesWithPreviousVersionFork() {
    // Setup Fork Versions
    // The values of these don't really matter, it just makes sense that
    // previous version is less than current version.
    UInt64 previousVersion = UInt64.ZERO;
    UInt64 currentVersion = previousVersion.add(UInt64.valueOf(1L));

    // Setup Epochs
    UInt64 givenEpoch = UInt64.valueOf(100L);
    UInt64 forkEpoch = givenEpoch.add(UInt64.valueOf(1L));

    // Setup Fork
    Fork fork = new Fork(previousVersion, currentVersion, forkEpoch);

    // Iterate Over the Possible Signature Domains
    // 0 - DOMAIN_DEPOSIT
    // 1 - DOMAIN_ATTESTATION
    // 2 - DOMAIN_PROPOSAL
    // 3 - DOMAIN_EXIT
    // 4 - DOMAIN_RANDAO
    for (int domain = 0; domain <= 4; ++domain) {
      assertEquals(
          BeaconStateUtil.get_domain(fork, givenEpoch, domain),
          UInt64.valueOf(
              (BeaconStateUtil.get_fork_version(fork, givenEpoch).toLong() << 32) + domain));
    }
  }

  @Test
  void getDomainReturnsAsExpectedForAllSignatureDomainTypesWithCurrentVersionFork() {
    // Setup Fork Versions
    // The values of these don't really matter, it just makes sense that
    // previous version is less than current version.
    UInt64 previousVersion = UInt64.ZERO;
    UInt64 currentVersion = previousVersion.add(UInt64.valueOf(1L));

    // Setup Epochs
    UInt64 forkEpoch = UInt64.valueOf(100L);
    UInt64 givenEpoch = forkEpoch.add(UInt64.valueOf(1L));

    // Setup Fork
    Fork fork = new Fork(previousVersion, currentVersion, forkEpoch);

    // Iterate Over the Possible Signature Domains
    // 0 - DOMAIN_DEPOSIT
    // 1 - DOMAIN_ATTESTATION
    // 2 - DOMAIN_PROPOSAL
    // 3 - DOMAIN_EXIT
    // 4 - DOMAIN_RANDAO
    for (int domain = 0; domain <= 4; ++domain) {
      assertEquals(
          BeaconStateUtil.get_domain(fork, givenEpoch, domain),
          UInt64.valueOf(
              (BeaconStateUtil.get_fork_version(fork, givenEpoch).toLong() << 32) + domain));
    }
  }
  // *************** END Fork Tests ***************

  @Test
  @Disabled
  // TODO Fill out and enable this test case when bls_verify is complete.
  void validateProofOfPosessionReturnsTrueIfTheBLSSignatureIsValidForGivenDepositInputData() {
    BeaconState beaconState = null;
    Bytes48 pubkey = null;
    BLSSignature proofOfPossession = null;
    Bytes32 withdrawalCredentials = null;

    assertTrue(
        BeaconStateUtil.validate_proof_of_possession(
            beaconState, pubkey, proofOfPossession, withdrawalCredentials));
  }

  @Test
  @Disabled
  // TODO Fill out and enable this test case when bls_verify is complete.
  void validateProofOfPosessionReturnsFalseIfTheBLSSignatureIsNotValidForGivenDepositInputData() {
    BeaconState beaconState = null;
    Bytes48 pubkey = null;
    BLSSignature proofOfPossession = null;
    Bytes32 withdrawalCredentials = null;

    assertFalse(
        BeaconStateUtil.validate_proof_of_possession(
            beaconState, pubkey, proofOfPossession, withdrawalCredentials));
  }

  @Test
  void processDepositAddsNewValidatorWhenPubkeyIsNotFoundInRegistry() {
    // Data Setup
    Bytes48 pubkey = Bytes48.random();
    UInt64 amount = UInt64.valueOf(100L);
    BLSSignature proofOfPossession = Constants.EMPTY_SIGNATURE;
    Bytes32 withdrawalCredentials = Bytes32.random();

    BeaconState beaconState = createBeaconState();

    int originalValidatorRegistrySize = beaconState.getValidator_registry().size();
    int originalValidatorBalancesSize = beaconState.getValidator_balances().size();

    // Attempt to process deposit with above data.
    BeaconStateUtil.process_deposit(
        beaconState, pubkey, amount, proofOfPossession, withdrawalCredentials);

    assertTrue(
        beaconState.getValidator_registry().size() == (originalValidatorRegistrySize + 1),
        "No validator was added to the validator registry.");
    assertTrue(
        beaconState.getValidator_balances().size() == (originalValidatorBalancesSize + 1),
        "No balance was added to the validator balances.");
    assertEquals(
        new Validator(
            pubkey,
            withdrawalCredentials,
            Constants.FAR_FUTURE_EPOCH,
            Constants.FAR_FUTURE_EPOCH,
            Constants.FAR_FUTURE_EPOCH,
            Constants.FAR_FUTURE_EPOCH,
            UInt64.ZERO),
        beaconState.getValidator_registry().get(originalValidatorRegistrySize));
    assertEquals(amount, beaconState.getValidator_balances().get(originalValidatorBalancesSize));
  }

  @Test
  void processDepositTopsUpValidatorBalanceWhenPubkeyIsFoundInRegistry() {
    // Data Setup
    Bytes48 pubkey = Bytes48.random();
    UInt64 amount = UInt64.valueOf(100L);
    BLSSignature proofOfPossession = Constants.EMPTY_SIGNATURE;
    Bytes32 withdrawalCredentials = Bytes32.random();

    Validator knownValidator =
        new Validator(
            pubkey,
            withdrawalCredentials,
            Constants.FAR_FUTURE_EPOCH,
            Constants.FAR_FUTURE_EPOCH,
            Constants.FAR_FUTURE_EPOCH,
            Constants.FAR_FUTURE_EPOCH,
            UInt64.ZERO);

    BeaconState beaconState = createBeaconState(amount, knownValidator);

    int originalValidatorRegistrySize = beaconState.getValidator_registry().size();
    int originalValidatorBalancesSize = beaconState.getValidator_balances().size();

    // Attempt to process deposit with above data.
    BeaconStateUtil.process_deposit(
        beaconState, pubkey, amount, proofOfPossession, withdrawalCredentials);

    assertTrue(
        beaconState.getValidator_registry().size() == originalValidatorRegistrySize,
        "A new validator was added to the validator registry, but should not have been.");
    assertTrue(
        beaconState.getValidator_balances().size() == originalValidatorBalancesSize,
        "A new balance was added to the validator balances, but should not have been.");
    assertEquals(
        knownValidator, beaconState.getValidator_registry().get(originalValidatorRegistrySize - 1));
    assertEquals(
        amount.multiply(UInt64.valueOf(2L)),
        beaconState.getValidator_balances().get(originalValidatorBalancesSize - 1));
  }

  private BeaconState createBeaconState() {
    return createBeaconState(false, null, null);
  }

  private BeaconState createBeaconState(UInt64 amount, Validator knownValidator) {
    return createBeaconState(true, amount, knownValidator);
  }

  private BeaconState createBeaconState(
      boolean addToList, UInt64 amount, Validator knownValidator) {
    BeaconState beaconState = new BeaconState();
    beaconState.setSlot(randomUInt64());
    beaconState.setFork(new Fork(randomUInt64(), randomUInt64(), randomUInt64()));

    List<Validator> validatorList =
        new ArrayList<>(Arrays.asList(randomValidator(), randomValidator(), randomValidator()));
    List<UInt64> balanceList =
        new ArrayList<>(Arrays.asList(randomUInt64(), randomUInt64(), randomUInt64()));

    if (addToList) {
      validatorList.add(knownValidator);
      balanceList.add(amount);
    }

    beaconState.setValidator_registry(validatorList);
    beaconState.setValidator_balances(balanceList);
    return beaconState;
  }
}
