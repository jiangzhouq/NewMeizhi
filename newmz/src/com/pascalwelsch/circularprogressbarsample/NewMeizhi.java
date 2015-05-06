package com.pascalwelsch.circularprogressbarsample;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.pascalwelsch.holocircularprogressbar.HoloCircularProgressBar;
import com.umeng.update.UmengUpdateAgent;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * The Class CircularProgressBarSample.
 *
 * @author Pascal Welsch
 * @since 05.03.2013
 */
public class NewMeizhi extends Activity {

    private static final String TAG = NewMeizhi.class.getSimpleName();

    private final int STATE_NULL = 0;
    private final int STATE_END = 1;
    private final int STATE_RUNNING = 2;

    private int mState = STATE_NULL;
    private boolean mEnableBling = true;

    /**
     * The Switch button.
     */
    private ImageButton mStartButton;
    private ImageButton mEndButton;
    private ImageButton mBlingButton;

    private TextView mTime ;
    private HoloCircularProgressBar mHoloCircularProgressBar;

    private ObjectAnimator mProgressBarAnimator;
    private RelativeLayout mButtonsLayout;
    private RelativeLayout mRangeBarLayout;
    private boolean mPaused = false;
    private TextView mEndTextView;
    private TextView mStartTextView;
    private TextView mBlingTextView;
    private RangeBar mRangeBar;
    private int mUserTime = 60 * 15;
    private boolean isUserTimeSelectorShow = false;
    private SoundPool soundPool;
    private CircleImageView mProfileImage;
    private SlidingMenu menu;
    private RelativeLayout mState2Layout;
    private TextView mState2;
    final AlphaAnimation zeroAnimation = new AlphaAnimation(1, 0);
    final AlphaAnimation oneAnimation = new AlphaAnimation(0, 1);
    private  TextView blueToothState;
    private BluetoothService.BlueBinder mBlueBinder;
    private BluetoothService mBlueService;
    private ServiceConnection mBlueConn ;
    private int mSoundFlag = 0;
    private ImageButton mBlueSetting;
    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        if (getIntent() != null) {
            final Bundle extras = getIntent().getExtras();
            if (extras != null) {
                final int theme = extras.getInt("theme");
                if (theme != 0) {
                    setTheme(theme);
                }
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        UmengUpdateAgent.update(this);
        mHoloCircularProgressBar = (HoloCircularProgressBar) findViewById(
                R.id.holoCircularProgressBar);
        mProfileImage = (CircleImageView) findViewById(R.id.profile_image);
        mProfileImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.toggle();
            }
        });
        mEndTextView = (TextView) findViewById(R.id.end_text);
        mBlueSetting = (ImageButton) findViewById(R.id.blue_setting);
        mBlueSetting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                ComponentName cn = new ComponentName("com.android.settings",
                        "com.android.settings.bluetooth.BluetoothSettings");
                intent.setComponent(cn);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity( intent);
            }
        });
        mBlingTextView = (TextView) findViewById(R.id.bling_text);
        mStartTextView = (TextView) findViewById(R.id.start_text);
        mState2Layout = (RelativeLayout) findViewById(R.id.state_2);
        mState2 = (TextView) findViewById(R.id.device_state_2);
        mButtonsLayout = (RelativeLayout) findViewById(R.id.buttons);
        mRangeBarLayout = (RelativeLayout) findViewById(R.id.rangebar_layout);
        mRangeBar = (RangeBar) findViewById(R.id.rangebar);
        mRangeBar.setOnRangeBarChangeListener( new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int i, int i2, String s, String s2) {
                mUserTime = 60 * Integer.parseInt(s2);
                mTime.setText(makeTime(mUserTime));
            }
        });
        mRangeBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case (MotionEvent.ACTION_UP):
                        showUserTimeSelector(false);
                        break;
                }
                return false;
            }
        });
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.LEFT);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.menu);
        mStartButton = (ImageButton) findViewById(R.id.start);
        mStartButton.setImageResource(R.drawable.start_src);
