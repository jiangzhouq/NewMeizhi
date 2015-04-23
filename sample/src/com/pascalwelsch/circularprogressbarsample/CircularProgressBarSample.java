package com.pascalwelsch.circularprogressbarsample;

import com.pascalwelsch.holocircularprogressbar.HoloCircularProgressBar;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

import java.util.Random;

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

    protected boolean mAnimationHasEnded = false;

    /**
     * The Switch button.
     */
    private Button mStartButton;
    private Button mEndButton;
    private Button mBlingButton;

    private HoloCircularProgressBar mHoloCircularProgressBar;

    private ObjectAnimator mProgressBarAnimator;

    private boolean mPaused = false;
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


        mStartButton = (Button) findViewById(R.id.start);
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
                        if (mProgressBarAnimator != null) {
                            mProgressBarAnimator.cancel();
                        }
                        animate(mHoloCircularProgressBar, null, 1f, 10000);
                        mState = STATE_RUNNING;
                        break;
                    case STATE_RUNNING:
                        mProgressBarAnimator.pause();
                        mState = STATE_PAUSING;
                        break;
                    case STATE_PAUSING:
                        mProgressBarAnimator.resume();
                        mState = STATE_RUNNING;
                        break;

                }
            }
        });

        mEndButton = (Button) findViewById(R.id.end);
        mEndButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mProgressBarAnimator != null) {
                    mProgressBarAnimator.pause();
                    mProgressBarAnimator.cancel();
                    mState = STATE_END;
                    animate(mHoloCircularProgressBar, null, 0.5f, 1000);
                }
            }
        });

        mBlingButton = (Button) findViewById(R.id.bling);
        mBlingButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

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
        getMenuInflater().inflate(R.menu.circular_progress_bar_sample, menu);
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
                progressBar.setProgress(progress);
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
                progressBar.setProgress((Float) animation.getAnimatedValue());
            }
        });
        progressBar.setMarkerProgress(progress);
        mProgressBarAnimator.start();
    }

}
