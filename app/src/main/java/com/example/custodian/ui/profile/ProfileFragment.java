package com.example.custodian.ui.profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.custodian.R;
import com.example.custodian.model.home.PlaceAutoSuggestAdapter;
import com.example.custodian.model.profile.User;
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

import java.io.ByteArrayOutputStream;

public class ProfileFragment extends Fragment implements View.OnKeyListener, DatePickerDialog.OnDateSetListener {

    private EditText profile_txtFirstName, profile_txtLastName, profile_txtMail, profile_txtBirthday, birthday_reg;
    private TextView profile_fullname;
    private ProgressBar progress;
    private AutoCompleteTextView profile_txtAddress;
    private FirebaseAuth mAuth;
    private FirebaseUser fUser;
    private de.hdodenhof.circleimageview.CircleImageView image;
    private final int TAKE_PICTURE = 1, PROFILE_PICTURE = 2;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private Uri dpPath;
    private User user;
    private final boolean success[] = {false};
    private String dob_reg;
    private AlertDialog dialog, dialog_Reg;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        Button btnSave, btnLogout;
        mAuth = FirebaseAuth.getInstance();
//        mAuth.signOut();
        fUser = mAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        profile_txtFirstName = view.findViewById(R.id.profile_txtFirstName);
        profile_txtLastName = view.findViewById(R.id.profile_txtLastName);
        profile_txtMail = view.findViewById(R.id.profile_txtMail);
        profile_txtAddress = view.findViewById(R.id.profile_txtAddress);
        btnLogout = view.findViewById(R.id.logout);
        profile_fullname = view.findViewById(R.id.profile_fullname);
        profile_txtBirthday = view.findViewById(R.id.profile_dob);

