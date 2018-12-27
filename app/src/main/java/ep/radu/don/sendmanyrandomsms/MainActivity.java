package ep.radu.don.sendmanyrandomsms;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView textView_anonymous;
    EditText editText_textToSend;
    EditText editText_numberMessages;
    EditText editText_phoneNumber;
    RadioGroup radioGroup_anonim;
    Button btn_reset;
    Button btn_send;
    RandomStrings randomStrings;
    static final String LOGTAG = "RADUEP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startProcess();
    }

    public void startProcess() {
        setContentView(R.layout.activity_main);
        Log.e(LOGTAG, "MAIN ");
        randomStrings = new RandomStrings(getString(R.string.words), getString(R.string.complements),
                getString(R.string.punctuation), getString(R.string.emoticons));
        textView_anonymous = (TextView) findViewById(R.id.anonymous_textview);
        editText_textToSend = (EditText) findViewById(R.id.text_to_send);
        editText_numberMessages = (EditText) findViewById(R.id.input_number_messages);
        editText_phoneNumber = (EditText) findViewById(R.id.input_phone);
        btn_reset = (Button) findViewById(R.id.btn_reset_phrase);
        radioGroup_anonim = (RadioGroup) findViewById(R.id.radioGroup_anonymous);
        btn_send = (Button) findViewById(R.id.btn_send_message);
        btn_reset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                generateAnonymoustextPhrase();
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                sendMessage();
            }
        });
    }

    public void generateAnonymoustextPhrase() {
        editText_textToSend.setText(randomStrings.generatePhrase());
    }

    public void sendMessage() {
        String phoneNumber = editText_phoneNumber.getText().toString();
        int numberOfMessages = Integer.parseInt(editText_numberMessages.getText().toString());
        if (!phoneNumber.equals("") && isSMSPermissionGranted()) {
            sendMessages(numberOfMessages, phoneNumber);
        }
    }

    public boolean isSMSPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(LOGTAG, "Permission is granted");
                return true;
            } else {

                Log.v(LOGTAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 0);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(LOGTAG, "Permission is granted");
            return true;
        }
    }

    public void sendMessages(final int number, final String phoneNumber) {
        if (number < 1) return;
        String textMessage = editText_textToSend.getText().toString();
        try {
            if (radioGroup_anonim.getCheckedRadioButtonId() == R.id.yes_anonymous_button) {
                textMessage = randomStrings.generatePhrase();
            }
            PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
            PendingIntent deliveredIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);

            String SENT = "SMS_SENT";
            String DELIVERED = "SMS_DELIVERED";
            PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                    new Intent(SENT), 0);

            PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                    new Intent(DELIVERED), 0);

            //---when the SMS has been sent---
            registerReceiver(new BroadcastReceiver(){
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    switch (getResultCode())
                    {
                        case Activity.RESULT_OK:
                            Log.e(LOGTAG, "SMS SENT OK");
                            sendMessages(number-1, phoneNumber);
                            // do what you need to do if OK
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            sendMessages(number-1, phoneNumber);
                            Log.e(LOGTAG, "Err1");
                            // do what you need to do if GENERIC FAILURE
                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            Log.e(LOGTAG, "Err2");
                            // do what you need to do if no Service
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            Log.e(LOGTAG, "Err3");

                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            Log.e(LOGTAG, "Err4");

                            break;
                    }
                }
            }, new IntentFilter(SENT));

            //---when the SMS has been delivered---
            registerReceiver(new BroadcastReceiver(){
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    switch (getResultCode())
                    {
                        case Activity.RESULT_OK:
                            Log.e(LOGTAG, "Delivered");
                            // Send next sms when previous one delivered your choice

                            break;
                        case Activity.RESULT_CANCELED:
                            //Do something if not delivered
                            break;
                    }
                }
            }, new IntentFilter(DELIVERED));
            SmsManager sms = SmsManager.getDefault();
            Toast.makeText(this, "Sending message: " + textMessage + "\nNumber: " + phoneNumber, Toast.LENGTH_LONG).show();

            sms.sendTextMessage(phoneNumber, null, textMessage, sentPI, deliveredPI);
        } catch (Exception e) {
            Log.e(LOGTAG, "ERr: " + e.toString());
        }
    }

}
