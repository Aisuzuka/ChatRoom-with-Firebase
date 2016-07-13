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
    int firstToolbarOffset;
    int dY;
    private int mToolbarOffset = 0;
    private int mToolbarHeight;
    private boolean mControlsVisible = true;
    private boolean firstMove = true;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_MOVE:
                if(firstMove){
                    pastY = (int) event.getY();
                    firstToolbarOffset = mToolbarOffset;
                    firstMove = false;
                } else {
                    dY = (int) (pastY - event.getY());
                    pastY = (int) event.getY();
                    if ((mToolbarOffset < mToolbarHeight && dY > 0) || (mToolbarOffset > 0 && dY < 0)) {
                    Log.e("mToolbarHeight", String.valueOf(mToolbarHeight));
                    Log.e("mToolbarOffset", String.valueOf(mToolbarOffset));
                    Log.e("dY", String.valueOf(dY));
                        mToolbarOffset += dY;
                        clipToolbarOffset();
                        onMoved(mToolbarOffset);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                firstMove = true;
                if((mToolbarOffset > mToolbarHeight * 1/2 && mToolbarOffset - firstToolbarOffset > 0) || (mToolbarOffset > mToolbarHeight * 1/2 && mToolbarOffset - firstToolbarOffset < 0)){
                    onHide(mToolbarOffset, mToolbarHeight);
                } else {
                    onShow(mToolbarOffset, 0);
                }
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
    public abstract void onMoved(int distance);
    public abstract void onShow(int nowDistance, int distance);
    public abstract void onHide(int nowDistance, int distance);


}
