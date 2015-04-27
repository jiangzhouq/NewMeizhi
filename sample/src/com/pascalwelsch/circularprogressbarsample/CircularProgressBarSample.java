package com.pascalwelsch.circularprogressbarsample;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.pascalwelsch.holocircularprogressbar.HoloCircularProgressBar;
import org.w3c.dom.Text;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;
import java.util.Set;

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

    private boolean mPaused = false;

    private SoundPool soundPool;
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

        SlidingMenu menu = new SlidingMenu(this);
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

                        mHoloCircularProgressBar.setProgress(0.0f);
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
                        }, 1f, 60000);
                        mState = STATE_RUNNING;
                        mStartButton.setImageResource(R.drawable.pause_src);
                        mEndButton.setImageResource(R.drawable.stop_src);
                        mEndButton.setEnabled(true);
                        break;
                    case STATE_RUNNING:
                        mProgressBarAnimator.pause();
                        mStartButton.setImageResource(R.drawable.start_src);
                        mState = STATE_PAUSING;
                        break;
                    case STATE_PAUSING:
                        mProgressBarAnimator.resume();
                        mStartButton.setImageResource(R.drawable.pause_src);
                        mState = STATE_RUNNING;
                        break;

                }
            }
        });

        mEndButton = (ImageButton) findViewById(R.id.end);
        mEndButton.setEnabled(false);
        mEndButton.setImageResource(R.drawable.stop_d);
        mEndButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finalProgress = mHoloCircularProgressBar.getProgress();
                mProgressBarAnimator.cancel();
                mState = STATE_END;
                finalProgress = 0;
                animate(mHoloCircularProgressBar, new AnimatorListener() {
                    @Override
                    public void onAnimationCancel(final Animator animation) {
                        animation.end();
                    }

                    @Override
                    public void onAnimationEnd(final Animator animation) {
                        mState = STATE_END;
                        mEndButton.setImageResource(R.drawable.stop_d);
                        mEndButton.setEnabled(false);

                    }

                    @Override
                    public void onAnimationRepeat(final Animator animation) {
                    }

                    @Override
                    public void onAnimationStart(final Animator animation) {
                    }
                }, 0f, 500);
            }
        });

        mBlingButton = (ImageButton) findViewById(R.id.bling);
        mBlingButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(mEnableBling){
//                    mEnableBling = false;
//                    mBlingButton.setImageResource(R.drawable.bling_off_src);
//                    soundPool.load(CircularProgressBarSample.this, R.raw.shortbling,1);
//                    soundPool.play(1,1, 1, 0, 0, 1);
//                }else{
//                    mEnableBling = true;
//                    mBlingButton.setImageResource(R.drawable.bling_on_src);
//                    soundPool.load(CircularProgressBarSample.this, R.raw.longbling,1);
//                    soundPool.play(1,1, 1, 0, 0, 1);
//                }
                int flag = -1;
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    // Loop through paired devices
                    for (BluetoothDevice device : pairedDevices) {
                        // Add the name and address to an array adapter to show in a ListView
                        Log.d("qiqi", device.getName() + " " + device.getAddress());
                        BluetoothSocket tmp = null;
                        Method method;
                        try {
                            method = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                            tmp = (BluetoothSocket) method.invoke(device, 1);
                        } catch (Exception e) {
                            Log.e("TAG", e.toString());
                        }
                        BluetoothSocket socket = null;
                        socket = tmp;
                        try {
                            socket.connect();
                            Log.d("qiqi", "connect:"+socket.isConnected());
                            OutputStream outStream = socket.getOutputStream();
                            outStream.write(getHexBytes("AA000100045502100A0000CC33C33C"));
                        } catch (Exception e) {
                            Log.e("qiqi", e.toString());
                        }
                    }
                }
            }
        });

//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//
//                    mOne.setEnabled(false);
//                    mZero.setEnabled(false);
//
//                    animate(mHoloCircularProgressBar, new AnimatorListener() {
//
//                        @Override
//                        public void onAnimationCancel(final Animator animation) {
//                            animation.end();
//                        }
//
//                        @Override
//                        public void onAnimationEnd(final Animator animation) {
//                            if (!mAnimationHasEnded) {
//                                animate(mHoloCircularProgressBar, this);
//                            } else {
//                                mAnimationHasEnded = false;
//                            }
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(final Animator animation) {
//                        }
//
//                        @Override
//                        public void onAnimationStart(final Animator animation) {
//                        }
//                    });
//                } else {
//                    mAnimationHasEnded = true;
//                    mProgressBarAnimator.cancel();
//
//                    mOne.setEnabled(true);
//                    mZero.setEnabled(true);
//                }
//
//            }
        mTime = (TextView) findViewById(R.id.time);

        int myWhite = Color.rgb(255, 255, 240);
        int myRed = Color.rgb(205,76, 76);
        mHoloCircularProgressBar.setProgressColor(myRed);
        mHoloCircularProgressBar.setProgressBackgroundColor(myWhite);
        soundPool = new SoundPool(2, AudioManager.STREAM_SYSTEM, 5);
    }
    private byte[] getHexBytes(String message) {
        int len = message.length() / 2;
        char[] chars = message.toCharArray();
        String[] hexStr = new String[len];
        byte[] bytes = new byte[len];
        for (int i = 0, j = 0; j < len; i += 2, j++) {
            hexStr[j] = "" + chars[i] + chars[i + 1];
            bytes[j] = (byte) Integer.parseInt(hexStr[j], 16);
        }
        return bytes;
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
    private float finalProgress = 0.0f;
    private void animate(final HoloCircularProgressBar progressBar, final AnimatorListener listener,
            final float progress, final int duration) {

        mProgressBarAnimator = ObjectAnimator.ofFloat(progressBar, "progress", progress);
        mProgressBarAnimator.setDuration(duration);

        mProgressBarAnimator.addListener(new AnimatorListener() {

            @Override
            public void onAnimationCancel(final Animator animation) {
            }

            @Override
            public void onAnimationEnd(final Animator animation) {
                progressBar.setProgress(finalProgress == 0.0f ? progress :finalProgress);
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
                Log.d("qiqi","value:" + lastTime);

                int mMin = (int)(lastTime/60);
                String time1 = (mMin < 10 ? "0":"") + mMin;
                int mS = (int)(lastTime%60);
                String time2 = (mS < 10 ? "0":"") + mS;
                mTime.setText(time1 + ":" + time2);
                progressBar.setProgress((Float) animation.getAnimatedValue());
            }
        });
        progressBar.setMarkerProgress(progress);
        mProgressBarAnimator.start();
    }

}
