/*
 * Apache DropDownView
 * Copyright 2017 The Apache Software Foundation
 *
 * This product includes software developed at
 * The Apache Software Foundation (http://www.apache.org/).
 */

package com.anthonyfdev.dropdownview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * A view for displaying a typical drop down with built-in animations.
 * <p>
 * Note: The height on this view should be set to <code>wrap_content</code> for easier use. Also,
 * you must use {@link #setExpandedView(View)} and {@link #setHeaderView(View)} for this view to work
 * correctly.
 *
 * @author Anthony Fermin (Fuzz)
 */

public class DropDownView extends RelativeLayout {

    private static final long COLLAPSE_TRANSITION_DURATION = 250L;
    private static final int DROP_DOWN_CONTAINER_MIN_DEFAULT_VIEWS = 1;
    private static final int DROP_DOWN_HEADER_CONTAINER_MIN_DEFAULT_VIEWS = 0;
    @Nullable
    private View expandedView;
    @Nullable
    private View headerView;
    private ViewGroup dropDownHeaderContainer;
    private LinearLayout dropDownContainer;
    private boolean isExpanded;
    private View emptyDropDownSpace;
    private TransitionSet expandTransitionSet;
    private TransitionSet collapseTransitionSet;
    private ObjectAnimator shadowFadeOutAnimator;
    private boolean isTransitioning;
    private DropDownListener dropDownListener;
    private int backgroundColor;
    private int overlayColor;

    public DropDownView(Context context) {
        super(context);
        init(context, null);
    }

    public DropDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DropDownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DropDownView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    /**
     * @return the {@link DropDownListener} that was set by you. Default is null.
     * @see #setDropDownListener(DropDownListener)
     */
    @Nullable
    public DropDownListener getDropDownListener() {
        return dropDownListener;
    }

    /**
     * @param dropDownListener your implementation of {@link DropDownListener}.
     * @see DropDownListener
     */
    public void setDropDownListener(DropDownListener dropDownListener) {
        this.dropDownListener = dropDownListener;
    }

    /**
     * @return true if the view is expanded, false otherwise.
     */
    public boolean isExpanded() {
        return isExpanded;
    }

    /**
     * Sets the view that will always be visible and expandable. The height of your provided view will
     * determine the height of the entire {@link DropDownView} in collapsed mode
     * (only if you set <code>wrap_content</code> to the {@link DropDownView}).
     *
     * @param headerView your header view
     */
    public void setHeaderView(@NonNull View headerView) {
        this.headerView = headerView;
        if (dropDownHeaderContainer.getChildCount() > DROP_DOWN_HEADER_CONTAINER_MIN_DEFAULT_VIEWS) {
            for (int i = DROP_DOWN_HEADER_CONTAINER_MIN_DEFAULT_VIEWS; i < dropDownHeaderContainer.getChildCount(); i++) {
                dropDownHeaderContainer.removeViewAt(i);
            }
        }
        dropDownHeaderContainer.addView(headerView);
    }

    /**
     * Sets the view that will always be visible only when the {@link DropDownView} is in expanded mode.
     * The height of your provided view and your provided header view will determine the height of
     * the entire {@link DropDownView} in expanded mode.
     * (only if you set <code>wrap_content</code> to the {@link DropDownView}).
     *
     * @param expandedView your header view
     */
    public void setExpandedView(@NonNull View expandedView) {
        this.expandedView = expandedView;
        if (dropDownContainer.getChildCount() > DROP_DOWN_CONTAINER_MIN_DEFAULT_VIEWS) {
            for (int i = DROP_DOWN_CONTAINER_MIN_DEFAULT_VIEWS; i < dropDownContainer.getChildCount(); i++) {
                dropDownContainer.removeViewAt(i);
            }
        }
        dropDownContainer.addView(expandedView);
        expandedView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
    }

    /**
     * Animates and expands the drop down, displaying the provided expanded view. Must set expanded
     * view before this for the drop down to expand.
     *
     * @see #setExpandedView(View)
     */
    public void expandDropDown() {
        if (!isExpanded && !isTransitioning && expandedView != null) {
            beginDelayedExpandTransition();
            if (dropDownListener != null) {
                dropDownListener.onExpandDropDown();
            }
            emptyDropDownSpace.setVisibility(View.VISIBLE);
            expandedView.setVisibility(View.VISIBLE);
            isExpanded = true;
        }
    }

    /**
     * Animates and collapses the drop down, displaying only the provided header view. Must set expanded
     * view before this for the drop down to collapse.
     *
     * @see #setExpandedView(View)
     */
    public void collapseDropDown() {
        if (isExpanded && !isTransitioning && expandedView != null) {
            beginDelayedCollapseTransition();
            expandedView.setVisibility(View.GONE);
            if (dropDownListener != null) {
                dropDownListener.onCollapseDropDown();
            }
            isExpanded = false;
        }
    }

    private void init(Context context, AttributeSet attrs) {
        handleAttrs(context, attrs);
        inflate(getContext(), R.layout.view_ddv_drop_down, this);
        bindViews();
        setupViews();
        setupTransitionSets();
    }

    private void handleAttrs(Context context, AttributeSet attrs) {
        if (context != null && attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.DropDownView,
                    0, 0);

            try {
                backgroundColor = a.getColor(R.styleable.DropDownView_containerBackgroundColor, ContextCompat.getColor(context, R.color.dDVColorPrimary));
                overlayColor = a.getColor(R.styleable.DropDownView_overlayColor, ContextCompat.getColor(context, R.color.dDVTransparentGray));
            } finally {
                a.recycle();
            }
        }
        if (backgroundColor == 0) {
            backgroundColor = ContextCompat.getColor(context, R.color.dDVColorPrimary);
        }
        if (overlayColor == 0) {
            overlayColor = ContextCompat.getColor(context, R.color.dDVTransparentGray);
        }
    }

    private void setupViews() {
        emptyDropDownSpace.setOnClickListener(emptyDropDownSpaceClickListener);
        dropDownHeaderContainer.setOnClickListener(dropDownHeaderClickListener);

        dropDownContainer.setBackgroundColor(backgroundColor);
        dropDownHeaderContainer.setBackgroundColor(backgroundColor);
        emptyDropDownSpace.setBackgroundColor(overlayColor);
    }

    private void bindViews() {
        dropDownContainer = (LinearLayout) findViewById(R.id.drop_down_container);
        emptyDropDownSpace = findViewById(R.id.empty_drop_down_space);
        dropDownHeaderContainer = (ViewGroup) findViewById(R.id.drop_down_header);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void beginDelayedExpandTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(this, expandTransitionSet);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void beginDelayedCollapseTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            shadowFadeOutAnimator.start();
            TransitionManager.beginDelayedTransition(dropDownContainer, collapseTransitionSet);
        } else {
            emptyDropDownSpace.setVisibility(View.GONE);
        }
    }

    private void setupTransitionSets() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            shadowFadeOutAnimator = ObjectAnimator.ofFloat(emptyDropDownSpace, View.ALPHA, 0f);
            shadowFadeOutAnimator.setDuration(COLLAPSE_TRANSITION_DURATION);
            shadowFadeOutAnimator.setInterpolator(new AccelerateInterpolator());
            shadowFadeOutAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    emptyDropDownSpace.setVisibility(View.GONE);
                    emptyDropDownSpace.setAlpha(1f);
                }
            });
            expandTransitionSet = createTransitionSet();
            collapseTransitionSet = createTransitionSet();
            collapseTransitionSet.setDuration(COLLAPSE_TRANSITION_DURATION);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private TransitionSet createTransitionSet() {
        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.addTarget(dropDownContainer);
        Fade fade = new Fade();
        fade.addTarget(emptyDropDownSpace);
        TransitionSet transitionSet = new TransitionSet();
        transitionSet.addTransition(changeBounds);
        transitionSet.addTransition(fade);
        transitionSet.setInterpolator(new AccelerateDecelerateInterpolator());
        transitionSet.addListener(new TransitionListenerAdapter() {
            @Override
            public void onTransitionStart(Transition transition) {
                super.onTransitionStart(transition);
                isTransitioning = true;
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                super.onTransitionEnd(transition);
                isTransitioning = false;
            }
        });
        return transitionSet;
    }

    private final OnClickListener dropDownHeaderClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isExpanded) {
                collapseDropDown();
            } else {
                expandDropDown();
            }
        }
    };

    private final OnClickListener emptyDropDownSpaceClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            collapseDropDown();
        }
    };

    @TargetApi(Build.VERSION_CODES.KITKAT)
    class TransitionListenerAdapter implements Transition.TransitionListener {

        @Override
        public void onTransitionStart(Transition transition) {

        }

        @Override
        public void onTransitionEnd(Transition transition) {

        }

        @Override
        public void onTransitionCancel(Transition transition) {

        }

        @Override
        public void onTransitionPause(Transition transition) {

        }

        @Override
        public void onTransitionResume(Transition transition) {

        }
    }

    /**
     * A listener that wraps functionality to be performed when the drop down is successfully expanded
     * or collapsed.
     */
    public interface DropDownListener {
        /**
         * This method will only be triggered when {@link #expandDropDown()} is called successfully.
         */
        void onExpandDropDown();

        /**
         * This method will only be triggered when {@link #collapseDropDown()} is called successfully.
         */
        void onCollapseDropDown();
    }
}
