package com.ghondar.vlcplayer;


import java.util.ArrayList;

import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.view.ViewGroup;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import android.widget.Toast;
import android.widget.ImageView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import android.net.Uri;
import android.app.Activity;
import android.content.Intent;
import android.annotation.TargetApi;
import android.graphics.PixelFormat;

import com.vlcplayer.R;

import org.videolan.libvlc.Media;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.MediaPlayer;


public class PlayerActivity extends Activity implements IVLCVout.Callback {
    private int sourceIdx;
    private String mFilePath;
    private ArrayList<String> sources;

    private LinearLayout layout;
    private SurfaceView mSurface;
    private SurfaceHolder holder;

    private LibVLC libvlc;
    private MediaPlayer mMediaPlayer;
    private MediaPlayer.EventListener mPlayerListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);

        layout = (LinearLayout) findViewById(R.id.vlc_container);
        mSurface = (SurfaceView) findViewById(R.id.vlc_surface);

        Intent intent = getIntent();
        sources = intent.getStringArrayListExtra(VLCPlayer.SOURCES);
        sourceIdx = intent.getExtras().getInt(VLCPlayer.INDEX);
        mFilePath = sources.get(sourceIdx);

        mPlayerListener = new VLCPlayerListener(this);
        playMovie(mFilePath);
    }

    @Override
    protected void onResume() {
        mMediaPlayer.play();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mMediaPlayer.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        releasePlayer();
        super.onDestroy();
    }

    public void playMovie(String media) {
        if(mMediaPlayer != null && mMediaPlayer.isPlaying())
            return;

        layout.setVisibility(View.VISIBLE);
        holder = mSurface.getHolder();

        releasePlayer();
        setupControls();

        try {
            Intent intent = getIntent();
            libvlc = new LibVLC(intent.getStringArrayListExtra(VLCPlayer.OPTIONS));
            mMediaPlayer = new MediaPlayer(libvlc);
            holder.setFormat(PixelFormat.RGBX_8888);
            holder.setKeepScreenOn(true);
            mMediaPlayer.setEventListener(mPlayerListener);

            final IVLCVout vout = mMediaPlayer.getVLCVout();
            if(!vout.areViewsAttached()) {
                vout.setVideoView(mSurface);
                vout.addCallback(this);
                vout.attachViews();
            }
        }
        catch(Exception e) {
            Toast.makeText(this, "Error starting player", Toast.LENGTH_LONG).show();
            finish();
        }
        playSource(media);
    }

    @Override
    public void onNewLayout(IVLCVout vout, int w, int h, int vbW, int vbH, int sN, int sD) {
        if (w * h == 0) {
            return;
        }
        changeSurfaceSize(w, h, vbW, vbH, sN, sD);
    }

    @Override
    public void onSurfacesCreated(IVLCVout vout) {
        return;
    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vout) {
        return;
    }

    @Override
    public void onHardwareAccelerationError(IVLCVout vout) {
        this.releasePlayer();
        Toast.makeText(this, "Error with hardware acceleration", Toast.LENGTH_LONG).show();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void changeSurfaceSize(int w, int h, int vbW, int vbH, int sN, int sD) {
        int screenWidth = getWindow().getDecorView().getWidth();
        int screenHeight = getWindow().getDecorView().getHeight();

        double aspectRatio, visibleWidth;
        double displayWidth = screenWidth, displayHeight = screenHeight;

        if(mMediaPlayer != null) {
            final IVLCVout vlcVout = mMediaPlayer.getVLCVout();
            vlcVout.setWindowSize(screenWidth, screenHeight);
        }
        if(screenWidth < screenHeight) {
            displayWidth = screenHeight;
            displayHeight = screenWidth;
        }
        if(displayWidth * displayHeight <= 1 || w * h <= 1) {
            return;
        }
        if(sD == sN) {
            visibleWidth = vbW;
            aspectRatio = (double) vbW / (double) vbH;
        }
        else {
            visibleWidth = vbW * (double) sN / sD;
            aspectRatio = visibleWidth / vbH;
        }

        double displayAspectRatio = displayWidth / displayHeight;
        int finalWidth = (int) Math.ceil(displayWidth * w / vbW);
        int finalHeight = (int) Math.ceil(displayHeight * h / vbH);

        SurfaceHolder holder = mSurface.getHolder();
        holder.setFixedSize(finalWidth, finalHeight);

        ViewGroup.LayoutParams lp = mSurface.getLayoutParams();
        lp.width = finalWidth;
        lp.height = finalHeight;
        mSurface.setLayoutParams(lp);
        mSurface.invalidate();
    }

    private void playSource(String src) {
        if(mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        Media m = new Media(libvlc, Uri.parse(src));
        m.setHWDecoderEnabled(true, false);
        m.addOption(":network-caching=150");
        m.addOption(":clock-jitter=0");
        m.addOption(":clock-synchro=0");

        try {
            mMediaPlayer.setMedia(m);
            mMediaPlayer.play();
        }
        catch(Exception exc) {
            Toast.makeText(this, "Unable to play source", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void toggleFullscreen(boolean fullscreen) {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        if(fullscreen) {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            layout.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE          |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN      |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY       |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION        |
                View.SYSTEM_UI_FLAG_FULLSCREEN
            );
        }
        else {
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        }
        getWindow().setAttributes(attrs);
    }

    private void setupControls() {
        this.mSurface.setOnTouchListener(new OnSwipeTouchListener(PlayerActivity.this) {
            @Override
            public void onSwipeRight() {
                super.onSwipeLeft();

                if(sources != null && sources.size() > 1) {                        
                    if(--sourceIdx < 0) {
                        sourceIdx = sources.size()-1;                            
                    }
                    playSource(sources.get(sourceIdx));
                }
            }
            @Override
            public void onSwipeLeft() {
                super.onSwipeRight();

                if(sources != null && sources.size() > 1) {                        
                    if(++sourceIdx > sources.size()-1) {
                        sourceIdx = 0;
                    }
                    playSource(sources.get(sourceIdx));
                }
            }
            @Override
            public void onSwipeUp() {
                PlayerActivity.this.finish();
            }

/*  TODO CHECKME
            @Override
            public void onClick() {
                FrameLayout vlcOverlay;
                vlcOverlay = (FrameLayout) PlayerActivity.this.findViewById(R.id.overlay);

                if(mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    vlcOverlay.setVisibility(View.VISIBLE);
                }
                else {
                    mMediaPlayer.play();
                    vlcOverlay.setVisibility(View.GONE);
                }
            } */
        });
    }

    protected void releasePlayer() {
        if(libvlc == null) {
            return;
        }
        mMediaPlayer.stop();

        final IVLCVout vout = mMediaPlayer.getVLCVout();
        vout.removeCallback(this);
        vout.detachViews();
        libvlc.release();

        holder = null;
        libvlc = null;
    }
}
