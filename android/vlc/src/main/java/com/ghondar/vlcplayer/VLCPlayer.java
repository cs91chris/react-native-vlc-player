package com.ghondar.vlcplayer;


import java.io.File;
import java.util.ArrayList;

import android.widget.Toast;
import android.content.Intent;
import android.os.Environment;

import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.util.VLCUtil;


public class VLCPlayer extends ReactContextBaseJavaModule {

    public final static String INDEX = "index";
    public final static String SOURCES = "source_list";
    public final static String OPTIONS = "options";

    private ArrayList<String> options = new ArrayList<String>();
    private ReactApplicationContext context;
    private LibVLC mLibVLC = null;


    public VLCPlayer(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext;

        try {
            mLibVLC = new LibVLC();
        }
        catch(IllegalStateException e) {
            Toast.makeText(reactContext,
                "Error initializing the libVLC multimedia framework!",
                Toast.LENGTH_LONG
            ).show();
        }
    }

    @Override
    public String getName() {
        return "VLCPlayer";
    }

    @ReactMethod
    public void setOption(String opt) {
        this.options.add(opt);
    }

    @ReactMethod
    public void setDefaultOptions() {
        int deblocking = getDeblocking(-1);

        options.add("--avcodec-skip-frame");     options.add("0");
        options.add("--avcodec-skip-idct");      options.add("0");
        options.add("--avcodec-skiploopfilter"); options.add("" + deblocking);
        options.add("--androidwindow-chroma");   options.add("RV32");
    }

    @ReactMethod
    public void play(String src) {
        ArrayList<String> adaptedSrc = new ArrayList<String>();
        adaptedSrc.add(src);
        playSources(adaptedSrc, 0);
    }

    @ReactMethod
    public void playList(ReadableArray sources, int idx) {
        ArrayList<String> adaptedSrc = (ArrayList<String>)(ArrayList<?>)(sources.toArrayList());
        playSources(adaptedSrc, idx);
    }

    private void playSources(ArrayList sources, int idx) {
        if(idx > sources.size()-1) {
            idx = 0;
        }
        Intent intent = new Intent(this.context, PlayerActivity.class);
        intent.putExtra(INDEX, idx);
        intent.putStringArrayListExtra(SOURCES, sources);
        intent.putStringArrayListExtra(OPTIONS, options);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        this.context.startActivity(intent);
    }

    private static int getDeblocking(int deblocking) {
        int ret = deblocking;

        if(deblocking < 0) {
            VLCUtil.MachineSpecs m = VLCUtil.getMachineSpecs();
            if(m == null) {
                return ret;
            }
            if((m.hasArmV6 && !(m.hasArmV7)) || m.hasMips) {
                ret = 4;
            }
            else if((m.frequency >= 1200 || m.bogoMIPS >= 1200) && m.processors > 2) {
                ret = 1;
            }
            else {
                ret = 3;
            }
        }
        else if(deblocking > 4) {
            ret = 3;
        }
        return ret;
    }
}
