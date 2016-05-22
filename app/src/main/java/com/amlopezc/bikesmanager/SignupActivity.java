package com.amlopezc.bikesmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.amlopezc.bikesmanager.entity.BikeUser;
import com.amlopezc.bikesmanager.net.HttpConstants;
import com.amlopezc.bikesmanager.net.HttpDispatcher;
import com.amlopezc.bikesmanager.util.AsyncTaskListener;
import com.amlopezc.bikesmanager.util.DeviceUtilities;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Registers new users
 */
public class SignupActivity extends AppCompatActivity implements AsyncTaskListener<String> {

    private EditText editTextFullName, editTextEmail, editTextUsername, editTextPassword;
    private TextInputLayout inputLayoutFullName, inputLayoutEmail, inputLayoutUsername, inputLayoutPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextFullName = (EditText) findViewById(R.id.editText_fullName);
        editTextFullName.addTextChangedListener(new MyTextWatcher(editTextFullName));
        editTextEmail = (EditText) findViewById(R.id.editText_mail);
        editTextEmail.addTextChangedListener(new MyTextWatcher(editTextEmail));
        editTextUsername = (EditText) findViewById(R.id.editText_sign_username);
        editTextUsername.addTextChangedListener(new MyTextWatcher(editTextUsername));
        editTextPassword = (EditText) findViewById(R.id.editText_password);
        editTextPassword.addTextChangedListener(new MyTextWatcher(editTextPassword));

        inputLayoutFullName = (TextInputLayout) findViewById(R.id.inputLayout_fullName);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.inputLayout_mail);
        inputLayoutUsername = (TextInputLayout) findViewById(R.id.inputLayout_sign_username);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.inputLayout_password);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks
        int id = item.getItemId();

        if (id == R.id.action_tick_signup) {
            DeviceUtilities.hideSoftKeyboard(this); //Hides keyboard
            scrollToTheTop(); //Scrolls to the top of the screen, useful for smaller ones
            submit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void scrollToTheTop() {
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView_signUp);
        scrollView.smoothScrollTo(0,0);
    }

    private void submit() {
        //Some checks over the text fields
        if(!validateInput(editTextFullName, inputLayoutFullName))
            return;
        if(!validateInput(editTextEmail, inputLayoutEmail))
            return;
        if(!validateInput(editTextUsername, inputLayoutUsername))
            return;
        if(!validateInput(editTextPassword, inputLayoutPassword))
            return;

        //everything goes fine so far
        registerUser();
    }

    private void registerUser() {
        String userName = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String passwordSHA1 = new String(Hex.encodeHex(DigestUtils.sha1(password)));
        String fullName = editTextFullName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();

        BikeUser bikeUser = BikeUser.getInstance();
        bikeUser.setNewUserData(userName, passwordSHA1, fullName, email);

        //Save data consistently
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.file_user_preferences), Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(getString(R.string.text_user_name), userName)
                .putString(getString(R.string.text_password),  passwordSHA1)
                .apply();

        HttpDispatcher httpDispatcher = new HttpDispatcher(this, HttpConstants.ENTITY_USER);
        httpDispatcher.doPost(this, bikeUser);
    }

    //Method to validate text fields data (not empty, adequate format for the email...)
    private boolean validateInput(EditText editText, TextInputLayout inputLayout) {
        String editTextString = editText.getText().toString().trim();
        boolean badString = editTextString.isEmpty();
        if(editText.getId() == R.id.editText_mail)
            badString = editTextString.isEmpty() || !isValidEmail(editTextString);

        if (badString) {
            inputLayout.setError(getErrorMsg(editText.getId()));
            requestFocus(editText);
            return false;
        } else {
            inputLayout.setError(null);
            inputLayout.setErrorEnabled(false);
        }

        return true;
    }

    //Adapting the error message to the field in trouble
    private String getErrorMsg(int id) {
        String errorMsg;
        switch(id) {
            case R.id.editText_fullName: errorMsg = i18n(R.string.text_err_name); break;
            case R.id.editText_mail: errorMsg = i18n(R.string.text_err_mail); break;
            case R.id.editText_sign_username: errorMsg = i18n(R.string.text_err_username); break;
            case R.id.editText_password: errorMsg = i18n(R.string.text_err_password); break;
            default: errorMsg = null;
        }

        return errorMsg;
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus())
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    //Method to process the server result
    @Override
    public void processServerResult(String result, int operation) {
        switch (operation) {
            case HttpConstants.OPERATION_POST:
                //Just showing Toast for user feedback
                switch (result) {
                    case HttpConstants.SERVER_RESPONSE_OK:
                        Intent intent = new Intent(this, WelcomeActivity.class);
                        startActivity(intent);
                        break;
                    case HttpConstants.SERVER_RESPONSE_KO:
                        //Undo changes
                        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.file_user_preferences), Context.MODE_PRIVATE);
                        sharedPreferences.edit()
                                .putString(getString(R.string.text_user_name), "")
                                .putString(getString(R.string.text_password), "")
                                .apply();

                        Toast.makeText(this,
                                i18n(R.string.toast_user_signup_error),
                                Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(this,
                                i18n(R.string.toast_sync_error),
                                Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //Internationalization method
    private String i18n(int resourceId, Object ... formatArgs) {
        return getResources().getString(resourceId, formatArgs);
    }

    //Private class to control changes in text fields
    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        public void afterTextChanged(Editable editable) {
            switch(view.getId()) {
                case R.id.editText_fullName:
                    validateInput(editTextFullName, inputLayoutFullName);
                    break;
                case R.id.editText_mail:
                    validateInput(editTextEmail, inputLayoutEmail);
                    break;
                case R.id.editText_sign_username:
                    validateInput(editTextUsername, inputLayoutUsername);
                    break;
                case R.id.editText_password:
                    validateInput(editTextPassword, inputLayoutPassword);
                    break;
            }
        }
    }

}
