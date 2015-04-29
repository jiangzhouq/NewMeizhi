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
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.pascalwelsch.holocircularprogressbar.HoloCircularProgressBar;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * The Class CircularProgressBarSample.
 *
 * @author Pascal Welsch
 * @since 05.03.2013
 */
public class CircularProgressBarSample extends Activity {

    private static final String TAG = CircularProgressBarSample.class.getSimpleName();

    private final int STATE_END = 1;
    private final int STATE_RUNNING = 2;
    private final int STATE_PAUSING =3;

    private int mState = STATE_END;
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
    private RangeBar mRangeBar;
    private int mUserTime = 60 * 15;
    private boolean isUserTimeSelectorShow = false;
    private SoundPool soundPool;
    private CircleImageView mProfileImage;
    private SlidingMenu menu;
    final AlphaAnimation zeroAnimation = new AlphaAnimation(1, 0);
    final AlphaAnimation oneAnimation = new AlphaAnimation(0, 1);
    private  TextView blueToothState;
    private BluetoothService.BlueBinder mBlueBinder;
    private BluetoothService mBlueService;
    private ServiceConnection mBlueConn ;
    private int mSoundFlag = 0;
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
        mStartTextView = (TextView) findViewById(R.id.start_text);
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
                switch (motionEvent.getAction()){
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
        mStartButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                if (mProgressBarAnimator != null) {
//                    mProgressBarAnimator.cancel();
//                }
//                animate(mHoloCircularProgressBar, null, 0f, 1000);
//                mHoloCircularProgressBar.setMarkerProgress(0f);
                switch (mState){
                    case STATE_END:
//                        if (mProgressBarAnimator != null) {
//                            mProgressBarAnimator.cancel();
//                        }

                        mHoloCircularProgressBar.setProgress(1.0f);
                        animate(mHoloCircularProgressBar, new AnimatorListener() {
                            @Override
                            public void onAnimationCancel(final Animator animation) {
                                animation.end();
                            }

                            @Override
                            public void onAnimationEnd(final Animator animation) {
                                mStartButton.setImageResource(R.drawable.start_src);
                                mState = STATE_END;
                            }

                            @Override
                            public void onAnimationRepeat(final Animator animation) {
                            }

                            @Override
                            public void onAnimationStart(final Animator animation) {
                            }
                        }, 0.0f, mUserTime*1000);
                        mState = STATE_RUNNING;
                        if (soundPool != null){
                            mSoundFlag = soundPool.play(1,1,1,0,-1,1);
                        }
                        mStartButton.setImageResource(R.drawable.pause_src);
                        mStartTextView.setText(R.string.btn_pause);
                        mEndButton.setImageResource(R.drawable.stop_src);
                        mEndTextView.setTextColor(Color.LTGRAY);
                        mEndButton.setEnabled(true);
                        if(mBlueBinder != null){
                            mBlueBinder.start(mUserTime);
                        }
                        break;
                    case STATE_RUNNING:
                        mProgressBarAnimator.pause();
                        mStartButton.setImageResource(R.drawable.start_src);
                        mStartTextView.setText(R.string.btn_start);
                        if (soundPool != null){
                            soundPool.pause(mSoundFlag);
                        }
                        mState = STATE_PAUSING;
                        break;
                    case STATE_PAUSING:
                        mProgressBarAnimator.resume();
                        if (soundPool != null){
                            soundPool.resume(mSoundFlag);
                        }
                        mStartButton.setImageResource(R.drawable.pause_src);
                        mStartTextView.setText(R.string.btn_pause);
                        mState = STATE_RUNNING;
                        break;

                }
            }
        });

        mEndButton = (ImageButton) findViewById(R.id.end);
        mEndButton.setEnabled(false);
        mEndButton.setImageResource(R.drawable.stop_d);
        mEndTextView.setTextColor(Color.rgb(124,124,124));
        mEndButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finalProgress = mHoloCircularProgressBar.getProgress();
                mProgressBarAnimator.cancel();
                mState = STATE_END;
                finalProgress = 1.0f;
                soundPool.stop(mSoundFlag);
                mEndButton.setImageResource(R.drawable.stop_d);
                mState = STATE_END;
                mEndTextView.setTextColor(Color.rgb(124, 124, 124));
                mEndButton.setEnabled(false);
                animate(mHoloCircularProgressBar, new AnimatorListener() {
                    @Override
                    public void onAnimationCancel(final Animator animation) {
                        animation.end();
                    }

                    @Override
                    public void onAnimationEnd(final Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(final Animator animation) {
                    }

                    @Override
                    public void onAnimationStart(final Animator animation) {
                    }
                }, 1.0f, 500);
                if(mBlueBinder != null){
                    mBlueBinder.stop();
                }
            }
        });

        mBlingButton = (ImageButton) findViewById(R.id.bling);
        mBlingButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mTime = (TextView) findViewById(R.id.time);
        mTime.setText(makeTime(mUserTime));
        mTime.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {
                if(mState == STATE_END){
                    showUserTimeSelector(!isUserTimeSelectorShow);
                }
            }
        });
        int myWhite = Color.rgb(255, 255, 240);
        int myRed = Color.rgb(205,76, 76);
        mHoloCircularProgressBar.setProgressColor(myRed);
        mHoloCircularProgressBar.setProgressBackgroundColor(myWhite);
        zeroAnimation.setDuration(500);
        oneAnimation.setDuration(500);
        soundPool = new SoundPool(2, AudioManager.STREAM_SYSTEM, 5);
        soundPool.load(CircularProgressBarSample.this, R.raw.shortbling,1);
        soundPool.load(CircularProgressBarSample.this, R.raw.longbling,2);


        Intent intent = new Intent("com.pascalwelsch.circularprogressbarsample.BLUE_SERVICE");
        mBlueConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mBlueBinder = (BluetoothService.BlueBinder)iBinder;
                mBlueService = mBlueBinder.getService();
                mBlueService.setOnBTStateListener(new BluetoothService.OnBTStateListener() {
                    @Override
                    public void onStateChanged(int state) {
                        Log.d("qiqi", "state:" + state);
                        switch (state){
                            case BluetoothService.STATE_BT_OFF:
                                blueToothState.setText(R.string.bluetooth_off);
                                break;
                            case BluetoothService.STATE_BT_ON:
                                break;
                            case BluetoothService.STATE_DISCONNECTED:
                                blueToothState.setText(R.string.device_disconnected);
                                break;
                            case BluetoothService.STATE_CONNECTING:
                                blueToothState.setText(R.string.device_connecting);
                                break;
                            case BluetoothService.STATE_CONNECTED:
                                blueToothState.setText(R.string.device_connected);
                                break;
                        }
                    }
                });
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        bindService(intent, mBlueConn, Context.BIND_AUTO_CREATE);
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

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.circular_progress_bar_sample, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_switch_theme:
                switchTheme();
                break;

            default:
                Log.w(TAG, "couldn't map a click action for " + item);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Switch theme.
     */
    public void switchTheme() {

        final Intent intent = getIntent();
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            final int theme = extras.getInt("theme", -1);
            if (theme == R.style.AppThemeLight) {
                getIntent().removeExtra("theme");
            } else {
                intent.putExtra("theme", R.style.AppThemeLight);
            }
        } else {
            intent.putExtra("theme", R.style.AppThemeLight);
        }
        finish();
        startActivity(intent);
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
                mTime.setText(makeTime(lastTime == 0.0f ? mUserTime : lastTime));
                progressBar.setProgress(1.0f - (Float) animation.getAnimatedValue());
                if(lastTime == 0.0f){
                    soundPool.play(2,1,1,0,3,1);
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
