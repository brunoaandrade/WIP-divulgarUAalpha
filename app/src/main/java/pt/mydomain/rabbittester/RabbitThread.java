package pt.mydomain.rabbittester;

import android.os.Looper;
import android.util.Log;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;

/**
 * Created by tjrs0_000 on 17/06/2015.
 */
public class RabbitThread extends Thread {

    private final static String QUEUE_NAME = "hello";

    private static final String ip = "192.168.160.32";

    private MessageConsumer mConsumer;
    public static boolean isRuning = false;

    public RabbitThread() {
    }

    @Override
    public void run() {

        Looper.prepare();

        Log.i("Thread","A INICIAR THREAD");
        isRuning = true;

        mConsumer = new MessageConsumer(ip,
                QUEUE_NAME,
                "");

        mConsumer.connectToRabbitMQ();

        //register for messages
        mConsumer.setOnReceiveMessageHandler(new MessageConsumer.OnReceiveMessageHandler() {

            public void onReceiveMessage(byte[] message) {
                String text = "";
                try {
                    text = new String(message, "UTF8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Log.i("Thread",text);
            }
        });

        Looper.loop();

    }
}
