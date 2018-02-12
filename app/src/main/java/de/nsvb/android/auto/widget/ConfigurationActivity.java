package de.nsvb.android.auto.widget;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.Toolbar;

/**
 * Created by ns130291 on 27.01.2018.
 */

public class ConfigurationActivity extends Activity {

    private static final String TAG = ConfigurationActivity.class.getName();

    public static final int REQUEST_PICK_APPWIDGET = 0;
    public static final int REQUEST_CREATE_APPWIDGET = 1;
    public static final String WIDGET_ID = "widget_id";
    public static final String PREFS_NAME = "widgetviewer";

    private FrameLayout mWidgetContainer;
    private Button mButtonAddWidget;

    private AppWidgetManager mAppWidgetManager;
    private AppWidgetHost mAppWidgetHost;

    private int widgetID = -1;

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
        int appWidgetId = mAppWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        startActivityForResult(pickIntent, REQUEST_PICK_APPWIDGET);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_PICK_APPWIDGET || requestCode == REQUEST_CREATE_APPWIDGET) {
            if(resultCode == RESULT_OK) {
                if(requestCode == REQUEST_PICK_APPWIDGET) {
                    configureWidget(data);
                } else {
                    createWidget(data);
                }
            } else {
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

    /**
     * Checks if the widget needs any configuration. If it needs, launches the
     * configuration activity.
     */
    private void configureWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        if (appWidgetInfo.configure != null) {
            //Toast.makeText(this, "Configure is not supported", Toast.LENGTH_SHORT).show();
            //removeWidget();
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            startActivityForResult(intent, REQUEST_CREATE_APPWIDGET);
        } else {
            createWidget(data);
        }
    }

    public void createWidget(Intent data) {
        Bundle extras = data.getExtras();

        widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
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

        //mButtonAddWidget.setEnabled(false);
        mButtonAddWidget.setText("Remove Widget");

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
