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

package com.google.blocks.ftcrobotcontroller.hardware;

import static com.google.blocks.ftcrobotcontroller.util.CurrentGame.CURRENT_GAME_NAME;
import static com.google.blocks.ftcrobotcontroller.util.CurrentGame.TFOD_CURRENT_GAME_BLOCKS_FIRST_NAME;
import static com.google.blocks.ftcrobotcontroller.util.CurrentGame.VUFORIA_CURRENT_GAME_BLOCKS_FIRST_NAME;
import static com.google.blocks.ftcrobotcontroller.util.ProjectsUtil.escapeSingleQuotes;

import android.content.Context;
import android.content.res.AssetManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.google.blocks.ftcrobotcontroller.util.AvailableTtsLocalesProvider;
import com.google.blocks.ftcrobotcontroller.util.FileUtil;
import com.google.blocks.ftcrobotcontroller.util.Identifier;
import com.google.blocks.ftcrobotcontroller.util.ProjectsUtil;
import com.google.blocks.ftcrobotcontroller.util.SoundsUtil;
import com.google.blocks.ftcrobotcontroller.util.ToolboxFolder;
import com.google.blocks.ftcrobotcontroller.util.ToolboxIcon;
import com.google.blocks.ftcrobotcontroller.util.ToolboxUtil;
import com.qualcomm.ftccommon.configuration.RobotConfigFile;
import com.qualcomm.ftccommon.configuration.RobotConfigFileManager;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver.BlinkinPattern;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.configuration.DeviceConfiguration;
import com.qualcomm.robotcore.util.RobotLog;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.ExportToBlocks;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.android.AndroidSoundPool;
import org.firstinspires.ftc.robotcore.external.android.AndroidTextToSpeech;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaCurrentGame;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaRoverRuckus;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaSkyStone;
import org.firstinspires.ftc.robotcore.external.tfod.TfodCurrentGame;
import org.firstinspires.ftc.robotcore.external.tfod.TfodRoverRuckus;
import org.firstinspires.ftc.robotcore.external.tfod.TfodSkyStone;
import org.firstinspires.ftc.robotcore.internal.opmode.BlocksClassFilter;
import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;
import org.firstinspires.ftc.robotcore.internal.opmode.RegisteredOpModes;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

/**
 * A class that provides utility methods related to hardware.
 *
 * @author lizlooney@google.com (Liz Looney)
 */
public class HardwareUtil {
  private static final SensorManager sensorManager = (SensorManager) AppUtil.getDefContext().getSystemService(Context.SENSOR_SERVICE);

  private static final String DC_MOTOR_EX_CATEGORY_NAME = "Extended";
  private static final String DC_MOTOR_DUAL_CATEGORY_NAME = "Dual";
  private static final String GAMEPAD_CATEGORY_NAME = "Gamepad"; // see toolbox/gamepad.xml
  private static final String LINEAR_OP_MODE_CATEGORY_NAME = "LinearOpMode"; // see toolbox/linear_op_mode.xml
  private static final String COLOR_CATEGORY_NAME = "Color"; // see toolbox/utilities.xml
  private static final String ELAPSED_TIME_CATEGORY_NAME = "ElapsedTime"; // see toolbox/utilities.xml

  public static final String SWITCHABLE_CAMERA_NAME = "Switchable Camera";

  public enum Capability {
    CAMERA("camera"),
    WEBCAM("webcam"),
    SWITCHABLE_CAMERA("switchableCamera"),
    VUFORIA("vuforia"),
    TFOD("tfod");

    private final String placeholderType;

    Capability(String placeholderType) {
      this.placeholderType = placeholderType;
    }

    static Capability fromPlaceholderType(String type) {
      for (Capability capability : Capability.values()) {
        if (capability.placeholderType.equals(type)) {
          return capability;
        }
      }
      throw new IllegalArgumentException("Unexpected capability name " + type);
    }
  }

  private static final Set<String> reservedWordsForFtcJava = buildReservedWordsForFtcJava();

  /**
   * A {@link Map} from xmlTag to List of {@link HardwareType}.
   */
  private static final Map<String, List<HardwareType>> XML_TAG_TO_HARDWARE_TYPES =
      new HashMap<String, List<HardwareType>>();
  static {
    for (HardwareType hardwareType : HardwareType.values()) {
      for (String xmlTag : hardwareType.xmlTags) {
        List<HardwareType> list = (List<HardwareType>) XML_TAG_TO_HARDWARE_TYPES.get(xmlTag);
        if (list == null) {
          list = new ArrayList<HardwareType>();
          XML_TAG_TO_HARDWARE_TYPES.put(xmlTag, list);
        }
        list.add(hardwareType);
      }
    }
  }

  // Prevent instantiation of util class.
  private HardwareUtil() {
  }

  /**
   * Returns the corresponding {@link HardwareType}s for the given XML tag.
   */
  // visible for testing
  static Iterable<HardwareType> getHardwareTypes(String xmlTag) {
    return XML_TAG_TO_HARDWARE_TYPES.containsKey(xmlTag)
        ? Collections.<HardwareType>unmodifiableList(XML_TAG_TO_HARDWARE_TYPES.get(xmlTag))
        : Collections.<HardwareType>emptyList();
  }

  /**
   * Returns the corresponding {@link HardwareType}s for the given {@link DeviceConfiguration}.
   */
  static Iterable<HardwareType> getHardwareTypes(DeviceConfiguration deviceConfiguration) {
    return getHardwareTypes(deviceConfiguration.getConfigurationType().getXmlTag());
  }

  /**
   * Returns the JavaScript code related to the hardware in the active configuration.
   */
  public static String fetchJavaScriptForHardware() throws IOException {
    return fetchJavaScriptForHardware(HardwareItemMap.newHardwareItemMap());
  }

