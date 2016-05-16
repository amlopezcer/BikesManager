package com.amlopezc.bikesmanager;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amlopezc.bikesmanager.entity.BikeUser;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.file_user_preferences), Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(getString(R.string.text_user_name), "");
        setTitle(username);

        Button buttonDeposit = (Button)findViewById(R.id.button_deposit_money);
        buttonDeposit.setOnClickListener(this);
        Button buttonEdit = (Button)findViewById(R.id.button_edit_profile);
        buttonEdit.setOnClickListener(this);
        Button buttonDeleteAccount = (Button)findViewById(R.id.button_delete_account);
        buttonDeleteAccount.setOnClickListener(this);

        initData();
    }

    private void initData() {
        BikeUser bikeUser = BikeUser.getInstance();

        TextView textView;

        textView = (TextView) findViewById(R.id.textView_address_bike);
        if(bikeUser.ismBookTaken()) {
            textView.setText(bikeUser.getmBookAddress());
            textView = (TextView) findViewById(R.id.textView_date_bike);
            textView.setText(bikeUser.getmBookDate());
        } else
            textView.setText("No tienes ninguna bicicleta reservada");

        textView = (TextView) findViewById(R.id.textView_address_moorings);
        if(bikeUser.ismMooringsTaken()) {
            textView.setText(bikeUser.getmMooringsAddress());
            textView = (TextView) findViewById(R.id.textView_date_moorings);
            textView.setText(bikeUser.getmMooringsDate());
        } else
            textView.setText("No tienes ningún anclaje reservado");

        textView = (TextView) findViewById(R.id.textView_profile_balance);
        textView.setText(i18n(R.string.text_format_money, bikeUser.getmBalance()));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_deposit_money:
                Toast.makeText(this,
                        "deposit",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_edit_profile:
                Toast.makeText(this,
                        "edit",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_delete_account:
                Toast.makeText(this,
                        "delete",
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }

    //Internationalization method
    private String i18n(int resourceId, Object ... formatArgs) {
        return getResources().getString(resourceId, formatArgs);
    }

}
