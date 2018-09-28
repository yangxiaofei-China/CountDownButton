package com.hanergy.countdownbutton;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * @author yangxiaofei
 * @data 2018/9/26 14:22
 * @description 倒计时Button
 * 使用方法：
 * 1.在attrs.xml中添加
 * <declare-styleable name="CountDownButton">
<attr name="time" format="integer"/>
<attr name="startText" format="string"/>
<attr name="countDownText" format="string"/>
<attr name="endText" format="string"/>
<attr name="startTextColor" format="color"/>
<attr name="countDownTextColor" format="color"/>
<attr name="endTextColor" format="color"/>
</declare-styleable>

 * 2.在xml中使用，在CountDownButton控件的xml中添加xmlns:countDownButton="http://schemas.android.com/apk/res-auto"
 * countDownButton可以自己更改，但是要与xml控件里的countDownButton:time="20"对应
 * res-auto可以用res/包名代替
 *
 * 3.在activity中调用，需要注意：在activity中的onStop()或者onDestroy()中调用countDownButton.onDestroy();
 * countDownButton = (CountDownButton)findViewById(R.id.count);
 *
 */

public class CountDownButton extends AppCompatButton implements View.OnClickListener{
    private final Context mContext;
    /** 倒计时时间 单位秒*/
    private int time;
    /** 初始倒计时按钮显示文字 */
    private String startText = "获取验证码";
    /** 正在倒计时时显示的文字 */
    private String countDownText="";
    /** 倒计时结束时按钮显示文字 */
    private String endText = "重新获取";
    /** 初始倒计时按钮显示文字的颜色 */
    private int startTextColor;
    /** 正在倒计时时显示的文字的颜色 */
    private int countDownTextColor;
    /** 倒计时结束时按钮显示文字的颜色 */
    private int endTextColor;
    private TimeCount timeCount;
    private long countTime;
    private SharedPreferences sp;
    private OnCountDownListener onCountDownListener;

    public CountDownButton(Context context) {
        this(context,null);
    }

    public CountDownButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CountDownButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.CountDownButton);
        time = array.getInteger(R.styleable.CountDownButton_time, 0);
        String startTextCdb = array.getString(R.styleable.CountDownButton_startText);
        if (startTextCdb != null){
            startText = startTextCdb;
        }
        String countDownTextCdb = array.getString(R.styleable.CountDownButton_countDownText);
        if (countDownTextCdb != null){
            countDownText = countDownTextCdb;
        }
        String endTextCdb = array.getString(R.styleable.CountDownButton_endText);
        if (endTextCdb != null){
            endText = endTextCdb;
        }
        startTextColor = array.getColor(R.styleable.CountDownButton_startTextColor, Color.GRAY);
        countDownTextColor = array.getColor(R.styleable.CountDownButton_countDownTextColor, Color.GRAY);
        endTextColor = array.getColor(R.styleable.CountDownButton_endTextColor, Color.GRAY);
        array.recycle();
        init();
    }

    private void init() {
        setOnClickListener(this);
        setText(startText);
        sp = mContext.getSharedPreferences("CountDownButton",Context.MODE_PRIVATE);
        long cTime = sp.getLong("cTime", 0);
        long time = sp.getLong("time", 0);
        if (cTime != 0 && time > 0){
            if ((System.currentTimeMillis() - cTime) < time * 1000){
                startCountDown((int) time);
                this.setClickable(false);
            }
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.commit();
        }
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setCountDownText(String countDownText) {
        this.countDownText = countDownText;
    }

    public void setEndText(String endText) {
        this.endText = endText;
    }

    public void setStartText(String startText) {
        this.startText = startText;
    }

    public void setStartTextColor(int startTextColor) {
        this.startTextColor = startTextColor;
    }

    public void setCountDownTextColor(int countDownTextColor) {
        this.countDownTextColor = countDownTextColor;
    }

    public void setEndTextColor(int endTextColor) {
        this.endTextColor = endTextColor;
    }

    public void setOnCountDownListener(OnCountDownListener onCountDownListener) {
        this.onCountDownListener = onCountDownListener;
    }

    @Override
    public void onClick(View v) {
        startCountDown(time);
        if (onCountDownListener != null){
            onCountDownListener.OnStartCountDownListener(this);
        }
    }

    private void startCountDown(int time) {
        timeCount = new TimeCount(time * 1000 + 1120,1000);
        timeCount.start();
    }

    /** 定义一个倒计时的内部类 */
    class TimeCount extends CountDownTimer {
        /**参数依次为总时长,和计时的时间间隔*/
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            CountDownButton.this.setClickable(false);

        }

        @Override
        public void onFinish() {// 计时完毕时触发
            Log.i("123","=======================结束=======================");
            CountDownButton.this.setText(endText);
            CountDownButton.this.setClickable(true);
            if (onCountDownListener != null) {
                onCountDownListener.OnEndCountDownListener(CountDownButton.this);
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            Log.i("123","millisUntilFinished="+millisUntilFinished);
            countTime = millisUntilFinished / 1000 - 1;
            CountDownButton.this.setText(countTime + "S"+countDownText);
        }
    }

    /**
     * 防止异常结束时数据丢失保存数据
     * 注意：this控件在xml中添加必须命名id：android:id="@+id/XXX"才会触发
     */
    @Override
    public Parcelable onSaveInstanceState() {
        Log.i("123","onSaveInstanceState");
        Bundle bundle = new Bundle();
        Parcelable superData = super.onSaveInstanceState();
        bundle.putParcelable("super_data",superData);
        if (timeCount != null){
            timeCount.cancel();
            timeCount = null;
            bundle.putLong("cTime",System.currentTimeMillis());
            bundle.putLong("time",countTime);
        }
        return bundle;

    }
    /**
     * 防止异常结束时数据丢失恢复数据
     * 注意：this控件在xml中添加必须命名id：android:id="@+id/XXX"才会触发
     */
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        Log.i("123","onRestoreInstanceState");
        Bundle bundle = (Bundle)state;
        Parcelable superData = bundle.getParcelable("super_data");
        long cTime = bundle.getLong("cTime");
        long time = bundle.getLong("time");
        if ((System.currentTimeMillis() - cTime) < time * 1000){
            startCountDown((int) time);
        }
        super.onRestoreInstanceState(superData);
    }

    /**
     * 同步activity的onDestroy()或者onStop()
     */
    protected void onDestroy() {
        if (timeCount != null){
            timeCount.cancel();
            SharedPreferences.Editor editor = sp.edit();
            editor.putLong("cTime",System.currentTimeMillis());
            editor.putLong("time",countTime);
            editor.commit();
        }
    }
    interface OnCountDownListener{
        /** 倒计时按钮开始时监听*/
        void OnStartCountDownListener(View countDownButton);
        /** 倒计时结束时的监听*/
        void OnEndCountDownListener(View countDownButton);
    }
}
