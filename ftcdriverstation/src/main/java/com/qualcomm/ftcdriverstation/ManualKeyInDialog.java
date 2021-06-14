package com.qualcomm.ftcdriverstation;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class ManualKeyInDialog extends Builder {
   Button doneBtn;
   EditText input;
   ManualKeyInDialog.Listener listener;
   String title;

   public ManualKeyInDialog(Context var1, String var2, ManualKeyInDialog.Listener var3) {
      super(var1);
      this.title = var2;
      this.listener = var3;
   }

   public static void setSoftInputMode(Context var0, int var1) {
      ((InputMethodManager)var0.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(var1, 0);
   }

   public AlertDialog show() {
      setTitle(this.title);
      setCancelable(false);
      View inflate = create().getLayoutInflater().inflate(R.layout.custom_input_dialog_layout, (ViewGroup) null, false);
      this.input = (EditText) inflate.findViewById(R.id.input);
      setView(inflate);
      //FIX THIS
      setPositiveButton("positive", new DialogInterface.OnClickListener() {
         public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
            ManualKeyInDialog.this.listener.onInput(ManualKeyInDialog.this.input.getText().toString());
            ManualKeyInDialog.setSoftInputMode(ManualKeyInDialog.this.getContext(), 1);
         }
      });
      //FIX THIS
      setNegativeButton("negative", new DialogInterface.OnClickListener() {
         public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.cancel();
            ManualKeyInDialog.setSoftInputMode(ManualKeyInDialog.this.getContext(), 1);
         }
      });
      AlertDialog show = super.show();
      this.doneBtn = show.getButton(-1);
      setSoftInputMode(getContext(), 2);
      return show;

   }

   public abstract static class Listener {
      public abstract void onInput(String var1);
   }
}
