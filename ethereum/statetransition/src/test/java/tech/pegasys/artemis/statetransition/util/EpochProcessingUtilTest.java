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
import static tech.pegasys.artemis.datastructures.util.DataStructureUtil.randomDeposits;
import static tech.pegasys.artemis.datastructures.util.DataStructureUtil.randomEth1Data;
import static tech.pegasys.artemis.statetransition.util.BeaconStateUtil.get_total_effective_balance;

import java.util.ArrayList;
import java.util.List;
import net.consensys.cava.junit.BouncyCastleExtension;
import net.consensys.cava.units.bigints.UInt64;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import tech.pegasys.artemis.datastructures.Constants;
import tech.pegasys.artemis.datastructures.operations.Deposit;
import tech.pegasys.artemis.datastructures.state.Validator;
import tech.pegasys.artemis.statetransition.BeaconState;

@ExtendWith(BouncyCastleExtension.class)
class EpochProcessingUtilTest {

  BeaconState createArbitraryBeaconState(int numValidators) {
    ArrayList<Deposit> deposits = randomDeposits(numValidators);
    // get initial state
    BeaconState state =
        BeaconStateUtil.get_initial_beacon_state(
            deposits, UInt64.valueOf(Constants.GENESIS_SLOT), randomEth1Data());

    UInt64 currentEpoch = BeaconStateUtil.get_current_epoch(state);

    // set validators to active
    for (Validator validator : state.getValidator_registry()) {
      validator.setActivation_epoch(currentEpoch);
    }
    return state;
  }

  @Test
  @Disabled
  void processEjectionsTest() {

    BeaconState state = createArbitraryBeaconState(25);
    UInt64 currentEpoch = BeaconStateUtil.get_current_epoch(state);

    List<UInt64> lowBalances = new ArrayList<>();
    lowBalances.add(UInt64.valueOf(Constants.EJECTION_BALANCE / 4));
    lowBalances.add(UInt64.valueOf(Constants.EJECTION_BALANCE / 8));
    lowBalances.add(UInt64.valueOf(0L));
    lowBalances.add(UInt64.valueOf(Constants.EJECTION_BALANCE / 2));
    lowBalances.add(UInt64.valueOf(Constants.EJECTION_BALANCE));
    // make 4 validators have balance below threshold and 1 right at the threshhold
    // validators to be ejected
    state.getValidator_balances().set(0, lowBalances.get(0));
    state.getValidator_balances().set(5, lowBalances.get(1));
    state.getValidator_balances().set(15, lowBalances.get(2));
    state.getValidator_balances().set(20, lowBalances.get(3));
    // validator stays active
    state.getValidator_balances().set(1, lowBalances.get(4));

    UInt64 lowBalance = UInt64.ZERO;
    for (int i = 0; i < lowBalances.size(); i++) {
      lowBalance = lowBalance.add(lowBalances.get(i));
    }

    // flag the validators with a balance below the threshold
    EpochProcessorUtil.process_ejections(state);
    // increment the epoch to the time where the validator will be considered ejected
    currentEpoch = BeaconStateUtil.get_entry_exit_effect_epoch(currentEpoch);

    List<Validator> after_active_validators =
        ValidatorsUtil.get_active_validators(state.getValidator_registry(), currentEpoch);
    int expected_num_validators = 21;

    assertEquals(expected_num_validators, after_active_validators.size());
  }

