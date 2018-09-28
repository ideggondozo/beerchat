package com.mmsone.beercheer;

import android.app.Application;
import android.util.Log;

import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.unifiedpush.PushRegistrar;
import org.jboss.aerogear.android.unifiedpush.RegistrarManager;
import org.jboss.aerogear.android.unifiedpush.fcm.AeroGearFCMPushConfiguration;

import java.net.URI;

public class BeerChatApplication extends Application {

    private final String VARIANT_ID       = "2f7c3c5b-840d-4779-a465-6c98ef0a1543";
    private final String SECRET           = "22a4e317-4388-4097-a686-0512f852d82c";
    private final String GCM_SENDER_ID    = "493208613744";
    private final String UNIFIED_PUSH_URL = "http://192.168.1.31:8080/ag-push/";
    private boolean registered;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void registerPush(String uid) {
        if (registered) {
            return;
        }

        RegistrarManager.config("register", AeroGearFCMPushConfiguration.class)
                .setPushServerURI(URI.create(UNIFIED_PUSH_URL))
                .setSenderId(GCM_SENDER_ID)
                .setVariantID(VARIANT_ID)
                .setSecret(SECRET)
                .setAlias(uid)
                .asRegistrar();

        PushRegistrar registrar = RegistrarManager.getRegistrar("register");
        registrar.register(getApplicationContext(), new Callback<Void>() {
            @Override
            public void onSuccess(Void data) {
                registered = true;
                Log.d("BeerChatApplication", "Registration success");
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("BeerChatApplication", e.getMessage(), e);
            }
        });
    }
}
