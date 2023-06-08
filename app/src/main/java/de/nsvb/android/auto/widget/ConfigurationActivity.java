package de.nsvb.android.auto.widget;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * Created by ns130291 on 27.01.2018.
 */

public class ConfigurationActivity extends Activity {

    private static final String TAG = ConfigurationActivity.class.getName();

    public static final int REQUEST_PICK_APPWIDGET = 0;
    public static final int REQUEST_CREATE_APPWIDGET = 1;
    private static final int REQUEST_APPWIDGET_BOUND = 2;
    public static final String WIDGET_ID = "widget_id";
    public static final String PREFS_NAME = "widgetviewer";

    private FrameLayout mWidgetContainer;
    private Button mButtonAddWidget;

    private AppWidgetManager mAppWidgetManager;
    private AppWidgetHost mAppWidgetHost;

    private int widgetID = -1;
    // workaround for Samsung widgets which after configure deliver no intent data
    private int requestedAppWidgetID = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        mAppWidgetManager = AppWidgetManager.getInstance(this);
        mAppWidgetHost = new AppWidgetHost(this, 123456);

        mWidgetContainer = findViewById(R.id.frameLayout);
        mButtonAddWidget = findViewById(R.id.button3);

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle(R.string.app_name);
        setActionBar(tb);

        widgetID = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getInt(WIDGET_ID, -1);
        Log.d(TAG, "Saved widget ID: " + widgetID);
        if(widgetID != -1) {
            createWidget(widgetID);
        }

        mButtonAddWidget.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(widgetID == -1) {
                    selectWidget();
                } else {
                    removeWidget();
                    mButtonAddWidget.setText("Add Widget");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAppWidgetHost.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mAppWidgetHost.stopListening();
    }

    @Override
    protected void onStop() {
        super.onStop();


    }

    public void selectWidget() {
        requestedAppWidgetID = mAppWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(this, PickAppWidgetActivity.class);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, requestedAppWidgetID);
        startActivityForResult(pickIntent, REQUEST_PICK_APPWIDGET);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult resultCode=" + resultCode + " requestCode=" + requestCode);
        if(requestCode == REQUEST_PICK_APPWIDGET || requestCode == REQUEST_CREATE_APPWIDGET || requestCode == REQUEST_APPWIDGET_BOUND) {
            if(resultCode == RESULT_OK) {
                Log.d(TAG, "RESULT_OK");
                if(requestCode == REQUEST_PICK_APPWIDGET) {
                    Log.d(TAG, "REQUEST_PICK_APPWIDGET");
                    if (data != null) {
                        if (!data.getBooleanExtra(PickAppWidgetActivity.EXTRA_WIDGET_BIND_ALLOWED, false)) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                requestBindWidget(data);
                            } else {
                                configureWidget(data);
                            }
                        } else {
                            configureWidget(data);
                        }
                    } else {
                        Log.i(TAG, "Widget picker failed");
                    }
                } else if(requestCode == REQUEST_APPWIDGET_BOUND) {
                    Log.d(TAG, "REQUEST_APPWIDGET_BOUND");
                    if (data != null) {
                        configureWidget(data);
                    } else {
                        Log.i(TAG, "Widget bind failed");
                    }
                } else {
                    Log.d(TAG, "create widget");
                    createWidget(data);
                }
            } else {
                requestedAppWidgetID = -1;
                if (data != null) {
                    int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
                    if (appWidgetId != -1) {
                        mAppWidgetHost.deleteAppWidgetId(appWidgetId);
                    }
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_config, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void requestBindWidget(@NonNull Intent data) {
        final int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        if (requestedAppWidgetID != appWidgetId) {
            requestedAppWidgetID = appWidgetId;
        }
        final ComponentName provider = data.getParcelableExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER);
        final UserHandle profile;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            profile = data.getParcelableExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER_PROFILE);
        } else {
            profile = null;
        }

        new Handler().postDelayed(() -> {
            Log.d(TAG, "asking for permission");

            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_BIND);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, provider);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER_PROFILE, profile);
            }

            startActivityForResult(intent, REQUEST_APPWIDGET_BOUND);
        }, 500);
    }

    /**
     * Checks if the widget needs any configuration. If it needs, launches the
     * configuration activity.
     */
    private void configureWidget(Intent data) {
        int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        if (requestedAppWidgetID != appWidgetId) {
            requestedAppWidgetID = appWidgetId;
        }
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        if (appWidgetInfo.configure != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mAppWidgetHost.startAppWidgetConfigureActivityForResult(this, appWidgetId, 0, REQUEST_CREATE_APPWIDGET, null);
            } else {
                Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
                intent.setComponent(appWidgetInfo.configure);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                startActivityForResult(intent, REQUEST_CREATE_APPWIDGET);
            }
        } else {
            createWidget(data);
        }
    }

    public void createWidget(Intent data) {
        if (data != null) {
            requestedAppWidgetID = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        }
        widgetID = requestedAppWidgetID;
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().putInt(WIDGET_ID, widgetID).commit();
        createWidget(widgetID);
    }

    /**
     * Creates the widget and adds to our view layout.
     */
    public void createWidget(int appWidgetId) {
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);

        AppWidgetHostView hostView = mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);
        hostView.setAppWidget(appWidgetId, appWidgetInfo);

        mWidgetContainer.addView(hostView);

        mButtonAddWidget.setText("Remove Widget");

        requestedAppWidgetID = -1;
        Log.i(TAG, "The widget size is: " + appWidgetInfo.minWidth + "*" + appWidgetInfo.minHeight);
    }

    public void removeWidget() {
        mWidgetContainer.removeAllViews();
        if(widgetID != -1) {
            mAppWidgetHost.deleteAppWidgetId(widgetID);
        }
        widgetID = -1;
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().putInt(WIDGET_ID, widgetID).commit();
    }

}
