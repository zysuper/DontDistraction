package com.u3.dontdistraction.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.u3.dontdistraction.R;
import com.u3.dontdistraction.other.Problems;
import com.u3.dontdistraction.util.Recoder;

/**
 * Created by U3 on 2015/5/29.
 */
public class ScreenLockActivity extends Activity {
    TextView text;
    int lockTime;
    Button putAnswer;
    Button endLock;
    TextView problem;
    Problems problems;
    EditText answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Window win = getWindow();
        win.addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_screenlock);

        initView();
        initProblem();
        timeCountDown();
        initListener();
        setEndReciver();
    }

    public void timeCountDown() {
        lockTime = Recoder.lockTime;
        lockTime = lockTime * 60 * 1000;
        new CountDownTimer(lockTime, 1000) {
            public void onTick(long millisUntilFinished) {
                text.setText(getResources().getString(R.string.time_remain)
                        + millisUntilFinished / (60 * 1000)
                        + getResources().getString(R.string.minute)
                        + (millisUntilFinished / 1000 - (millisUntilFinished / (60 * 1000) * 60))
                        + getResources().getString(R.string.second));
            }

            public void onFinish() {
                text.setText(getResources().getString(R.string.time_end));
                Recoder.isTimeEnd = true;
                putAnswer.setVisibility(View.GONE);
                endLock.setText(getResources().getString(R.string.complete));
                endLock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mIntent = new Intent(ScreenLockActivity.this, ResultActivity.class);
                        startActivity(mIntent);
                        finish();
                    }
                });
            }
        }.start();
    }

    private void initView() {
        text = (TextView) findViewById(R.id.tv_msg);
        putAnswer = (Button) findViewById(R.id.bt_enteranswer);
        problem = (TextView) findViewById(R.id.tv_problem);
        answer = (EditText) findViewById(R.id.et_answer);
        endLock = (Button) findViewById(R.id.bt_endlock);
    }

    private void initListener() {
        putAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answer.getText().toString() != null && problems.isAnswerRight(answer.getText().toString())) {
                    Recoder.isTimeEnd = true;
                    Recoder.isFront = false;
                    Intent mIntent = new Intent(ScreenLockActivity.this, ResultActivity.class);
                    startActivity(mIntent);
                    finish();
                } else {
                    Toast.makeText(ScreenLockActivity.this, getResources().getString(R.string.wrong_answer), Toast.LENGTH_LONG).show();
                }
            }
        });
        endLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Recoder.isTimeEnd = false;
                Recoder.isFront = false;
                Intent mIntent = new Intent(ScreenLockActivity.this, ResultActivity.class);
                startActivity(mIntent);
                finish();
            }
        });
    }

    private void initProblem() {
        problems = new Problems();
        problem.setText(problems.getProblem());
    }


    private void setEndReciver() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction("com.u3.end");
        registerReceiver(endReciver, filter);
    }

    final BroadcastReceiver endReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return true;
    }
    //屏蔽menu
    @Override
    public void onWindowFocusChanged(boolean pHasWindowFocus) {
        super.onWindowFocusChanged(pHasWindowFocus);
        if (!pHasWindowFocus) {
            sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        }
    }
    @Override
    protected void onStart() {
       Recoder.isFront = true;
        super.onStart();
    }
}
