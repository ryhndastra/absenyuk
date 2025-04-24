package com.example.absensiqr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

public class QRCodeScanner extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new IntentIntegrator(this)
                .setPrompt("Scan QR Code")
                .setOrientationLocked(true)
                .setBeepEnabled(true)
                .setCaptureActivity(CaptureActivity.class)
                .initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode, data);
        if(result != null){
            if(result.getContents() == null){
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            }else{
                String scannedData = result.getContents();
                Toast.makeText(this, "Scanned : " + scannedData, Toast.LENGTH_LONG).show();

                Intent intent = new Intent();
                intent.putExtra("scanned result", scannedData);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
