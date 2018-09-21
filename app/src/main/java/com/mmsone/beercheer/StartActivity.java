package  com.mmsone.beercheer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private Button mRegButton;
    private Button mLogButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mLogButton = (Button)findViewById(R.id.startLoginButton);
        mRegButton = (Button)findViewById(R.id.startRegButton);

        mRegButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                Intent reg_intent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(reg_intent);

            }
        });

        mLogButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                Intent reg_intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(reg_intent);

            }
        });
    }
}
