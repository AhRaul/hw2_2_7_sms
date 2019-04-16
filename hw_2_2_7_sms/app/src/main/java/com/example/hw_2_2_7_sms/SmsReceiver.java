package com.example.hw_2_2_7_sms;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.widget.TextView;

import static com.example.hw_2_2_7_sms.MainActivity.getTextOutput;

public class SmsReceiver extends BroadcastReceiver {

    private int messageId = 0;
    private TextView outputTextView = getTextOutput();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null) { // Минимальные проверки
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");  // Получаем сообщения
            SmsMessage[] messages = new SmsMessage[pdus.length];
            for (int i = 0; i < pdus.length; i++)
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

            String smsFromPhone = messages[0].getDisplayOriginatingAddress();
            StringBuilder body = new StringBuilder();
            for (int i = 0; i < messages.length; i++)
                body.append(messages[i].getMessageBody());

            String bodyText = body.toString();
            outputTextView.append(bodyText);            //вывод сообщения в textView
            makeNote(context, smsFromPhone, bodyText);
            abortBroadcast();   // Это будет работать только на Андроиде ниже 4.4
        }
    }

    private void makeNote(Context context, String addressFrom, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "2")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(String.format("Sms [%s]", addressFrom))
                .setContentText(message);
        Intent resultIntent = new Intent(context, SmsReceiver.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        builder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(messageId++, builder.build());
    }
}
