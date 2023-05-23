/*
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.blocks.ftcrobotcontroller.runtime;

import android.webkit.JavascriptInterface;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * A class that provides JavaScript access to a {@link LinearOpMode}.
 *
 * @author lizlooney@google.com (Liz Looney)
 */
class LinearOpModeAccess extends Access {
  private final BlocksOpMode blocksOpMode;

  LinearOpModeAccess(BlocksOpMode blocksOpMode, String identifier, String projectName) {
    super(blocksOpMode, identifier, projectName);
    this.blocksOpMode = blocksOpMode;
  }

  @SuppressWarnings("unused")
  @JavascriptInterface
  public void waitForStart() {
    startBlockExecution(BlockType.FUNCTION, ".waitForStart");
    blocksOpMode.waitForStartForBlocks();
  }

  @SuppressWarnings("unused")
  @JavascriptInterface
  public void idle() {
    startBlockExecution(BlockType.FUNCTION, ".idle");
    blocksOpMode.idle();
  }

  @SuppressWarnings("unused")
  @JavascriptInterface
  public void sleep(double millis) {
    startBlockExecution(BlockType.FUNCTION, ".sleep");
    blocksOpMode.sleepForBlocks((long) millis);
  }

  @SuppressWarnings("unused")
  @JavascriptInterface
  public boolean opModeIsActive() {
    startBlockExecution(BlockType.FUNCTION, ".opModeIsActive");
    return blocksOpMode.opModeIsActive();
  }

  @SuppressWarnings("unused")
  @JavascriptInterface
  public boolean isStarted() {
    startBlockExecution(BlockType.FUNCTION, ".isStarted");
    return blocksOpMode.isStartedForBlocks();
  }

  @SuppressWarnings("unused")
  @JavascriptInterface
  public boolean isStopRequested() {
    startBlockExecution(BlockType.FUNCTION, ".isStopRequested");
    return blocksOpMode.isStopRequestedForBlocks();
  }

  @SuppressWarnings("unused")
  @JavascriptInterface
  public double getRuntime() {
    startBlockExecution(BlockType.FUNCTION, ".getRuntime");
    return blocksOpMode.getRuntime();
  }
}