//        mStartButton.setEnabled(false);
        mStartTextView.setTextColor(Color.rgb(124, 124, 124));
        mStartButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                if (mProgressBarAnimator != null) {
//                    mProgressBarAnimator.cancel();
//                }
//                animate(mHoloCircularProgressBar, null, 0f, 1000);
//                mHoloCircularProgressBar.setMarkerProgress(0f);
                switch (mState){
                    case STATE_NULL:
//                        if (mProgressBarAnimator != null) {
//                            mProgressBarAnimator.cancel();
//                        }
                        start();
                        mState = STATE_RUNNING;
                        break;
                    case STATE_END:
                        stop();
                        mState = STATE_NULL;
                        break;
                    case STATE_RUNNING:
                        stop();
                        mState = STATE_NULL;
//                        mProgressBarAnimator.pause();
//                        mStartButton.setImageResource(R.drawable.start_src);
//                        mStartTextView.setText(R.string.btn_start);
//                        if (soundPool != null){
//                            soundPool.stop(mSoundFlag);
//                        }
//                        mState = STATE_PAUSING;
                        break;

                }
            }
        });

        mEndButton = (ImageButton) findViewById(R.id.end);
        mEndButton.setEnabled(false);
        mEndButton.setImageResource(R.drawable.stop_p);
        mEndTextView.setTextColor(Color.rgb(124,124,124));
        mEndButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                stop();
            }
        });

        mBlingButton = (ImageButton) findViewById(R.id.bling);
        mBlingButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mEnableBling){
                    mEnableBling = false;
                    mBlingButton.setImageResource(R.drawable.bling_off_src);
                    mBlingTextView.setText(R.string.btn_bling_off);
                    switch(mState){
                        case STATE_END:
                            break;
                        case STATE_RUNNING:
                            if (soundPool != null){
                                soundPool.stop(mSoundFlag);
                            }
                            break;
                    }
                }else{
                    mEnableBling= true;
                    mBlingButton.setImageResource(R.drawable.bling_on_src);
                    mBlingTextView.setText(R.string.btn_bling);
                    switch(mState){
                        case STATE_END:
                            break;
                        case STATE_RUNNING:
                            if (soundPool != null){
                                mSoundFlag = soundPool.play(1,0.2f,0.2f,0,-1,1);
                            }
                            break;
                    }
                }
            }
        });

        mTime = (TextView) findViewById(R.id.time);
        mTime.setText(makeTime(mUserTime));
//        mTime.setOnClickListener(new OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                if(mState == STATE_NULL){
//                    showUserTimeSelector(!isUserTimeSelectorShow);
//                }
//            }
//        });
        mHoloCircularProgressBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mState == STATE_NULL){
                    showUserTimeSelector(!isUserTimeSelectorShow);
                }
            }
        });


        int myWhite = Color.rgb(255, 192, 203);
        int myRed = Color.rgb(255,255, 240);
        mHoloCircularProgressBar.setProgressColor(myRed);
        mHoloCircularProgressBar.setProgressBackgroundColor(myWhite);
        zeroAnimation.setDuration(500);
        oneAnimation.setDuration(500);
        soundPool = new SoundPool(2, AudioManager.STREAM_SYSTEM, 5);
        soundPool.load(NewMeizhi.this, R.raw.shortbling,1);
        soundPool.load(NewMeizhi.this, R.raw.longbling,2);


