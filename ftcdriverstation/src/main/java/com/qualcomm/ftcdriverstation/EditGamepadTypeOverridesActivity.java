package com.qualcomm.ftcdriverstation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.qualcomm.ftcdriverstation.GamepadTypeOverrideMapper;
import com.qualcomm.ftcdriverstation.KeyEventCapturingProgressDialog;
import com.qualcomm.ftcdriverstation.SelectGamepadMappingDialog;
import com.qualcomm.hardware.logitech.LogitechGamepadF310;
import com.qualcomm.hardware.microsoft.MicrosoftGamepadXbox360;
import com.qualcomm.hardware.sony.SonyGamepadPS4;
import com.qualcomm.robotcore.hardware.Gamepad;
import java.util.ArrayList;
import java.util.Iterator;

public class EditGamepadTypeOverridesActivity extends Activity {
   boolean changesMade = false;
   KeyEventCapturingProgressDialog detectionDialog;
   ArrayList<GamepadTypeOverrideMapper.GamepadTypeOverrideEntry> entries = new ArrayList<>();
   ListView listView;
   GamepadTypeOverrideMapper mapper;
   GamepadOverrideEntryAdapter overrideEntryAdapter;

   /* access modifiers changed from: protected */
   public void onCreate(Bundle bundle) {
      super.onCreate(bundle);
      setContentView(R.layout.activity_edit_gamepad_type_overrides);
      GamepadTypeOverrideMapper gamepadTypeOverrideMapper = new GamepadTypeOverrideMapper(this);
      this.mapper = gamepadTypeOverrideMapper;
      this.entries.addAll(gamepadTypeOverrideMapper.getEntries());
      this.listView = (ListView) findViewById(R.id.overridesList);
      GamepadOverrideEntryAdapter gamepadOverrideEntryAdapter = new GamepadOverrideEntryAdapter(this, 17367044, this.entries);
      this.overrideEntryAdapter = gamepadOverrideEntryAdapter;
      this.listView.setAdapter(gamepadOverrideEntryAdapter);
      this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
         }
      });
      this.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
         public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long j) {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditGamepadTypeOverridesActivity.this);
            GamepadTypeOverrideMapper.GamepadTypeOverrideEntry gamepadTypeOverrideEntry = EditGamepadTypeOverridesActivity.this.entries.get(i);
            builder.setTitle("Delete entry?");
            builder.setMessage(String.format("Delete entry which maps %d:%d to %s?", new Object[]{Integer.valueOf(gamepadTypeOverrideEntry.vid), Integer.valueOf(gamepadTypeOverrideEntry.pid), gamepadTypeOverrideEntry.mappedType.toString()}));
            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialogInterface, int i) {
                  EditGamepadTypeOverridesActivity.this.entries.remove(i);
                  EditGamepadTypeOverridesActivity.this.overrideEntryAdapter.notifyDataSetChanged();
                  EditGamepadTypeOverridesActivity.this.changesMade = true;
               }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialogInterface, int i) {
               }
            });
            builder.show();
            return false;
         }
      });
   }

   public void handleSaveClicked(View view) {
      if (!this.changesMade) {
         finish();
         return;
      }
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setCancelable(false);
      builder.setTitle("Save changes?");
      builder.setMessage("Save changes and exit?");
      builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
         public void onClick(DialogInterface dialogInterface, int i) {
         }
      });
      builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
         public void onClick(DialogInterface dialogInterface, int i) {
            EditGamepadTypeOverridesActivity.this.mapper.setEntries(EditGamepadTypeOverridesActivity.this.entries);
            EditGamepadTypeOverridesActivity.this.finish();
         }
      });
      builder.show();
   }

   public void handleCancelClicked(View view) {
      if (!this.changesMade) {
         finish();
         return;
      }
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setCancelable(false);
      builder.setTitle("Discard changes?");
      builder.setMessage("Discard changes and exit?");
      builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
         public void onClick(DialogInterface dialogInterface, int i) {
         }
      });
      builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
         public void onClick(DialogInterface dialogInterface, int i) {
            EditGamepadTypeOverridesActivity.this.finish();
         }
      });
      builder.show();
   }

   public void handleAddEntryClicked(View view) {
      KeyEventCapturingProgressDialog keyEventCapturingProgressDialog = new KeyEventCapturingProgressDialog(this);
      this.detectionDialog = keyEventCapturingProgressDialog;
      keyEventCapturingProgressDialog.setTitle("Gamepad identification");
      this.detectionDialog.setMessage("Please press any key on the gamepad.");
      this.detectionDialog.setCancelable(false);
      this.detectionDialog.setProgressStyle(0);
      this.detectionDialog.setButton(-2, "Abort", new DialogInterface.OnClickListener() {
         public void onClick(DialogInterface dialogInterface, int i) {
            Toast.makeText(EditGamepadTypeOverridesActivity.this, "Aborted gamepad detection", Toast.LENGTH_SHORT).show();
         }
      });
      this.detectionDialog.setListener(new KeyEventCapturingProgressDialog.Listener() {
         public void onKeyDown(KeyEvent keyEvent) {
            if (Gamepad.isGamepadDevice(keyEvent.getDeviceId()) && EditGamepadTypeOverridesActivity.this.detectionDialog != null && EditGamepadTypeOverridesActivity.this.detectionDialog.isShowing()) {
               EditGamepadTypeOverridesActivity.this.detectionDialog.dismiss();
               InputDevice device = InputDevice.getDevice(keyEvent.getDeviceId());
               int vendorId = device.getVendorId();
               int productId = device.getProductId();
               Iterator<GamepadTypeOverrideMapper.GamepadTypeOverrideEntry> it = EditGamepadTypeOverridesActivity.this.entries.iterator();
               while (it.hasNext()) {
                  if (it.next().usbIdsMatch(vendorId, productId)) {
                     EditGamepadTypeOverridesActivity.this.showEntryAlreadyExistsDialog(vendorId, productId);
                     return;
                  }
               }
               if (LogitechGamepadF310.matchesVidPid(vendorId, productId) || MicrosoftGamepadXbox360.matchesVidPid(vendorId, productId) || SonyGamepadPS4.matchesVidPid(vendorId, productId)) {
                  EditGamepadTypeOverridesActivity.this.showOfficiallySupportedDialog(vendorId, productId);
               } else {
                  EditGamepadTypeOverridesActivity.this.showGamepadTypeChooserDialog(vendorId, productId);
               }
            }
         }
      });
      this.detectionDialog.show();
   }

   public void showEntryAlreadyExistsDialog(int i, int i2) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setCancelable(false);
      builder.setTitle("Already Exists");
      builder.setMessage(String.format("An entry which maps %d:%d already exits. If you'd like to change the mapping target, please delete the current entry first.", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)}));
      builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
         public void onClick(DialogInterface dialogInterface, int i) {
         }
      });
      builder.show();
   }

   public void showOfficiallySupportedDialog(int i, int i2) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setCancelable(false);
      builder.setTitle("Officially Supported");
      builder.setMessage(String.format("The USB identifier %d:%d is that of an officially supported gamepad. Overrides are not allowed for officially supported gamepads.", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)}));
      builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
         public void onClick(DialogInterface dialogInterface, int i) {
         }
      });
      builder.show();
   }

   public void showGamepadTypeChooserDialog(final int i, final int i2) {
      SelectGamepadMappingDialog selectGamepadMappingDialog = new SelectGamepadMappingDialog(this);
      selectGamepadMappingDialog.setListener(new SelectGamepadMappingDialog.Listener() {
         public void onOk(Gamepad.Type type) {
            EditGamepadTypeOverridesActivity.this.entries.add(new GamepadTypeOverrideMapper.GamepadTypeOverrideEntry(i, i2, type));
            EditGamepadTypeOverridesActivity.this.overrideEntryAdapter.notifyDataSetChanged();
            EditGamepadTypeOverridesActivity.this.changesMade = true;
         }
      });
      selectGamepadMappingDialog.show();
   }

   public boolean dispatchKeyEvent(KeyEvent keyEvent) {
      if (keyEvent.getKeyCode() != 4 || keyEvent.getAction() != 0) {
         return super.dispatchKeyEvent(keyEvent);
      }
      handleCancelClicked((View) null);
      return true;
   }
}