package com.example.chienhua.chatroom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by chienhua on 2016/7/12.
 */
public abstract class HidingScrollListener implements View.OnTouchListener {
    public HidingScrollListener(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        mToolbarHeight = toolbarHeight;
    }

    int pastY;
    int dY;
    private int mToolbarOffset = 0;
    private int mToolbarHeight;
    private boolean mControlsVisible = true;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        clipToolbarOffset();
        onMoved(mToolbarOffset);
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.e("pastY", String.valueOf(pastY));
                pastY = (int) event.getY();
                mToolbarOffset = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                dY = (int) (pastY - event.getY());
                pastY = (int) event.getY();
                if((mToolbarOffset <mToolbarHeight && dY>0) || (mToolbarOffset >0 && dY<0)) {
//                    Log.e("mToolbarHeight", String.valueOf(mToolbarHeight));
//                    Log.e("mToolbarOffset", String.valueOf(mToolbarOffset));
//                    Log.e("dY", String.valueOf(dY));
                    mToolbarOffset += dY;
                }
                break;
            case MotionEvent.ACTION_UP:
                dY = 0;
                break;
        }

        return false;
    }
    private void clipToolbarOffset() {
        if(mToolbarOffset > mToolbarHeight) {
            mToolbarOffset = mToolbarHeight;
        } else if(mToolbarOffset < 0) {
            mToolbarOffset = 0;
        }
    }

    private void setVisible() {
        if(mToolbarOffset > 0) {
            onShow();
            mToolbarOffset = 0;
        }
        mControlsVisible = true;
    }

    private void setInvisible() {
        if(mToolbarOffset < mToolbarHeight) {
            onHide();
            mToolbarOffset = mToolbarHeight;
        }
        mControlsVisible = false;
    }
    public abstract void onMoved(int distance);
    public abstract void onShow();
    public abstract void onHide();


}
