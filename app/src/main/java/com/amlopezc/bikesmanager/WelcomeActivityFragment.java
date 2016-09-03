package com.amlopezc.bikesmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Something nice to salute a new user
 */
public class WelcomeActivityFragment extends Fragment implements View.OnClickListener {

    public static final float NEW_USER_PRESENT = 5.00f; //Some money for the new user (programatically assigned while the user creation, see constructor in BikeUser.java)

    public WelcomeActivityFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_welcome, container, false);
        initComponentsUI(view);

        return view;
    }

    private void initComponentsUI(View view) {
        Button buttonGreat = (Button) view.findViewById(R.id.button_great);
        assert buttonGreat != null;
        buttonGreat.setOnClickListener(this);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.file_user_preferences), Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(getString(R.string.text_user_name), "");
        TextView textViewWelcome = (TextView) view.findViewById(R.id.textView_welcome);
        assert textViewWelcome != null;
        textViewWelcome.setText(i18n(R.string.textView_welcome, username));

        TextView textViewMoney = (TextView) view.findViewById(R.id.textView_welcomeMoney);
        assert textViewMoney != null;
        textViewMoney.setText(i18n(R.string.text_format_money, NEW_USER_PRESENT));
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()) {
            case R.id.button_great:
                intent = new Intent(getActivity(), MapsActivity.class);
                startActivity(intent);
                break;
        }
    }

    //Internationalization method
    private String i18n(int resourceId, Object ... formatArgs) {
        return getResources().getString(resourceId, formatArgs);
    }
}
