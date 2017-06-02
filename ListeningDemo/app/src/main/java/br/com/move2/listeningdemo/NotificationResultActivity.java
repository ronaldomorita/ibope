package br.com.move2.listeningdemo;

import android.app.NotificationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NotificationResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_result);

        refreshNotificationContent();

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.cancelAll();

        Button clear = (Button) findViewById(R.id.buttonClearNotifications);
        clear.setOnClickListener(clearNotifications);

        Button close = (Button) findViewById(R.id.buttonCloseNotification);
        close.setOnClickListener(backToHome);
    }

    private View.OnClickListener clearNotifications = new View.OnClickListener(){
        public void onClick(View v){
            NotifiableActivity.clearNotificationContent();
            refreshNotificationContent();
        }
    };

    private View.OnClickListener backToHome = new View.OnClickListener(){
        public void onClick(View v){
            finish();
        }
    };

    private void refreshNotificationContent(){
        TextView textNotifications = (TextView) findViewById(R.id.textNofitication);
        textNotifications.setText(NotifiableActivity.getNotificationContent());

    }
}