//        Intent intent = new Intent("com.pascalwelsch.circularprogressbarsample.BLUE_SERVICE");
//        mBlueConn = new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//                mBlueBinder = (BluetoothService.BlueBinder)iBinder;
//                mBlueService = mBlueBinder.getService();
//                mBlueService.setOnBTStateListener(new BluetoothService.OnBTStateListener() {
//                    @Override
//                    public void onStateChanged(int state) {
//                        Log.d("qiqi", "state:" + state);
//                        switch (state){
//                            case BluetoothService.STATE_BT_OFF:
//                                blueToothState.setText(R.string.bluetooth_off);
//                                mStartButton.setEnabled(false);
//                                mState2Layout.setVisibility(View.VISIBLE);
//                                mState2.setText(R.string.state_2_open_blue);
//                                break;
//                            case BluetoothService.STATE_BT_ON:
//                                break;
//                            case BluetoothService.STATE_DISCONNECTED:
//                                blueToothState.setText(R.string.device_disconnected);
//                                mStartButton.setEnabled(false);
//                                mState2Layout.setVisibility(View.VISIBLE);
//                                mState2.setText(R.string.state_2_reconnect);
//                                break;
//                            case BluetoothService.STATE_CONNECTING:
//                                blueToothState.setText(R.string.device_connecting);
//                                mStartButton.setEnabled(false);
//                                mState2Layout.setVisibility(View.GONE);
//                                break;
//                            case BluetoothService.STATE_CONNECTED:
//                                blueToothState.setText(R.string.device_connected);
//                                mStartButton.setEnabled(true);
//                                mStartButton.setImageResource(R.drawable.start_src);
//                                mStartTextView.setTextColor(Color.rgb(255,255,240));
//                                mState2Layout.setVisibility(View.GONE);
//                                break;
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void onServiceDisconnected(ComponentName componentName) {
//
//            }
//        };
//        bindService(intent, mBlueConn, Context.BIND_AUTO_CREATE);
    }

    private void stop(){
        finalProgress = mHoloCircularProgressBar.getProgress();
        mProgressBarAnimator.cancel();
        finalProgress = 1.0f;
        soundPool.stop(mSoundFlag);
        mProgressBarAnimator = ObjectAnimator.ofFloat(mHoloCircularProgressBar, "progress", 1.0f);
        mProgressBarAnimator.setDuration(500);
        mProgressBarAnimator.reverse();
        mTime.setText(makeTime(mUserTime));
        mProgressBarAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                float lastTime = (animation.getDuration() - animation.getCurrentPlayTime())/1000;
                mHoloCircularProgressBar.setProgress(1.0f - (Float) animation.getAnimatedValue());
            }
        });
        mHoloCircularProgressBar.setMarkerProgress(1.0f);
        mProgressBarAnimator.start();

        if(mBlueBinder != null){
            mBlueBinder.stop();
        }
        mStartButton.setImageResource(R.drawable.start_src);
        mStartTextView.setText(R.string.btn_start);
    }
    private void start(){
        mHoloCircularProgressBar.setProgress(1.0f);
        animate(mHoloCircularProgressBar, new AnimatorListener() {
            @Override
            public void onAnimationCancel(final Animator animation) {
                animation.end();
            }

            @Override
            public void onAnimationEnd(final Animator animation) {
                mStartButton.setImageResource(R.drawable.restart_src);
                mStartTextView.setText(R.string.btn_restart);
                mState = STATE_END;
            }

            @Override
            public void onAnimationRepeat(final Animator animation) {
            }

            @Override
            public void onAnimationStart(final Animator animation) {
            }
        }, 0.0f, mUserTime*1000);
        if (soundPool != null && mEnableBling){
            mSoundFlag = soundPool.play(1,0.2f,0.2f,0,-1,1);
        }
        mStartButton.setImageResource(R.drawable.stop_src);
        mStartTextView.setText(R.string.btn_end);
        if(mBlueBinder != null){
            mBlueBinder.start(mUserTime);
        }
    }
    private void enableBling(){

    }
    private void disableBling(){

    }

    @Override
    protected void onResume() {
        super.onResume();
        blueToothState = (TextView) findViewById(R.id.device_state);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mBlueConn);
    }

    /**
     * generates random colors for the ProgressBar
     */
    protected void switchColor() {
        Random r = new Random();
        int randomColor = Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256));
        mHoloCircularProgressBar.setProgressColor(randomColor);

        randomColor = Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256));
        mHoloCircularProgressBar.setProgressBackgroundColor(randomColor);
    }

    /**
     * Animate.
     *
     * @param progressBar the progress bar
     * @param listener    the listener
     */
    private float finalProgress = 1.0f;
    private void animate(final HoloCircularProgressBar progressBar, final AnimatorListener listener,
            final float progress, final int duration) {

        mProgressBarAnimator = ObjectAnimator.ofFloat(progressBar, "progress", progress);
        mProgressBarAnimator.setDuration(duration/60);

        mProgressBarAnimator.addListener(new AnimatorListener() {

            @Override
            public void onAnimationCancel(final Animator animation) {
            }

            @Override
            public void onAnimationEnd(final Animator animation) {
                progressBar.setProgress(finalProgress == 1.0f ? progress :finalProgress);
            }

            @Override
            public void onAnimationRepeat(final Animator animation) {
            }

            @Override
            public void onAnimationStart(final Animator animation) {
            }
        });
        if (listener != null) {
            mProgressBarAnimator.addListener(listener);
        }
        mProgressBarAnimator.reverse();
        mProgressBarAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                float lastTime = (animation.getDuration() - animation.getCurrentPlayTime())/1000;
                mTime.setText(makeTime(lastTime));
                progressBar.setProgress(1.0f - (Float) animation.getAnimatedValue());
                if(lastTime == 0.0f && mEnableBling){
                    soundPool.play(2,0.5f,0.5f,0,1,1);
                }
            }
        });
        progressBar.setMarkerProgress(progress);
        mProgressBarAnimator.start();
    }

    private String makeTime(float time){
        int mMin = (int)(time/60);
        String time1 = (mMin < 10 ? "0":"") + mMin;
        int mS = (int)(time%60);
        String time2 = (mS < 10 ? "0":"") + mS;
        return time1 + ":" + time2;
    }
    private void showUserTimeSelector(boolean show){
        if(show){
            mButtonsLayout.setAnimation(zeroAnimation);
            zeroAnimation.startNow();
            mButtonsLayout.setVisibility(View.GONE);
            zeroAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mRangeBarLayout.setVisibility(View.VISIBLE);
                    mRangeBarLayout.setAnimation(oneAnimation);
                    oneAnimation.startNow();
                    isUserTimeSelectorShow = true;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }else{
            mRangeBarLayout.setAnimation(zeroAnimation);
            zeroAnimation.startNow();
            mRangeBarLayout.setVisibility(View.GONE);
            zeroAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mButtonsLayout.setVisibility(View.VISIBLE);
                    mButtonsLayout.setAnimation(oneAnimation);
                    oneAnimation.startNow();
                    isUserTimeSelectorShow = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        }
    }
}
