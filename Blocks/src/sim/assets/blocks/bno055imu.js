/**
 * @license
 * Copyright 2017 Google LLC
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

/**
 * @fileoverview FTC robot blocks related to BNO055IMU.
 * @author lizlooney@google.com (Liz Looney)
 */

// The following are generated dynamically in HardwareUtil.fetchJavaScriptForHardware():
// createBNO055IMUDropdown
// The following are defined in vars.js:
// getPropertyColor
// functionColor
// setPropertyColor

Blockly.JavaScript['bno055imu_setProperty'] = function(block) {
  var identifier = block.getFieldValue('IDENTIFIER');
  var property = block.getFieldValue('PROP');
  var value = Blockly.JavaScript.valueToCode(
      block, 'VALUE', Blockly.JavaScript.ORDER_NONE);
  return identifier + '.set' + property + '(' + value + ');\n';
};

Blockly.FtcJava['bno055imu_setProperty'] = function(block) {
  var identifier = Blockly.FtcJava.importDeclareAssign_(block, 'IDENTIFIER', 'BNO055IMU');
  var property = block.getFieldValue('PROP');
  var value = Blockly.FtcJava.valueToCode(
      block, 'VALUE', Blockly.FtcJava.ORDER_NONE);
  var code;
  switch (property) {
    default:
      throw 'Unexpected property ' + property + ' (bno055imu_setProperty).';
  }
  return code;
};

Blockly.Blocks['bno055imu_getProperty'] = {
  init: function() {
    var PROPERTY_CHOICES = [
        ['Acceleration', 'Acceleration'],
        ['AngularOrientation', 'AngularOrientation'],
        ['AngularVelocity', 'AngularVelocity'],
    ];
    this.setOutput(true); // no type, for compatibility
    this.appendDummyInput()
        .appendField(createBNO055IMUDropdown(), 'IDENTIFIER')
        .appendField('.')
        .appendField(new Blockly.FieldDropdown(PROPERTY_CHOICES), 'PROP');
    this.setColour(getPropertyColor);
    // Assign 'this' to a variable for use in the tooltip closure below.
    var thisBlock = this;
    var TOOLTIPS = [
        ['Acceleration', 'Returns an Acceleration object representing the last observed ' +
            'acceleration of the sensor. Note that this does not communicate with the sensor, ' +
            'but rather returns the most recent value reported to the acceleration integration ' +
            'algorithm.'],
        ['AngularOrientation', 'Returns an Orientation object representing the absolute ' +
            'orientation of the sensor as a set three angles.'],
        ['AngularVelocity', 'Returns an AngularVelocity object representing the rate of change ' +
            'of the absolute orientation of the sensor.'],
    ];
    this.setTooltip(function() {
      var key = thisBlock.getFieldValue('PROP');
      for (var i = 0; i < TOOLTIPS.length; i++) {
        if (TOOLTIPS[i][0] == key) {
          return TOOLTIPS[i][1];
        }
      }
      return '';
    });
  }
};

Blockly.JavaScript['bno055imu_getProperty'] = function(block) {
  var identifier = block.getFieldValue('IDENTIFIER');
  var property = block.getFieldValue('PROP');
  var code = identifier + '.get' + property + '()';
  return [code, Blockly.JavaScript.ORDER_FUNCTION_CALL];
};

Blockly.FtcJava['bno055imu_getProperty'] = function(block) {
  var identifier = Blockly.FtcJava.importDeclareAssign_(block, 'IDENTIFIER', 'BNO055IMU');
  var property = block.getFieldValue('PROP');
  var code;
  switch (property) {
    case 'Acceleration':
    case 'AngularOrientation':
    case 'AngularVelocity':
  }
  return [code, Blockly.FtcJava.ORDER_FUNCTION_CALL];
};

Blockly.Blocks['bno055imu_getProperty_Acceleration'] = {
  init: function() {
    var PROPERTY_CHOICES = [
        ['Acceleration', 'Acceleration'],
    ];
    this.setOutput(true, 'Acceleration');
    this.appendDummyInput()
        .appendField(createBNO055IMUDropdown(), 'IDENTIFIER')
        .appendField('.')
        .appendField(new Blockly.FieldDropdown(PROPERTY_CHOICES), 'PROP');
    this.setColour(getPropertyColor);
    // Assign 'this' to a variable for use in the tooltip closure below.
    var thisBlock = this;
    var TOOLTIPS = [
        ['Acceleration', 'Returns an Acceleration object representing the last observed ' +
            'acceleration of the sensor. Note that this does not communicate with the sensor, ' +
            'but rather returns the most recent value reported to the acceleration integration ' +
            'algorithm.'],
    ];
    this.setTooltip(function() {
      var key = thisBlock.getFieldValue('PROP');
      for (var i = 0; i < TOOLTIPS.length; i++) {
        if (TOOLTIPS[i][0] == key) {
          return TOOLTIPS[i][1];
        }
      }
      return '';
    });
  }
};

