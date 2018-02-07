package de.nsvb.android.auto.widget;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.github.martoreto.aademo.BuildConfig;
import com.github.martoreto.aademo.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView tv = findViewById(R.id.textView);
        tv.setText(Html.fromHtml("Version " + BuildConfig.VERSION_NAME + "<br><br>" +
                "This app is based on the following projects:<br><br>" +
                "aauto-sdk-demo by martoreto<br>" +
                "<a href=\"https://github.com/martoreto/aauto-sdk-demo\">https://github.com/martoreto/aauto-sdk-demo</a><br><br>" +
                "Widget Host Example by Leonardo Fischer<br>" +
                "<a href=\"https://github.com/lgfischer/WidgetHostExample\">https://github.com/lgfischer/WidgetHostExample</a>"));
        tv.setMovementMethod(LinkMovementMethod.getInstance());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
