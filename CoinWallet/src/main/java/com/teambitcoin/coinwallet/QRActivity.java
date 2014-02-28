package com.teambitcoin.coinwallet;

import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Displays a QR code image on screen, generated from an {@code Address}.
 */
public class QRActivity extends Activity {
    private static int QR_IMAGE_WIDTH = 200;
    private static int QR_IMAGE_HEIGHT = 200;
    
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
        
        // Download and display the QR code
        generateQrCode(bitcoinAddress, qrImageView);
        
        // Bind the back button to return to the previous screen
        qrBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }
    
    private String constructGoogleGraphAPIRemoteUrl(String address, int height, int width) {
        return "https://chart.googleapis.com/chart?chs=" + 
               height + "x" + width + "&cht=qr&chl=" + address; 
    }
    
    private void generateQrCode(String address, ImageView imageView) {
        String url = constructGoogleGraphAPIRemoteUrl(address, QR_IMAGE_HEIGHT, QR_IMAGE_WIDTH);
        
        Picasso.with(this).load(url).into(imageView);
    }
    
}
