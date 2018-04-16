package lry.lpdip.servercheck;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.widget.EditText;
import android.widget.Toast;

import static android.telephony.SmsManager.getDefault;

public class Mainpage extends AppCompatActivity
{

    private EditText numTel, valtemp;
    private Intent intentSms;
    private PendingIntent spi;
    private SmsManager sms;

    private IntentFilter intentfilter;

    private static final int  MY_PERMISSIONS_REQUEST_SEND_SMS =1;
    private String num, msg;
    private float BatteryTemp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        this.numTel = (EditText) findViewById(R.id.txt_numero);
        this.valtemp = (EditText) findViewById(R.id.val_tmp);

        this.intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        this.registerReceiver(broadcastreceiver,intentfilter);

    }
    private void send_sms (String txt)
    {
        this.sms = getDefault();
        this.intentSms = new Intent("SMS_ACTION_SENT");
        this.spi = PendingIntent.getBroadcast(this, 0, this.intentSms, 0);

        this.num = this.numTel.getText().toString();
        this.msg = txt;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this, "Permission is not granted, request permission send" , Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);

        }
        else
        {
            this.sms.sendTextMessage(this.num , null, this.msg , this.spi, this.spi);
        }
    }

    private BroadcastReceiver broadcastreceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            BatteryTemp = (float)intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)/10f;
            valtemp.setText(String.valueOf(BatteryTemp));
            if (BatteryTemp>=30)
            {
                send_sms("Attention le serveur est actuellement a "+BatteryTemp+" °C");
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    this.sms.sendTextMessage(this.num , null, this.msg , this.spi, this.spi);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                }
                else
                {
                    Toast.makeText(this, "Permission is not granted we can't send a sms" , Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
}
