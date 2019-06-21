package dschik.noticeboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogProfileActivity extends DialogFragment {

    private static final String[] dept = {"CSE", "ECE", "IT", "AEIE", "BT", "EE", "ChE", "ME", "CE"};
    private static final String[] year = {"1", "2", "3", "4"};
    private String deptStr;
    private String yrStr;
    private int flag0 = 0;
    private int flag1 = 0;
    private DialogListerner listerner;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listerner = (DialogListerner) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement dialog listener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_profile, null);

        Spinner deptSpin = view.findViewById(R.id.dept);
        Spinner yearSpin = view.findViewById(R.id.yr);


        ArrayAdapter<CharSequence> adapterdept = ArrayAdapter
                .createFromResource(getActivity()
                        , R.array.deptName, R.layout.spinner_custom_dialog);

        adapterdept.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapteryr = ArrayAdapter
                .createFromResource(getActivity(),
                        R.array.yearName, R.layout.spinner_custom_dialog);

        adapteryr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        deptSpin.setPrompt("Select your Department");
        yearSpin.setPrompt("Select your Year");
        deptSpin.setAdapter(adapterdept);
        yearSpin.setAdapter(adapteryr);

        deptSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    deptStr = dept[position - 1];
                    flag0 = 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                flag0 = 0;
            }
        });

        yearSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    yrStr = year[position - 1];
                    flag1 = 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                flag1 = 0;
            }
        });


        alert.setView(view).setTitle("Personal Information")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (flag0 == 1 && flag1 == 1) {
                            listerner.applyData(deptStr, yrStr);
                        } else {
                            Toast.makeText(getActivity(), "Enter Proper Department and Year", Toast.LENGTH_LONG).show();
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return alert.create();

    }


    public interface DialogListerner {

        public void applyData(String department, String Year);
    }
}



