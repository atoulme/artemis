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

package tech.pegasys.artemis.util.bitwise;

import net.consensys.cava.units.bigints.UInt64;

public class BitwiseOps {

  /** Returns a bitwiseOr of the parameters a and b */
  public static UInt64 or(UInt64 a, UInt64 b) {
    return a.or(b);
  }

  // Shift methods below might differ from actual bitwise implementations
  // i.e. leftShift might result in an overflow, where it should only
  // slide the bits to the left, and drop the one at most significant position

  /** Performs bitwise left shift on number a, i times */
  public static UInt64 leftShift(UInt64 a, int i) {
    return a.shiftLeft(i);
  }

  /** Performs bitwise right shift on number a, i times */
  public static UInt64 rightShift(UInt64 a, int i) {
    return a.shiftRight(i);
  }
}
