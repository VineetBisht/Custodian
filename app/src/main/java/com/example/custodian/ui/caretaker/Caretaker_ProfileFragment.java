package com.example.custodian.ui.caretaker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.custodian.R;
import com.example.custodian.model.caretaker.Caretaker;
import com.example.custodian.model.caretaker.CaretakerManager;
import com.example.custodian.model.caretaker.Hired;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class Caretaker_ProfileFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private String hire_date;
    private Caretaker caretaker;
    FirebaseAuth mAuth;
    FirebaseUser fUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.caretaker_profile, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final TextView exp, fullname, address, email, displayN, des, dob;
        final Button hire;
        de.hdodenhof.circleimageview.CircleImageView image;
        int position = getArguments().getInt("pos");
        caretaker = CaretakerManager.getCaretakers().get(position);

        exp = view.findViewById(R.id.care_exp);
        fullname = view.findViewById(R.id.care_fullname);
        address = view.findViewById(R.id.care_address);
        email = view.findViewById(R.id.care_mail);
        image = view.findViewById(R.id.care_photo);
        displayN = view.findViewById(R.id.care_profile_fullname);
        des = view.findViewById(R.id.care_profile_bio);
        dob = view.findViewById(R.id.care_dob);
        hire = view.findViewById(R.id.care_profile_hire);

        exp.setText(exp.getHint()+" "+String.valueOf(caretaker.getExp()+" years"));
        fullname.setText(caretaker.getFullname());
        address.setText(address.getHint()+" "+caretaker.getAddress());
        email.setText(email.getHint()+" "+caretaker.getMail());
        displayN.setText(caretaker.getFullname());
        des.setText(caretaker.getDescription());
        dob.setText(dob.getHint()+" "+caretaker.getBirthday());
        Picasso.get().load(caretaker.getImage()).into(image);

        hire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                fUser = mAuth.getCurrentUser();

                if (fUser == null) {
                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                    navController.navigate(R.id.navigation_profile);
                    return;
                }

                Calendar calendar = Calendar.getInstance();

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(), Caretaker_ProfileFragment.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.i("Date Set","date");
        hire_date = year + "-" + month + "-" + dayOfMonth;
        upload();
    }

    public void upload() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("hired_" + fUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("Data Set","data");
                Hired hired = dataSnapshot.getValue(Hired.class);
                if(hired == null){
                    hired = new Hired(true);
                    hired.addCaretaker(caretaker);
                    hired.addDate_of_hire(hire_date);
                    databaseReference.setValue(hired);
                    Toast.makeText(getContext(), "Successfull", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    hired.addCaretaker(caretaker);
                    hired.addDate_of_hire(hire_date);
                    databaseReference.setValue(hired);
                    Toast.makeText(getContext(), "Successfull", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.navigation_home);
    }
}
