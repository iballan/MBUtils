package com.mbh.mbutils.ui;

import android.view.MotionEvent;
import android.view.View;

import java.util.Calendar;

/**
 * Created By MBH on 2016-06-20.
 */
public class MBFourCornersTouchHandler {
    private boolean close_1 = false;
    private int close_1_second = 0;
    private boolean close_2 = false;
    private int close_2_second = 0;
    private boolean close_3 = false;
    private int close_3_second = 0;
    private boolean close_4 = false;
    private int close_4_second = 0;

    private int viewWidth, viewHeight;
    private OnFourCornersClickedListener mOnFourCornersClickedListener;

    public MBFourCornersTouchHandler(View view, OnFourCornersClickedListener onFourCornersClickedListener) {
        this.mOnFourCornersClickedListener = onFourCornersClickedListener;
        view.setOnTouchListener(getOnTouchListener());
    }

    public static MBFourCornersTouchHandler applyTo(View view, OnFourCornersClickedListener onFourCornersClickedListener) {
        return new MBFourCornersTouchHandler(view, onFourCornersClickedListener);
    }

    private View.OnTouchListener getOnTouchListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        viewWidth = v.getWidth();
                        viewHeight = v.getHeight();
                        Calendar c = Calendar.getInstance();

                        int y = (int) event.getY();
                        int x = (int) event.getX();
                        int rightCorner = (viewWidth - 100);
                        int upperCorner = (viewHeight - 100);

                        if (x > rightCorner && y < 100) {
                            close_1 = true;
                            close_1_second = c.get(Calendar.SECOND);
                        }else
                        if (x > rightCorner && y > upperCorner) {
                            close_2 = true;
                            close_2_second = c.get(Calendar.SECOND);
                        }else
                        if (x < 100 && y > upperCorner) {
                            close_3 = true;
                            close_3_second = c.get(Calendar.SECOND);
                        }else
                        if (x < 100 && y < 100) {
                            close_4 = true;
                            close_4_second = c.get(Calendar.SECOND);
                            if (close_1 && close_2
                                    && close_3 && close_4) {
                                if (close_2_second - close_1_second < 3) {
                                    if (close_3_second - close_2_second < 3) {
                                        if (close_4_second - close_3_second < 3) {
                                            close_3 = false;
                                            close_3_second = 0;
                                            close_1 = false;
                                            close_1_second = 0;
                                            close_2 = false;
                                            close_2_second = 0;
                                            close_4 = false;
                                            close_4_second = 0;
                                            if (mOnFourCornersClickedListener != null)
                                                mOnFourCornersClickedListener.onFourCornersClicked();
                                        }
                                    }
                                }
                            }
                        }else{
                            if (mOnFourCornersClickedListener != null)
                                return mOnFourCornersClickedListener.onOtherTouch(event);
                        }
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        };
    }

    public interface OnFourCornersClickedListener {
        void onFourCornersClicked();
        boolean onOtherTouch(MotionEvent event);
    }
}