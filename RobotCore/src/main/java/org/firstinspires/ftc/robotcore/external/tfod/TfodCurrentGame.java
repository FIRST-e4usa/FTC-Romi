/*
 * Copyright 2020 Google LLC
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

package org.firstinspires.ftc.robotcore.external.tfod;

/**
 * A class that provides simplified access to TensorFlow Object Detection for the current game.
 *
 * @author lizlooney@google.com (Liz Looney)
 */
public class TfodCurrentGame extends TfodBase {
  // TODO(lizlooney): This file should be updated when the FTC game changes.
  public static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";

  public static final String[] LABELS = {
    "Quad",
    "Single",
  };

  public TfodCurrentGame() {
    super(TFOD_MODEL_ASSET, LABELS);
  }
}
