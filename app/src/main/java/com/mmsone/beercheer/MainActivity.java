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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference ref;
    private List<User> userList = new ArrayList<User>();
    private RecyclerView recyclerView;
    FirebaseFirestore db;

    private DatabaseReference mNotificationDatabase;

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

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("push")){
                    String message = intent.getStringExtra("message");
                    showNotification("BeerApp", message);
                }
            }
        };


        if( currentUser == null){
            Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
            startActivity(startIntent);
            finish();
        }





//        db = FirebaseFirestore.getInstance();
//        db.collection("friends")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d("Here we go", document.getId() + " => " + document.getData());
//                                User user = new User(document.get("name").toString(), document.get("profession").toString());
//                                userList.add(user);
//                            }
//                            UserAdapter userAdapter = new UserAdapter(userList);
//                            recyclerView.setAdapter(userAdapter);
//                            Toast.makeText(getApplicationContext(), String.valueOf(userList.size()).toString(), Toast.LENGTH_LONG).show();
//                        } else {
//                            Log.w("Nope", "Error getting documents.", task.getException());
//                        }
//                    }
//                });
    }

    private void showNotification(String title, String message) {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder b = new NotificationCompat.Builder(getApplicationContext());
        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(contentIntent);

        NotificationManager notificationManager = (NotificationManager)getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,b.build());
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
                                User user = new User(document.get("name").toString(), document.get("profession").toString(), mAuth.getCurrentUser().getUid().toString());

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

//            Toast.makeText(getApplicationContext(), String.valueOf(userList.get(0).isSelected()).toString(), Toast.LENGTH_LONG).show();
//            FirebaseMessaging.getInstance().subscribeToTopic("beerchat")
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            String msg = "megjött";
//                            if (!task.isSuccessful()) {
//                                msg = "nem jött meg";
//                            }
//                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
//                        }
//                    });

            Iterator it = selectedUsers.iterator();

            HashMap<String,String> notificationData = new HashMap<>();
            notificationData.put("from", FirebaseAuth.getInstance().getCurrentUser().getUid());
            notificationData.put("type", "Beer");

            String senderID = mAuth.getCurrentUser().getUid();

            while (it.hasNext()) {
                User u = (User) it.next();
                mNotificationDatabase.child(u.getUID()).push().setValue(notificationData);

                FirebaseMessaging.getInstance().subscribeToTopic(u.getUID());

                FirebaseMessaging fm = FirebaseMessaging.getInstance();
                fm.send(new RemoteMessage.Builder(senderID + "\"@gcm.googleapis.com\"")
                        .setMessageId(u.getUID())
                        .addData("message", "Hello World")
                        .build());
            }

            System.out.println( userList.toString());
            System.out.println("\n\n Selected: \n\n" + selectedUsers.toString());
        }

        return true;
    }
}

/*
nem müködik a checkbox
nem ugrál, csak nem jegyzi meg hogy ki van jelezve és ki nem
holnapra dolog

 */