  @Test
  @Disabled
  void updateValidatorRegistryTest() {
    BeaconState state = createArbitraryBeaconState(25);
    UInt64 currentEpoch = BeaconStateUtil.get_current_epoch(state);

    // make 4 validators have balance below threshold and 1 right at the threshhold
    List<Validator> validators =
        ValidatorsUtil.get_active_validators(state.getValidator_registry(), currentEpoch);
    // validators to be ejected
    state.getValidator_balances().set(0, UInt64.valueOf(Constants.EJECTION_BALANCE / 4));
    validators.get(0).setStatus_flags(UInt64.valueOf(Constants.INITIATED_EXIT));
    state.getValidator_balances().set(5, UInt64.valueOf(Constants.EJECTION_BALANCE / 8));
    validators.get(5).setStatus_flags(UInt64.valueOf(Constants.INITIATED_EXIT));
    state.getValidator_balances().set(15, UInt64.valueOf(0L));
    validators.get(15).setStatus_flags(UInt64.valueOf(Constants.INITIATED_EXIT));
    state.getValidator_balances().set(20, UInt64.valueOf(Constants.EJECTION_BALANCE / 2));
    validators.get(20).setStatus_flags(UInt64.valueOf(Constants.INITIATED_EXIT));
    // validator stays active
    state.getValidator_balances().set(1, UInt64.valueOf(Constants.EJECTION_BALANCE));

    // flag the validators with a balance below the threshold
    EpochProcessorUtil.update_validator_registry(state);
    // increment the epoch to the time where the validator will be considered ejected
    currentEpoch = BeaconStateUtil.get_entry_exit_effect_epoch(currentEpoch);

    List<Validator> after_active_validators =
        ValidatorsUtil.get_active_validators(state.getValidator_registry(), currentEpoch);

    int expected_num_validators = 21;

    assertEquals(expected_num_validators, after_active_validators.size());
  }

  @Test
  @Disabled
  void updateValidatorRegistryTest_missingFlag() {
    BeaconState state = createArbitraryBeaconState(25);
    UInt64 currentEpoch = BeaconStateUtil.get_current_epoch(state);

    // make 4 validators have balance below threshold and 1 right at the threshhold
    // validators to be ejected
    long val_balance = Constants.EJECTION_BALANCE - 6;
    state.getValidator_balances().set(0, UInt64.valueOf(val_balance));

    // flag the validators with a balance below the threshold
    EpochProcessorUtil.update_validator_registry(state);
    // increment the epoch to the time where the validator will be considered ejected
    currentEpoch = BeaconStateUtil.get_entry_exit_effect_epoch(currentEpoch);

    List<Validator> after_active_validators =
        ValidatorsUtil.get_active_validators(state.getValidator_registry(), currentEpoch);

    int expected_num_validators = 25;
    assertEquals(expected_num_validators, after_active_validators.size());
  }

  @Disabled
  @Test
  void processPenaltiesAndExitsTest() {
    BeaconState state = createArbitraryBeaconState(25);
    // TODO: Figure out how to test PenaltiesAndExits
    UInt64 currentEpoch = BeaconStateUtil.get_current_epoch(state);

    List<Integer> before_active_validators =
        ValidatorsUtil.get_active_validator_indices(state.getValidator_registry(), currentEpoch);
    UInt64 before_total_balance = get_total_effective_balance(state, before_active_validators);

    List<Validator> validators =
        ValidatorsUtil.get_active_validators(state.getValidator_registry(), currentEpoch);
    // validators to withdrawal
    state.getValidator_balances().set(0, UInt64.valueOf(Constants.MAX_DEPOSIT_AMOUNT));
    validators.get(0).setStatus_flags(UInt64.valueOf(Constants.WITHDRAWABLE));

    // flag the validators with a balance below the threshold
    EpochProcessorUtil.process_penalties_and_exits(state);
    // increment the epoch to the time where the validator will be considered ejected
    currentEpoch = BeaconStateUtil.get_entry_exit_effect_epoch(currentEpoch);

    List<Integer> after_active_validators =
        ValidatorsUtil.get_active_validator_indices(state.getValidator_registry(), currentEpoch);
    UInt64 after_total_balance = get_total_effective_balance(state, after_active_validators);

    int expected_num_validators = 24;
    UInt64 deposit_amount = UInt64.valueOf(Constants.MAX_DEPOSIT_AMOUNT);
    UInt64 expected_total_balance =
        UInt64.valueOf(expected_num_validators).multiply(deposit_amount);

    assertEquals(expected_num_validators, after_active_validators.size());
    assertEquals(expected_total_balance, after_total_balance);
  }
}
