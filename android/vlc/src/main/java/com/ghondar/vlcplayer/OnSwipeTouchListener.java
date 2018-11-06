package com.ghondar.vlcplayer;


import android.view.View;
import android.view.MotionEvent;
import android.view.GestureDetector;

import android.content.Context;


public class OnSwipeTouchListener implements View.OnTouchListener {

    private GestureDetector gestureDetector;


    public OnSwipeTouchListener(Context c) {
        gestureDetector = new GestureDetector(c, new GestureListener());
    }

    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }

    public void onSwipeRight()  {}
    public void onSwipeLeft()   {}
    public void onSwipeUp()     {}
    public void onSwipeDown()   {}
    public void onClick()       {}
    public void onDoubleClick() {}
    public void onLongClick()   {}


    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VEL_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            onClick();
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            onDoubleClick();
            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            onLongClick();
            super.onLongPress(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velX, float velY) {
            boolean result = true;

            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();

                if(Math.abs(diffX) > Math.abs(diffY)) {
                    if(Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velX) > SWIPE_VEL_THRESHOLD) {
                        if(diffX > 0) {
                            onSwipeRight();
                        }
                        else {
                            onSwipeLeft();
                        }
                    }
                }
                else {
                    if(Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velY) > SWIPE_VEL_THRESHOLD) {
                        if(diffY > 0) {
                            onSwipeDown();
                        }
                        else {
                            onSwipeUp();
                        }
                    }
                }
            }
            catch(Exception exception) {
                result = false;
            }
            return result;
        }
    }
}