  /**
   * Returns the JavaScript code related to the hardware in the given {@link HardwareItem}.
   */
  // visible for testing
  public static String fetchJavaScriptForHardware(HardwareItemMap hardwareItemMap) throws IOException {
    Context context = AppUtil.getDefContext();
    AssetManager assetManager = context.getAssets();
    StringBuilder jsHardware = new StringBuilder().append("\n");
    Map<Capability, Boolean> capabilities = getCapabilities(hardwareItemMap);

    Set<String> additionalReservedWordsForFtcJava = new HashSet<>();
    Set<String> methodLookupStrings = new HashSet<>();
    String toolbox = generateToolbox(hardwareItemMap, capabilities, assetManager, additionalReservedWordsForFtcJava, methodLookupStrings)
        .replace("\n", " ")
        .replaceAll("\\> +\\<", "><");
    // The toolbox is added at the end, because it makes it easier to troubleshoot problems with
    // this code.

    Set<String> teleOpNames = new TreeSet<>();
    RegisteredOpModes registeredOpModes = RegisteredOpModes.getInstance();
    registeredOpModes.waitOpModesRegistered();
    for (OpModeMeta opModeMeta : registeredOpModes.getOpModes()) {
      if (opModeMeta.flavor == OpModeMeta.Flavor.TELEOP) {
        teleOpNames.add(opModeMeta.name);
      }
    }
    jsHardware.append("var AUTO_TRANSITION_OPTIONS = [\n");
    for (String teleOpName : teleOpNames) {
      jsHardware.append("  '").append(escapeSingleQuotes(teleOpName)).append("',\n");
    }
    jsHardware.append("];\n\n");

    jsHardware
        .append("var currentGameName = '").append(escapeSingleQuotes(CURRENT_GAME_NAME)).append("';\n")
        .append("var tfodCurrentGameBlocksFirstName = '").append(escapeSingleQuotes(TFOD_CURRENT_GAME_BLOCKS_FIRST_NAME)).append("';\n")
        .append("var vuforiaCurrentGameBlocksFirstName = '").append(escapeSingleQuotes(VUFORIA_CURRENT_GAME_BLOCKS_FIRST_NAME)).append("';\n")
        .append("\n");

    jsHardware
        .append("var methodLookupStrings = [\n");
    for (String methodLookupString : methodLookupStrings) {
      jsHardware.append("  '").append(methodLookupString).append("',\n");
    }
    jsHardware
        .append("];\n\n");


    jsHardware
        .append("function isValidProjectName(projectName) {\n")
        .append("  if (projectName) {\n")
        .append("    return /").append(ProjectsUtil.VALID_PROJECT_REGEX).append("/.test(projectName);\n")
        .append("  }\n")
        .append("  return false;\n")
        .append("}\n\n");

    jsHardware
        .append("function isValidSoundName(soundName) {\n")
        .append("  if (soundName) {\n")
        .append("    return /").append(SoundsUtil.VALID_SOUND_REGEX).append("/.test(soundName);\n")
        .append("  }\n")
        .append("  return false;\n")
        .append("}\n\n");

    StringBuilder blinkinPatternTooltips = new StringBuilder();
    StringBuilder blinkinPatternFromTextTooltip = new StringBuilder();
    blinkinPatternTooltips
        .append("var BLINKIN_PATTERN_TOOLTIPS = [\n");
    blinkinPatternFromTextTooltip
        .append("var BLINKIN_PATTERN_FROM_TEXT_TOOLTIP =\n")
        .append("    'Returns the pattern associated with the given text. Valid input is ' +\n");
    BlinkinPattern[] blinkinPatterns = BlinkinPattern.values();
    BlinkinPattern blinkinPattern = blinkinPatterns[0];
    for (int i = 0; i < blinkinPatterns.length - 1; blinkinPattern = blinkinPatterns[++i]) {
      blinkinPatternTooltips
          .append("  ['").append(blinkinPattern).append("', 'The BlinkinPattern value ")
          .append(blinkinPattern).append(".'],\n");
      blinkinPatternFromTextTooltip
          .append("    '").append(blinkinPattern).append(", ' +\n");
    }
    blinkinPatternTooltips
        .append("];\n");
    blinkinPatternFromTextTooltip
      .append("    'or ").append(blinkinPattern).append(".';\n");
    jsHardware
        .append(blinkinPatternTooltips)
        .append("\n")
        .append(blinkinPatternFromTextTooltip)
        .append("\n");

    // Locales
    SortedMap<String, String> languageCodes = new TreeMap<String, String>();
    SortedMap<String, String> countryCodes = new TreeMap<String, String>();
    for (Locale locale : AvailableTtsLocalesProvider.getInstance().getAvailableTtsLocales()) {
      languageCodes.put(locale.getLanguage(), locale.getDisplayLanguage());
      String countryCode = locale.getCountry();
      if (!countryCode.isEmpty()) {
        countryCodes.put(countryCode, locale.getDisplayCountry());
      }
    }
    Locale defaultLocale = Locale.getDefault();
    // Languages
    String defaultLanguageCode = defaultLocale.getLanguage();
    StringBuilder createLanguageCodeDropdown = new StringBuilder();
    StringBuilder languageCodeTooltips = new StringBuilder();
    createLanguageCodeDropdown
        .append("function createLanguageCodeDropdown() {\n")
        .append("  var CHOICES = [\n");
    languageCodeTooltips
        .append("var LANGUAGE_CODE_TOOLTIPS = [\n");
    // First item is the default language.
    addLanguage(defaultLanguageCode, defaultLocale.getDisplayLanguage(), createLanguageCodeDropdown, languageCodeTooltips);
    // Remaining lanuages are sorted alphabetically.
    for (Map.Entry<String, String> entry : languageCodes.entrySet()) {
      String languageCode = entry.getKey();
      if (!languageCode.equals(defaultLanguageCode)) {
        String languageName = entry.getValue();
        addLanguage(languageCode, languageName, createLanguageCodeDropdown, languageCodeTooltips);
      }
    }
    createLanguageCodeDropdown.append("  ];\n")
        .append("  return createFieldDropdown(CHOICES);\n")
        .append("}\n\n");
    languageCodeTooltips
        .append("];\n");
    jsHardware
        .append(createLanguageCodeDropdown)
        .append(languageCodeTooltips)
        .append("\n");
    // Countries
    String defaultCountryCode = defaultLocale.getCountry();
    StringBuilder createCountryCodeDropdown = new StringBuilder();
    StringBuilder countryCodeTooltips = new StringBuilder();
    createCountryCodeDropdown
        .append("function createCountryCodeDropdown() {\n")
        .append("  var CHOICES = [\n");
    countryCodeTooltips
        .append("var COUNTRY_CODE_TOOLTIPS = [\n");
    // First item is the default country.
    addCountry(defaultCountryCode, defaultLocale.getDisplayCountry(), createCountryCodeDropdown, countryCodeTooltips);
    // Remaining countries are sorted alphabetically.
    for (Map.Entry<String, String> entry : countryCodes.entrySet()) {
      String countryCode = entry.getKey();
      if (!countryCode.equals(defaultCountryCode)) {
        String countryName = entry.getValue();
        addCountry(countryCode, countryName, createCountryCodeDropdown, countryCodeTooltips);
      }
    }
    createCountryCodeDropdown.append("  ];\n")
        .append("  return createFieldDropdown(CHOICES);\n")
        .append("}\n\n");
    countryCodeTooltips
        .append("];\n");
    jsHardware
        .append(createCountryCodeDropdown)
        .append(countryCodeTooltips)
        .append("\n");

    // SkyStone sound resources
    StringBuilder createSkyStoneSoundResourceDropdown = new StringBuilder();
    StringBuilder skyStoneSoundResourceTooltips = new StringBuilder();
    createSkyStoneSoundResourceDropdown
        .append("function createSkyStoneSoundResourceDropdown() {\n")
        .append("  var CHOICES = [\n");
    skyStoneSoundResourceTooltips
        .append("var SKY_STONE_SOUND_RESOURCE_TOOLTIPS = [\n");
    List<String> resourceNames = new ArrayList<String>();
    for (java.lang.reflect.Field field : com.qualcomm.ftccommon.R.raw.class.getFields()) {
      String resourceName = field.getName();
      if (resourceName.toUpperCase().startsWith("SS_")) {
        resourceNames.add(resourceName);
      }
    }
    Collections.sort(resourceNames);
    for (String resourceName : resourceNames) {
      createSkyStoneSoundResourceDropdown
          .append("      ['")
          .append(escapeSingleQuotes(makeVisibleNameForDropdownItem(resourceName)))
          .append("', '")
          .append(escapeSingleQuotes(resourceName))
          .append("'],\n");
      skyStoneSoundResourceTooltips
          .append("  ['")
          .append(escapeSingleQuotes(resourceName))
          .append("', 'The SoundResource value ")
          .append(escapeSingleQuotes(resourceName))
          .append(".'],\n");
    }
    createSkyStoneSoundResourceDropdown.append("  ];\n")
        .append("  return createFieldDropdown(CHOICES);\n")
        .append("}\n\n");
    skyStoneSoundResourceTooltips
        .append("];\n");
    jsHardware
        .append(createSkyStoneSoundResourceDropdown)
        .append(skyStoneSoundResourceTooltips)
        .append("\n");

    jsHardware
        .append("var androidSoundPoolRawResPrefix = '")
        .append(AndroidSoundPool.RAW_RES_PREFIX)
        .append("';\n");

    // Identifiers
    for (Identifier identifier : Identifier.values()) {
      if (identifier.variableForJavaScript != null) {
        jsHardware
            .append("var ").append(identifier.variableForJavaScript).append(" = '")
            .append(identifier.identifierForJavaScript).append("';\n");
      }
      if (identifier.variableForFtcJava != null) {
        jsHardware
            .append("var ").append(identifier.variableForFtcJava).append(" = '")
            .append(identifier.identifierForFtcJava).append("';\n");
      }
    }

    jsHardware.append("\n");

    // Webcam device names
    jsHardware
        .append("function createWebcamDeviceNameDropdown() {\n")
        .append("  var CHOICES = [\n");
    List<HardwareItem> hardwareItemsForWebcam =
        hardwareItemMap.getHardwareItems(HardwareType.WEBCAM_NAME);
    for (HardwareItem hardwareItemForWebcam : hardwareItemsForWebcam) {
      jsHardware
          .append("    ['").append(escapeSingleQuotes(hardwareItemForWebcam.visibleName)).append("', '")
          .append(escapeSingleQuotes(hardwareItemForWebcam.deviceName)).append("'],\n");
    }
    jsHardware
        .append("  ];\n")
        .append("  return createFieldDropdown(CHOICES);\n")
        .append("}\n\n");

    jsHardware
        .append("var switchableCameraName = '")
        .append(SWITCHABLE_CAMERA_NAME)
        .append("';\n");

    // TFOD Rover Ruckus labels
    StringBuilder createTfodRoverRuckusLabelDropdown = new StringBuilder();
    StringBuilder tfodRoverRuckusLabelTooltips = new StringBuilder();
    createTfodRoverRuckusLabelDropdown
        .append("function createTfodRoverRuckusLabelDropdown() {\n")
        .append("  var CHOICES = [\n");
    tfodRoverRuckusLabelTooltips
        .append("var TFOD_ROVER_RUCKUS_LABEL_TOOLTIPS = [\n");
    for (String tfodLabel : TfodRoverRuckus.LABELS) {
      createTfodRoverRuckusLabelDropdown
          .append("      ['").append(escapeSingleQuotes(makeVisibleNameForDropdownItem(tfodLabel))).append("', '")
          .append(escapeSingleQuotes(tfodLabel)).append("'],\n");
      tfodRoverRuckusLabelTooltips
          .append("  ['").append(escapeSingleQuotes(tfodLabel)).append("', 'The Label value ")
          .append(escapeSingleQuotes(tfodLabel)).append(".'],\n");
    }
    createTfodRoverRuckusLabelDropdown.append("  ];\n")
        .append("  return createFieldDropdown(CHOICES);\n")
        .append("}\n\n");
    tfodRoverRuckusLabelTooltips
        .append("];\n");
    jsHardware
        .append(createTfodRoverRuckusLabelDropdown)
        .append(tfodRoverRuckusLabelTooltips)
        .append("\n");

    // Rover Ruckus trackable names
    StringBuilder createVuforiaRoverRuckusTrackableNameDropdown = new StringBuilder();
    StringBuilder vuforiaRoverRuckusTrackableNameTooltips = new StringBuilder();
    createVuforiaRoverRuckusTrackableNameDropdown
        .append("function createVuforiaRoverRuckusTrackableNameDropdown() {\n")
        .append("  var CHOICES = [\n");
    vuforiaRoverRuckusTrackableNameTooltips
        .append("var VUFORIA_ROVER_RUCKUS_TRACKABLE_NAME_TOOLTIPS = [\n");
    for (String trackableName : VuforiaRoverRuckus.TRACKABLE_NAMES) {
      createVuforiaRoverRuckusTrackableNameDropdown
          .append("      ['").append(escapeSingleQuotes(makeVisibleNameForDropdownItem(trackableName))).append("', '")
          .append(escapeSingleQuotes(trackableName)).append("'],\n");
      vuforiaRoverRuckusTrackableNameTooltips
          .append("  ['").append(escapeSingleQuotes(trackableName)).append("', 'The TrackableName value ")
          .append(escapeSingleQuotes(trackableName)).append(".'],\n");
    }
    createVuforiaRoverRuckusTrackableNameDropdown.append("  ];\n")
        .append("  return createFieldDropdown(CHOICES);\n")
        .append("}\n\n");
    vuforiaRoverRuckusTrackableNameTooltips
        .append("];\n");
    jsHardware
        .append(createVuforiaRoverRuckusTrackableNameDropdown)
        .append(vuforiaRoverRuckusTrackableNameTooltips)
        .append("\n");

    // SKYSTONE tfod labels
    StringBuilder createTfodSkyStoneLabelDropdown = new StringBuilder();
    StringBuilder tfodSkyStoneLabelTooltips = new StringBuilder();
    createTfodSkyStoneLabelDropdown
        .append("function createTfodSkyStoneLabelDropdown() {\n")
        .append("  var CHOICES = [\n");
    tfodSkyStoneLabelTooltips
        .append("var TFOD_SKY_STONE_LABEL_TOOLTIPS = [\n");
    for (String tfodLabel : TfodSkyStone.LABELS) {
      createTfodSkyStoneLabelDropdown
          .append("      ['").append(escapeSingleQuotes(makeVisibleNameForDropdownItem(tfodLabel))).append("', '")
          .append(escapeSingleQuotes(tfodLabel)).append("'],\n");
      tfodSkyStoneLabelTooltips
          .append("  ['").append(escapeSingleQuotes(tfodLabel)).append("', 'The Label value ")
          .append(escapeSingleQuotes(tfodLabel)).append(".'],\n");
    }
    createTfodSkyStoneLabelDropdown.append("  ];\n")
        .append("  return createFieldDropdown(CHOICES);\n")
        .append("}\n\n");
    tfodSkyStoneLabelTooltips
        .append("];\n");
    jsHardware
        .append(createTfodSkyStoneLabelDropdown)
        .append(tfodSkyStoneLabelTooltips)
        .append("\n");

    // SKYSTONE trackable names
    StringBuilder createVuforiaSkyStoneTrackableNameDropdown = new StringBuilder();
    StringBuilder vuforiaSkyStoneTrackableNameTooltips = new StringBuilder();
    createVuforiaSkyStoneTrackableNameDropdown
        .append("function createVuforiaSkyStoneTrackableNameDropdown() {\n")
        .append("  var CHOICES = [\n");
    vuforiaSkyStoneTrackableNameTooltips
        .append("var VUFORIA_SKY_STONE_TRACKABLE_NAME_TOOLTIPS = [\n");
    for (String trackableName : VuforiaSkyStone.TRACKABLE_NAMES) {
      createVuforiaSkyStoneTrackableNameDropdown
          .append("      ['").append(escapeSingleQuotes(makeVisibleNameForDropdownItem(trackableName))).append("', '")
          .append(escapeSingleQuotes(trackableName)).append("'],\n");
      vuforiaSkyStoneTrackableNameTooltips
          .append("  ['").append(escapeSingleQuotes(trackableName)).append("', 'The TrackableName value ")
          .append(escapeSingleQuotes(trackableName)).append(".'],\n");
    }
    createVuforiaSkyStoneTrackableNameDropdown.append("  ];\n")
        .append("  return createFieldDropdown(CHOICES);\n")
        .append("}\n\n");
    vuforiaSkyStoneTrackableNameTooltips
        .append("];\n");
    jsHardware
        .append(createVuforiaSkyStoneTrackableNameDropdown)
        .append(vuforiaSkyStoneTrackableNameTooltips)
        .append("\n");

    // Current game tfod labels
    StringBuilder createTfodCurrentGameLabelDropdown = new StringBuilder();
    StringBuilder tfodCurrentGameLabelTooltips = new StringBuilder();
    createTfodCurrentGameLabelDropdown
        .append("function createTfodCurrentGameLabelDropdown() {\n")
        .append("  var CHOICES = [\n");
    tfodCurrentGameLabelTooltips
        .append("var TFOD_CURRENT_GAME_LABEL_TOOLTIPS = [\n");
    for (String tfodLabel : TfodCurrentGame.LABELS) {
      createTfodCurrentGameLabelDropdown
          .append("      ['").append(escapeSingleQuotes(makeVisibleNameForDropdownItem(tfodLabel))).append("', '")
          .append(escapeSingleQuotes(tfodLabel)).append("'],\n");
      tfodCurrentGameLabelTooltips
          .append("  ['").append(escapeSingleQuotes(tfodLabel)).append("', 'The Label value ")
          .append(escapeSingleQuotes(tfodLabel)).append(".'],\n");
    }
    createTfodCurrentGameLabelDropdown.append("  ];\n")
        .append("  return createFieldDropdown(CHOICES);\n")
        .append("}\n\n");
    tfodCurrentGameLabelTooltips
        .append("];\n");
    jsHardware
        .append(createTfodCurrentGameLabelDropdown)
        .append(tfodCurrentGameLabelTooltips)
        .append("\n");

    // Current game vuforia trackable names
    StringBuilder createVuforiaCurrentGameTrackableNameDropdown = new StringBuilder();
    StringBuilder vuforiaCurrentGameTrackableNameTooltips = new StringBuilder();
    createVuforiaCurrentGameTrackableNameDropdown
        .append("function createVuforiaCurrentGameTrackableNameDropdown() {\n")
        .append("  var CHOICES = [\n");
    vuforiaCurrentGameTrackableNameTooltips
        .append("var VUFORIA_CURRENT_GAME_TRACKABLE_NAME_TOOLTIPS = [\n");
    for (String trackableName : VuforiaCurrentGame.TRACKABLE_NAMES) {
      createVuforiaCurrentGameTrackableNameDropdown
          .append("      ['").append(escapeSingleQuotes(makeVisibleNameForDropdownItem(trackableName))).append("', '")
          .append(escapeSingleQuotes(trackableName)).append("'],\n");
      vuforiaCurrentGameTrackableNameTooltips
          .append("  ['").append(escapeSingleQuotes(trackableName)).append("', 'The TrackableName value ")
          .append(escapeSingleQuotes(trackableName)).append(".'],\n");
    }
    createVuforiaCurrentGameTrackableNameDropdown.append("  ];\n")
        .append("  return createFieldDropdown(CHOICES);\n")
        .append("}\n\n");
    vuforiaCurrentGameTrackableNameTooltips
        .append("];\n");
    jsHardware
        .append(createVuforiaCurrentGameTrackableNameDropdown)
        .append(vuforiaCurrentGameTrackableNameTooltips)
        .append("\n");

    // Hardware
    for (HardwareType hardwareType : HardwareType.values()) {
      // Some HardwareTypes might have a null createDropdownFunctionName. This allows us to support
      // certain hardware types, even though we don't actually provide blocks.
      if (hardwareType.createDropdownFunctionName != null) {
        List<HardwareItem> hardwareItems = hardwareItemMap.getHardwareItems(hardwareType);
        appendCreateDropdownFunction(jsHardware, hardwareType.createDropdownFunctionName,
            hardwareItems);

        // Special case for DC_MOTOR: create the dropdown function for DcMotorEx.
        if (hardwareType == HardwareType.DC_MOTOR) {
          List<HardwareItem> hardwareItemsForDcMotorEx = getHardwareItemsForDcMotorEx(hardwareItems);
          appendCreateDropdownFunction(jsHardware, "createDcMotorExDropdown", hardwareItemsForDcMotorEx);
        }
      }
    }
    jsHardware
        .append("function getHardwareIdentifierSuffixes() {\n")
        .append("  var suffixes = [\n");
    for (HardwareType hardwareType : HardwareType.values()) {
      jsHardware
          .append("    '" + hardwareType.identifierSuffixForJavaScript + "',\n");
    }
    jsHardware
        .append("  ];\n")
        .append("  return suffixes;\n")
        .append("}\n\n");

    jsHardware
        .append("function addReservedWordsForJavaScript() {\n")
        .append("  Blockly.JavaScript.addReservedWords('callRunOpMode');\n")
        .append("  Blockly.JavaScript.addReservedWords('telemetryAddTextData');\n")
        .append("  Blockly.JavaScript.addReservedWords('telemetrySpeak');\n")
        .append("  Blockly.JavaScript.addReservedWords('callJava');\n")
        .append("  Blockly.JavaScript.addReservedWords('callJava_boolean');\n")
        .append("  Blockly.JavaScript.addReservedWords('callJava_String');\n");
    for (HardwareItem hardwareItem : hardwareItemMap.getAllHardwareItems()) {
      jsHardware
          .append("  Blockly.JavaScript.addReservedWords('")
          .append(hardwareItem.identifier).append("');\n");
    }
    List<String> identifiersForJavaScript = new ArrayList<>();
    for (Identifier identifier : Identifier.values()) {
      if (identifier.identifierForJavaScript != null && !identifier.identifierForJavaScript.isEmpty()) {
        identifiersForJavaScript.add(identifier.identifierForJavaScript);
      }
    }
    Collections.sort(identifiersForJavaScript);
    for (String identifierForJavaScript : identifiersForJavaScript) {
      jsHardware
          .append("  Blockly.JavaScript.addReservedWords('")
          .append(identifierForJavaScript).append("');\n");
    }
    jsHardware
        .append("}\n\n");

    jsHardware
        .append("function getHardwareItemDeviceName(identifier) {\n")
        .append("  switch (identifier) {\n");
    for (HardwareItem hardwareItem : hardwareItemMap.getAllHardwareItems()) {
      jsHardware
          .append("    case '").append(hardwareItem.identifier).append("':\n")
          .append("      return '").append(escapeSingleQuotes(hardwareItem.deviceName)).append("';\n");
    }
    jsHardware
        .append("  }\n")
        .append("  throw 'Unexpected identifier (' + identifier + ').';\n")
        .append("}\n\n");

    jsHardware
        .append("function getIdentifierForFtcJava(identifier) {\n")
        .append("  switch (identifier) {\n");
    Set<String> set = new HashSet<>();
    for (HardwareItem hardwareItem : hardwareItemMap.getAllHardwareItems()) {
      String identifierForFtcJava = HardwareItem.makeIdentifier(hardwareItem.deviceName);
      // Check that it isn't a reserved word.
      if (reservedWordsForFtcJava.contains(identifierForFtcJava)) {
        identifierForFtcJava += hardwareItem.hardwareType.identifierSuffixForFtcJava;
      }
      // Check if identifierForFtcJava is already in the set.
      if (!set.add(identifierForFtcJava)) {
        // Make the identifierForFtcJava unique by adding a hardware type suffix.
        identifierForFtcJava += hardwareItem.hardwareType.identifierSuffixForFtcJava;
        set.add(identifierForFtcJava);
      }
      jsHardware
          .append("    case '").append(hardwareItem.identifier).append("':\n")
          .append("      return '").append(identifierForFtcJava).append("';\n");
    }
    jsHardware
        .append("  }\n")
        .append("  throw 'Unexpected identifier (' + identifier + ').';\n")
        .append("}\n\n");

    jsHardware
        .append("function addReservedWordsForFtcJava() {\n");
    for (String word : reservedWordsForFtcJava) {
      jsHardware
          .append("  Blockly.FtcJava.addReservedWords('").append(word).append("');\n");
    }
    for (String word : additionalReservedWordsForFtcJava) {
      jsHardware
          .append("  Blockly.FtcJava.addReservedWords('").append(word).append("');\n");
    }
    for (HardwareItem hardwareItem : hardwareItemMap.getAllHardwareItems()) {
      jsHardware
          .append("  Blockly.FtcJava.addReservedWords(getIdentifierForFtcJava('")
          .append(hardwareItem.identifier).append("'));\n");
    }
    for (Identifier identifier : Identifier.values()) {
      if (identifier.identifierForFtcJava != null && !identifier.identifierForFtcJava.isEmpty()) {
        jsHardware
            .append("  Blockly.FtcJava.addReservedWords('")
            .append(identifier.identifierForFtcJava).append("');\n");
      }
    }
    jsHardware
        .append("}\n\n");

    jsHardware
        .append("function getIconClass(categoryName) {\n");
    for (HardwareType hardwareType : HardwareType.values()) {
      // Some HardwareTypes might have a null toolboxCategoryName. This allows us to support
      // certain hardware types, even though we don't actually provide blocks.
      // Also, some HardwareTypes, such as BNO055IMU, do not (yet) have a toolbox icon.
      if (hardwareType.toolboxCategoryName != null &&
          hardwareType.toolboxIcon != null) {
        jsHardware
            .append("  if (categoryName == '").append(escapeSingleQuotes(hardwareType.toolboxCategoryName)).append("') {\n")
            .append("    return '").append(escapeSingleQuotes(hardwareType.toolboxIcon.cssClass)).append("';\n")
            .append("  }\n");
      }
    }
    jsHardware
        .append("  if (categoryName == '").append(escapeSingleQuotes(DC_MOTOR_DUAL_CATEGORY_NAME)).append("') {\n")
        .append("    return '").append(escapeSingleQuotes(ToolboxIcon.DC_MOTOR.cssClass)).append("';\n")
        .append("  }\n")
        .append("  if (categoryName == '").append(escapeSingleQuotes(GAMEPAD_CATEGORY_NAME)).append("') {\n")
        .append("    return '").append(escapeSingleQuotes(ToolboxIcon.GAMEPAD.cssClass)).append("';\n")
        .append("  }\n")
        .append("  if (categoryName == '").append(escapeSingleQuotes(LINEAR_OP_MODE_CATEGORY_NAME)).append("') {\n")
        .append("    return '").append(escapeSingleQuotes(ToolboxIcon.LINEAR_OPMODE.cssClass)).append("';\n")
        .append("  }\n")
        .append("  if (categoryName == '").append(escapeSingleQuotes(COLOR_CATEGORY_NAME)).append("') {\n")
        .append("    return '").append(escapeSingleQuotes(ToolboxIcon.COLOR_SENSOR.cssClass)).append("';\n")
        .append("  }\n")
        .append("  if (categoryName == '").append(escapeSingleQuotes(ELAPSED_TIME_CATEGORY_NAME)).append("') {\n")
        .append("    return '").append(escapeSingleQuotes(ToolboxIcon.ELAPSED_TIME.cssClass)).append("';\n")
        .append("  }\n")
        .append("  return '';\n")
        .append("}\n\n");

    // Generate the JS method getWarningForCapabilityRequestedBySample which takes a capability
    // that is requested by a sample op mode.
    // If the system does not have the capability and the user should be warned about this,
    // the method returns the warning message.
    // If the system has the capability or no warning is needed, the method returns ''.
    jsHardware
        .append("function getWarningForCapabilityRequestedBySample(capability) {\n")
        .append("  switch (capability) {\n");

    for (Capability capability : Capability.values()) {
      boolean capable = capabilities.get(capability);

      // If there's no built-in camera, but there is a webcam, our code will use the webcam. So,
      // no warning is necessary.
      if (capability == Capability.CAMERA && !capable) {
        if (capabilities.get(Capability.WEBCAM)) {
          capable = true;
        }
      }

      if (!capable) {
        String warning = getCapabilityWarning(capability);
        if (warning != null) {
          jsHardware
              .append("    case '").append(capability).append("':\n")
              .append("      return '").append(warning).append("';\n");
        }
      }
    }
    jsHardware
        .append("  }\n")
        .append("  return '';\n")
        .append("}\n\n");

    // Put the toolbox at the end, because it makes it easier to troubleshoot problems with this
    // code.
    jsHardware
        .append("function getToolbox() {\n")
        .append("  return '").append(escapeSingleQuotes(toolbox)).append("';\n")
        .append("}\n\n");

    return jsHardware.toString();
  }

