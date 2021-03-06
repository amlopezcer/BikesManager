package com.amlopezc.bikesmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ScrollView;

import com.amlopezc.bikesmanager.entity.BikeUser;
import com.amlopezc.bikesmanager.net.HttpConstants;
import com.amlopezc.bikesmanager.net.HttpDispatcher;
import com.amlopezc.bikesmanager.util.AsyncTaskListener;
import com.amlopezc.bikesmanager.util.DeviceUtilities;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * To register new users
 */
public class SignUpActivityFragment extends Fragment implements AsyncTaskListener<String> {

    private EditText mEditTextFullName, mEditTextEmail, mEditTextUsername, mEditTextPassword;
    private TextInputLayout mInputLayoutFullName, mInputLayoutEmail, mInputLayoutUsername, mInputLayoutPassword;


    public SignUpActivityFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Listen to the Action Bar events
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        initComponentsUI(view);

        return view;
    }

    private void initComponentsUI(View view) {
        mEditTextFullName = (EditText) view.findViewById(R.id.editText_fullName);
        mEditTextFullName.addTextChangedListener(new MyTextWatcher(mEditTextFullName));
        mEditTextEmail = (EditText) view.findViewById(R.id.editText_mail);
        mEditTextEmail.addTextChangedListener(new MyTextWatcher(mEditTextEmail));
        mEditTextUsername = (EditText) view.findViewById(R.id.editText_sign_username);
        mEditTextUsername.addTextChangedListener(new MyTextWatcher(mEditTextUsername));
        mEditTextPassword = (EditText) view.findViewById(R.id.editText_password);
        mEditTextPassword.addTextChangedListener(new MyTextWatcher(mEditTextPassword));

        mInputLayoutFullName = (TextInputLayout) view.findViewById(R.id.inputLayout_fullName);
        mInputLayoutEmail = (TextInputLayout) view.findViewById(R.id.inputLayout_mail);
        mInputLayoutUsername = (TextInputLayout) view.findViewById(R.id.inputLayout_sign_username);
        mInputLayoutPassword = (TextInputLayout) view.findViewById(R.id.inputLayout_password);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_signup, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks
        int id = item.getItemId();

        if (id == R.id.action_tick_signup) {
            DeviceUtilities.hideSoftKeyboard(getActivity()); //Hides keyboard
            scrollToTheTop(); //Scrolls to the top of the screen, useful for smaller ones
            submit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void scrollToTheTop() {
        ScrollView scrollView = (ScrollView) getActivity().findViewById(R.id.scrollView_signUp); //The scroll view is located in the activity
        scrollView.smoothScrollTo(0,0);
    }

    private void submit() {
        //Some checks over the text fields
        if(!validateInput(mEditTextFullName, mInputLayoutFullName))
            return;
        if(!validateInput(mEditTextEmail, mInputLayoutEmail))
            return;
        if(!validateInput(mEditTextUsername, mInputLayoutUsername))
            return;
        if(!validateInput(mEditTextPassword, mInputLayoutPassword))
            return;

        //everything goes fine so far
        registerUser();
    }

    //Validate text fields data (not empty, adequate format for the email...)
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

    //Adapt the error message to the field in trouble
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
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    //Complete the singleton instance and the POST with the server
    private void registerUser() {
        String userName = mEditTextUsername.getText().toString().trim();
        String password = mEditTextPassword.getText().toString().trim();
        String passwordSHA1 = new String(Hex.encodeHex(DigestUtils.sha1(password)));
        String fullName = mEditTextFullName.getText().toString().trim();
        String email = mEditTextEmail.getText().toString().trim().toLowerCase(); //To lower case to accomplish email patterns

        BikeUser bikeUser = BikeUser.getInstance();
        bikeUser.setNewUserData(userName, passwordSHA1, fullName, email);

        //Save data consistently
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.file_user_preferences), Context.MODE_PRIVATE);
        sharedPreferences.edit().
                putString(getString(R.string.text_user_name), userName).
                putString(getString(R.string.text_password),  passwordSHA1).
                apply();

        HttpDispatcher httpDispatcher = new HttpDispatcher(getActivity(), HttpConstants.ENTITY_USER);
        httpDispatcher.doPost(this, bikeUser);
    }

    @Override
    public void processServerResult(String result, int operation) {
        switch (operation) {
            case HttpConstants.OPERATION_POST:
                if (result.contains(HttpConstants.SERVER_RESPONSE_OK)) {
                    Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                    startActivity(intent);
                } else if (result.contains(HttpConstants.SERVER_RESPONSE_KO)) {
                    //User cannot be created, undo changes and notify
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.file_user_preferences), Context.MODE_PRIVATE);
                    sharedPreferences.edit().
                            putString(getString(R.string.text_user_name), "").
                            putString(getString(R.string.text_password), "").
                            apply();

                    showBasicErrorDialog(i18n(R.string.toast_user_not_available), i18n(R.string.text_ok));

                } else
                    showBasicErrorDialog(i18n(R.string.text_sync_error), i18n(R.string.text_ok));
        }
    }

    // Show a basic error dialog with a custom message
    private void showBasicErrorDialog(String message, String positiveButtonText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(i18n(R.string.text_error)).
                setIcon(R.drawable.ic_error_outline).
                setMessage(message).
                setPositiveButton(
                        positiveButtonText,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
                    validateInput(mEditTextFullName, mInputLayoutFullName);
                    break;
                case R.id.editText_mail:
                    validateInput(mEditTextEmail, mInputLayoutEmail);
                    break;
                case R.id.editText_sign_username:
                    validateInput(mEditTextUsername, mInputLayoutUsername);
                    break;
                case R.id.editText_password:
                    validateInput(mEditTextPassword, mInputLayoutPassword);
                    break;
            }
        }
    }

}
