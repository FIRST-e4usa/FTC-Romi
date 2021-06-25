package com.qualcomm.ftcdriverstation;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import java.util.List;

public class GamepadOverrideEntryAdapter extends BaseAdapter implements ListAdapter {
   private Activity activity;
   private List gamepadOverrideEntries;
   private int list_id;

   public GamepadOverrideEntryAdapter(Activity var1, int var2, List var3) {
      this.activity = var1;
      this.gamepadOverrideEntries = var3;
      this.list_id = var2;
   }

   public int getCount() {
      return this.gamepadOverrideEntries.size();
   }

   public Object getItem(int var1) {
      return this.gamepadOverrideEntries.get(var1);
   }

   public long getItemId(int var1) {
      return 0L;
   }

   public View getView(int var1, View var2, ViewGroup var3) {
      View var4 = var2;
      if (var2 == null) {
         var4 = this.activity.getLayoutInflater().inflate(this.list_id, var3, false);
      }

      GamepadTypeOverrideMapper.GamepadTypeOverrideEntry var5 = (GamepadTypeOverrideMapper.GamepadTypeOverrideEntry)this.gamepadOverrideEntries.get(var1);
      ((TextView)var4.findViewById(android.R.id.text2)).setText(String.format("Mapped as %s", var5.mappedType.toString()));
      ((TextView)var4.findViewById(android.R.id.text1)).setText(String.format("VID: %d, PID: %d", var5.vid, var5.pid));
      return var4;
   }
}
