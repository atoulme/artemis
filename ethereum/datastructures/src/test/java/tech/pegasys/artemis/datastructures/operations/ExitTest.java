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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static tech.pegasys.artemis.datastructures.util.DataStructureUtil.randomUInt64;

import net.consensys.cava.bytes.Bytes;
import net.consensys.cava.bytes.Bytes48;
import net.consensys.cava.units.bigints.UInt64;
import org.junit.jupiter.api.Test;

class ExitTest {

  private UInt64 epoch = randomUInt64();
  private UInt64 validatorIndex = randomUInt64();
  private BLSSignature signature = new BLSSignature(Bytes48.random(), Bytes48.random());

  private Exit exit = new Exit(epoch, validatorIndex, signature);

  @Test
  void equalsReturnsTrueWhenObjectsAreSame() {
    Exit testExit = exit;

    assertEquals(exit, testExit);
  }

  @Test
  void equalsReturnsTrueWhenObjectFieldsAreEqual() {
    Exit testExit = new Exit(epoch, validatorIndex, signature);

    assertEquals(exit, testExit);
  }

  @Test
  void equalsReturnsFalseWhenEpochsAreDifferent() {
    Exit testExit = new Exit(epoch.add(randomUInt64()), validatorIndex, signature);

    assertNotEquals(exit, testExit);
  }

  @Test
  void equalsReturnsFalseWhenValidatorIndicesAreDifferent() {
    Exit testExit = new Exit(epoch, validatorIndex.add(randomUInt64()), signature);

    assertNotEquals(exit, testExit);
  }

  @Test
  void equalsReturnsFalseWhenSignaturesAreDifferent() {
    // Create copy of signature and reverse to ensure it is different.
    BLSSignature reverseSignature = new BLSSignature(signature.getC1(), signature.getC0());

    Exit testExit = new Exit(epoch, validatorIndex, reverseSignature);

    assertNotEquals(exit, testExit);
  }

  @Test
  void rountripSSZ() {
    Bytes sszExitBytes = exit.toBytes();
    assertEquals(exit, Exit.fromBytes(sszExitBytes));
  }
}