  /**
   * Generates a visible name for a blockly dropdown item.
   */
  static String makeVisibleNameForDropdownItem(String name) {
    int length = name.length();
    StringBuilder visibleName = new StringBuilder();

    for (int i = 0; i < length; i++) {
      char ch = name.charAt(i);
      if (ch == ' ') {
        visibleName.append('\u00A0');
      } else {
        visibleName.append(ch);
      }
    }
    return visibleName.toString();
  }

  private static void addLanguage(String languageCode, String languageName, StringBuilder dropdown, StringBuilder tooltips) {
    dropdown
        .append("      ['")
        .append(escapeSingleQuotes(makeVisibleNameForDropdownItem(languageCode)))
        .append("', '")
        .append(escapeSingleQuotes(languageCode))
        .append("'],\n");
    tooltips
        .append("  ['")
        .append(escapeSingleQuotes(languageCode))
        .append("', 'The language code for ")
        .append(escapeSingleQuotes(languageName))
        .append(".'],\n");
  }

  private static void addCountry(String countryCode, String countryName, StringBuilder dropdown, StringBuilder tooltips) {
    dropdown
        .append("      ['")
        .append(escapeSingleQuotes(makeVisibleNameForDropdownItem(countryCode)))
        .append("', '")
        .append(escapeSingleQuotes(countryCode))
        .append("'],\n");
    tooltips
        .append("  ['")
        .append(escapeSingleQuotes(countryCode))
        .append("', 'The country code for ")
        .append(escapeSingleQuotes(countryName))
        .append(".'],\n");
  }

  private static void appendCreateDropdownFunction(StringBuilder jsHardware,
      String functionName, List<HardwareItem> hardwareItems) {
    jsHardware
        .append("function ").append(functionName).append("() {\n")
        .append("  var CHOICES = [\n");
    for (HardwareItem hardwareItem : hardwareItems) {
      jsHardware
          .append("      ['").append(escapeSingleQuotes(hardwareItem.visibleName)).append("', '")
          .append(hardwareItem.identifier).append("'],\n");
    }
    jsHardware
        .append("  ];\n")
        .append("  return createFieldDropdown(CHOICES);\n")
        .append("}\n\n");
  }

  private static List<HardwareItem> getHardwareItemsForDcMotorEx(List<HardwareItem> hardwareItemsForDcMotor) {
    List<HardwareItem> hardwareItemsForDcMotorEx = new ArrayList<>();
    for (HardwareItem hardwareItemForDcMotor : hardwareItemsForDcMotor) {
      if (hardwareItemForDcMotor.hasAncestor(HardwareType.LYNX_MODULE)) {
        hardwareItemsForDcMotorEx.add(hardwareItemForDcMotor);
      }
    }
    return hardwareItemsForDcMotorEx;
  }

  /**
   * Generates the toolbox for the blocks editor, excluding the categories for {@link HardwareType}s
   * that do not exist in the given {@link HardwareItemMap}.
   */
  @SuppressWarnings("deprecation")
  private static String generateToolbox(HardwareItemMap hardwareItemMap,
      Map<Capability, Boolean> capabilities, AssetManager assetManager,
      Set<String> additionalReservedWordsForFtcJava,
      Set<String> methodLookupStrings) throws IOException {
    StringBuilder xmlToolbox = new StringBuilder();
    xmlToolbox.append("<xml id=\"toolbox\" style=\"display: none\">\n");

    // assetManager can be null during tests.
    if (assetManager != null) {
      addAsset(xmlToolbox, assetManager, "toolbox/linear_op_mode.xml");
      addAsset(xmlToolbox, assetManager, "toolbox/gamepad.xml");
    }

    for (ToolboxFolder toolboxFolder : ToolboxFolder.values()) {
      xmlToolbox.append(" <category name=\"").append(toolboxFolder.label)
          .append("\">\n");
      // Sort the hardware types by toolboxCategoryName.
      SortedSet<HardwareType> hardwareTypes = new TreeSet<>(HardwareType.BY_TOOLBOX_CATEGORY_NAME);
      hardwareTypes.addAll(hardwareItemMap.getHardwareTypes());
      for (HardwareType hardwareType : hardwareTypes) {
        if (hardwareType.toolboxFolder == toolboxFolder) {
          // Some HardwareTypes might have a null toolboxCategoryName. This allows us to support
          // certain hardware types, even though we don't actually provide blocks.
          if (hardwareType.toolboxCategoryName != null) {
            addHardwareCategoryToToolbox(
                xmlToolbox, hardwareType, hardwareItemMap.getHardwareItems(hardwareType), assetManager);
            if (hardwareType == HardwareType.BNO055IMU) {
              if (assetManager != null) {
                addAsset(
                    xmlToolbox, assetManager, "toolbox/bno055imu_parameters.xml");
              }
            }
          }
        }
      }
      xmlToolbox.append(" </category>\n");
    }

    addAndroidCategoriesToToolbox(xmlToolbox, assetManager);

    if (assetManager != null) {
      addAssetWithPlaceholders(xmlToolbox, assetManager, capabilities, "toolbox/utilities.xml");
      addAsset(xmlToolbox, assetManager, "toolbox/misc.xml");
    }

    Map<Class, Set<Method>> methodsByClass = BlocksClassFilter.getInstance().getMethodsByClass();
    if (!methodsByClass.isEmpty()) {
      xmlToolbox.append("<category name=\"Java Classes\">\n");
      for (Map.Entry<Class, Set<Method>> entry : methodsByClass.entrySet()) {
        Class clazz = entry.getKey();
        String className = clazz.getName();
        if (className.startsWith("org.firstinspires.ftc.teamcode.")) {
          className = className.substring(31);
          additionalReservedWordsForFtcJava.add(className);
        }
        String userVisibleClassName = className.replace('$', '.');
        xmlToolbox.append("<category name=\"").append(userVisibleClassName).append("\">\n");
        Set<Method> methods = entry.getValue();
        for (Method method : methods) {
          ExportToBlocks exportToBlocks = method.getAnnotation(ExportToBlocks.class);
          if (exportToBlocks == null) {
            continue;
          }
          String returnType = method.getReturnType().getName();
          String blockType = returnType.equals("void") ? "misc_callJava_noReturn" : "misc_callJava_return";
          String methodName = method.getName();
          Class[] parameterTypes = method.getParameterTypes();
          String comment = exportToBlocks.comment();
          String tooltip = exportToBlocks.tooltip();
          String[] parameterLabels = getParameterLabels(method);
          String methodLookupString = BlocksClassFilter.getLookupString(method);
          methodLookupStrings.add(methodLookupString);
          xmlToolbox
              .append("<block type=\"").append(blockType).append("\">\n")
              .append("<field name=\"CLASS_NAME\">").append(userVisibleClassName).append("</field>")
              .append("<field name=\"METHOD_NAME\">").append(methodName).append("</field>")
              .append("<mutation")
              .append(" methodLookupString=\"").append(methodLookupString).append("\"")
              .append(" parameterCount=\"").append(parameterTypes.length).append("\"")
              .append(" returnType=\"").append(returnType).append("\"")
              .append(" comment=\"").append(comment).append("\"")
              .append(" tooltip=\"").append(tooltip).append("\"")
              .append(" accessMethod=\"").append(accessMethod(method.getReturnType())).append("\"")
              .append(" convertReturnValue=\"").append(convertReturnValue(method.getReturnType())).append("\"");
          StringBuilder argValues = new StringBuilder();
          int i = 0;
          List<String> gamepads = new ArrayList<>();
          for (Class parameterType : parameterTypes) {
            xmlToolbox.append(" argLabel").append(i).append("=\"").append(parameterLabels[i]).append("\"");
            String argType = parameterType.getName();
            xmlToolbox.append(" argType").append(i).append("=\"").append(argType).append("\"");
            String argAuto = parameterProvidedAutomatically(parameterType, parameterLabels[i], gamepads);
            xmlToolbox.append(" argAuto").append(i).append("=\"").append(argAuto != null ? argAuto : "").append("\"");
            if (argAuto != null) {
              // No socket if parameter is provided automatically.
            } else if (argType.equals("boolean")
                || argType.equals("java.lang.Boolean")) {
              argValues
                  .append("<value name=\"ARG" + i + "\">")
                  .append(ToolboxUtil.makeBooleanShadow(false))
                  .append("</value>\n");
            } else if (argType.equals("char")
                || argType.equals("java.lang.Character")
                || argType.equals("java.lang.String")) {
              argValues
                  .append("<value name=\"ARG" + i + "\">")
                  .append(ToolboxUtil.makeTextShadow("A"))
                  .append("</value>\n");
            } else if (argType.equals("byte")
                || argType.equals("java.lang.Byte")
                || argType.equals("short")
                || argType.equals("java.lang.Short")
                || argType.equals("int")
                || argType.equals("java.lang.Integer")
                || argType.equals("long")
                || argType.equals("java.lang.Long")
                || argType.equals("float")
                || argType.equals("java.lang.Float")
                || argType.equals("double")
                || argType.equals("java.lang.Double")) {
              argValues
                  .append("<value name=\"ARG" + i + "\">")
                  .append(ToolboxUtil.makeNumberShadow(0))
                  .append("</value>\n");
            } else {
              // Leave other sockets empty?
            }
            i++;
          }
          xmlToolbox
              .append("/>"); // end of mutation
          xmlToolbox
              .append(argValues)
              .append("</block>\n");
        }
        xmlToolbox.append("</category>\n");
      }
      xmlToolbox.append("</category>\n");
    }

    xmlToolbox.append("</xml>");
    return xmlToolbox.toString();
  }

