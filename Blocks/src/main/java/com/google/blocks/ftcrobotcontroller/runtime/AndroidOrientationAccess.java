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
import org.firstinspires.ftc.robotcore.external.android.AndroidOrientation;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/**
 * A class that provides JavaScript access to the Android sensors for Orientation.
 *
 * @author lizlooney@google.com (Liz Looney)
 */
class AndroidOrientationAccess extends Access {
  private final AndroidOrientation androidOrientation;

  AndroidOrientationAccess(BlocksOpMode blocksOpMode, String identifier) {
    super(blocksOpMode, identifier, "AndroidOrientation");
    androidOrientation = new AndroidOrientation();
  }

  // Access methods

  @Override
  void close() {
    androidOrientation.stopListening();
  }

  // Javascript methods

  @SuppressWarnings("unused")
  @JavascriptInterface
  public void setAngleUnit(String angleUnitString) {
    startBlockExecution(BlockType.SETTER, ".AngleUnit");
    AngleUnit angleUnit = checkArg(angleUnitString, AngleUnit.class, "");
    if (angleUnit != null) {
      androidOrientation.setAngleUnit(angleUnit);
    }
  }

  @SuppressWarnings("unused")
  @JavascriptInterface
  public double getAzimuth() {
    startBlockExecution(BlockType.GETTER, ".Azimuth");
    return androidOrientation.getAzimuth();
  }

  @SuppressWarnings("unused")
  @JavascriptInterface
  public double getPitch() {
    startBlockExecution(BlockType.GETTER, ".Pitch");
    return androidOrientation.getPitch();
  }

  @SuppressWarnings("unused")
  @JavascriptInterface
  public double getRoll() {
    startBlockExecution(BlockType.GETTER, ".Roll");
    return androidOrientation.getRoll();
  }

  @SuppressWarnings("unused")
  @JavascriptInterface
  public double getAngle() {
    startBlockExecution(BlockType.GETTER, ".Angle");
    return androidOrientation.getAngle();
  }

  @SuppressWarnings("unused")
  @JavascriptInterface
  public double getMagnitude() {
    startBlockExecution(BlockType.GETTER, ".Magnitude");
    return androidOrientation.getMagnitude();
  }

  @SuppressWarnings("unused")
  @JavascriptInterface
  public String getAngleUnit() {
    startBlockExecution(BlockType.GETTER, ".AngleUnit");
    return androidOrientation.getAngleUnit().toString();
  }

  @SuppressWarnings("unused")
  @JavascriptInterface
  public boolean isAvailable() {
    startBlockExecution(BlockType.FUNCTION, ".isAvailable");
    return androidOrientation.isAvailable();
  }

  @SuppressWarnings("unused")
  @JavascriptInterface
  public void startListening() {
    startBlockExecution(BlockType.FUNCTION, ".startListening");
    androidOrientation.startListening();
  }

  @SuppressWarnings("unused")
  @JavascriptInterface
  public void stopListening() {
    startBlockExecution(BlockType.FUNCTION, ".stopListening");
    androidOrientation.stopListening();
  }
}
