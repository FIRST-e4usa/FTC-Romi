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
 * @fileoverview FTC robot blocks related to BNO055IMU.Parameters.
 * @author lizlooney@google.com (Liz Looney)
 */

// The following are generated dynamically in HardwareUtil.fetchJavaScriptForHardware():
// bno055imuParametersIdentifierForJavaScript
// The following are defined in vars.js:
// createNonEditableField
// getPropertyColor
// functionColor

Blockly.Blocks['bno055imuParameters_getProperty'] = {
  init: function() {
    var PROPERTY_CHOICES = [
        ['AccelUnit', 'AccelUnit'],
        ['AngleUnit', 'AngleUnit'],
        ['SensorMode', 'SensorMode'],
    ];
    this.setOutput(true); // no type, for compatibility
    this.appendDummyInput()
        .appendField(createNonEditableField('IMU-BNO055.Parameters'))
        .appendField('.')
        .appendField(new Blockly.FieldDropdown(PROPERTY_CHOICES), 'PROP');
    this.appendValueInput('BNO055IMU_PARAMETERS').setCheck('BNO055IMU.Parameters')
        .appendField('parameters')
        .setAlign(Blockly.ALIGN_RIGHT);
    this.setColour(getPropertyColor);
    // Assign 'this' to a variable for use in the tooltip closure below.
    var thisBlock = this;
    var TOOLTIPS = [
        ['AccelUnit', 'Returns the AccelUnit of the given IMU-BNO055.Parameters.'],
        ['AngleUnit', 'Returns the AngleUnit of the given IMU-BNO055.Parameters.'],
        ['SensorMode', 'Returns the SensorMode of the given IMU-BNO055.Parameters.'],
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

Blockly.JavaScript['bno055imuParameters_getProperty'] = function(block) {
  var property = block.getFieldValue('PROP');
  var bno055imuParameters = Blockly.JavaScript.valueToCode(
      block, 'BNO055IMU_PARAMETERS', Blockly.JavaScript.ORDER_NONE);
  var code = bno055imuParametersIdentifierForJavaScript + '.get' + property + '(' +
      bno055imuParameters + ')';
  return [code, Blockly.JavaScript.ORDER_FUNCTION_CALL];
};

Blockly.FtcJava['bno055imuParameters_getProperty'] = function(block) {
  var property = block.getFieldValue('PROP');
  var bno055imuParameters = Blockly.FtcJava.valueToCode(
      block, 'BNO055IMU_PARAMETERS', Blockly.FtcJava.ORDER_MEMBER);
  var code;
  switch (property) {
    case 'AngleUnit':
      code = bno055imuParameters + '.angleUnit.toAngleUnit()';
      return [code, Blockly.FtcJava.ORDER_FUNCTION_CALL];
    case 'SensorMode':
      property = 'mode';
      break;
    case 'AccelUnit':
      property = Blockly.FtcJava.makeFirstLetterLowerCase_(property);
      break;
    default:
      throw 'Unexpected property ' + property + ' (bno055imuParameters_getProperty).';
  }
  code = bno055imuParameters + '.' + property;
  return [code, Blockly.FtcJava.ORDER_MEMBER];
};

Blockly.Blocks['bno055imuParameters_getProperty_AccelUnit'] = {
  init: function() {
    var PROPERTY_CHOICES = [
        ['AccelUnit', 'AccelUnit'],
    ];
    this.setOutput(true, 'BNO055IMU.AccelUnit');
    this.appendDummyInput()
        .appendField(createNonEditableField('IMU-BNO055.Parameters'))
        .appendField('.')
        .appendField(new Blockly.FieldDropdown(PROPERTY_CHOICES), 'PROP');
    this.appendValueInput('BNO055IMU_PARAMETERS').setCheck('BNO055IMU.Parameters')
        .appendField('parameters')
        .setAlign(Blockly.ALIGN_RIGHT);
    this.setColour(getPropertyColor);
    // Assign 'this' to a variable for use in the tooltip closure below.
    var thisBlock = this;
    var TOOLTIPS = [
        ['AccelUnit', 'Returns the AccelUnit of the given IMU-BNO055.Parameters.'],
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

Blockly.JavaScript['bno055imuParameters_getProperty_AccelUnit'] =
    Blockly.JavaScript['bno055imuParameters_getProperty'];

Blockly.FtcJava['bno055imuParameters_getProperty_AccelUnit'] =
    Blockly.FtcJava['bno055imuParameters_getProperty'];

Blockly.Blocks['bno055imuParameters_getProperty_AngleUnit'] = {
  init: function() {
    var PROPERTY_CHOICES = [
        ['AngleUnit', 'AngleUnit'],
    ];
    this.setOutput(true, 'AngleUnit');
    this.appendDummyInput()
        .appendField(createNonEditableField('IMU-BNO055.Parameters'))
        .appendField('.')
        .appendField(new Blockly.FieldDropdown(PROPERTY_CHOICES), 'PROP');
    this.appendValueInput('BNO055IMU_PARAMETERS').setCheck('BNO055IMU.Parameters')
        .appendField('parameters')
        .setAlign(Blockly.ALIGN_RIGHT);
    this.setColour(getPropertyColor);
    // Assign 'this' to a variable for use in the tooltip closure below.
    var thisBlock = this;
    var TOOLTIPS = [
        ['AngleUnit', 'Returns the AngleUnit of the given IMU-BNO055.Parameters.'],
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

Blockly.JavaScript['bno055imuParameters_getProperty_AngleUnit'] =
    Blockly.JavaScript['bno055imuParameters_getProperty'];

Blockly.FtcJava['bno055imuParameters_getProperty_AngleUnit'] =
    Blockly.FtcJava['bno055imuParameters_getProperty'];

Blockly.Blocks['bno055imuParameters_getProperty_SensorMode'] = {
  init: function() {
    var PROPERTY_CHOICES = [
        ['SensorMode', 'SensorMode'],
    ];
    this.setOutput(true, 'BNO055IMU.SensorMode');
    this.appendDummyInput()
        .appendField(createNonEditableField('IMU-BNO055.Parameters'))
        .appendField('.')
        .appendField(new Blockly.FieldDropdown(PROPERTY_CHOICES), 'PROP');
    this.appendValueInput('BNO055IMU_PARAMETERS').setCheck('BNO055IMU.Parameters')
        .appendField('parameters')
        .setAlign(Blockly.ALIGN_RIGHT);
    this.setColour(getPropertyColor);
    // Assign 'this' to a variable for use in the tooltip closure below.
    var thisBlock = this;
    var TOOLTIPS = [
        ['SensorMode', 'Returns the SensorMode of the given IMU-BNO055.Parameters.'],
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

Blockly.JavaScript['bno055imuParameters_getProperty_SensorMode'] =
    Blockly.JavaScript['bno055imuParameters_getProperty'];

Blockly.FtcJava['bno055imuParameters_getProperty_SensorMode'] =
    Blockly.FtcJava['bno055imuParameters_getProperty'];

// Functions

Blockly.Blocks['bno055imuParameters_create'] = {
  init: function() {
    this.setOutput(true, 'BNO055IMU.Parameters');
    this.appendDummyInput()
        .appendField('new')
        .appendField(createNonEditableField('IMU-BNO055.Parameters'));
    this.setColour(functionColor);
    this.setTooltip('Creates a new IMU-BNO055.Parameters object.');
  }
};

Blockly.JavaScript['bno055imuParameters_create'] = function(block) {
  var code = bno055imuParametersIdentifierForJavaScript + '.create()';
  return [code, Blockly.JavaScript.ORDER_FUNCTION_CALL];
};

Blockly.FtcJava['bno055imuParameters_create'] = function(block) {
  var code = 'new BNO055IMU.Parameters()';
  Blockly.FtcJava.generateImport_('BNO055IMU');
  return [code, Blockly.FtcJava.ORDER_NEW];
};

Blockly.Blocks['bno055imuParameters_setAccelUnit'] = {
  init: function() {
    this.appendDummyInput()
        .appendField('call')
        .appendField(createNonEditableField('IMU-BNO055.Parameters'))
        .appendField('.')
        .appendField(createNonEditableField('setAccelUnit'));
    this.appendValueInput('BNO055IMU_PARAMETERS').setCheck('BNO055IMU.Parameters')
        .appendField('parameters')
        .setAlign(Blockly.ALIGN_RIGHT);
    this.appendValueInput('ACCEL_UNIT').setCheck('BNO055IMU.AccelUnit')
        .appendField('accelUnit')
        .setAlign(Blockly.ALIGN_RIGHT);
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setColour(functionColor);
    this.setTooltip('Sets the AccelUnit for the IMU-BNO055 sensor.');
  }
};

Blockly.JavaScript['bno055imuParameters_setAccelUnit'] = function(block) {
  var bno055imuParameters = Blockly.JavaScript.valueToCode(
      block, 'BNO055IMU_PARAMETERS', Blockly.JavaScript.ORDER_COMMA);
  var accelUnit = Blockly.JavaScript.valueToCode(
      block, 'ACCEL_UNIT', Blockly.JavaScript.ORDER_COMMA);
  return bno055imuParametersIdentifierForJavaScript + '.setAccelUnit(' +
      bno055imuParameters + ', ' + accelUnit + ');\n';
};

Blockly.FtcJava['bno055imuParameters_setAccelUnit'] = function(block) {
  var bno055imuParameters = Blockly.FtcJava.valueToCode(
      block, 'BNO055IMU_PARAMETERS', Blockly.FtcJava.ORDER_MEMBER);
  var accelUnit = Blockly.FtcJava.valueToCode(
      block, 'ACCEL_UNIT', Blockly.FtcJava.ORDER_ASSIGNMENT);
  return bno055imuParameters + '.accelUnit = ' + accelUnit + ';\n';
};

Blockly.Blocks['bno055imuParameters_setAngleUnit'] = {
  init: function() {
    this.appendDummyInput()
        .appendField('call')
        .appendField(createNonEditableField('IMU-BNO055.Parameters'))
        .appendField('.')
        .appendField(createNonEditableField('setAngleUnit'));
    this.appendValueInput('BNO055IMU_PARAMETERS').setCheck('BNO055IMU.Parameters')
        .appendField('parameters')
        .setAlign(Blockly.ALIGN_RIGHT);
    this.appendValueInput('ANGLE_UNIT').setCheck('AngleUnit')
        .appendField('angleUnit')
        .setAlign(Blockly.ALIGN_RIGHT);
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setColour(functionColor);
    this.setTooltip('Sets the AngleUnit for the IMU-BNO055 sensor.');
  }
};

Blockly.JavaScript['bno055imuParameters_setAngleUnit'] = function(block) {
  var bno055imuParameters = Blockly.JavaScript.valueToCode(
      block, 'BNO055IMU_PARAMETERS', Blockly.JavaScript.ORDER_COMMA);
  var angleUnit = Blockly.JavaScript.valueToCode(
      block, 'ANGLE_UNIT', Blockly.JavaScript.ORDER_COMMA);
  return bno055imuParametersIdentifierForJavaScript + '.setAngleUnit(' +
      bno055imuParameters + ', ' + angleUnit + ');\n';
};

Blockly.FtcJava['bno055imuParameters_setAngleUnit'] = function(block) {
  var bno055imuParameters = Blockly.FtcJava.valueToCode(
      block, 'BNO055IMU_PARAMETERS', Blockly.FtcJava.ORDER_MEMBER);
  var angleUnit = Blockly.FtcJava.valueToCode(
      block, 'ANGLE_UNIT', Blockly.FtcJava.ORDER_ASSIGNMENT);
  if (angleUnit.startsWith('AngleUnit.')) {
    angleUnit = 'BNO055IMU.' + angleUnit;
  } else {
    angleUnit = Blockly.FtcJava.valueToCode(
        block, 'ANGLE_UNIT', Blockly.FtcJava.ORDER_NONE);
    angleUnit = 'BNO055IMU.AngleUnit.fromAngleUnit(' + angleUnit + ')';
  }
  Blockly.FtcJava.generateImport_('BNO055IMU');
  return bno055imuParameters + '.angleUnit = ' + angleUnit + ';\n';
};

Blockly.Blocks['bno055imuParameters_setSensorMode'] = {
  init: function() {
    this.appendDummyInput()
        .appendField('call')
        .appendField(createNonEditableField('IMU-BNO055.Parameters'))
        .appendField('.')
        .appendField(createNonEditableField('setSensorMode'));
    this.appendValueInput('BNO055IMU_PARAMETERS').setCheck('BNO055IMU.Parameters')
        .appendField('parameters')
        .setAlign(Blockly.ALIGN_RIGHT);
    this.appendValueInput('SENSOR_MODE').setCheck('BNO055IMU.SensorMode')
        .appendField('sensorMode')
        .setAlign(Blockly.ALIGN_RIGHT);
    this.setPreviousStatement(true);
    this.setNextStatement(true);
    this.setColour(functionColor);
    this.setTooltip('Sets the SensorMode for the IMU-BNO055 sensor.');
  }
};

Blockly.JavaScript['bno055imuParameters_setSensorMode'] = function(block) {
  var bno055imuParameters = Blockly.JavaScript.valueToCode(
      block, 'BNO055IMU_PARAMETERS', Blockly.JavaScript.ORDER_COMMA);
  var sensorMode = Blockly.JavaScript.valueToCode(
      block, 'SENSOR_MODE', Blockly.JavaScript.ORDER_COMMA);
  return bno055imuParametersIdentifierForJavaScript + '.setSensorMode(' +
      bno055imuParameters + ', ' + sensorMode + ');\n';
};

Blockly.FtcJava['bno055imuParameters_setSensorMode'] = function(block) {
  var bno055imuParameters = Blockly.FtcJava.valueToCode(
      block, 'BNO055IMU_PARAMETERS', Blockly.FtcJava.ORDER_MEMBER);
  var sensorMode = Blockly.FtcJava.valueToCode(
      block, 'SENSOR_MODE', Blockly.FtcJava.ORDER_ASSIGNMENT);
  return bno055imuParameters + '.mode = ' + sensorMode + ';\n';
};

// Enums

Blockly.Blocks['bno055imuParameters_enum_accelUnit'] = {
  init: function() {
    var ACCEL_UNIT_CHOICES = [
        ['METERS_PERSEC_PERSEC', 'METERS_PERSEC_PERSEC'],
        ['MILLI_EARTH_GRAVITY', 'MILLI_EARTH_GRAVITY'],
    ];
    this.setOutput(true); // no type, for compatibility
    this.appendDummyInput()
        .appendField(createNonEditableField('AccelUnit'))
        .appendField('.')
        .appendField(new Blockly.FieldDropdown(ACCEL_UNIT_CHOICES), 'ACCEL_UNIT');
    this.setColour(getPropertyColor);
    // Assign 'this' to a variable for use in the tooltip closure below.
    var thisBlock = this;
    var TOOLTIPS = [
        ['METERS_PERSEC_PERSEC', 'The AccelUnit value METERS_PERSEC_PERSEC.'],
        ['MILLI_EARTH_GRAVITY', 'The AccelUnit value MILLI_EARTH_GRAVITY.'],
    ];
    this.setTooltip(function() {
      var key = thisBlock.getFieldValue('ACCEL_UNIT');
      for (var i = 0; i < TOOLTIPS.length; i++) {
        if (TOOLTIPS[i][0] == key) {
          return TOOLTIPS[i][1];
        }
      }
      return '';
    });
  }
};

Blockly.JavaScript['bno055imuParameters_enum_accelUnit'] = function(block) {
  var code = '"' + block.getFieldValue('ACCEL_UNIT') + '"';
  return [code, Blockly.JavaScript.ORDER_ATOMIC];
};

Blockly.FtcJava['bno055imuParameters_enum_accelUnit'] = function(block) {
  var code = 'BNO055IMU.AccelUnit.' + block.getFieldValue('ACCEL_UNIT');
  Blockly.FtcJava.generateImport_('BNO055IMU');
  return [code, Blockly.FtcJava.ORDER_MEMBER];
};

Blockly.Blocks['bno055imuParameters_typedEnum_accelUnit'] = {
  init: function() {
    var ACCEL_UNIT_CHOICES = [
        ['METERS_PERSEC_PERSEC', 'METERS_PERSEC_PERSEC'],
        ['MILLI_EARTH_GRAVITY', 'MILLI_EARTH_GRAVITY'],
    ];
    this.setOutput(true, 'BNO055IMU.AccelUnit');
    this.appendDummyInput()
        .appendField(createNonEditableField('AccelUnit'))
        .appendField('.')
        .appendField(new Blockly.FieldDropdown(ACCEL_UNIT_CHOICES), 'ACCEL_UNIT');
    this.setColour(getPropertyColor);
    // Assign 'this' to a variable for use in the tooltip closure below.
    var thisBlock = this;
    var TOOLTIPS = [
        ['METERS_PERSEC_PERSEC', 'The AccelUnit value METERS_PERSEC_PERSEC.'],
        ['MILLI_EARTH_GRAVITY', 'The AccelUnit value MILLI_EARTH_GRAVITY.'],
    ];
    this.setTooltip(function() {
      var key = thisBlock.getFieldValue('ACCEL_UNIT');
      for (var i = 0; i < TOOLTIPS.length; i++) {
        if (TOOLTIPS[i][0] == key) {
          return TOOLTIPS[i][1];
        }
      }
      return '';
    });
  }
};

Blockly.JavaScript['bno055imuParameters_typedEnum_accelUnit'] =
    Blockly.JavaScript['bno055imuParameters_enum_accelUnit'];

Blockly.FtcJava['bno055imuParameters_typedEnum_accelUnit'] =
    Blockly.FtcJava['bno055imuParameters_enum_accelUnit'];

Blockly.Blocks['bno055imuParameters_enum_sensorMode'] = {
  init: function() {
    var SENSOR_MODE_CHOICES = [
        /*['ACCONLY', 'ACCONLY'],
        ['MAGONLY', 'MAGONLY'],
        ['GYRONLY', 'GYRONLY'],
        ['ACCMAG', 'ACCMAG'],
        ['ACCGYRO', 'ACCGYRO'],
        ['MAGGYRO', 'MAGGYRO'],
        ['AMG', 'AMG'],*/
        ['IMU', 'IMU'],
        /*['COMPASS', 'COMPASS'],
        ['M4G', 'M4G'],
        ['NDOF_FMC_OFF', 'NDOF_FMC_OFF'],
        ['NDOF', 'NDOF'],*/
    ];
    this.setOutput(true, 'BNO055IMU.SensorMode');
    this.appendDummyInput()
        .appendField(createNonEditableField('SensorMode'))
        .appendField('.')
        .appendField(new Blockly.FieldDropdown(SENSOR_MODE_CHOICES),
            'SENSOR_MODE');
    this.setColour(getPropertyColor);
    // Assign 'this' to a variable for use in the tooltip closure below.
    var thisBlock = this;
    var TOOLTIPS = [
        /*['ACCONLY', 'The IMU-BNO055 SensorMode value ACCONLY'],
        ['MAGONLY', 'The IMU-BNO055 SensorMode value MAGONLY'],
        ['GYRONLY', 'The IMU-BNO055 SensorMode value GYRONLY'],
        ['ACCMAG', 'The IMU-BNO055 SensorMode value ACCMAG'],
        ['ACCGYRO', 'The IMU-BNO055 SensorMode value ACCGYRO'],
        ['MAGGYRO', 'The IMU-BNO055 SensorMode value MAGGYRO'],
        ['AMG', 'The IMU-BNO055 SensorMode value AMG'],*/
        ['IMU', 'The IMU-BNO055 SensorMode value IMU'],
        /*['COMPASS', 'The IMU-BNO055 SensorMode value COMPASS'],
        ['M4G', 'The IMU-BNO055 SensorMode value M4G'],
        ['NDOF_FMC_OFF', 'The IMU-BNO055 SensorMode value NDOF_FMC_OFF'],
        ['NDOF', 'The IMU-BNO055 SensorMode value NDOF'],*/
    ];
    this.setTooltip(function() {
      var key = thisBlock.getFieldValue('SENSOR_MODE');
      for (var i = 0; i < TOOLTIPS.length; i++) {
        if (TOOLTIPS[i][0] == key) {
          return TOOLTIPS[i][1];
        }
      }
      return '';
    });
  }
};

Blockly.JavaScript['bno055imuParameters_enum_sensorMode'] = function(block) {
  var code = '"' + block.getFieldValue('SENSOR_MODE') + '"';
  return [code, Blockly.JavaScript.ORDER_ATOMIC];
};

Blockly.FtcJava['bno055imuParameters_enum_sensorMode'] = function(block) {
  var code = 'BNO055IMU.SensorMode.' + block.getFieldValue('SENSOR_MODE');
  Blockly.FtcJava.generateImport_('BNO055IMU');
  return [code, Blockly.FtcJava.ORDER_MEMBER];
};

Blockly.Blocks['bno055imuParameters_typedEnum_sensorMode'] =
    Blockly.Blocks['bno055imuParameters_enum_sensorMode'];

Blockly.JavaScript['bno055imuParameters_typedEnum_sensorMode'] =
    Blockly.JavaScript['bno055imuParameters_enum_sensorMode'];

Blockly.FtcJava['bno055imuParameters_typedEnum_sensorMode'] =
    Blockly.FtcJava['bno055imuParameters_enum_sensorMode'];
