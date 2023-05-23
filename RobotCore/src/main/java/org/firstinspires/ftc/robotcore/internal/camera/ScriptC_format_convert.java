//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.firstinspires.ftc.robotcore.internal.camera;

import androidx.renderscript.Allocation;
import androidx.renderscript.Element;
import androidx.renderscript.FieldPacker;
import androidx.renderscript.RSRuntimeException;
import androidx.renderscript.RenderScript;
import androidx.renderscript.ScriptC;
import androidx.renderscript.Script.FieldID;
import androidx.renderscript.Script.KernelID;
import androidx.renderscript.Script.LaunchOptions;

public class ScriptC_format_convert extends ScriptC {
    private static final String __rs_resource_name = "format_convert";
    private Element __ALLOCATION;
    private Element __I32;
    private Element __U8_4;
    private FieldPacker __rs_fp_ALLOCATION;
    private FieldPacker __rs_fp_I32;
    private static final int mExportVarIdx_inputAllocation = 0;
    private Allocation mExportVar_inputAllocation;
    private static final int mExportVarIdx_outputWidth = 1;
    private int mExportVar_outputWidth;
    private static final int mExportVarIdx_outputHeight = 2;
    private int mExportVar_outputHeight;
    private static final int mExportForEachIdx_yuv2_to_argb8888 = 1;

    public ScriptC_format_convert(RenderScript rs) {
        super(rs, "format_convert", format_convertBitCode.getBitCode32(), format_convertBitCode.getBitCode64());
        this.__ALLOCATION = Element.ALLOCATION(rs);
        this.__I32 = Element.I32(rs);
        this.__U8_4 = Element.U8_4(rs);
    }

    public synchronized void set_inputAllocation(Allocation v) {
        this.setVar(0, v);
        this.mExportVar_inputAllocation = v;
    }

    public Allocation get_inputAllocation() {
        return this.mExportVar_inputAllocation;
    }

    public FieldID getFieldID_inputAllocation() {
        return this.createFieldID(0, (Element)null);
    }

    public synchronized void set_outputWidth(int v) {
        this.setVar(1, v);
        this.mExportVar_outputWidth = v;
    }

    public int get_outputWidth() {
        return this.mExportVar_outputWidth;
    }

    public FieldID getFieldID_outputWidth() {
        return this.createFieldID(1, (Element)null);
    }

    public synchronized void set_outputHeight(int v) {
        this.setVar(2, v);
        this.mExportVar_outputHeight = v;
    }

    public int get_outputHeight() {
        return this.mExportVar_outputHeight;
    }

    public FieldID getFieldID_outputHeight() {
        return this.createFieldID(2, (Element)null);
    }

    public KernelID getKernelID_yuv2_to_argb8888() {
        return this.createKernelID(1, 58, (Element)null, (Element)null);
    }

    public void forEach_yuv2_to_argb8888(Allocation aout) {
        this.forEach_yuv2_to_argb8888(aout, (LaunchOptions)null);
    }

    public void forEach_yuv2_to_argb8888(Allocation aout, LaunchOptions sc) {
        if (!aout.getType().getElement().isCompatible(this.__U8_4)) {
            throw new RSRuntimeException("Type mismatch with U8_4!");
        } else {
            this.forEach(1, (Allocation)null, aout, (FieldPacker)null, sc);
        }
    }
}
