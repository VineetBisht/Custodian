package com.example.custodian.ui.profile;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.custodian.R;
import com.example.custodian.ui.home.PlaceAutoSuggestAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileFragment extends Fragment implements View.OnKeyListener, DatePickerDialog.OnDateSetListener {

    private EditText profile_txtFirstName, profile_txtLastName, profile_txtMail, profile_txtBirthday, birthday_reg;
    private AutoCompleteTextView profile_txtAddress;
    private FirebaseAuth mAuth;
    private FirebaseUser fUser;
    private ImageButton image,prof_image;
    private final int TAKE_PICTURE = 1,PROFILE_PICTURE=2;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private Uri dpPath,prof_dpPath;
    private User user;
    private String dob_reg;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        Button btnSave;
        mAuth= FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("data");
        profile_txtFirstName = view.findViewById(R.id.profile_txtFirstName);
        profile_txtLastName = view.findViewById(R.id.profile_txtLastName);
        profile_txtMail = view.findViewById(R.id.profile_txtMail);
        profile_txtAddress = view.findViewById(R.id.profile_txtAddress);
        profile_txtBirthday = view.findViewById(R.id.profile_dob);
        prof_image = view.findViewById(R.id.profile_photo);
        btnSave = view.findViewById(R.id.profile_btnSave);
        mAuth = FirebaseAuth.getInstance();

        profile_txtAddress.setAdapter(new PlaceAutoSuggestAdapter(getContext(), android.R.layout.simple_list_item_1));
        profile_txtAddress.setOnKeyListener(this);
        profile_txtMail.setOnKeyListener(this);
        profile_txtLastName.setOnKeyListener(this);
        profile_txtFirstName.setOnKeyListener(this);
        profile_txtBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(), ProfileFragment.this, 1900, 1, 1);
                datePickerDialog.show();
                profile_txtBirthday.setText(dob_reg);
            }
        });
        prof_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureIntent.setType("image/*");
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, PROFILE_PICTURE);
                }
            }
        });

        if (fUser==null) {
            final Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.login);

            final EditText username = dialog.findViewById(R.id.first_reg);
            final EditText password = dialog.findViewById(R.id.last_reg);

            username.setOnKeyListener(this);
            password.setOnKeyListener(this);

            Button register = dialog.findViewById(R.id.cancel_reg);
            final Button login = dialog.findViewById(R.id.reg_reg);

            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialogReg = new Dialog(getContext());
                    image = dialogReg.findViewById(R.id.login_image);
                    final Button reg_reg, cancel;
                    final EditText firstN, lastN, username, password, password_con, email_reg;
                    birthday_reg = dialogReg.findViewById(R.id.dob_reg);
                    reg_reg = dialogReg.findViewById(R.id.reg_reg);
                    cancel = dialogReg.findViewById(R.id.cancel_reg);
                    firstN = dialogReg.findViewById(R.id.first_reg);
                    lastN = dialogReg.findViewById(R.id.last_reg);
                    username = dialogReg.findViewById(R.id.username_reg);
                    password = dialogReg.findViewById(R.id.pass_reg);
                    password_con = dialogReg.findViewById(R.id.pass_con_reg);
                    email_reg = dialogReg.findViewById(R.id.email_reg);
                    ProfileFragment pf=new ProfileFragment();

                    final AutoCompleteTextView address_reg = dialogReg.findViewById(R.id.address_reg);
                    address_reg.setAdapter(new PlaceAutoSuggestAdapter(getContext(), android.R.layout.simple_list_item_1));

                    firstN.setOnKeyListener(pf);
                    lastN.setOnKeyListener(pf);
                    email_reg.setOnKeyListener(pf);
                    username.setOnKeyListener(pf);
                    password.setOnKeyListener(pf);
                    password_con.setOnKeyListener(pf);
                    address_reg.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                                InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                in.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                            }
                            return false;
                        }
                    });
                    birthday_reg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            in.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                            DatePickerDialog datePickerDialog = new DatePickerDialog(
                                    getContext(), ProfileFragment.this, 1900, 1, 1);
                            datePickerDialog.show();
                            birthday_reg.setText(dob_reg);
                        }
                    });
                    image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            takePictureIntent.setType("image/*");
                            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivityForResult(takePictureIntent, TAKE_PICTURE);
                            }
                        }
                    });
                    reg_reg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (TextUtils.isEmpty(username.getText().toString()) || TextUtils.isEmpty(email_reg.getText().toString())
                                    || TextUtils.isEmpty(password.getText().toString()) || TextUtils.isEmpty(password_con.getText().toString())
                                    || TextUtils.isEmpty(firstN.getText().toString()) || TextUtils.isEmpty(lastN.getText().toString())) {
                                Toast.makeText(getContext(), "All Fields Required", Toast.LENGTH_LONG).show();
                                return;
                            }

                            if (password.getText().toString() != password_con.getText().toString()) {
                                Toast.makeText(getContext(), "Passwords Do Not Match", Toast.LENGTH_LONG).show();
                                return;
                            }
                            register(email_reg.getText().toString(), password.getText().toString());
                            if (dpPath != null) {
                                uploadFile(dpPath);
                            } else {
                                Log.i(getTag(), "No file upload");
                            }
                            User user = new User(firstN.getText().toString(),
                                    lastN.getText().toString(),
                                    email_reg.getText().toString(),
                                    address_reg.getText().toString(),
                                    birthday_reg.getText().toString());
                            databaseReference.setValue(user);
                            Log.i(getTag(), "Successfully uploaded data");
                            dialogReg.dismiss();
                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogReg.dismiss();
                        }
                    });
                }
            });

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(username.getText().toString()) || TextUtils.isEmpty(password.getText().toString())) {
                        username.setError("Required");
                        password.setError("Required");
                        return;
                    } else {
                        username.setError(null);
                        password.setError(null);
                    }
                    signIn(username.getText().toString(), password.getText().toString());
                }
            });

        } else {
            Log.i(getTag(), "Authenticated: " + fUser.getEmail());
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    user = dataSnapshot.getValue(User.class);
                    profile_txtAddress.setText(user.getAddress());
                    profile_txtBirthday.setText(user.getBirthday());
                    profile_txtFirstName.setText(user.getFirstName());
                    profile_txtLastName.setText(user.getLastName());
                    profile_txtMail.setText(user.getMail());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.i(getTag(), "Data Read Cancelled");
                }
            });
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (TextUtils.isEmpty(profile_txtAddress.getText().toString()) || TextUtils.isEmpty(profile_txtBirthday.getText().toString())
                        || TextUtils.isEmpty(profile_txtFirstName.getText().toString()) || TextUtils.isEmpty(profile_txtLastName.getText().toString())
                        || TextUtils.isEmpty(profile_txtMail.getText().toString())) {
                    Toast.makeText(getContext(), "All Fields Required!", Toast.LENGTH_LONG).show();
                    return;
                }
                user = new User(profile_txtFirstName.getText().toString(),
                        profile_txtLastName.getText().toString(),
                        profile_txtMail.getText().toString(),
                        profile_txtAddress.getText().toString(),
                        profile_txtBirthday.getText().toString());
                if(prof_image!=null)
                uploadFile(prof_dpPath);
                databaseReference.setValue(user);
                Toast.makeText(getContext(),"Successfully Updated",Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {
            InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void register(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(getTag(), "createUserWithEmail:success");
                            fUser = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(getTag(), "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(getTag(), "signInWithEmail:success");
                            fUser = mAuth.getCurrentUser();

                            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                            navController.navigate(R.id.navigation_profile);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(getTag(), "signInWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //Result for the camera intent
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK) {
            dpPath = data.getData();
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            image.setImageBitmap(imageBitmap);
        }else if (requestCode == PROFILE_PICTURE && resultCode == Activity.RESULT_OK) {
            prof_dpPath = data.getData();
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            image.setImageBitmap(imageBitmap);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
      dob_reg = year + "-" + month + "-" + dayOfMonth;
    }

    private void uploadFile(Uri dpPath) {
        // Code for showing progressDialog while uploading
        final ProgressDialog progressDialog
                = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        // Defining the child of storageReference
        StorageReference ref
                = storageReference
                .child(
                        "images/dp");

        // adding listeners on upload
        // or failure of image
        ref.putFile(dpPath)
                .addOnSuccessListener(
                        new OnSuccessListener<UploadTask.TaskSnapshot>() {

                            @Override
                            public void onSuccess(
                                    UploadTask.TaskSnapshot taskSnapshot) {

                                // Image uploaded successfully
                                // Dismiss dialog
                                progressDialog.dismiss();
                                Toast
                                        .makeText(getContext(),
                                                "Image Uploaded!!",
                                                Toast.LENGTH_SHORT)
                                        .show();
                            }
                        })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        // Error, Image not uploaded
                        progressDialog.dismiss();
                        Toast
                                .makeText(getContext(),
                                        "Failed " + e.getMessage(),
                                        Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .addOnProgressListener(
                        new OnProgressListener<UploadTask.TaskSnapshot>() {

                            // Progress Listener for loading
                            // percentage on the dialog box
                            @Override
                            public void onProgress(
                                    UploadTask.TaskSnapshot taskSnapshot) {
                                double progress
                                        = (100.0
                                        * taskSnapshot.getBytesTransferred()
                                        / taskSnapshot.getTotalByteCount());
                                progressDialog.setMessage(
                                        "Uploaded "
                                                + (int) progress + "%");
                            }
                        });
    }
}

