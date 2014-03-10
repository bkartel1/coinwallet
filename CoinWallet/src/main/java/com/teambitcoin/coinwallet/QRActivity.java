package com.teambitcoin.coinwallet;

import com.squareup.picasso.Callback;
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
    private static int QR_IMAGE_WIDTH  = 200;
    private static int QR_IMAGE_HEIGHT = 200;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_layout);
        
        // UI elements on the layout
        Button qrBackBtn = (Button) findViewById(R.id.qr_back_btn);
        ImageView qrImageView = (ImageView) findViewById(R.id.qrcode_image);
        TextView qrAddressTextView = (TextView) findViewById(R.id.qrcode_address_text);
        Button qrShareBtn = (Button) findViewById(R.id.qr_share_btn);
        
        // Get some passed-in data from the previous activity
        Intent intent = getIntent();
        final String bitcoinAddress = intent.getStringExtra("bitcoinAddress");
        
        // Populate the text view with the address of the QR code
        qrAddressTextView.setText(bitcoinAddress);
        
        // Download and display the QR code
        displayQrCode(bitcoinAddress, qrImageView);
        
        // Bind the back button to return to the previous screen
        qrBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }
        });
        
        // Use Android's sharing Intent to share the address when clicked
        qrShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAddressIntent(bitcoinAddress);
            }
        });
    }
    
    private String constructGoogleGraphAPIRemoteUrl(String address, int height, int width) {
        return "https://chart.googleapis.com/chart?chs=" + 
               height + "x" + width + "&cht=qr&chl=" + address; 
    }
    
    private void displayQrCode(String address, ImageView imageView) {
        String url = constructGoogleGraphAPIRemoteUrl(address, QR_IMAGE_HEIGHT, QR_IMAGE_WIDTH);
        
        Picasso.with(this).load(url).into(imageView, new Callback() {
            TextView placeholderTextView = (TextView) findViewById(R.id.qrcode_image_placeholder_text);
            
            @Override
            public void onSuccess() {
                placeholderTextView.setText("");
            }

            @Override
            public void onError() {
                placeholderTextView.setText("Error: Could not generate QR code.");
            }
        });
    }
    
    private void shareAddressIntent(String address) {
    	String shortAddress = " \"" + address.substring(0, 5)+ "...\"";
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,getResources().getString(R.string.qr_share_body_text) + address);
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,getResources().getString(R.string.qr_share_subject_text) + shortAddress);
        startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }
    
}
