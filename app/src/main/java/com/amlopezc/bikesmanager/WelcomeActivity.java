package com.amlopezc.bikesmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Something nice to salute a new user
 */
public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    public static final float NEW_USER_PRESENT = 5.00f; //Some money for the new user (programatically assigned while the user creation, see constructor in BikeUser.java)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button buttonGreat = (Button) findViewById(R.id.button_great);
        buttonGreat.setOnClickListener(this);

        TextView textViewWelcome = (TextView) findViewById(R.id.textView_welcome);
        textViewWelcome.setText(i18n(R.string.textView_welcome, "USER")); //TODO: Sacarlo del preferences

        TextView textViewMoney = (TextView) findViewById(R.id.textView_welcomeMoney);
        textViewMoney.setText(i18n(R.string.textView_welcomeMoney, NEW_USER_PRESENT));
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()) {
            case R.id.button_great:
                intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                break;
        }
    }

    //Internationalization method
    private String i18n(int resourceId, Object ... formatArgs) {
        return getResources().getString(resourceId, formatArgs);
    }
}