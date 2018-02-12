package de.nsvb.android.auto.widget;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by ns130291 on 27.01.2018.
 */

public class WidgetFragment extends CarFragment {

    private static final String TAG = WidgetFragment.class.getName();

    private FrameLayout mWidgetContainer;

    private AppWidgetManager mAppWidgetManager;
    private AppWidgetHost mAppWidgetHost;
    private AppWidgetHostView mHostView;

    private int widgetID = -1;
    private boolean fullscreen = false;

    public WidgetFragment(){
        setTitle("WidgetViewer");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Log.d(TAG, "onAttach");

        fullscreen = context.getSharedPreferences(ConfigurationActivity.PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(getString(R.string.key_fullscreen_switch), false);

        mAppWidgetManager = AppWidgetManager.getInstance(context);
        mAppWidgetHost = new AppWidgetHost(context, 123456);

        widgetID = context.getSharedPreferences(ConfigurationActivity.PREFS_NAME, Context.MODE_PRIVATE)
                .getInt(ConfigurationActivity.WIDGET_ID, -1);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_widget, container, false);

        mWidgetContainer = view.findViewById(R.id.widget_container);
        if(fullscreen) {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mWidgetContainer.getLayoutParams();
            layoutParams.setMargins(0,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16,
                            getResources().getDisplayMetrics()), 0, 0);
        }

        /*int widgetID = getActivity().getPreferences(Context.MODE_PRIVATE).getInt(ConfigurationActivity.WIDGET_ID, -1);
        if(widgetID != -1) {
            createWidget(widgetID);
        } else {
            Log.d(TAG, "No widget selected");
        }*/

        /*FragmentActivity activity = getActivity();
        if(activity != null) {
            widgetID = activity.getPreferences(Context.MODE_PRIVATE).getInt(ConfigurationActivity.WIDGET_ID, -1);
            Log.d(TAG, "widget ID: " + widgetID);
            createWidget(widgetID);
        } else {
            Log.e(TAG, "activity is null!");
        }*/
        if(widgetID != -1) {
            Log.d(TAG, "widget ID: " + widgetID);
            createWidget(widgetID);
        } else {
            Log.d(TAG, "no widget selected");
        }

        Log.i(TAG, "Frame size: " + mWidgetContainer.getWidth() + "*" + mWidgetContainer.getHeight());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");
        mAppWidgetHost.startListening();
        mWidgetContainer.post(new Runnable() {
            @Override
            public void run() {
                mHostView.updateAppWidgetSize(null, mWidgetContainer.getWidth() - 100, mWidgetContainer.getHeight() - 100, mWidgetContainer.getWidth() - 100, mWidgetContainer.getHeight() - 100);
                AppWidgetProviderInfo appWidgetInfo = mHostView.getAppWidgetInfo();
                Log.i(TAG, "Frame size: " + mWidgetContainer.getWidth() + "*" + mWidgetContainer.getHeight());
                Log.i(TAG, "The widget start size is: " + appWidgetInfo.minWidth + "*" + appWidgetInfo.minHeight);
                Log.i(TAG, "The min widget size is: " + appWidgetInfo.minResizeWidth + "*" + appWidgetInfo.minResizeHeight);
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "onPause");
        mAppWidgetHost.stopListening();
    }

    public void createWidget(int appWidgetId) {
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);

        mHostView = mAppWidgetHost.createView(getContext(), appWidgetId, appWidgetInfo);
        mHostView.setAppWidget(appWidgetId, appWidgetInfo);

        mWidgetContainer.addView(mHostView);


        //mHostView.updateAppWidgetSize(null, mWidgetContainer.getWidth(), mWidgetContainer.getHeight(), mWidgetContainer.getWidth(), mWidgetContainer.getHeight());
        Log.i(TAG, "Frame size: " + mWidgetContainer.getWidth() + "*" + mWidgetContainer.getHeight());
        Log.i(TAG, "The widget size is: " + appWidgetInfo.minWidth + "*" + appWidgetInfo.minHeight);
    }
}