  public static String[] getParameterLabels(Method method) {
    ExportToBlocks exportToBlocks = method.getAnnotation(ExportToBlocks.class);
    String[] parameterLabels;
    if (exportToBlocks != null) {
      parameterLabels = exportToBlocks.parameterLabels();
    } else {
      parameterLabels = new String[0];
    }
    int length = method.getParameterTypes().length;
    if (parameterLabels.length != length) {
      parameterLabels = new String[length];
      for (int i = 0; i < parameterLabels.length; i++) {
        parameterLabels[i] = "";
      }
    }
    return parameterLabels;
  }

  private static String accessMethod(Class returnType) {
    if (returnType.equals(boolean.class) ||
        returnType.equals(Boolean.class)) {
      return "callJava_boolean";
    } else if (
        returnType.equals(char.class) ||
        returnType.equals(Character.class) ||
        returnType.equals(String.class) ||
        returnType.equals(byte.class) ||
        returnType.equals(Byte.class) ||
        returnType.equals(short.class) ||
        returnType.equals(Short.class) ||
        returnType.equals(int.class) ||
        returnType.equals(Integer.class) ||
        returnType.equals(long.class) ||
        returnType.equals(Long.class) ||
        returnType.equals(float.class) ||
        returnType.equals(Float.class) ||
        returnType.equals(double.class) ||
        returnType.equals(Double.class) ||
        returnType.isEnum()) {
      return "callJava_String";
    }
    return "callJava";
  }

  private static String convertReturnValue(Class returnType) {
    if (returnType.equals(byte.class) ||
        returnType.equals(Byte.class) ||
        returnType.equals(short.class) ||
        returnType.equals(Short.class) ||
        returnType.equals(int.class) ||
        returnType.equals(Integer.class) ||
        returnType.equals(long.class) ||
        returnType.equals(Long.class) ||
        returnType.equals(float.class) ||
        returnType.equals(Float.class) ||
        returnType.equals(double.class) ||
        returnType.equals(Double.class)) {
      return "Number";
    }
    return "";
  }

  private static String parameterProvidedAutomatically(Class parameterType, String parameterLabel, List<String> gamepads) {
    // Return the value that should be used for the parameter when blocks is exported to java.
    // For Javascript, null is used since the real value is determined in MiscAccess.java
    if (parameterType.equals(LinearOpMode.class) ||
        parameterType.equals(OpMode.class)) {
      return "this";
    } else if (parameterType.equals(HardwareMap.class)) {
      return "hardwareMap";
    } else if (parameterType.equals(Telemetry.class)) {
      return "telemetry";
    } else if (parameterType.equals(Gamepad.class)) {
      // If the parameter label is gamepad1 or gamepad2, return that.
      if (parameterLabel.equals("gamepad1") || parameterLabel.equals("gamepad2")) {
        return parameterLabel;
      }
      // Otherwise, return the first element in the gamepads list. This will be "gamepad1" for the
      // first Gamepad parameter and "gamepad2" for the second Gamepad parameter.
      if (gamepads.isEmpty()) {
        gamepads.add("gamepad1");
        gamepads.add("gamepad2");
      }
      return gamepads.remove(0);
    }
    return null;
  }

  private static String getCapabilityWarning(Capability capability) {
    switch (capability) {
      case CAMERA:
        return "This device does not have a camera.";
      case WEBCAM:
        return "The current configuration has no webcam.";
      case SWITCHABLE_CAMERA:
        return "The current configuration does not have multiple webcams.";
      default:
        // No warning for Capability.VUFORIA or Capability.TFOD. The user will see the warning about camera/webcam.
        return null;
    }
  }

  @SuppressWarnings("deprecation")
  public static Map<Capability, Boolean> getCapabilities(HardwareItemMap hardwareItemMap) {
    Map<Capability, Boolean> capabilities = new HashMap<>();
    // PackageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) incorrectly returns true on a
    // control hub, so here I use Camera.getNumberOfCameras() to determine whether there are any
    // built-in cameras.
    boolean camera = android.hardware.Camera.getNumberOfCameras() > 0;
    int numberOfWebcams = hardwareItemMap.getHardwareItems(HardwareType.WEBCAM_NAME).size();
    boolean webcam = numberOfWebcams > 0;
    boolean switchableCamera = numberOfWebcams > 1;
    capabilities.put(Capability.CAMERA, camera);
    capabilities.put(Capability.WEBCAM, webcam);
    capabilities.put(Capability.SWITCHABLE_CAMERA, switchableCamera);
    capabilities.put(Capability.VUFORIA, camera || webcam);
    capabilities.put(Capability.TFOD, camera || webcam);
    return capabilities;
  }

  /**
   * Appends the text content of an asset to the given StringBuilder.
   */
  private static void addAsset(StringBuilder sb, AssetManager assetManager, String assetName)
      throws IOException {
    FileUtil.readAsset(sb, assetManager, assetName);
  }

