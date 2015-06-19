package pt.mydomain.rabbittester;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.app.Notification;
import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import deti.ua.main.R;

public class RabbitService extends Service {

    private static final String LOG_TAG = "RabbitService";

    private MessageConsumer mConsumer;

    private NotificationManager mNM;

    private String text = "";

    public RabbitService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Log.i(LOG_TAG, "Yup isto chega aqui");

        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        mConsumer = new MessageConsumer("192.168.160.32",
                "hello",
                "fanout");

        new Thread( new Runnable() {

            @Override
            public void run() {

                mConsumer.connectToRabbitMQ();

                mConsumer.setOnReceiveMessageHandler(new MessageConsumer.OnReceiveMessageHandler(){
                    public void onReceiveMessage(byte[] message){
                        try {
                            text = new String(message, "UTF8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Log.i(LOG_TAG, text);
                        startApplication();
                    }
                });
            }
        }).start();


        return START_STICKY;                // tells the OS to recreate the service after it has enough memory
    }

    private void startApplication()
    {
        Log.i(LOG_TAG,"Iniciar Notificacao");
        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);


        Intent previousIntent = new Intent();
        previousIntent.setAction(Constants.ACTION.ACCEPT);
        PendingIntent acceptIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent nextIntent = new Intent();
        previousIntent.setAction(Constants.ACTION.DENNY);
        PendingIntent dennyIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_stat_notification);
        builder.setContentIntent(pendingIntent);

        builder.setAutoCancel(true);
        builder.addAction(R.drawable.ic_stat_notification,
                "Aceitar", acceptIntent);
        builder.addAction(R.drawable.ic_stat_notification,
                "Ignorar", acceptIntent);
        builder.setContentTitle("WIP");
        builder.setContentText("Foste convidado para novo projeto");

        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "In onDestroy");
    }
}
