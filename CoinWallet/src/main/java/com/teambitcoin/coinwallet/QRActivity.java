package com.teambitcoin.coinwallet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Displays a QR code image on screen, generated from an {@code Address}.
 */
public class QRActivity extends Activity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_layout);
        
        // UI elements on the layout
        Button qrBackBtn = (Button) findViewById(R.id.qr_back_btn);
        ImageView qrImageView = (ImageView) findViewById(R.id.qrcode_image);
        TextView qrAddressTextView = (TextView) findViewById(R.id.qrcode_address_text);
        
        // Get some passed-in data from the previous activity
        Intent intent = getIntent();
        String bitcoinAddress = intent.getStringExtra("bitcoinAddress");
        
        // Populate the text view with the address of the QR code
        qrAddressTextView.setText(bitcoinAddress);
        
        // Bind the back button to return to the previous screen
        qrBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }
    
}
