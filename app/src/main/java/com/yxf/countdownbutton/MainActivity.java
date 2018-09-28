package com.yxf.countdownbutton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/**
 * @author yangxiaofei
 */
public class MainActivity extends AppCompatActivity implements CountDownButton.OnCountDownListener{

    private CountDownButton countDownButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        countDownButton = (CountDownButton) findViewById(R.id.count_down_button);
        countDownButton.setOnCountDownListener(this);

    }
    @Override
    protected void onStop() {
        //        countDownButton.onDestroy();
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        countDownButton.onDestroy();
        super.onDestroy();
    }

    @Override
    public void OnStartCountDownListener(View countDownButton) {
        Toast.makeText(MainActivity.this,"开始倒计时",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnEndCountDownListener(View countDownButton) {
        Toast.makeText(MainActivity.this,"倒计时结束",Toast.LENGTH_SHORT).show();
    }
}
