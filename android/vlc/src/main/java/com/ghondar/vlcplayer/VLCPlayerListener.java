package com.ghondar.vlcplayer;


import android.view.View;

import android.widget.Toast;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.vlcplayer.R;

import java.lang.ref.WeakReference;
import org.videolan.libvlc.MediaPlayer;



class VLCPlayerListener implements MediaPlayer.EventListener {

    private PlayerActivity player;


    public VLCPlayerListener(PlayerActivity owner) {
        WeakReference<PlayerActivity> mOwner = new WeakReference<PlayerActivity>(owner);
        this.player = mOwner.get();
    }

    @Override
    public void onEvent(MediaPlayer.Event event) {
        switch(event.type) {
            case MediaPlayer.Event.EndReached:
                player.releasePlayer();
            break;
            case MediaPlayer.Event.EncounteredError:
                Toast.makeText(player, "Unable to play source", Toast.LENGTH_LONG).show();
                player.finish();
            break;
            case MediaPlayer.Event.MediaChanged:
            break;
            case MediaPlayer.Event.Opening:
                spinnerShow(View.VISIBLE);
            break;
            case MediaPlayer.Event.Playing:
                spinnerShow(View.GONE);
            break;
            case MediaPlayer.Event.Paused:
            break;
            case MediaPlayer.Event.Stopped:
            break;
            default:
            break;
        }
    }

    private void spinnerShow(int visible) {
        ProgressBar spinner = (ProgressBar) player.findViewById(R.id.spinner);
        FrameLayout vlcOverlay = (FrameLayout) this.player.findViewById(R.id.overlay);

        vlcOverlay.setVisibility(visible);
        spinner.setVisibility(visible);
    }
}
