package  com.mmsone.beercheer;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyMessageService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){

        handleNotification(remoteMessage.getNotification().getBody());
        
    }

    private void handleNotification(String body) {
        Intent pushNoti = new Intent("push");
        pushNoti.putExtra("message",body);
        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNoti);

    }
}
