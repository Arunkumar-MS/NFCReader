package com.hackathon.apps.nfcreader;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import static android.nfc.NfcAdapter.EXTRA_TAG;

public class MainActivity extends AppCompatActivity implements ResponseHandler{
    public static final String TAG = "NfcDemo";
    public static final String MIME_TEXT_PLAIN = "text/plain";

    TextView nfcData;
    NfcAdapter nfcAdapter;
    PendingIntent nfcPendingIntent;
    IntentFilter[] intentFiltersArray;
    NfcReaderTask nfcReaderTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nfcData = (TextView) findViewById(R.id.explanation);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(nfcAdapter == null){
            Toast.makeText(this,"This device does not support NFC", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (!nfcAdapter.isEnabled()) {
            nfcData.setText("NFC is disabled.");
        } else {
            nfcData.setText("NFC is enabled");
        }

        Intent nfcIntent = new Intent(this, getClass());
        nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        nfcPendingIntent = PendingIntent.getActivity(this, 0, nfcIntent, 0);
        IntentFilter tagIntentFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            tagIntentFilter.addDataType(MIME_TEXT_PLAIN);
            tagIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            intentFiltersArray = new IntentFilter[]{tagIntentFilter};
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
            Toast.makeText(this, "Malformed MIME type exception ", Toast.LENGTH_SHORT).show();
        }

        GetProductTask productTask = (GetProductTask) new GetProductTask().execute();
        handleIntent(getIntent());
    }

    /*
    This method is called when NFC detected either app in background or foreground
    Check the action and call async.execute to get data in seperate thread
     */
    private void handleIntent(Intent intent) {
        if(intent == null){
            Toast.makeText(this, "intent is null handleIntent ", Toast.LENGTH_SHORT).show();
            return;
        }
        String action = intent.getAction();

        if(nfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)){
            String type = intent.getType();
            if(MIME_TEXT_PLAIN.equals(type)){
                Tag tag = intent.getParcelableExtra(EXTRA_TAG);

                nfcReaderTask = new NfcReaderTask();
                nfcReaderTask.SetResponseListener(this);
                nfcReaderTask.execute(tag);
            }
        }
    }


    //Need to enable the detection of NFC when app is open
    @Override
    protected void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this,
                nfcPendingIntent,
                intentFiltersArray,
                null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    //This is called when NFC is detected in app background state or in killed state
    //delivering to handle intent method to parse the data
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    //call back after parsing nfc data successfully
    @Override
    public void OnSuccessfullResponse(String result) {
        nfcData.setText(result);
    }
}