  private static void addAssetWithPlaceholders(StringBuilder xmlToolbox, AssetManager assetManager,
      Map<Capability, Boolean> capabilities, String assetName) throws IOException {
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(assetManager.open(assetName)))) {
      String line = null;
      while ((line = reader.readLine()) != null) {
        line = line.trim()
            .replace("placeholder_current_game_name", CURRENT_GAME_NAME)
            .replace("<placeholder_tfod_current_game_labels/>", getTfodCurrentGameLabelBlocks())
            .replace("<placeholder_vuforia_current_game_trackable_names/>", getVuforiaCurrentGameTrackableNameBlocks());

        String prefix = "<placeholder_";
        String suffix = "/>";
        if (line.startsWith(prefix) && line.endsWith(suffix)) {
          int startOfType = prefix.length();
          int endOfType = line.indexOf('_', startOfType);
          if (endOfType != -1) {
            String type = line.substring(startOfType, endOfType);
            String childAssetName = "toolbox/" +
                line.substring(endOfType + 1, line.length() - suffix.length()).trim() + ".xml";
            Boolean allowed = capabilities.get(Capability.fromPlaceholderType(type));
            if (allowed != null) {
              if (allowed) {
                addAssetWithPlaceholders(xmlToolbox, assetManager, capabilities, childAssetName);
              } else {
                RobotLog.w("Skipping " + childAssetName + " because type \"" + type + "\" " +
                    "is not supported by this device and/or hardware.");
              }
            } else {
              RobotLog.e("Error: Skipping " + childAssetName + " because type \"" + type + "\" " +
                  "is not recognized.");
            }
          } else {
            RobotLog.e("Error: Unable to parse placeholder \"" + line + "\"");
          }
        } else {
          xmlToolbox.append(line).append("\n");
        }
      }
    }
  }

  private static String getTfodCurrentGameLabelBlocks() {
    StringBuilder tfodCurrentGameLabelBlocks = new StringBuilder();
    if (TfodCurrentGame.LABELS.length <= 3) {
      for (String tfodLabel : TfodCurrentGame.LABELS) {
        tfodCurrentGameLabelBlocks
            .append("<block type=\"tfodCurrentGame_typedEnum_label\"><field name=\"LABEL\">")
            .append(tfodLabel)
            .append("</field></block>\n");
      }
    } else {
      tfodCurrentGameLabelBlocks
          .append("<block type=\"tfodCurrentGame_typedEnum_label\"></block>\n");
    }
    return tfodCurrentGameLabelBlocks.toString();
  }

  private static String getVuforiaCurrentGameTrackableNameBlocks() {
    StringBuilder vuforiaCurrentGameTrackableNameBlocks = new StringBuilder();
    if (VuforiaCurrentGame.TRACKABLE_NAMES.length <= 3) {
      for (String trackableName : VuforiaCurrentGame.TRACKABLE_NAMES) {
        vuforiaCurrentGameTrackableNameBlocks
            .append("<block type=\"vuforiaCurrentGame_typedEnum_trackableName\"><field name=\"TRACKABLE_NAME\">")
            .append(trackableName)
            .append("</field></block>\n");
      }
    } else {
      vuforiaCurrentGameTrackableNameBlocks
          .append("<block type=\"vuforiaCurrentGame_typedEnum_trackableName\"></block>\n");
    }
    return vuforiaCurrentGameTrackableNameBlocks.toString();
  }


  /**
   * Adds the category for Android functionality to the toolbox, iff there is at least one
   * sub-category supported by the Android device.
   */
  private static void addAndroidCategoriesToToolbox(
      StringBuilder xmlToolbox, AssetManager assetManager)
      throws IOException {
    boolean hasAccelerometer = !sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).isEmpty();
    boolean hasGyroscope = !sensorManager.getSensorList(Sensor.TYPE_GYROSCOPE).isEmpty();
    boolean hasMagneticField = !sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).isEmpty();

    StringBuilder sb = new StringBuilder();
    boolean empty = true;

    if (hasAccelerometer) {
      if (assetManager != null) {
        addAsset(sb, assetManager, "toolbox/android_accelerometer.xml");
        empty = false;
      }
    } else {
      RobotLog.w("Skipping toolbox/android_accelerometer.xml because this device does not have " +
          "an accelerometer.");
    }
    if (hasGyroscope) {
      if (assetManager != null) {
        addAsset(sb, assetManager, "toolbox/android_gyroscope.xml");
        empty = false;
      }
    } else {
      RobotLog.w("Skipping toolbox/android_gyroscope.xml because this device does not have " +
          "a gyroscope.");
    }
    if (hasAccelerometer && hasMagneticField) {
      if (assetManager != null) {
        addAsset(sb, assetManager, "toolbox/android_orientation.xml");
        empty = false;
      }
    } else {
      RobotLog.w("Skipping toolbox/android_gyroscope.xml because this device does not have " +
          "an accelerometer and a magnetic field sensor.");
    }
    if (assetManager != null) {
      addAsset(sb, assetManager, "toolbox/android_sound_pool.xml");
      addAsset(sb, assetManager, "toolbox/android_text_to_speech.xml");
      empty = false;
    }

    if (!empty) {
      xmlToolbox.append("<category name=\"Android\">\n")
          .append(sb)
          .append("</category>\n");
    }
  }

  /**
   * Adds the category for the given {@link HardwareType} to the toolbox, iff there is at least one
   * {@link HardwareItem} of that HardwareType.
   */
  private static void addHardwareCategoryToToolbox(
      StringBuilder xmlToolbox, HardwareType hardwareType,
      List<HardwareItem> hardwareItems, AssetManager assetManager) throws IOException {
    if (hardwareItems != null && hardwareItems.size() > 0) {
      xmlToolbox.append("  <category name=\"").append(hardwareType.toolboxCategoryName)
          .append("\">\n");

      switch (hardwareType) {
        case ACCELERATION_SENSOR:
          addAccelerationSensorCategoryToToolbox(xmlToolbox, hardwareType, hardwareItems);
          break;
        case ANALOG_INPUT:
          addAnalogInputCategoryToToolbox(xmlToolbox, hardwareType, hardwareItems);
          break;
        case ANALOG_OUTPUT:
          addAnalogOutputCategoryToToolbox(xmlToolbox, hardwareType, hardwareItems);
          break;
        case BNO055IMU:
          addBNO055IMUCategoryToToolbox(xmlToolbox, hardwareType, hardwareItems);
          break;
        case COLOR_RANGE_SENSOR:
          addColorRangeSensorCategoryToToolbox(xmlToolbox, hardwareType, hardwareItems);
          break;
        case COLOR_SENSOR:
          addColorSensorCategoryToToolbox(xmlToolbox, hardwareType, hardwareItems);
          break;
        case COMPASS_SENSOR:
          addCompassSensorCategoryToToolbox(xmlToolbox, hardwareType, hardwareItems);
          break;
        case CR_SERVO:
          addCRServoCategoryToToolbox(xmlToolbox, hardwareType, hardwareItems);
          break;
        case DC_MOTOR:
          addDcMotorCategoryToToolbox(xmlToolbox, hardwareType, hardwareItems);
          break;
        case DIGITAL_CHANNEL:
          addDigitalChannelCategoryToToolbox(xmlToolbox, hardwareType, hardwareItems);
          break;
        case DISTANCE_SENSOR:
          addDistanceSensorCategoryToToolbox(xmlToolbox, hardwareType, hardwareItems);
          break;
        case GYRO_SENSOR:
          addGyroSensorCategoryToToolbox(xmlToolbox, hardwareType, hardwareItems);
          break;
        case IR_SEEKER_SENSOR:
          addIrSeekerSensorCategoryToToolbox(xmlToolbox, hardwareType, hardwareItems);
          break;
        case LED:
          addLedCategoryToToolbox(xmlToolbox, hardwareType, hardwareItems);
          break;
        case LIGHT_SENSOR:
          addLightSensorCategoryToToolbox(xmlToolbox, hardwareType, hardwareItems);
          break;
        case MR_I2C_COMPASS_SENSOR:
          addMrI2cCompassSensorCategoryToToolbox(xmlToolbox, hardwareType, hardwareItems);;
          break;
        case MR_I2C_RANGE_SENSOR:
          addMrI2cRangeSensorCategoryToToolbox(xmlToolbox, hardwareType, hardwareItems);;
          break;
        case OPTICAL_DISTANCE_SENSOR:
          addOpticalDistanceSensorCategoryToToolbox(xmlToolbox, hardwareType, hardwareItems);
          break;
        case REV_BLINKIN_LED_DRIVER:
          addRevBlinkinLedDriverCategoryToToolbox(xmlToolbox, assetManager);
          break;
        case SERVO:
          addServoCategoryToToolbox(xmlToolbox, hardwareType, hardwareItems);
          break;
        case SERVO_CONTROLLER:
          addServoControllerCategoryToToolbox(xmlToolbox, hardwareType, hardwareItems);
          break;
        case TOUCH_SENSOR:
          addTouchSensorCategoryToToolbox(xmlToolbox, hardwareType, hardwareItems);
          break;
        case ULTRASONIC_SENSOR:
          addUltrasonicSensorCategoryToToolbox(xmlToolbox, hardwareType, hardwareItems);
          break;
        case VOLTAGE_SENSOR:
          addVoltageSensorCategoryToToolbox(xmlToolbox, hardwareType, hardwareItems);
          break;
        default:
          throw new IllegalArgumentException("Unexpected hardware type " + hardwareType);
      }

      xmlToolbox.append("  </category>\n");
    }
  }

  /**
   * Adds the category for acceleration sensor to the toolbox.
   */
  private static void addAccelerationSensorCategoryToToolbox(
      StringBuilder xmlToolbox, HardwareType hardwareType, List<HardwareItem> hardwareItems) {
    String identifier = hardwareItems.get(0).identifier;

    // Properties
    SortedMap<String, String> properties = new TreeMap<String, String>();
    properties.put("Acceleration", "Acceleration");
    properties.put("XAccel", "Number");
    properties.put("YAccel", "Number");
    properties.put("ZAccel", "Number");
    ToolboxUtil.addProperties(xmlToolbox, hardwareType, identifier, properties,
        null /* setterValues */, null /* enumBlocks */);

    // Functions
    Map<String, Map<String, String>> functions = new TreeMap<String, Map<String, String>>();
    functions.put("toText", null);
    ToolboxUtil.addFunctions(xmlToolbox, hardwareType, identifier, functions);
  }

  /**
   * Adds the category for BNO055IMU to the toolbox.
   */
  private static void addBNO055IMUCategoryToToolbox(
      StringBuilder xmlToolbox, HardwareType hardwareType, List<HardwareItem> hardwareItems) {
    String identifier = hardwareItems.get(0).identifier;

    // Properties
    SortedMap<String, String> properties = new TreeMap<String, String>();
    properties.put("Acceleration", "Acceleration");
    properties.put("AngularOrientation", "Orientation");
    properties.put("AngularOrientationAxes", "Array");
    properties.put("AngularVelocity", "AngularVelocity");
    properties.put("AngularVelocityAxes", "Array");
    properties.put("CalibrationStatus", "String");
    properties.put("Gravity", "Acceleration");
    properties.put("I2cAddress7Bit", "Number");
    properties.put("I2cAddress8Bit", "Number");
    properties.put("LinearAcceleration", "Acceleration");
    properties.put("MagneticFieldStrength", "MagneticFlux");
    properties.put("OverallAcceleration", "Acceleration");
    properties.put("Position", "Position");
    properties.put("QuaternionOrientation", "Quaternion");
    properties.put("SystemError", "String");
    properties.put("SystemStatus", "SystemStatus");
    properties.put("Temperature", "Temperature");
    properties.put("Velocity", "Velocity");
    Map<String, String[]> setterValues = new HashMap<String, String[]>();
    setterValues.put("I2cAddress7Bit", new String[] { ToolboxUtil.makeNumberShadow(8) });
    setterValues.put("I2cAddress8Bit", new String[] { ToolboxUtil.makeNumberShadow(16) });
    Map<String, String[]> enumBlocks = new HashMap<String, String[]>();
    enumBlocks.put("SystemStatus", new String[] { ToolboxUtil.makeTypedEnumBlock(hardwareType, "systemStatus") });
    ToolboxUtil.addProperties(xmlToolbox, hardwareType, identifier, properties,
        setterValues, enumBlocks);

    // Functions
    Map<String, Map<String, String>> functions = new TreeMap<String, Map<String, String>>();
    Map<String, String> initializeArgs = new LinkedHashMap<String, String>();
    initializeArgs.put("PARAMETERS", ToolboxUtil.makeVariableGetBlock("parameters"));
    functions.put("initialize", initializeArgs);
    Map<String, String> startAccelerationIntegration_with1Args = new LinkedHashMap<String, String>();
    startAccelerationIntegration_with1Args.put("MS_POLL_INTERVAL", ToolboxUtil.makeNumberShadow(1000));
    functions.put("startAccelerationIntegration_with1", startAccelerationIntegration_with1Args);
    Map<String, String> startAccelerationIntegration_with3Args = new LinkedHashMap<String, String>();
    startAccelerationIntegration_with3Args.put("INITIAL_POSITION", ToolboxUtil.makeVariableGetBlock("position"));
    startAccelerationIntegration_with3Args.put("INITIAL_VELOCITY", ToolboxUtil.makeVariableGetBlock("velocity"));
    startAccelerationIntegration_with3Args.put("MS_POLL_INTERVAL", ToolboxUtil.makeNumberShadow(1000));
    functions.put("startAccelerationIntegration_with3", startAccelerationIntegration_with3Args);
    functions.put("stopAccelerationIntegration", null);
    functions.put("isSystemCalibrated", null);
    functions.put("isGyroCalibrated", null);
    functions.put("isAccelerometerCalibrated", null);
    functions.put("isMagnetometerCalibrated", null);
    Map<String, String> saveCalibrationDataArgs = new LinkedHashMap<String, String>();
    saveCalibrationDataArgs.put("FILE_NAME", ToolboxUtil.makeTextShadow("IMUCalibration.json"));
    functions.put("saveCalibrationData", saveCalibrationDataArgs);
    Map<String, String> getAngularVelocityArgs = new LinkedHashMap<String, String>();
    getAngularVelocityArgs.put("ANGLE_UNIT", ToolboxUtil.makeTypedEnumShadow("navigation", "angleUnit"));
    functions.put("getAngularVelocity", getAngularVelocityArgs);
    Map<String, String> getAngularOrientationArgs = new LinkedHashMap<String, String>();
    getAngularOrientationArgs.put("AXES_REFERENCE", ToolboxUtil.makeTypedEnumShadow("navigation", "axesReference"));
    getAngularOrientationArgs.put("AXES_ORDER", ToolboxUtil.makeTypedEnumShadow("navigation", "axesOrder"));
    getAngularOrientationArgs.put("ANGLE_UNIT", ToolboxUtil.makeTypedEnumShadow("navigation", "angleUnit"));
    functions.put("getAngularOrientation", getAngularOrientationArgs);
    ToolboxUtil.addFunctions(xmlToolbox, hardwareType, identifier, functions);
  }

  /**
   * Adds the category for analog input to the toolbox.
   */
  private static void addAnalogInputCategoryToToolbox(
      StringBuilder xmlToolbox, HardwareType hardwareType, List<HardwareItem> hardwareItems) {
    String identifier = hardwareItems.get(0).identifier;

    // Properties
    SortedMap<String, String> properties = new TreeMap<String, String>();
    properties.put("Voltage", "Number");
    properties.put("MaxVoltage", "Number");
    ToolboxUtil.addProperties(xmlToolbox, hardwareType, identifier, properties,
        null /* setterValues */, null /* enumBlocks */);
  }

  /**
   * Adds the category for analog output to the toolbox.
   */
  private static void addAnalogOutputCategoryToToolbox(
      StringBuilder xmlToolbox, HardwareType hardwareType, List<HardwareItem> hardwareItems) {
    String identifier = hardwareItems.get(0).identifier;

    // Functions
    Map<String, Map<String, String>> functions = new TreeMap<String, Map<String, String>>();
    Map<String, String> setAnalogOutputVoltageArgs = new LinkedHashMap<String, String>();
    setAnalogOutputVoltageArgs.put("VOLTAGE", ToolboxUtil.makeNumberShadow(512));
    functions.put("setAnalogOutputVoltage_Number", setAnalogOutputVoltageArgs);
    Map<String, String> setAnalogOutputFrequencyArgs = new LinkedHashMap<String, String>();
    setAnalogOutputFrequencyArgs.put("FREQUENCY", ToolboxUtil.makeNumberShadow(100));
    functions.put("setAnalogOutputFrequency_Number", setAnalogOutputFrequencyArgs);
    Map<String, String> setAnalogOutputModeArgs = new LinkedHashMap<String, String>();
    setAnalogOutputModeArgs.put("MODE", ToolboxUtil.makeNumberShadow(0));
    functions.put("setAnalogOutputMode_Number", setAnalogOutputModeArgs);
    ToolboxUtil.addFunctions(xmlToolbox, hardwareType, identifier, functions);
  }

  /**
   * Adds the category for color sensor to the toolbox.
   */
  private static void addColorSensorCategoryToToolbox(
      StringBuilder xmlToolbox, HardwareType hardwareType, List<HardwareItem> hardwareItems) {
    String identifier = hardwareItems.get(0).identifier;

    // Properties
    SortedMap<String, String> properties = new TreeMap<String, String>();
    properties.put("Red", "Number");
    properties.put("Green", "Number");
    properties.put("Blue", "Number");
    properties.put("Alpha", "Number");
    properties.put("Argb", "Number");
    properties.put("Gain", "Number");
    properties.put("I2cAddress7Bit", "Number");
    properties.put("I2cAddress8Bit", "Number");
    Map<String, String[]> setterValues = new HashMap<String, String[]>();
    setterValues.put("Gain", new String[] { ToolboxUtil.makeNumberShadow(2) });
    setterValues.put("I2cAddress7Bit", new String[] { ToolboxUtil.makeNumberShadow(8) });
    setterValues.put("I2cAddress8Bit", new String[] { ToolboxUtil.makeNumberShadow(16) });
    ToolboxUtil.addProperties(xmlToolbox, hardwareType, identifier, properties,
        setterValues, null /* enumBlocks */);

    // Functions
    Map<String, Map<String, String>> functions = new TreeMap<String, Map<String, String>>();
    Map<String, String> enableLedArgs = new LinkedHashMap<String, String>();
    enableLedArgs.put("ENABLE", ToolboxUtil.makeBooleanShadow(true));
    functions.put("enableLed_Boolean", enableLedArgs);
    functions.put("isLightOn", null);
    functions.put("getNormalizedColors", null);
    functions.put("toText", null);
    ToolboxUtil.addFunctions(xmlToolbox, hardwareType, identifier, functions);
  }

  /**
   * Adds the category for compass sensor to the toolbox.
   */
  private static void addCompassSensorCategoryToToolbox(
      StringBuilder xmlToolbox, HardwareType hardwareType, List<HardwareItem> hardwareItems) {
    String identifier = hardwareItems.get(0).identifier;

    // Properties
    SortedMap<String, String> properties = new TreeMap<String, String>();
    properties.put("Direction", "Number");
    properties.put("CalibrationFailed", "Boolean");
    ToolboxUtil.addProperties(xmlToolbox, hardwareType, identifier, properties,
        null /* setterValues */, null /* enumBlocks */);

    // Functions
    Map<String, Map<String, String>> functions = new TreeMap<String, Map<String, String>>();
    Map<String, String> setModeArgs = new LinkedHashMap<String, String>();
    setModeArgs.put("COMPASS_MODE", ToolboxUtil.makeTypedEnumShadow(hardwareType, "compassMode"));
    functions.put("setMode_CompassMode", setModeArgs);
    ToolboxUtil.addFunctions(xmlToolbox, hardwareType, identifier, functions);
  }

  /**
   * Adds the category for cr servo to the toolbox.
   */
  private static void addCRServoCategoryToToolbox(
      StringBuilder xmlToolbox, HardwareType hardwareType, List<HardwareItem> hardwareItems) {
    String identifier = hardwareItems.get(0).identifier;

    // Properties
    SortedMap<String, String> properties = new TreeMap<String, String>();
    properties.put("Direction", "Direction");
    properties.put("Power", "Number");
    Map<String, String[]> setterValues = new HashMap<String, String[]>();
    setterValues.put(
        "Direction", new String[] { ToolboxUtil.makeTypedEnumShadow(hardwareType, "direction") });
    setterValues.put("Power", new String[] {
      ToolboxUtil.makeNumberShadow(1),
      ToolboxUtil.makeNumberShadow(0)
    });
    ToolboxUtil.addProperties(xmlToolbox, hardwareType, identifier, properties,
        setterValues, null /* enumBlocks */);
  }

  /**
   * Adds the category for dc motor to the toolbox.
   */
  private static void addDcMotorCategoryToToolbox(
      StringBuilder xmlToolbox, HardwareType hardwareType, List<HardwareItem> hardwareItems) {
    String identifier = hardwareItems.get(0).identifier;
    String zero = ToolboxUtil.makeNumberShadow(0);
    String one = ToolboxUtil.makeNumberShadow(1);
    String five = ToolboxUtil.makeNumberShadow(5);
    String ten = ToolboxUtil.makeNumberShadow(10);
    String runMode = ToolboxUtil.makeTypedEnumShadow(hardwareType, "runMode");
    String zeroPowerBehavior = ToolboxUtil.makeTypedEnumShadow(hardwareType, "zeroPowerBehavior");
    String direction = ToolboxUtil.makeTypedEnumShadow(hardwareType, "direction");
    String angleUnit = ToolboxUtil.makeTypedEnumShadow("navigation", "angleUnit");
    String currentUnit = ToolboxUtil.makeTypedEnumShadow("navigation", "currentUnit");
    String runModeRunUsingEncoder =
        ToolboxUtil.makeTypedEnumShadow(hardwareType, "runMode", "RUN_MODE", "RUN_USING_ENCODER");

    // Properties
    SortedMap<String, String> properties = new TreeMap<String, String>();
    properties.put("CurrentPosition", "Number");
    properties.put("Direction", "Direction");
    properties.put("Mode", "RunMode");
    properties.put("Power", "Number");
    properties.put("PowerFloat", "Boolean");
    properties.put("TargetPosition", "Number");
    properties.put("ZeroPowerBehavior", "ZeroPowerBehavior");
    Map<String, String[]> setterValues = new HashMap<String, String[]>();
    setterValues.put("Direction", new String[] { direction });
    setterValues.put("Mode", new String[] { runMode });
    setterValues.put("Power", new String[] { one, zero });
    setterValues.put("TargetPosition", new String[] { zero });
    setterValues.put("ZeroPowerBehavior", new String[] { zeroPowerBehavior });
    ToolboxUtil.addProperties(xmlToolbox, hardwareType, identifier, properties,
        setterValues, null /* enumBlocks */);

    // Functions
    Map<String, Map<String, String>> functions = new TreeMap<String, Map<String, String>>();
    functions.put("isBusy", null);
    ToolboxUtil.addFunctions(xmlToolbox, hardwareType, identifier, functions);
    functions.clear();

    if (hardwareItems.size() > 1) {
      String identifier1 = hardwareItems.get(0).identifier;
      String identifier2 = hardwareItems.get(1).identifier;

      xmlToolbox.append("    <category name=\"" + DC_MOTOR_DUAL_CATEGORY_NAME + "\">\n");

      // Set power for both motors to 1.
      ToolboxUtil.addDualPropertySetters(xmlToolbox, hardwareType, "Power", "Number",
          identifier1, one,
          identifier2, one);
      // Set power for both motors to 0.
      ToolboxUtil.addDualPropertySetters(xmlToolbox, hardwareType, "Power", "Number",
          identifier1, zero,
          identifier2, zero);
      // Set run mode for both motors.
      ToolboxUtil.addDualPropertySetters(xmlToolbox, hardwareType, "Mode", "RunMode",
          identifier1, runMode,
          identifier2, runMode);
      // Set target position for both motors.
      ToolboxUtil.addDualPropertySetters(xmlToolbox, hardwareType, "TargetPosition", "Number",
          identifier1, zero,
          identifier2, zero);

      // Set zero power behavior for both motors.
      ToolboxUtil.addDualPropertySetters(xmlToolbox, hardwareType, "ZeroPowerBehavior", "ZeroPowerBehavior",
          identifier1, zeroPowerBehavior,
          identifier2, zeroPowerBehavior);

      xmlToolbox.append("    </category>\n");
    }

    List<HardwareItem> hardwareItemsForDcMotorEx = getHardwareItemsForDcMotorEx(hardwareItems);
    if (!hardwareItemsForDcMotorEx.isEmpty()) {
      String identifierForDcMotorEx = hardwareItemsForDcMotorEx.get(0).identifier;

      xmlToolbox.append("    <category name=\"" + DC_MOTOR_EX_CATEGORY_NAME + "\">\n");

      properties.clear();
      properties.put("TargetPositionTolerance", "Number");
      properties.put("Velocity", "Number");
      setterValues.clear();
      setterValues.put("TargetPositionTolerance", new String[] { ten });
      setterValues.put("Velocity", new String[] { ten });
      ToolboxUtil.addProperties(xmlToolbox, hardwareType, identifierForDcMotorEx, properties,
          setterValues, null /* enumBlocks */);

      if (hardwareItemsForDcMotorEx.size() > 1) {
        String identifier1ForDcMotorEx = hardwareItemsForDcMotorEx.get(0).identifier;
        String identifier2ForDcMotorEx = hardwareItemsForDcMotorEx.get(1).identifier;

        xmlToolbox.append("    <category name=\"" + DC_MOTOR_DUAL_CATEGORY_NAME + "\">\n");

        // Set target position tolerance for both motors.
        ToolboxUtil.addDualPropertySetters(xmlToolbox, hardwareType, "TargetPositionTolerance", "Number",
            identifier1ForDcMotorEx, ten,
            identifier2ForDcMotorEx, ten);

        // Set velocity for both motors.
        ToolboxUtil.addDualPropertySetters(xmlToolbox, hardwareType, "Velocity", "Number",
            identifier1ForDcMotorEx, ten,
            identifier2ForDcMotorEx, ten);

        xmlToolbox.append("    </category>\n");
      }

      functions = new LinkedHashMap<String, Map<String, String>>();
      functions.put("setMotorEnable", null);
      functions.put("setMotorDisable", null);
      functions.put("isMotorEnabled", null);
      Map<String, String> setVelocity_withAngleUnitArgs = new LinkedHashMap<String, String>();
      setVelocity_withAngleUnitArgs.put("ANGULAR_RATE", ten);
      setVelocity_withAngleUnitArgs.put("ANGLE_UNIT", angleUnit);
      functions.put("setVelocity_withAngleUnit", setVelocity_withAngleUnitArgs);
      Map<String, String> getVelocity_withAngleUnitArgs = new LinkedHashMap<String, String>();
      getVelocity_withAngleUnitArgs.put("ANGLE_UNIT", angleUnit);
      functions.put("getVelocity_withAngleUnit", getVelocity_withAngleUnitArgs);
      Map<String, String> setVelocityPIDFCoefficientsArgs = new LinkedHashMap<String, String>();
      setVelocityPIDFCoefficientsArgs.put("P", ToolboxUtil.makeNumberShadow(10));
      setVelocityPIDFCoefficientsArgs.put("I", ToolboxUtil.makeNumberShadow(10));
      setVelocityPIDFCoefficientsArgs.put("D", ToolboxUtil.makeNumberShadow(10));
      setVelocityPIDFCoefficientsArgs.put("F", ToolboxUtil.makeNumberShadow(10));
      functions.put("setVelocityPIDFCoefficients", setVelocityPIDFCoefficientsArgs);
      Map<String, String> setPositionPIDFCoefficientsArgs = new LinkedHashMap<String, String>();
      setPositionPIDFCoefficientsArgs.put("P", ToolboxUtil.makeNumberShadow(5));
      functions.put("setPositionPIDFCoefficients", setPositionPIDFCoefficientsArgs);
      Map<String, String> setPIDFCoefficientsArgs = new LinkedHashMap<String, String>();
      setPIDFCoefficientsArgs.put("RUN_MODE", runModeRunUsingEncoder);
      setPIDFCoefficientsArgs.put("PIDF_COEFFICIENTS", ToolboxUtil.makeVariableGetBlock("pidfCoefficients"));
      functions.put("setPIDFCoefficients", setPIDFCoefficientsArgs);
      Map<String, String> getPIDFCoefficientsArgs = new LinkedHashMap<String, String>();
      getPIDFCoefficientsArgs.put("RUN_MODE", runModeRunUsingEncoder);
      functions.put("getPIDFCoefficients", getPIDFCoefficientsArgs);
      Map<String, String> getCurrentArgs = new LinkedHashMap<String, String>();
      getCurrentArgs.put("CURRENT_UNIT", currentUnit);
      functions.put("getCurrent", getCurrentArgs);
      Map<String, String> setCurrentAlertArgs = new LinkedHashMap<String, String>();
      setCurrentAlertArgs.put("CURRENT", five);
      setCurrentAlertArgs.put("CURRENT_UNIT", currentUnit);
      functions.put("setCurrentAlert", setCurrentAlertArgs);
      Map<String, String> getCurrentAlertArgs = new LinkedHashMap<String, String>();
      getCurrentAlertArgs.put("CURRENT_UNIT", currentUnit);
      functions.put("getCurrentAlert", getCurrentAlertArgs);
      functions.put("isOverCurrent", null);
      ToolboxUtil.addFunctions(xmlToolbox, hardwareType, identifierForDcMotorEx, functions);

      xmlToolbox.append("    </category>\n");
    }

  }

  /**
   * Adds the category for digital channel to the toolbox.
   */
  private static void addDigitalChannelCategoryToToolbox(
      StringBuilder xmlToolbox, HardwareType hardwareType, List<HardwareItem> hardwareItems) {
    String identifier = hardwareItems.get(0).identifier;
    String mode = ToolboxUtil.makeTypedEnumShadow(hardwareType, "mode");

    // Properties
    SortedMap<String, String> properties = new TreeMap<String, String>();
    properties.put("Mode", "DigitalChannelMode");
    properties.put("State", "Boolean");
    Map<String, String[]> setterValues = new HashMap<String, String[]>();
    setterValues.put("Mode", new String[] { mode });
    setterValues.put("State", new String[] { ToolboxUtil.makeBooleanShadow(true) });
    ToolboxUtil.addProperties(xmlToolbox, hardwareType, identifier, properties,
        setterValues, null /* enumBlocks */);
  }

  /**
   * Adds the category for distance sensor to the toolbox.
   */
  private static void addDistanceSensorCategoryToToolbox(
      StringBuilder xmlToolbox, HardwareType hardwareType, List<HardwareItem> hardwareItems) {
    HardwareItem hardwareItem = hardwareItems.get(0);
    String identifier = hardwareItem.identifier;

    // Functions
    Map<String, Map<String, String>> functions = new TreeMap<String, Map<String, String>>();
    Map<String, String> getDistanceArgs = new LinkedHashMap<String, String>();
    getDistanceArgs.put("DISTANCE_UNIT", ToolboxUtil.makeTypedEnumShadow("navigation", "distanceUnit"));
    functions.put("getDistance", getDistanceArgs);
    ToolboxUtil.addFunctions(xmlToolbox, hardwareType, identifier, functions);
  }

  /**
   * Adds the category for gyro sensor to the toolbox.
   */
  private static void addGyroSensorCategoryToToolbox(
      StringBuilder xmlToolbox, HardwareType hardwareType, List<HardwareItem> hardwareItems) {
    String identifier = hardwareItems.get(0).identifier;
    String headingMode = ToolboxUtil.makeTypedEnumShadow(hardwareType, "headingMode");

    // Properties
    SortedMap<String, String> properties = new TreeMap<String, String>();
    properties.put("Heading", "Number");
    properties.put("HeadingMode", "HeadingMode");
    properties.put("I2cAddress7Bit", "Number");
    properties.put("I2cAddress8Bit", "Number");
    properties.put("IntegratedZValue", "Number");
    properties.put("RawX", "Number");
    properties.put("RawY", "Number");
    properties.put("RawZ", "Number");
    properties.put("RotationFraction", "Number");
    properties.put("AngularVelocityAxes", "Array");
    properties.put("AngularOrientationAxes", "Array");
    Map<String, String[]> setterValues = new HashMap<String, String[]>();
    setterValues.put("HeadingMode", new String[] { headingMode });
    setterValues.put("I2cAddress7Bit", new String[] { ToolboxUtil.makeNumberShadow(8) });
    setterValues.put("I2cAddress8Bit", new String[] { ToolboxUtil.makeNumberShadow(16) });
    ToolboxUtil.addProperties(xmlToolbox, hardwareType, identifier, properties,
        setterValues, null /* enumBlocks */);

    // Functions
    Map<String, Map<String, String>> functions = new TreeMap<String, Map<String, String>>();
    functions.put("calibrate", null);
    functions.put("isCalibrating", null);
    functions.put("resetZAxisIntegrator", null);
    Map<String, String> getAngularVelocityArgs = new LinkedHashMap<String, String>();
    getAngularVelocityArgs.put("ANGLE_UNIT", ToolboxUtil.makeTypedEnumShadow("navigation", "angleUnit"));
    functions.put("getAngularVelocity", getAngularVelocityArgs);
    Map<String, String> getAngularOrientationArgs = new LinkedHashMap<String, String>();
    getAngularOrientationArgs.put("AXES_REFERENCE", ToolboxUtil.makeTypedEnumShadow("navigation", "axesReference"));
    getAngularOrientationArgs.put("AXES_ORDER", ToolboxUtil.makeTypedEnumShadow("navigation", "axesOrder"));
    getAngularOrientationArgs.put("ANGLE_UNIT", ToolboxUtil.makeTypedEnumShadow("navigation", "angleUnit"));
    functions.put("getAngularOrientation", getAngularOrientationArgs);
    ToolboxUtil.addFunctions(xmlToolbox, hardwareType, identifier, functions);
  }

  /**
   * Adds the category for IR seeker sensor to the toolbox.
   */
  private static void addIrSeekerSensorCategoryToToolbox(
      StringBuilder xmlToolbox, HardwareType hardwareType, List<HardwareItem> hardwareItems) {
    String identifier = hardwareItems.get(0).identifier;
    String mode = ToolboxUtil.makeTypedEnumShadow(hardwareType, "mode");
    String threshold = ToolboxUtil.makeNumberShadow(0.003);

    // Properties
    SortedMap<String, String> properties = new TreeMap<String, String>();
    properties.put("SignalDetectedThreshold", "Number");
    properties.put("Mode", "IrSeekerSensorMode");
    properties.put("IsSignalDetected", "Boolean");
    properties.put("Angle", "Number");
    properties.put("Strength", "Number");
    properties.put("I2cAddress7Bit", "Number");
    properties.put("I2cAddress8Bit", "Number");
    Map<String, String[]> setterValues = new HashMap<String, String[]>();
    setterValues.put("SignalDetectedThreshold", new String[] { threshold });
    setterValues.put("Mode", new String[] { mode });
    setterValues.put("I2cAddress7Bit", new String[] { ToolboxUtil.makeNumberShadow(8) });
    setterValues.put("I2cAddress8Bit", new String[] { ToolboxUtil.makeNumberShadow(16) });
    ToolboxUtil.addProperties(xmlToolbox, hardwareType, identifier, properties,
        setterValues, null /* enumBlocks */);
  }

  /**
   * Adds the category for LED to the toolbox.
   */
  private static void addLedCategoryToToolbox(
      StringBuilder xmlToolbox, HardwareType hardwareType, List<HardwareItem> hardwareItems) {
    String identifier = hardwareItems.get(0).identifier;

    // Functions
    Map<String, Map<String, String>> functions = new TreeMap<String, Map<String, String>>();
    Map<String, String> enableLedArgs = new LinkedHashMap<String, String>();
    enableLedArgs.put("ENABLE", ToolboxUtil.makeBooleanShadow(true));
    functions.put("enableLed_Boolean", enableLedArgs);
    functions.put("isLightOn", null);
    ToolboxUtil.addFunctions(xmlToolbox, hardwareType, identifier, functions);
  }

  /**
   * Adds the category for light sensor to the toolbox.
   */
  private static void addLightSensorCategoryToToolbox(
      StringBuilder xmlToolbox, HardwareType hardwareType, List<HardwareItem> hardwareItems) {
    String identifier = hardwareItems.get(0).identifier;

    // Properties
    SortedMap<String, String> properties = new TreeMap<String, String>();
    properties.put("LightDetected", "Number");
    properties.put("RawLightDetected", "Number");
    properties.put("RawLightDetectedMax", "Number");
    ToolboxUtil.addProperties(xmlToolbox, hardwareType, identifier, properties,
        null /* setterValues */, null /* enumBlocks */);

    // Functions
    Map<String, Map<String, String>> functions = new TreeMap<String, Map<String, String>>();
    Map<String, String> enableLedArgs = new LinkedHashMap<String, String>();
    enableLedArgs.put("ENABLE", ToolboxUtil.makeBooleanShadow(true));
    functions.put("enableLed_Boolean", enableLedArgs);
    ToolboxUtil.addFunctions(xmlToolbox, hardwareType, identifier, functions);
  }

  /**
   * Adds the category for ColorRangeSensor to the toolbox.
   */
  private static void addColorRangeSensorCategoryToToolbox(
      StringBuilder xmlToolbox, HardwareType hardwareType, List<HardwareItem> hardwareItems) {
    String identifier = hardwareItems.get(0).identifier;

    // Properties
    SortedMap<String, String> properties = new TreeMap<String, String>();
    properties.put("Red", "Number");
    properties.put("Green", "Number");
    properties.put("Blue", "Number");
    properties.put("Alpha", "Number");
    properties.put("Argb", "Number");
    properties.put("Gain", "Number");
    properties.put("I2cAddress7Bit", "Number");
    properties.put("I2cAddress8Bit", "Number");
    properties.put("LightDetected", "Number");
    properties.put("RawLightDetected", "Number");
    properties.put("RawLightDetectedMax", "Number");
    Map<String, String[]> setterValues = new HashMap<String, String[]>();
    setterValues.put("Gain", new String[] { ToolboxUtil.makeNumberShadow(2) });
    setterValues.put("I2cAddress7Bit", new String[] { ToolboxUtil.makeNumberShadow(8) });
    setterValues.put("I2cAddress8Bit", new String[] { ToolboxUtil.makeNumberShadow(16) });
    ToolboxUtil.addProperties(xmlToolbox, hardwareType, identifier, properties,
        setterValues, null /* enumBlocks */);

    // Functions
    Map<String, Map<String, String>> functions = new TreeMap<String, Map<String, String>>();
    Map<String, String> getDistanceArgs = new LinkedHashMap<String, String>();
    getDistanceArgs.put("UNIT", ToolboxUtil.makeTypedEnumShadow("navigation", "distanceUnit"));
    functions.put("getDistance_Number", getDistanceArgs);
    functions.put("getNormalizedColors", null);
    ToolboxUtil.addFunctions(xmlToolbox, hardwareType, identifier, functions);
  }

  /**
   * Adds the category for ModernRoboticsI2cCompassSensor to the toolbox.
   */
  private static void addMrI2cCompassSensorCategoryToToolbox(
      StringBuilder xmlToolbox, HardwareType hardwareType, List<HardwareItem> hardwareItems) {
    String identifier = hardwareItems.get(0).identifier;

    // Properties
    SortedMap<String, String> properties = new TreeMap<String, String>();
    properties.put("Direction", "Number");
    properties.put("XAccel", "Number");
    properties.put("YAccel", "Number");
    properties.put("ZAccel", "Number");
    properties.put("XMagneticFlux", "Number");
    properties.put("YMagneticFlux", "Number");
    properties.put("ZMagneticFlux", "Number");
    properties.put("I2cAddress7Bit", "Number");
    properties.put("I2cAddress8Bit", "Number");
    Map<String, String[]> setterValues = new HashMap<String, String[]>();
    setterValues.put("I2cAddress7Bit", new String[] { ToolboxUtil.makeNumberShadow(8) });
    setterValues.put("I2cAddress8Bit", new String[] { ToolboxUtil.makeNumberShadow(16) });
    ToolboxUtil.addProperties(xmlToolbox, hardwareType, identifier, properties,
        setterValues, null /* enumBlocks */);

    // Functions
    Map<String, Map<String, String>> functions = new TreeMap<String, Map<String, String>>();
    Map<String, String> setModeArgs = new LinkedHashMap<String, String>();
    setModeArgs.put("COMPASS_MODE", ToolboxUtil.makeTypedEnumShadow(hardwareType, "compassMode"));
    functions.put("setMode_CompassMode", setModeArgs);
    functions.put("isCalibrating", null);
    functions.put("calibrationFailed", null);
    ToolboxUtil.addFunctions(xmlToolbox, hardwareType, identifier, functions);
  }

  /**
   * Adds the category for ModernRoboticsI2cRangeSensor to the toolbox.
   */
  private static void addMrI2cRangeSensorCategoryToToolbox(
      StringBuilder xmlToolbox, HardwareType hardwareType, List<HardwareItem> hardwareItems) {
    String identifier = hardwareItems.get(0).identifier;

    // Properties
    SortedMap<String, String> properties = new TreeMap<String, String>();
    properties.put("LightDetected", "Number");
    properties.put("RawLightDetected", "Number");
    properties.put("RawLightDetectedMax", "Number");
    properties.put("RawUltrasonic", "Number");
    properties.put("RawOptical", "Number");
    properties.put("CmUltrasonic", "Number");
    properties.put("CmOptical", "Number");
    properties.put("I2cAddress7Bit", "Number");
    properties.put("I2cAddress8Bit", "Number");
    Map<String, String[]> setterValues = new HashMap<String, String[]>();
    setterValues.put("I2cAddress7Bit", new String[] { ToolboxUtil.makeNumberShadow(8) });
    setterValues.put("I2cAddress8Bit", new String[] { ToolboxUtil.makeNumberShadow(16) });
    ToolboxUtil.addProperties(xmlToolbox, hardwareType, identifier, properties,
        setterValues, null /* enumBlocks */);

    // Functions
    Map<String, Map<String, String>> functions = new TreeMap<String, Map<String, String>>();
    Map<String, String> getDistanceArgs = new LinkedHashMap<String, String>();
    getDistanceArgs.put("UNIT", ToolboxUtil.makeTypedEnumShadow(hardwareType, "distanceUnit"));
    functions.put("getDistance_Number", getDistanceArgs);
    ToolboxUtil.addFunctions(xmlToolbox, hardwareType, identifier, functions);
  }

  /**
   * Adds the category for optical distance sensor to the toolbox.
   */
  private static void addOpticalDistanceSensorCategoryToToolbox(
      StringBuilder xmlToolbox, HardwareType hardwareType, List<HardwareItem> hardwareItems) {
    String identifier = hardwareItems.get(0).identifier;

    // Properties
    SortedMap<String, String> properties = new TreeMap<String, String>();
    properties.put("LightDetected", "Number");
    properties.put("RawLightDetected", "Number");
    properties.put("RawLightDetectedMax", "Number");
    ToolboxUtil.addProperties(xmlToolbox, hardwareType, identifier, properties,
        null /* setterValues */, null /* enumBlocks */);

    // Functions
    Map<String, Map<String, String>> functions = new TreeMap<String, Map<String, String>>();
    Map<String, String> enableLedArgs = new LinkedHashMap<String, String>();
    enableLedArgs.put("ENABLE", ToolboxUtil.makeBooleanShadow(true));
    functions.put("enableLed_Boolean", enableLedArgs);
    ToolboxUtil.addFunctions(xmlToolbox, hardwareType, identifier, functions);
  }

  /**
   * Adds the category for RevBlinkinLedDriver to the toolbox.
   */
  private static void addRevBlinkinLedDriverCategoryToToolbox(
      StringBuilder xmlToolbox, AssetManager assetManager) throws IOException {
    addAsset(xmlToolbox, assetManager, "toolbox/rev_blinkin_led_driver.xml");
  }

  /**
   * Adds the category for servo to the toolbox.
   */
  private static void addServoCategoryToToolbox(
      StringBuilder xmlToolbox, HardwareType hardwareType, List<HardwareItem> hardwareItems) {
    String identifier = hardwareItems.get(0).identifier;

    // Properties
    SortedMap<String, String> properties = new TreeMap<String, String>();
    properties.put("Direction", "Direction");
    properties.put("Position", "Number");
    Map<String, String[]> setterValues = new HashMap<String, String[]>();
    setterValues.put(
        "Direction", new String[] { ToolboxUtil.makeTypedEnumShadow(hardwareType, "direction") });
    setterValues.put("Position", new String[] { ToolboxUtil.makeNumberShadow(0) });
    ToolboxUtil.addProperties(xmlToolbox, hardwareType, identifier, properties,
        setterValues, null /* enumBlocks */);

    // Functions
    Map<String, Map<String, String>> functions = new TreeMap<String, Map<String, String>>();
    Map<String, String> scaleRangeArgs = new LinkedHashMap<String, String>();
    scaleRangeArgs.put("MIN", ToolboxUtil.makeNumberShadow(0.2));
    scaleRangeArgs.put("MAX", ToolboxUtil.makeNumberShadow(0.8));
    functions.put("scaleRange_Number", scaleRangeArgs);
    ToolboxUtil.addFunctions(xmlToolbox, hardwareType, identifier, functions);
  }

  /**
   * Adds the category for servo controller to the toolbox.
   */
  private static void addServoControllerCategoryToToolbox(
      StringBuilder xmlToolbox, HardwareType hardwareType, List<HardwareItem> hardwareItems) {
    String identifier = hardwareItems.get(0).identifier;

    // Properties
    SortedMap<String, String> properties = new TreeMap<String, String>();
    properties.put("PwmStatus", "PwmStatus");
    ToolboxUtil.addProperties(xmlToolbox, hardwareType, identifier, properties,
        null /* setterValues */, null /* enumBlocks */);

    // Functions
    Map<String, Map<String, String>> functions = new TreeMap<String, Map<String, String>>();
    functions.put("pwmEnable", null);
    functions.put("pwmDisable", null);
    ToolboxUtil.addFunctions(xmlToolbox, hardwareType, identifier, functions);
  }

  /**
   * Adds the category for touch sensor to the toolbox.
   */
  private static void addTouchSensorCategoryToToolbox(
      StringBuilder xmlToolbox, HardwareType hardwareType, List<HardwareItem> hardwareItems) {
    String identifier = hardwareItems.get(0).identifier;

    // Properties
    SortedMap<String, String> properties = new TreeMap<String, String>();
    properties.put("IsPressed", "Boolean");
    ToolboxUtil.addProperties(xmlToolbox, hardwareType, identifier, properties,
        null /* setterValues */, null /* enumBlocks */);
  }

  /**
   * Adds the category for ultrasonic sensor to the toolbox.
   */
  private static void addUltrasonicSensorCategoryToToolbox(
      StringBuilder xmlToolbox, HardwareType hardwareType, List<HardwareItem> hardwareItems) {
    String identifier = hardwareItems.get(0).identifier;

    // Properties
    SortedMap<String, String> properties = new TreeMap<String, String>();
    properties.put("UltrasonicLevel", "Number");
    ToolboxUtil.addProperties(xmlToolbox, hardwareType, identifier, properties,
        null /* setterValues */, null /* enumBlocks */);
  }

  /**
   * Adds the category for voltage sensor to the toolbox.
   */
  private static void addVoltageSensorCategoryToToolbox(
      StringBuilder xmlToolbox, HardwareType hardwareType, List<HardwareItem> hardwareItems) {
    String identifier = hardwareItems.get(0).identifier;

    // Properties
    SortedMap<String, String> properties = new TreeMap<String, String>();
    properties.put("Voltage", "Number");
    ToolboxUtil.addProperties(xmlToolbox, hardwareType, identifier, properties,
        null /* setterValues */, null /* enumBlocks */);
  }

  /**
   * Upgrades the given js content based on the given {@link HardwareItemMap}.
   */
  public static String upgradeJs(String jsContent, HardwareItemMap hardwareItemMap) {
    // In previous versions, identifier suffix AsBNO055IMU was AsAdafruitBNO055IMU.
    jsContent = replaceIdentifierSuffixInJs(jsContent,
        hardwareItemMap.getHardwareItems(HardwareType.BNO055IMU),
        "AsAdafruitBNO055IMU", "AsBNO055IMU");
    // In previous versions, identifier bno055imuParametersAccess was adafruitBNO055IMUParametersAccess.
    jsContent = replaceIdentifierInJs(jsContent,
        "adafruitBNO055IMUParametersAccess", "bno055imuParametersAccess");
    return jsContent;
  }

  /**
   * Replaces an identifier suffix in js.
   */
  private static String replaceIdentifierSuffixInJs(
      String jsContent, List<HardwareItem> hardwareItemList,
      String oldIdentifierSuffix, String newIdentifierSuffix) {
    if (hardwareItemList != null) {
      for (HardwareItem hardwareItem : hardwareItemList) {
        String newIdentifier = hardwareItem.identifier;
        if (newIdentifier.endsWith(newIdentifierSuffix)) {
          String oldIdentifier = newIdentifier.substring(0, newIdentifier.length() - newIdentifierSuffix.length())
              + oldIdentifierSuffix;
          String oldCode = oldIdentifier + ".";
          String newCode = newIdentifier + ".";
          jsContent = jsContent.replace(oldCode, newCode);
        }
      }
    }
    return jsContent;
  }

  /**
   * Replaces an identifier in js.
   */
  private static String replaceIdentifierInJs(
      String jsContent, String oldIdentifier, String newIdentifier) {
    String oldCode = oldIdentifier + ".";
    String newCode = newIdentifier + ".";
    return jsContent.replace(oldCode, newCode);
  }

  /**
   * Returns the name of the active configuration.
   */
  public static String getConfigurationName() {
    RobotConfigFileManager robotConfigFileManager = new RobotConfigFileManager();
    RobotConfigFile activeConfig = robotConfigFileManager.getActiveConfig();
    return activeConfig.getName();
  }

  private static Set<String> buildReservedWordsForFtcJava() {
    Set<String> set = new HashSet<>();
    // Include all the classes that we could import (see generateImport_).
    // android.graphics
    set.add("Color");
    // com.qualcomm.ftccommon
    set.add("SoundPlayer");
    // com.qualcomm.hardware.modernrobotics
    set.add("ModernRoboticsI2cCompassSensor");
    set.add("ModernRoboticsI2cGyro");
    set.add("ModernRoboticsI2cRangeSensor");
    // com.qualcomm.hardware.bosch
    set.add("BNO055IMU");
    set.add("JustLoggingAccelerationIntegrator");
    // com.qualcomm.hardware.rev
    set.add("RevBlinkinLedDriver");
    // com.qualcomm.robotcore.eventloop
    set.add("Autonomous");
    set.add("Disabled");
    set.add("LinearOpMode");
    set.add("TeleOp");
    // com.qualcomm.robotcore.hardware
    set.add("AccelerationSensor");
    set.add("AnalogInput");
    set.add("AnalogOutput");
    set.add("CRServo");
    set.add("ColorSensor");
    set.add("CompassSensor");
    set.add("DcMotor");
    set.add("DcMotorEx");
    set.add("DcMotorSimple");
    set.add("DigitalChannel");
    set.add("DistanceSensor");
    set.add("GyroSensor");
    set.add("Gyroscope");
    set.add("I2cAddr");
    set.add("I2cAddrConfig");
    set.add("I2cAddressableDevice");
    set.add("IrSeekerSensor");
    set.add("LED");
    set.add("Light");
    set.add("LightSensor");
    set.add("MotorControlAlgorithm");
    set.add("NormalizedColorSensor");
    set.add("NormalizedRGBA");
    set.add("OpticalDistanceSensor");
    set.add("OrientationSensor");
    set.add("PIDCoefficients");
    set.add("PIDFCoefficients");
    set.add("PWMOutput");
    set.add("Servo");
    set.add("ServoController");
    set.add("SwitchableLight");
    set.add("TouchSensor");
    set.add("UltrasonicSensor");
    set.add("VoltageSensor");
    // com.qualcomm.robotcore.util
    set.add("ElapsedTime");
    set.add("Range");
    set.add("ReadWriteFile");
    set.add("RobotLog");
    // java.util
    set.add("ArrayList");
    set.add("Collections");
    set.add("List");
    // org.firstinspires.ftc.robotcore.external
    set.add("ClassFactory");
    set.add("JavaUtil");
    // org.firstinspires.ftc.robotcore.external.android
    set.add("AndroidAccelerometer");
    set.add("AndroidGyroscope");
    set.add("AndroidOrientation");
    set.add("AndroidSoundPool");
    set.add("AndroidTextToSpeech");
    // org.firstinspires.ftc.robotcore.external.hardware.camera
    set.add("WebcamName");
    // org.firstinspires.ftc.robotcore.external.matrices
    set.add("MatrixF");
    set.add("OpenGLMatrix");
    set.add("VectorF");
    // org.firstinspires.ftc.robotcore.external.navigation
    set.add("Acceleration");
    set.add("AngleUnit");
    set.add("AngularVelocity");
    set.add("AxesOrder");
    set.add("AxesReference");
    set.add("Axis");
    set.add("DistanceUnit");
    set.add("MagneticFlux");
    set.add("Orientation");
    set.add("Position");
    set.add("Quaternion");
    set.add("RelicRecoveryVuMark");
    set.add("Temperature");
    set.add("TempUnit");
    set.add("UnnormalizedAngleUnit");
    set.add("Velocity");
    set.add("VuforiaBase");
    set.add("VuforiaLocalizer");
    set.add("VuforiaRelicRecovery");
    set.add("VuforiaRoverRuckus");
    set.add("VuforiaTrackable");
    set.add("VuforiaTrackableDefaultListener");
    set.add("VuforiaTrackables");
    // org.firstinspires.ftc.robotcore.internal.system
    set.add("AppUtil");
    // org.firstinspires.ftc.robotcore.external.tfod
    set.add("Recognition");
    set.add("TfodBase");
    set.add("TfodRoverRuckus");
    // LinearOpMode members
    set.add("waitForStart");
    set.add("idle");
    set.add("sleep");
    set.add("opModeIsActive");
    set.add("isStarted");
    set.add("isStopRequested");
    set.add("init");
    set.add("init_loop");
    set.add("start");
    set.add("loop");
    set.add("stop");
    set.add("handleLoop");
    set.add("LinearOpModeHelper");
    set.add("internalPostInitLoop");
    set.add("internalPostLoop");
    set.add("waitOneFullHardwareCycle");
    set.add("waitForNextHardwareCycle");
    set.add("OpMode");
    set.add("gamepad1");
    set.add("gamepad2");
    set.add("telemetry");
    set.add("time");
    set.add("requestOpModeStop");
    set.add("getRuntime");
    set.add("resetStartTime");
    set.add("updateTelemetry");
    set.add("msStuckDetectInit");
    set.add("msStuckDetectInitLoop");
    set.add("msStuckDetectStart");
    set.add("msStuckDetectLoop");
    set.add("msStuckDetectStop");
    set.add("internalPreInit");
    set.add("internalOpModeServices");
    set.add("internalUpdateTelemetryNow");
    // https://docs.oracle.com/javase/tutorial/java/nutsandbolts/_keywords.html
    set.add("abstract");
    set.add("assert");
    set.add("boolean");
    set.add("break");
    set.add("byte");
    set.add("case");
    set.add("catch");
    set.add("char");
    set.add("class");
    set.add("const");
    set.add("continue");
    set.add("default");
    set.add("do");
    set.add("double");
    set.add("else");
    set.add("enum");
    set.add("extends");
    set.add("final");
    set.add("finally");
    set.add("float");
    set.add("for");
    set.add("goto");
    set.add("if");
    set.add("implements");
    set.add("import");
    set.add("instanceof");
    set.add("int");
    set.add("interface");
    set.add("long");
    set.add("native");
    set.add("new");
    set.add("package");
    set.add("private");
    set.add("protected");
    set.add("public");
    set.add("return");
    set.add("short");
    set.add("static");
    set.add("strictfp");
    set.add("super");
    set.add("switch");
    set.add("synchronized");
    set.add("this");
    set.add("throw");
    set.add("throws");
    set.add("transient");
    set.add("try");
    set.add("void");
    set.add("volatile");
    set.add("while");
    // java.lang.*
    set.add("AbstractMethodError");
    set.add("Appendable");
    set.add("ArithmeticException");
    set.add("ArrayIndexOutOfBoundsException");
    set.add("ArrayStoreException");
    set.add("AssertionError");
    set.add("AutoCloseable");
    set.add("Boolean");
    set.add("BootstrapMethodError");
    set.add("Byte");
    set.add("Character");
    set.add("Character.Subset");
    set.add("Character.UnicodeBlock");
    set.add("Character.UnicodeScript");
    set.add("CharSequence");
    set.add("Class");
    set.add("ClassCastException");
    set.add("ClassCircularityError");
    set.add("ClassFormatError");
    set.add("ClassLoader");
    set.add("ClassNotFoundException");
    set.add("ClassValue");
    set.add("Cloneable");
    set.add("CloneNotSupportedException");
    set.add("Comparable");
    set.add("Compiler");
    set.add("Deprecated");
    set.add("Double");
    set.add("Enum");
    set.add("Enum");
    set.add("EnumConstantNotPresentException");
    set.add("Error");
    set.add("Exception");
    set.add("ExceptionInInitializerError");
    set.add("Float");
    set.add("FunctionalInterface");
    set.add("IllegalAccessError");
    set.add("IllegalAccessException");
    set.add("IllegalArgumentException");
    set.add("IllegalMonitorStateException");
    set.add("IllegalStateException");
    set.add("IllegalThreadStateException");
    set.add("IncompatibleClassChangeError");
    set.add("IndexOutOfBoundsException");
    set.add("InheritableThreadLocal");
    set.add("InstantiationError");
    set.add("InstantiationException");
    set.add("Integer");
    set.add("InternalError");
    set.add("InterruptedException");
    set.add("Iterable");
    set.add("LinkageError");
    set.add("Long");
    set.add("Math");
    set.add("NegativeArraySizeException");
    set.add("NoClassDefFoundError");
    set.add("NoSuchFieldError");
    set.add("NoSuchFieldException");
    set.add("NoSuchMethodError");
    set.add("NoSuchMethodException");
    set.add("NullPointerException");
    set.add("Number");
    set.add("NumberFormatException");
    set.add("Object");
    set.add("OutOfMemoryError");
    set.add("Override");
    set.add("Package");
    set.add("Process");
    set.add("ProcessBuilder");
    set.add("ProcessBuilder.Redirect");
    set.add("ProcessBuilder.Redirect.Type");
    set.add("Readable");
    set.add("ReflectiveOperationException");
    set.add("Runnable");
    set.add("Runtime");
    set.add("RuntimeException");
    set.add("RuntimePermission");
    set.add("SafeVarargs");
    set.add("SecurityException");
    set.add("SecurityManager");
    set.add("Short");
    set.add("StackOverflowError");
    set.add("StackTraceElement");
    set.add("StrictMath");
    set.add("String");
    set.add("StringBuffer");
    set.add("StringBuilder");
    set.add("StringIndexOutOfBoundsException");
    set.add("SuppressWarnings");
    set.add("System");
    set.add("Thread");
    set.add("ThreadDeath");
    set.add("ThreadGroup");
    set.add("ThreadLocal");
    set.add("Thread.State");
    set.add("Thread.UncaughtExceptionHandler");
    set.add("Throwable");
    set.add("TypeNotPresentException");
    set.add("UnknownError");
    set.add("UnsatisfiedLinkError");
    set.add("UnsupportedClassVersionError");
    set.add("UnsupportedOperationException");
    set.add("VerifyError");
    set.add("VirtualMachineError");
    set.add("Void");
    return set;
  }
}
