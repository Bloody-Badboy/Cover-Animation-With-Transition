package com.bloody.badboy.animation;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Random;


public class CoverAnimView extends LinearLayout {

    private final long FADE_ANIMATION_DURATION = 500;
    private final long IMAGE_CHANGE_DURATION = 2000;
    float maxScaleFactor = 1.5f, minScaleFactor = 1.0f, scale1, scale2, translationX1, translationY1, translationX, translationY;
    private Random random = new Random();
    private int imageIndex = -1;
    private Handler handler = new Handler();
    private ImageView[] imageViews;
    private Runnable runnable;

    public CoverAnimView(Context context) {
        super(context);
        init();
    }

    public CoverAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CoverAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        View view = inflate(getContext(), R.layout.cover_anim_view, this);
        imageViews = new ImageView[3];
        imageViews[0] = (ImageView) view.findViewById(R.id.image0);
        imageViews[1] = (ImageView) view.findViewById(R.id.image1);
        imageViews[2] = (ImageView) view.findViewById(R.id.image2);
    }

    public void playScaleAnimation(View view) {
        scale1 = pickScale();
        scale2 = pickScale();

        translationX = pickTranslation(view.getWidth(), scale2);
        translationY = pickTranslation(view.getHeight(), scale2);

        translationX1 = pickTranslation(view.getWidth(), scale1);
        translationY1 = pickTranslation(view.getHeight(), scale1);

        view.setScaleX(scale1);
        view.setScaleY(scale1);
        view.setTranslationX(translationX1);
        view.setTranslationY(translationY1);
        ViewPropertyAnimator viewPropertyAnimator = view.animate().translationX(translationX).translationY(translationY).scaleX(scale2).scaleY(scale2).setDuration(IMAGE_CHANGE_DURATION);
        viewPropertyAnimator.start();
    }

    public void startCoverAnimation() {
        handler.post(runnable);
    }

    public void start() {
        if (imageIndex == -1) {
            imageIndex = 1;
            playScaleAnimation(imageViews[imageIndex]);
            return;
        }
        int index = imageIndex;
        imageIndex = (1 + imageIndex) % imageViews.length;

        ImageView imageView = imageViews[imageIndex];
        imageView.setAlpha(0.0f);
        playScaleAnimation(imageView);

        ImageView view = imageViews[index];


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(FADE_ANIMATION_DURATION);
        animatorSet.playTogether(ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.0f), ObjectAnimator.ofFloat(imageView, "alpha", 0.0f, 1.0f));
        animatorSet.start();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        startCoverAnimation();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacks(runnable);
    }

    public float pickScale() {
        return minScaleFactor + random.nextFloat() * (maxScaleFactor - minScaleFactor);
    }

    public float pickTranslation(int size, float f) {
        return size * (f - 1.0f) * (random.nextFloat() - 0.5f);
    }

    public void init() {
        runnable = new Runnable() {

            @Override
            public void run() {
                start();
                handler.postDelayed(runnable, IMAGE_CHANGE_DURATION);
            }
        };
    }

}