Blockly.JavaScript['bno055imu_getProperty_Acceleration'] =
    Blockly.JavaScript['bno055imu_getProperty'];

Blockly.FtcJava['bno055imu_getProperty_Acceleration'] =
    Blockly.FtcJava['bno055imu_getProperty'];

Blockly.Blocks['bno055imu_getProperty_Orientation'] = {
  init: function() {
    var PROPERTY_CHOICES = [
        ['AngularOrientation', 'AngularOrientation'],
    ];
    this.setOutput(true, 'Orientation');
    this.appendDummyInput()
        .appendField(createBNO055IMUDropdown(), 'IDENTIFIER')
        .appendField('.')
        .appendField(new Blockly.FieldDropdown(PROPERTY_CHOICES), 'PROP');
    this.setColour(getPropertyColor);
    // Assign 'this' to a variable for use in the tooltip closure below.
    var thisBlock = this;
    var TOOLTIPS = [
        ['AngularOrientation', 'Returns an Orientation object representing the absolute ' +
            'orientation of the sensor as a set three angles.'],
    ];
    this.setTooltip(function() {
      var key = thisBlock.getFieldValue('PROP');
      for (var i = 0; i < TOOLTIPS.length; i++) {
        if (TOOLTIPS[i][0] == key) {
          return TOOLTIPS[i][1];
        }
      }
      return '';
    });
  }
};

Blockly.JavaScript['bno055imu_getProperty_Orientation'] =
    Blockly.JavaScript['bno055imu_getProperty'];

Blockly.FtcJava['bno055imu_getProperty_Orientation'] =
    Blockly.FtcJava['bno055imu_getProperty'];

Blockly.Blocks['bno055imu_getProperty_AngularVelocity'] = {
  init: function() {
    var PROPERTY_CHOICES = [
        ['AngularVelocity', 'AngularVelocity'],
    ];
    this.setOutput(true, 'AngularVelocity');
    this.appendDummyInput()
        .appendField(createBNO055IMUDropdown(), 'IDENTIFIER')
        .appendField('.')
        .appendField(new Blockly.FieldDropdown(PROPERTY_CHOICES), 'PROP');
    this.setColour(getPropertyColor);
    // Assign 'this' to a variable for use in the tooltip closure below.
    var thisBlock = this;
    var TOOLTIPS = [
        ['AngularVelocity', 'Returns an AngularVelocity object representing the rate of change ' +
            'of the absolute orientation of the sensor.'],
    ];
    this.setTooltip(function() {
      var key = thisBlock.getFieldValue('PROP');
      for (var i = 0; i < TOOLTIPS.length; i++) {
        if (TOOLTIPS[i][0] == key) {
          return TOOLTIPS[i][1];
        }
      }
      return '';
    });
  }
};

Blockly.JavaScript['bno055imu_getProperty_AngularVelocity'] =
    Blockly.JavaScript['bno055imu_getProperty'];

Blockly.FtcJava['bno055imu_getProperty_AngularVelocity'] =
    Blockly.FtcJava['bno055imu_getProperty'];

// Enums

// Functions

Blockly.Blocks['bno055imu_initialize'] = {
  init: function() {
    this.appendDummyInput()
        .appendField('call')
        .appendField(createBNO055IMUDropdown(), 'IDENTIFIER')
        .appendField('.')
        .appendField(createNonEditableField('initialize'));
    this.appendValueInput('PARAMETERS').setCheck('BNO055IMU.Parameters')
        .appendField('parameters')
        .setAlign(Blockly.ALIGN_RIGHT);
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setColour(functionColor);
    this.setTooltip('Initializes the sensor using the given parameters. Note that this operation ' +
        'can take several tens of milliseconds.');
  }
};

Blockly.JavaScript['bno055imu_initialize'] = function(block) {
  var identifier = block.getFieldValue('IDENTIFIER');
  var parameters = Blockly.JavaScript.valueToCode(
      block, 'PARAMETERS', Blockly.JavaScript.ORDER_NONE);
  return identifier + '.initialize(' + parameters + ');\n';
};

Blockly.FtcJava['bno055imu_initialize'] = function(block) {
  var identifier = Blockly.FtcJava.importDeclareAssign_(block, 'IDENTIFIER', 'BNO055IMU');
  var parameters = Blockly.FtcJava.valueToCode(
      block, 'PARAMETERS', Blockly.FtcJava.ORDER_NONE);
  return identifier + '.initialize(' + parameters + ');\n';
};

