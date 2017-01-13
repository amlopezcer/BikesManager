package com.amlopezc.bikesmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import com.amlopezc.bikesmanager.entity.BikeUser;
import com.amlopezc.bikesmanager.net.HttpConstants;
import com.amlopezc.bikesmanager.net.HttpDispatcher;
import com.amlopezc.bikesmanager.util.AsyncTaskListener;
import com.amlopezc.bikesmanager.util.DeviceUtilities;


/**
 * To allow the user to modify some of his basic data
 */
public class EditProfileActivityFragment extends Fragment implements AsyncTaskListener<String> {

    private EditText mEditTextFullName, mEditTextEmail, mEditTextUsername;
    private TextInputLayout mInputLayoutFullName, mInputLayoutEmail, mInputLayoutUsername;
    private BikeUser mBikeUser;
    private String mLastUsername, mLastFullName, mLastEmail;


    public EditProfileActivityFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Listen to the Action Bar events
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        //Get the user and init components
        mBikeUser = BikeUser.getInstance();
        initComponentsUI(view);
        fillLayoutUserData();

        return view;
    }

    //Init basic UI components
    private void initComponentsUI(View view) {
        mEditTextFullName = (EditText) view.findViewById(R.id.editTextProfile_fullName);
        mEditTextFullName.addTextChangedListener(new MyTextWatcher(mEditTextFullName));
        mEditTextEmail = (EditText) view.findViewById(R.id.editTextProfile_mail);
        mEditTextEmail.addTextChangedListener(new MyTextWatcher(mEditTextEmail));
        mEditTextUsername = (EditText) view.findViewById(R.id.editTextProfile_username);
        mEditTextUsername.addTextChangedListener(new MyTextWatcher(mEditTextUsername));

        mInputLayoutFullName = (TextInputLayout) view.findViewById(R.id.inputLayoutProfile_fullName);
        mInputLayoutEmail = (TextInputLayout) view.findViewById(R.id.inputLayoutProfile_mail);
        mInputLayoutUsername = (TextInputLayout) view.findViewById(R.id.inputLayoutProfile_username);
    }

    private void fillLayoutUserData() {
        mEditTextFullName.setText(mBikeUser.getmFullName());
        mEditTextUsername.setText(mBikeUser.getmUserName());
        mEditTextEmail.setText(mBikeUser.getmEmail());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit_profile, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks
        int id = item.getItemId();

        if (id == R.id.action_tick_confirm_changes) {
            DeviceUtilities.hideSoftKeyboard(getActivity()); //Hides keyboard
            scrollToTheTop(); //Scrolls to the top of the screen, useful for smaller ones
            submit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void scrollToTheTop() {
        ScrollView scrollView = (ScrollView) getActivity().findViewById(R.id.scrollView_editProfile); //The scroll view is located in the activity
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

        //everything goes fine so far
        confirmProfileChanges();
    }

    //Validate text fields data (not empty, adequate format for the email...)
    private boolean validateInput(EditText editText, TextInputLayout inputLayout) {
        String editTextString = editText.getText().toString().trim();
        boolean badString = editTextString.isEmpty();
        if(editText.getId() == R.id.editTextProfile_mail)
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

    //Dialog to confirm changes
    private void confirmProfileChanges() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(i18n(R.string.dialog_confirm_changes)).
                setPositiveButton(
                        i18n(R.string.text_save),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                updateUser();
                                dialog.cancel();
                            }
                        }).
                setNegativeButton(
                        i18n(R.string.text_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    //Adapt the error message to the field in trouble
    private String getErrorMsg(int id) {
        String errorMsg;
        switch(id) {
            case R.id.editTextProfile_fullName: errorMsg = i18n(R.string.text_err_name); break;
            case R.id.editTextProfile_mail: errorMsg = i18n(R.string.text_err_mail); break;
            case R.id.editTextProfile_username: errorMsg = i18n(R.string.text_err_username); break;
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

    //Update the singleton instance and the server info
    private void updateUser() {
        backupBasicUserData(); //save a copy of the data changed, just in case it needs to be recovered

        mBikeUser.setmUserName(mEditTextUsername.getText().toString().trim());
        mBikeUser.setmFullName(mEditTextFullName.getText().toString().trim());
        mBikeUser.setmEmail(mEditTextEmail.getText().toString().trim().toLowerCase()); //To lower case to accomplish email patterns

        //Save data consistently
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.file_user_preferences), Context.MODE_PRIVATE);
        sharedPreferences.edit().
                putString(getString(R.string.text_user_name), mBikeUser.getmUserName()).
                apply();

        HttpDispatcher httpDispatcher = new HttpDispatcher(getActivity(), HttpConstants.ENTITY_USER);
        httpDispatcher.doPut(this, mBikeUser, HttpConstants.PUT_USER_BASIC_DATA);
    }

    private void backupBasicUserData() {
        mLastUsername = mBikeUser.getmUserName();
        mLastFullName = mBikeUser.getmFullName();
        mLastEmail = mBikeUser.getmEmail();
    }

    @Override
    public void processServerResult(String result, int operation) {
        switch (operation) {
            case HttpConstants.OPERATION_PUT:
                if (result.contains(HttpConstants.SERVER_RESPONSE_OK))
                    Toast.makeText(getActivity(),
                            i18n(R.string.toast_profile_updated),
                            Toast.LENGTH_SHORT).show();
                else if (result.contains(HttpConstants.SERVER_RESPONSE_KO)) {
                    //User cannot be updated, undo changes
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.file_user_preferences), Context.MODE_PRIVATE);
                    sharedPreferences.edit().
                            putString(getString(R.string.text_user_name), mLastUsername).
                            apply();

                    restoreBasicUserData();

                    showBasicErrorDialog(i18n(R.string.toast_user_not_available), i18n(R.string.text_ok));
                } else
                    showBasicErrorDialog(i18n(R.string.text_sync_error), i18n(R.string.text_ok));
        }

        fillLayoutUserData(); //Update layout
    }

    private void restoreBasicUserData() {
        //This can be performed with a complete GET of the user (with the ID), but with only three fields this is just easier and more efficient
        mBikeUser.setmUserName(mLastUsername);
        mBikeUser.setmFullName(mLastFullName);
        mBikeUser.setmEmail(mLastEmail);
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
                case R.id.editTextProfile_fullName:
                    validateInput(mEditTextFullName, mInputLayoutFullName);
                    break;
                case R.id.editTextProfile_mail:
                    validateInput(mEditTextEmail, mInputLayoutEmail);
                    break;
                case R.id.editTextProfile_username:
                    validateInput(mEditTextUsername, mInputLayoutUsername);
                    break;
            }
        }
    }

}
