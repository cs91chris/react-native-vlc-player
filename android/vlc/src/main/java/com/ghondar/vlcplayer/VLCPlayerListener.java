package com.ghondar.vlcplayer;


import java.lang.ref.WeakReference;
import org.videolan.libvlc.MediaPlayer;


class VLCPlayerListener implements MediaPlayer.EventListener {
    private WeakReference<PlayerActivity> mOwner;

    public VLCPlayerListener(PlayerActivity owner) {
        mOwner = new WeakReference<PlayerActivity>(owner);
    }

    @Override
    public void onEvent(MediaPlayer.Event event) {
        PlayerActivity player = mOwner.get();

        switch(event.type) {
            case MediaPlayer.Event.EndReached:
                player.releasePlayer();
            break;
            case MediaPlayer.Event.EncounteredError:
            break;
            case MediaPlayer.Event.MediaChanged:
            break;
            case MediaPlayer.Event.Opening:
                // OPEN SPINNER
            break;
            case MediaPlayer.Event.Playing:
                // CLOSE SPINNER
            break;
            case MediaPlayer.Event.Paused:
            break;
            case MediaPlayer.Event.Stopped:
            break;
            default:
            break;
        }
    }
}
