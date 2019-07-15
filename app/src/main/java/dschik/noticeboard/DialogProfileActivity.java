package dschik.noticeboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DialogProfileActivity extends DialogFragment {

    private static final String[] dept = {"CSE", "ECE", "IT", "AEIE", "BT", "EE", "ChE", "ME", "CE"};
    private static final String[] year = {"1", "2", "3", "4"};
    private static final String[] utype = {"Student", "Faculty"};
    private String deptStr;
    private String typeStr;
    private String yrStr;
    private int flag0 = 0;
    private int flag1 = 0;
    private int flag2 = 0;
    private boolean superFlag = false;
    private DialogListerner listerner;
    SharedPreferences sh;
    private String secret_db_key = "";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listerner = (DialogListerner) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement dialog listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(!superFlag)
            listerner.error("Some error/ Faculty Authentication Failed. Please Sign in again");
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbref = db.getReference();
         dbref.child("faculty_auth").addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                secret_db_key = dataSnapshot.getValue(String.class);
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_profile, null);
        sh = getContext().getSharedPreferences("shared",Context.MODE_PRIVATE);
        deptStr = "dept";
        yrStr = "year";
        typeStr = sh.getString("utype","type");

        final Spinner deptSpin = view.findViewById(R.id.dept);
        final Spinner yearSpin = view.findViewById(R.id.yr);
        final Spinner user_type = view.findViewById(R.id.user_type_spin);
        final TextInputEditText s_key = view.findViewById(R.id.secret_key);
        final TextInputLayout s_lay = view.findViewById(R.id.faculty_pass);
        final TextView dialog_warning = view.findViewById(R.id.profile_diaplog_warning);
        s_lay.setVisibility(View.GONE);
        s_key.setVisibility(View.GONE);


        if(getContext() instanceof ProfileActivity)
        {
            dialog_warning.setVisibility(View.GONE);
            user_type.setVisibility(View.GONE);
            flag2 = 1;
        }
        ArrayAdapter<CharSequence> adapter_utype = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.userType,
                R.layout.spinner_custom_dialog);

        adapter_utype.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


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
        user_type.setPrompt("Select user type");
        deptSpin.setAdapter(adapterdept);
        yearSpin.setAdapter(adapteryr);
        user_type.setAdapter(adapter_utype);

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

        user_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    typeStr = utype[position - 1];
                    flag2 = 1;
                    if (typeStr.equals("Faculty")) {
                        yearSpin.setVisibility(View.GONE);
                        s_key.setVisibility(View.VISIBLE);
                        s_lay.setVisibility(View.VISIBLE);
                        flag1 = 1;
                    } else {
                        yearSpin.setVisibility(View.VISIBLE);
                        s_key.setVisibility(View.GONE);
                        s_lay.setVisibility(View.GONE);

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                flag2 = 0;
            }
        });


        return getDialog(view,s_key);

    }

    private Dialog getDialog(View view, final TextInputEditText s_key )
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setCancelable(false);
        alert.setView(view).setTitle("Personal Information")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(typeStr.equals("Faculty"))
                        {
                            String secret_key ="";
                            if(s_key.getText() == null)
                            {
                                s_key.setError("Invalid key");
                            }else {
                                secret_key = s_key.getText().toString();
                            }
                            if (flag0 == 1 && flag1 == 1 && flag2 == 1) {
                                if(!secret_key.equals(secret_db_key))
                                {
                                    listerner.error("Some error/ Faculty Authentication Failed. Please Sign in again");
                                    dialog.cancel();
                                } else {
                                    listerner.applyData(deptStr, yrStr, typeStr);
                                    superFlag = true;
                                }
                            } else {
                                Toast.makeText(getActivity(), "Enter Proper Department and Secret Key", Toast.LENGTH_LONG).show();
                            }
                        } else if(typeStr.equals("Student")) {

                            if (flag0 == 1 && flag1 == 1 && flag2 == 1) {
                                listerner.applyData(deptStr, yrStr, typeStr);
                                superFlag = true;
                            } else {
                                Toast.makeText(getActivity(), "Enter Proper Department and Year", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listerner.error("Some error/ Faculty Authentication Failed. Please Sign in again");
                dialog.cancel();
            }
        });
        Dialog dialog = alert.create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }



    public interface DialogListerner {

        public void applyData(String department, String Year, String userType);
        public void error(String error);
    }
}