Blockly.Blocks['bno055imu_getAngularVelocity'] = {
  init: function() {
    this.setOutput(true, 'AngularVelocity');
    this.appendDummyInput()
        .appendField('call')
        .appendField(createBNO055IMUDropdown(), 'IDENTIFIER')
        .appendField('.')
        .appendField(createNonEditableField('getAngularVelocity'));
    this.appendValueInput('ANGLE_UNIT').setCheck('AngleUnit')
        .appendField('angleUnit')
        .setAlign(Blockly.ALIGN_RIGHT);
    this.setColour(functionColor);
    this.setTooltip('Returns an AngularVelocity object representing the angular rotation rate ' +
        'across all the axes measured by the sensor. Axes on which angular velocity is not ' +
        'measured are reported as zero.');
  }
};

Blockly.JavaScript['bno055imu_getAngularVelocity'] = function(block) {
  var identifier = block.getFieldValue('IDENTIFIER');
  var angleUnit = Blockly.JavaScript.valueToCode(
      block, 'ANGLE_UNIT', Blockly.JavaScript.ORDER_NONE);
  var code = identifier + '.getAngularVelocity(' + angleUnit + ')';
  return [code, Blockly.JavaScript.ORDER_FUNCTION_CALL];
};

Blockly.FtcJava['bno055imu_getAngularVelocity'] = function(block) {
  var identifier = Blockly.FtcJava.importDeclareAssign_(block, 'IDENTIFIER', 'BNO055IMU');
  var angleUnit = Blockly.FtcJava.valueToCode(
      block, 'ANGLE_UNIT', Blockly.FtcJava.ORDER_NONE);
  // This java code will throw ClassCastException if the BNO055IMU is not a Gyroscope.
  Blockly.FtcJava.generateImport_('Gyroscope');
  var code = '((Gyroscope) ' + identifier + ').getAngularVelocity(' + angleUnit + ')';
  return [code, Blockly.FtcJava.ORDER_FUNCTION_CALL];
};

Blockly.Blocks['bno055imu_getAngularOrientation'] = {
  init: function() {
    this.setOutput(true, 'Orientation');
    this.appendDummyInput()
        .appendField('call')
        .appendField(createBNO055IMUDropdown(), 'IDENTIFIER')
        .appendField('.')
        .appendField(createNonEditableField('getAngularOrientation'));
    this.appendValueInput('AXES_REFERENCE').setCheck('AxesReference')
        .appendField('axesReference')
        .setAlign(Blockly.ALIGN_RIGHT);
    this.appendValueInput('AXES_ORDER').setCheck('AxesOrder')
        .appendField('axesOrder')
        .setAlign(Blockly.ALIGN_RIGHT);
    this.appendValueInput('ANGLE_UNIT').setCheck('AngleUnit')
        .appendField('angleUnit')
        .setAlign(Blockly.ALIGN_RIGHT);
    this.setColour(functionColor);
    this.setTooltip('Returns an Orienation object representing the absolute orientation of the ' +
        'sensor as a set three angles. Axes on which absolute orientation is not measured are ' +
        'reported as zero.');
  }
};

Blockly.JavaScript['bno055imu_getAngularOrientation'] = function(block) {
  var identifier = block.getFieldValue('IDENTIFIER');
  var axesReference = Blockly.JavaScript.valueToCode(
      block, 'AXES_REFERENCE', Blockly.JavaScript.ORDER_COMMA);
  var axesOrder = Blockly.JavaScript.valueToCode(
      block, 'AXES_ORDER', Blockly.JavaScript.ORDER_COMMA);
  var angleUnit = Blockly.JavaScript.valueToCode(
      block, 'ANGLE_UNIT', Blockly.JavaScript.ORDER_COMMA);
  var code = identifier + '.getAngularOrientation(' + axesReference + ', ' + axesOrder + ', ' + angleUnit + ')';
  return [code, Blockly.JavaScript.ORDER_FUNCTION_CALL];
};

Blockly.FtcJava['bno055imu_getAngularOrientation'] = function(block) {
  var identifier = Blockly.FtcJava.importDeclareAssign_(block, 'IDENTIFIER', 'BNO055IMU');
  var axesReference = Blockly.FtcJava.valueToCode(
      block, 'AXES_REFERENCE', Blockly.FtcJava.ORDER_COMMA);
  var axesOrder = Blockly.FtcJava.valueToCode(
      block, 'AXES_ORDER', Blockly.FtcJava.ORDER_COMMA);
  var angleUnit = Blockly.FtcJava.valueToCode(
      block, 'ANGLE_UNIT', Blockly.FtcJava.ORDER_COMMA);
  var code = identifier + '.getAngularOrientation(' + axesReference + ', ' + axesOrder + ', ' + angleUnit + ')';
  return [code, Blockly.FtcJava.ORDER_FUNCTION_CALL];
};
