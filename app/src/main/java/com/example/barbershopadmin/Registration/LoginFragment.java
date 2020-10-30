package com.example.barbershopadmin.Registration;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.barbershopadmin.ModelClasses.CheckInternet;
import com.example.barbershopadmin.Dashboard;
import com.example.barbershopadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class   LoginFragment extends Fragment {

    EditText emailORphone, password;
    Button login_btn;
    TextView logoText, sloganText;
    RelativeLayout progressbar;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // String emailORphone
                CheckInternet checkInternet = new CheckInternet();
                if (!checkInternet.isConnected(getContext())) {
                    ShowCustomDialog().show();
                    return;
                } else {

                    emailORphone.setError(null);
                    password.setError(null);
                    if (emailORphone.getText().toString().isEmpty()) {
                        emailORphone.setError("Required");
                        return;
                    }
                    if (password.getText().toString().isEmpty()) {
                        password.setError("Required");
                        return;
                    }

                    if (VALID_EMAIL_ADDRESS_REGEX.matcher(emailORphone.getText().toString()).find()) {
                        progressbar.setVisibility(View.VISIBLE);
                        login(emailORphone.getText().toString());

                    } else if (emailORphone.getText().toString().matches("\\d{10}")) {
                        FirebaseFirestore.getInstance().collection("users").whereEqualTo("phone", emailORphone.getText().toString())
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    List<DocumentSnapshot> documentSnapshot = task.getResult().getDocuments();
                                    if (documentSnapshot.isEmpty()) {
                                        emailORphone.setError("phoneNumber not Found");
                                        progressbar.setVisibility(View.GONE);
                                        return;
                                    } else {
                                        String email = documentSnapshot.get(0).get("email").toString();
                                        login(email);
                                    }
                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                    progressbar.setVisibility(View.GONE);
                                }
                            }
                        });
                    } else {
                        emailORphone.setError("Please Enter a valid Email or PhoneNumber!");
                    }
                }
            }
        });

    }

    // Check internet connections//
    private AlertDialog.Builder ShowCustomDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("No Internet Connection");
        builder.setMessage("Please connected to the internet to proceed further");
        builder.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder;

    }

    private void init(View view) {
        //hooks
        emailORphone = view.findViewById(R.id.edt1);
        password = view.findViewById(R.id.edt2);
        logoText = view.findViewById(R.id.logo_name);
        sloganText = view.findViewById(R.id.slogan_name);
        login_btn = view.findViewById(R.id.letTheUserLogIn);
        progressbar = view.findViewById(R.id.login_progress_bar);
    }
    private void login(String email){
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email,password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent intent=new Intent(getContext(), Dashboard.class);
                    startActivity(intent);
                    getActivity().finish();

                }else {
                    String error=task.getException().getMessage();
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}