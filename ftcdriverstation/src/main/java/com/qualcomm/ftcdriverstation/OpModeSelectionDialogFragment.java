package com.qualcomm.ftcdriverstation;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;

public class OpModeSelectionDialogFragment extends DialogFragment {
   private OpModeSelectionDialogFragment.OpModeSelectionDialogListener listener = null;
   private List opModes = new LinkedList();
   private int title = 0;

   public Dialog onCreateDialog(Bundle var1) {
      View inflate = LayoutInflater.from(this.getActivity()).inflate(R.layout.opmode_dialog_title_bar, (ViewGroup)null);
      ((TextView)inflate.findViewById(R.id.opmodeDialogTitle)).setText(this.title);
      Builder var3 = new Builder(this.getActivity());
      var3.setCustomTitle(inflate);
      ArrayAdapter var4 = new ArrayAdapter(this.getActivity(), 2131427419, 2131231011, this.opModes) {
         public View getView(int i, View view, ViewGroup viewGroup) {
            View view2 = super.getView(i, view, viewGroup);
            ((ImageView) view2.findViewById(R.id.opmodeDialogItemTextSeparator)).setVisibility((i >= OpModeSelectionDialogFragment.this.opModes.size() + -1 || ((OpModeMeta) OpModeSelectionDialogFragment.this.opModes.get(i)).group.equals(((OpModeMeta) OpModeSelectionDialogFragment.this.opModes.get(i + 1)).group)) ? View.GONE : View.VISIBLE);
            return view2;
         }

      };
      var3.setTitle(this.title);
      var3.setAdapter(var4, new OnClickListener() {
         public void onClick(DialogInterface var1, int var2) {
            if (OpModeSelectionDialogFragment.this.listener != null) {
               OpModeSelectionDialogFragment.this.listener.onOpModeSelectionClick((OpModeMeta)OpModeSelectionDialogFragment.this.opModes.get(var2));
            }

         }
      });
      return var3.create();
   }

   public void onStart() {
      super.onStart();
      Dialog var1 = this.getDialog();
      var1.findViewById(var1.getContext().getResources().getIdentifier("android:id/titleDivider", (String)null, (String)null)).setBackground(var1.findViewById(R.id.opmodeDialogTitleLine).getBackground());
   }

   public void setOnSelectionDialogListener(OpModeSelectionDialogFragment.OpModeSelectionDialogListener var1) {
      this.listener = var1;
   }

   public void setOpModes(List<OpModeMeta> list) {
      LinkedList linkedList = new LinkedList(list);
      this.opModes = linkedList;
      Collections.sort(linkedList, new Comparator<OpModeMeta>() {
         public int compare(OpModeMeta opModeMeta, OpModeMeta opModeMeta2) {
            int compareTo = opModeMeta.group.compareTo(opModeMeta2.group);
            return compareTo == 0 ? opModeMeta.name.compareTo(opModeMeta2.name) : compareTo;
         }
      });
   }

   public void setTitle(int var1) {
      this.title = var1;
   }

   public interface OpModeSelectionDialogListener {
      void onOpModeSelectionClick(OpModeMeta var1);
   }
}
