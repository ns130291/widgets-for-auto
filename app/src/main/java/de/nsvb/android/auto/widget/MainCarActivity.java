package de.nsvb.android.auto.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.google.android.apps.auto.sdk.CarActivity;
import com.google.android.apps.auto.sdk.CarUiController;
import com.google.android.apps.auto.sdk.StatusBarController;

public class MainCarActivity extends CarActivity {
    private static final String TAG = "MainCarActivity";

    static final String MENU_HOME = "home";
    static final String MENU_DEBUG = "debug";
    static final String MENU_DEBUG_LOG = "log";
    static final String MENU_DEBUG_TEST_NOTIFICATION = "test_notification";
    static final String MENU_LISTVIEW = "listview";
    static final String MENU_WIDGET = "widget";

    private static final String FRAGMENT_DEMO = "demo";
    private static final String FRAGMENT_LOG = "log";
    private static final String FRAGMENT_LISTVIEW = "listview";
    private static final String FRAGMENT_WIDGET = "widget";

    private static final String CURRENT_FRAGMENT_KEY = "app_current_fragment";

    private static final int TEST_NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "car";

    private String mCurrentFragmentTag;
    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle bundle) {
        setTheme(R.style.AppTheme_Car);
        super.onCreate(bundle);

        setContentView(R.layout.activity_car_main);

        CarUiController carUiController = getCarUiController();
        carUiController.getStatusBarController().showTitle();

        FragmentManager fragmentManager = getSupportFragmentManager();
        WidgetFragment widgetFragment = new WidgetFragment();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, widgetFragment, FRAGMENT_WIDGET)
                .commitNow();




        //MenuController menuController = carUiController.getMenuController();
        //menuController.setRootMenuAdapter(mainMenu);
        //menuController.showMenuButton();

        StatusBarController statusBarController = carUiController.getStatusBarController();
        //statusBarController.setAppBarAlpha(1f);
        //statusBarController.setAppBarBackgroundColor(getResources().getColor(R.color.car_accent));
        statusBarController.setTitle(getResources().getText(R.string.app_name));

        boolean fullscreen = getSharedPreferences(ConfigurationActivity.PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(getString(R.string.key_fullscreen_switch), false);
        Log.d(TAG, "fullscreen=" + fullscreen);
        if(fullscreen) {
            statusBarController.hideAppHeader();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

}
