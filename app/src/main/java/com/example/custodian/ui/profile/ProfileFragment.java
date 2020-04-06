package com.example.custodian.ui.profile;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.custodian.R;

import java.util.Calendar;

public class ProfileFragment extends Fragment {

    Button btnSave;
    EditText profile_txtFirstName, profile_txtLastName, profile_txtMail, profile_txtBirthday, profile_txtAddress;
    User user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profile_txtFirstName = view.findViewById(R.id.profile_txtFirstName);
        profile_txtLastName = view.findViewById(R.id.profile_txtLastName);
        profile_txtMail = view.findViewById(R.id.profile_txtMail);
        profile_txtAddress = view.findViewById(R.id.profile_txtAddress);
        profile_txtBirthday = view.findViewById(R.id.profile_txtBirthday);
        btnSave = (Button) view.findViewById(R.id.profile_btnSave);
        TextWatcher tw = new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                        clean = String.format("%02d%02d%02d", day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    profile_txtAddress.setText(current);
                    profile_txtAddress.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        profile_txtAddress.addTextChangedListener(tw);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String fname = profile_txtFirstName.getText().toString();
                String lname = profile_txtLastName.getText().toString();
                String mail = profile_txtMail.getText().toString();
                String address = profile_txtAddress.getText().toString();
                String birthday = profile_txtBirthday.getText().toString();
                user = new User(fname, lname, mail, address, birthday);
                System.out.println(user.toString());
            }
        });
    }

}
