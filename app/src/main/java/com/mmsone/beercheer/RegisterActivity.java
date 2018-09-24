package  com.mmsone.beercheer;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mCreateBtn;


    private Toolbar mToolbar;

    private DatabaseReference mDatabase;

    //ProgressDialog
    private ProgressDialog mRegProgress;

    //Firebase Auth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mRegProgress = new ProgressDialog(this);




        mAuth = FirebaseAuth.getInstance();


        mDisplayName = (TextInputLayout) findViewById(R.id.regDispName);
        mEmail = (TextInputLayout) findViewById(R.id.regEmail);
        mPassword = (TextInputLayout) findViewById(R.id.logPassword);
        mCreateBtn = (Button) findViewById(R.id.regCreateButton);


        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String display_name = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                if(!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){

                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Please wait while we create your account !");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();

                    register_user(display_name, email, password);

                }



            }
        });


    }

    private void register_user(final String display_name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {


                FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = current_user.getUid();


//                    String device_token = FirebaseInstanceId.getInstance().getToken();

                HashMap<String, String> user = new HashMap<>();
                user.put("name", display_name);
                user.put("status", "Ki kell törölni");
                user.put("uid", mAuth.getCurrentUser().getUid());
                FirebaseFirestore db = FirebaseFirestore.getInstance();


                db.collection("friends")
                        .add(user)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(getApplicationContext(), "Added", Toast.LENGTH_LONG).show();
                            Intent mainIntent = new Intent( RegisterActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getApplicationContext(), "Failed to add", Toast.LENGTH_LONG).show();
                            Log.e("ERRORMESSAGE",e.getMessage());

                });

//

            }
            else
            {
                Log.e("ERRORMESSAGE",task.getException().getLocalizedMessage());
            }
        });
    }
}