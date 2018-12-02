package com.android.projects.ultimatesentinal;

/**
 * Created by snehakannan on 27/11/18.
 */

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SMSBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = SMSBroadcastReceiver.class.getSimpleName();
    public static final String pdu_type = "pdus";
    Ringtone r;
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {

        final SharedPreferences shp = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        String code = "";
        code = shp.getString("secretCode", "");

        // Get the SMS message.
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs;
        String strMessage = "";
        String format = bundle.getString("format");
        // Retrieve the SMS message received.
        Object[] pdus = (Object[]) bundle.get(pdu_type);
        if (pdus != null) {
            // Check the Android version.
            boolean isVersionM =
                    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
            // Fill the msgs array.
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                // Check Android version and use appropriate createFromPdu.
                if (isVersionM) {
                    // If Android version M or newer:
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                } else {
                    // If Android version L or older:
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                // Build the message to show.
                strMessage += "SMS from " + msgs[i].getOriginatingAddress();
                strMessage += " :" + msgs[i].getMessageBody() + "\n";
                // Log and display the SMS message.
                Log.d(TAG, "onReceive: " + strMessage);

                //Prints message
                // Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show();

                if(msgs[i].getMessageBody().equals("STOP"))
                {
                    String recoveryPH = "9999999999";
                    recoveryPH = shp.getString("RecoveryNumber", "");
                    if(msgs[i].getOriginatingAddress().equals(recoveryPH))
                    {
                        Toast.makeText(context, "In receiver" , Toast.LENGTH_LONG).show();
                        Intent stopIntent = new Intent(context, RingtonePlayingService.class);
                        context.stopService(stopIntent);
                        Intent go = new Intent(context, MainActivity.class);
                        context.startActivity(go);
                    }

                }
                if(code.equals(msgs[i].getMessageBody()))
                {
                    Toast.makeText(context, "Alert! Secret Code has been sent! Initialising Phone Recovery", Toast.LENGTH_LONG).show();
                    SharedPreferences.Editor preferencesEditor = shp.edit();
                    preferencesEditor.putString("RecoveryNumber", msgs[i].getOriginatingAddress());
                    preferencesEditor.commit();
                    Intent go = new Intent(context, TriggerAlertHome.class);
                    context.startActivity(go);
                }
            }
        }
    }
}
