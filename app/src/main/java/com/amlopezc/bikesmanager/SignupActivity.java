package com.amlopezc.bikesmanager;


import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * Registers new users
 */
public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextFullName, editTextEmail, editTextUsername, editTextPassword;
    private TextInputLayout inputLayoutFullName, inputLayoutEmail, inputLayoutUsername, inputLayoutPassword;
    private Button buttonConfirm;

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

        buttonConfirm = (Button)findViewById(R.id.button_signUp_confirm);
        buttonConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_signUp_confirm:
                submit();
                break;
        }
    }

    private void submit() {
        if(!validateInput(editTextFullName, inputLayoutFullName))
            return;
        if(!validateInput(editTextEmail, inputLayoutEmail))
            return;
        if(!validateInput(editTextUsername, inputLayoutUsername))
            return;
        if(!validateInput(editTextPassword, inputLayoutPassword))
            return;

        Toast.makeText(this,
                "Done",
                Toast.LENGTH_SHORT).show();
    }

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
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    //Internationalization method
    private String i18n(int resourceId, Object ... formatArgs) {
        return getResources().getString(resourceId, formatArgs);
    }

    //Private class to control changes in TextFields
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