        btnSave = view.findViewById(R.id.profile_btnSave);
        mAuth = FirebaseAuth.getInstance();
        profile_txtAddress.setAdapter(new PlaceAutoSuggestAdapter(getContext(), android.R.layout.simple_list_item_1));
        profile_txtAddress.setOnKeyListener(this);
        profile_txtMail.setOnKeyListener(this);
        profile_txtLastName.setOnKeyListener(this);
        profile_txtFirstName.setOnKeyListener(this);
        profile_txtBirthday.setInputType(InputType.TYPE_NULL);
        profile_txtBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(), ProfileFragment.this, 2020, 1, 1);
                datePickerDialog.show();
                profile_txtBirthday.setText(dob_reg);
            }
        });

        if (fUser == null) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = getLayoutInflater();
            final View root_login = inflater.inflate(R.layout.login, null);

            alert.setView(root_login);
            alert.setCancelable(true);
            dialog = alert.create();

            final EditText username = root_login.findViewById(R.id.username_login);
            final EditText password = root_login.findViewById(R.id.password_login);
            Button register = root_login.findViewById(R.id.cancel_reg);
            final Button login = root_login.findViewById(R.id.reg_reg);

            username.setOnKeyListener(new View.OnKeyListener() {
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
            password.setOnKeyListener(this);
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = getActivity();
                    AlertDialog.Builder alert_reg = new AlertDialog.Builder(activity);
                    LayoutInflater inflater = getLayoutInflater();
                    View root_reg = inflater.inflate(R.layout.register, null);
                    alert_reg.setView(root_reg);
                    alert_reg.setCancelable(true);
                    dialog_Reg = alert_reg.create();

                    final Button reg_reg, cancel;
                    final EditText firstN, lastN, username, password, password_con, email_reg;

                    image = root_reg.findViewById(R.id.login_image);
                    birthday_reg = root_reg.findViewById(R.id.dob_reg);
                    reg_reg = root_reg.findViewById(R.id.reg_reg);
                    cancel = root_reg.findViewById(R.id.cancel_reg);
                    firstN = root_reg.findViewById(R.id.first_reg);
                    progress = root_reg.findViewById(R.id.reg_progress);
                    lastN = root_reg.findViewById(R.id.last_reg);
                    username = root_reg.findViewById(R.id.username_reg);
                    password = root_reg.findViewById(R.id.pass_reg);
                    password_con = root_reg.findViewById(R.id.pass_con_reg);
                    email_reg = root_reg.findViewById(R.id.email_reg);

                    progress.setVisibility(View.INVISIBLE);
                    final AutoCompleteTextView address_reg = root_reg.findViewById(R.id.address_reg);
                    address_reg.setAdapter(new PlaceAutoSuggestAdapter(getContext(), android.R.layout.simple_list_item_1));

                    firstN.setOnKeyListener(new View.OnKeyListener() {
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
                    lastN.setOnKeyListener(new View.OnKeyListener() {
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
                    email_reg.setOnKeyListener(new View.OnKeyListener() {
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
                    username.setOnKeyListener(new View.OnKeyListener() {
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
                    password.setOnKeyListener(new View.OnKeyListener() {
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
                    password_con.setOnKeyListener(new View.OnKeyListener() {
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
                    birthday_reg.setInputType(InputType.TYPE_NULL);
                    birthday_reg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatePickerDialog datePickerDialog = new DatePickerDialog(
                                    getContext(), ProfileFragment.this, 2020, 1, 1);
                            datePickerDialog.show();
                        }
                    });
                    image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivityForResult(takePictureIntent, TAKE_PICTURE);
                            } else {
                                Log.i("here", "Take picture intent is null");
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

                            if (password.getText().toString() == password_con.getText().toString()) {
                                Toast.makeText(getContext(), "Passwords Do Not Match" + password.getText().toString() + password_con.getText().toString(), Toast.LENGTH_LONG).show();
                                return;
                            }
                            user = new User(firstN.getText().toString(),
                                    lastN.getText().toString(),
                                    email_reg.getText().toString(),
                                    address_reg.getText().toString(),
                                    dob_reg, username.getText().toString().trim());
                            register_email(email_reg.getText().toString(), password.getText().toString());
                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog_Reg.dismiss();
                        }
                    });

                    dialog_Reg.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                    dialog_Reg.show();
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

            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.show();
        } else {
            Log.i("authenticated", "Authenticated: " + fUser.getEmail());

            image = view.findViewById(R.id.profile_photo);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, PROFILE_PICTURE);
                    }
                }
            });

            databaseReference.child("data_" + fUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    profile_txtAddress.setText(user.getAddress());
                    profile_txtBirthday.setText(user.getBirthday());
                    profile_txtFirstName.setText(user.getFirstName());
                    profile_txtLastName.setText(user.getLastName());
                    profile_txtMail.setText(user.getMail());
                    profile_fullname.setText(user.getUsername());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.i(getTag(), "Data Read Cancelled");
                }
            });

            final long ONE_MEGABYTE = 1024 * 1024;
            storageReference.child("dp" + fUser.getUid()).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    image.setImageBitmap(bmp);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
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
                        profile_txtBirthday.getText().toString(),
                        user.getUsername());
                if (image != null)
                    uploadFile(dpPath);
                databaseReference.child("data_" + fUser.getUid()).setValue(user);
                Toast.makeText(getContext(), "Successfully Updated", Toast.LENGTH_LONG);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                        navController.navigate(R.id.navigation_profile); }
                }, 3000);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.navigation_profile);
            }
        });

        profile_txtBirthday.setOnKeyListener(this);
        profile_txtFirstName.setOnKeyListener(this);
        profile_txtLastName.setOnKeyListener(this);
        profile_txtMail.setOnKeyListener(this);
        profile_txtAddress.setOnKeyListener(this);
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

    private void register_email(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("register", "createUserWithEmail:success");
                            fUser = mAuth.getCurrentUser();
                            success[0] = true;
                            onAuthStateChanged();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("register", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), task.getException().toString(),
                                    Toast.LENGTH_SHORT).show();
                            success[0] = false;
                            onAuthStateChanged();
                        }
                    }
                });
        progress.setVisibility(View.VISIBLE);
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

                            dialog.dismiss();
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
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            dpPath = getImageUri(getActivity().getApplicationContext(), imageBitmap);
            Log.i("dppath:", dpPath.getPath());
            image.setImageBitmap(imageBitmap);
        } else if (requestCode == PROFILE_PICTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            dpPath = getImageUri(getActivity().getApplicationContext(), imageBitmap);
            Log.i("prof_dppath:", dpPath.getPath());
            image.setImageBitmap(imageBitmap);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month += 1;
        dob_reg = year + "-" + month + "-" + dayOfMonth;
        if (birthday_reg != null)
            birthday_reg.setText(dob_reg);
        else
            profile_txtBirthday.setText(dob_reg);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void uploadFile(Uri dpPath) {
        StorageReference mountainsRef = storageReference.child("dp" + fUser.getUid());
        image.setDrawingCacheEnabled(true);
        image.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        Log.i("Image Upload", "Starting ");

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                Log.i("Image Upload", "Failed " + exception);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getContext(), "Image Uploaded successfully", Toast.LENGTH_SHORT).show();
                Log.i("Image Upload", "Success");
                if (dialog_Reg != null) dialog_Reg.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getContext(), "Uploading Image..", Toast.LENGTH_SHORT).show();
                Log.i("Image Upload", "In Progress");
            }
        });
    }

    public void onAuthStateChanged() {
        progress.setVisibility(View.INVISIBLE);
        if (success[0]) {
            Log.i("Authemtication:", "Auth state changed");
            if (dpPath != null) {
                uploadFile(dpPath);
            } else {
                Log.i("Image:", "No file upload");
            }
            databaseReference.child("data_" + fUser.getUid()).setValue(user);
            Log.i("Uploading data", "Successfully uploaded data");
            success[0] = false;
            dialog_Reg.dismiss();
            dialog.dismiss();
            Toast.makeText(getContext(), "Successfully Registered", Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                    navController.navigate(R.id.navigation_profile);
                    // yourMethod();
                }
            }, 2000);
        }
    }
}

