package com.qualcomm.ftcdriverstation;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import com.qualcomm.robotcore.hardware.Gamepad;

public class SelectGamepadMappingDialog extends Builder {
    private ArrayAdapter fieldTypeAdapter;
    private Spinner fieldTypeSpinner;
    private SelectGamepadMappingDialog.Listener listener;

    public SelectGamepadMappingDialog(Context var1) {
        super(var1);
    }

    private void setupTypeSpinner() {
        this.fieldTypeAdapter = new ArrayAdapter(this.getContext(), R.layout.support_simple_spinner_dropdown_item);
        Gamepad.Type[] var1 = Gamepad.Type.values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            Gamepad.Type var4 = var1[var3];
            this.fieldTypeAdapter.add(var4.toString());
        }

        this.fieldTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.fieldTypeSpinner.setAdapter(this.fieldTypeAdapter);
    }

    public void setListener(SelectGamepadMappingDialog.Listener var1) {
        this.listener = var1;
    }

    public AlertDialog show() {
        setTitle("Choose Mapping");
        LayoutInflater layoutInflater = create().getLayoutInflater();
        FrameLayout frameLayout = new FrameLayout(getContext());
        setView(frameLayout);
        layoutInflater.inflate(R.layout.dialog_gamepad_mapping_type, frameLayout);
        this.fieldTypeSpinner = (Spinner) frameLayout.findViewById(R.id.gamepadMappingSpinner);
        setupTypeSpinner();
        setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (SelectGamepadMappingDialog.this.listener != null) {
                    SelectGamepadMappingDialog.this.listener.onOk(Gamepad.Type.valueOf((String) SelectGamepadMappingDialog.this.fieldTypeSpinner.getSelectedItem()));
                }
            }
        });
        setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return super.show();
    }


    interface Listener {
        void onOk(Gamepad.Type var1);
    }
}