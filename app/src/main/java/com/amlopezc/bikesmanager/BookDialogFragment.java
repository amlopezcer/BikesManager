package com.amlopezc.bikesmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.amlopezc.bikesmanager.entity.BikeUser;

/**
 * Dialog to select the booking you want to do: slots or bikes
 */
public class BookDialogFragment extends DialogFragment {

    //For the caller activity
    public static final String CLASS_ID = "BookDialogFragment";

    private CheckBox mCheckBox_bike;
    private CheckBox mCheckBox_slots;
    private BikeUser mBikeUser;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater and the view from it
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_book, null);

        mCheckBox_bike = (CheckBox) view.findViewById(R.id.checkbox_bookBike);
        mCheckBox_slots = (CheckBox) view.findViewById(R.id.checkbox_bookSlots);
        mBikeUser = BikeUser.getInstance();

        setCheckBoxAvailability();

        builder.setMessage(i18n(R.string.builder_book_msg)).
                setView(view).
                setPositiveButton(i18n(R.string.text_book),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                boolean isBikeBooked = mCheckBox_bike.isChecked();
                                boolean isSlotsBooked =  mCheckBox_slots.isChecked();
                                ((MapsActivity) getActivity()).doPositiveClickBookDialog(isBikeBooked, isSlotsBooked);
                                dialog.cancel();
                            }
                        }).
                setNegativeButton(i18n(R.string.text_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false); //Disabled by default

        //Listeners to enable/disable positive button
        mCheckBox_bike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).
                        setEnabled(mCheckBox_bike.isChecked() || mCheckBox_slots.isChecked());
            }
        });
        mCheckBox_slots.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).
                        setEnabled(mCheckBox_bike.isChecked() || mCheckBox_slots.isChecked());
            }
        });

        return dialog;
    }

    //Enable or disable checkboxes depending on user current booking status
    private void setCheckBoxAvailability() {
        mCheckBox_bike.setEnabled(!(mBikeUser.ismBikeTaken() || mBikeUser.ismBookTaken())); //this "book" refers to bikes
        mCheckBox_slots.setEnabled(!mBikeUser.ismSlotsTaken()); //This filed refers only to a slots booking, you cannot take slots
    }

    //Internationalization method
    private String i18n(int resourceId, Object ... formatArgs) {
        return getResources().getString(resourceId, formatArgs);
    }

}