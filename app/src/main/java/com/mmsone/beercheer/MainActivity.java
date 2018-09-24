package com.mmsone.beercheer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.jboss.aerogear.unifiedpush.DefaultPushSender;
import org.jboss.aerogear.unifiedpush.PushSender;
import org.jboss.aerogear.unifiedpush.message.UnifiedMessage;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {


    private DatabaseReference ref;
    private List<User> userList = new ArrayList<User>();
    private RecyclerView recyclerView;
    FirebaseFirestore db;
    private final PushSender sender =
            DefaultPushSender.withRootServerURL("http://192.168.1.31:8080/ag-push/")
                    .pushApplicationId("c1401743-9f90-4773-9c05-c984e3573de3")
                    .masterSecret("38395136-aa5e-4e13-94e0-a658b42c5973")
                    .build();

    private DatabaseReference mNotificationDatabase;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private List<User> selectedUsers = new ArrayList<>();


    private Toolbar mToolbar;
    private Button mainMenu;
    private Button sendButton;
    private Button selectButton;
    private Callback mCallback;

    FirebaseAuth mAuth;

    //pushnotihoz

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);



        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
//
//        mToolbar = findViewById(R.id.mainBar);
        getSupportActionBar().setTitle("BeerApp");


        mainMenu = findViewById(R.id.main_logout);
        sendButton = findViewById(R.id.sendButton);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        ((BeerChatApplication)getApplication()).registerPush(currentUser.getUid());

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("push")){
                    String message = intent.getStringExtra("message");
                }
            }
        };



        if( currentUser == null){
            Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
            startActivity(startIntent);
            finish();
        }

    }


    @Override
    public void onStart() {
        super.onStart();
//         Check if user is signed in (non-null) and update UI accordingly.
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        if(currentUser == null){
            sendToStart();
        }
    }


    public void sendToStart(){
        Intent startInent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startInent);
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        db = FirebaseFirestore.getInstance();
        db.collection("friends")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            userList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Here we go", document.getId() + " => " + document.getData());
                                User user = new User(document.get("name").toString(), document.get("status").toString(), document.get("uid").toString());

                                userList.add(user);
                            }
                            UserAdapter userAdapter = new UserAdapter(userList, new Callback() {
                                @Override
                                public void onSucces(Object o) {
                                    selectedUsers = (ArrayList<User>)o;
                                }

                                @Override
                                public void onException() {

                                }

                                @Override
                                public void onCancel() {

                                }
                            });


                            recyclerView.setAdapter(userAdapter);

                            Toast.makeText(getApplicationContext(), String.valueOf(userList.get(0).isSelected()).toString(), Toast.LENGTH_LONG).show();
                        } else {
                            Log.w("Nope", "Error getting documents.", task.getException());
                        }
                    }
                });
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter("regComplete"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter("push"));
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);


        if(item.getItemId() == R.id.main_logout){

            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }

        if(item.getItemId() == R.id.sendButton){

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications");
            reference.child("token").setValue(FirebaseInstanceId.getInstance()
                    .getToken());


//
//
//            String senderID = mAuth.getCurrentUser().getUid();


            List<String> aliases = new ArrayList<>();

            for(User u : selectedUsers){
                aliases.add(u.getUID());
            }

            final UnifiedMessage unifiedMessage = UnifiedMessage.withMessage()
                    .criteria()
                        .aliases(aliases)
                    .message()
                    .alert("Das boot")
                    .build();


            executor.submit(() -> sender.send(unifiedMessage, () -> Log.d("MainActivity", "Push message sent")));
//


        }

        return true;
    }

    public static void sendNotificationToUser(String user, final String message) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("notificationRequests");

        Map notification = new HashMap<>();
        notification.put("username", user);
        notification.put("message", message);

        ref.push().setValue(notification);
    }
}
/*
a push notikat nem kapja meg a másik

 */