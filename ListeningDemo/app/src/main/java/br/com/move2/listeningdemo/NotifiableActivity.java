package br.com.move2.listeningdemo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;

public abstract class NotifiableActivity extends AppCompatActivity {

    private static final StringBuffer notificationContentBuffer = new StringBuffer("");

    public void generateNotification(int Id, String content){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Super Oferta!")
                .setContentText(content)
                .setAutoCancel(true);
        Intent resultIntent = new Intent(this, NotificationResultActivity.class);

        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(Id, mBuilder.build());

        notificationContentBuffer.append(content).append("\n");

    }

    protected static String getNotificationContent(){
        return notificationContentBuffer.toString();
    }

    protected static void clearNotificationContent(){
        notificationContentBuffer.delete(0,notificationContentBuffer.length());
    }
}
