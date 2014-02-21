package com.teambitcoin.coinwallet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.teambitcoin.coinwallet.api.Account;
import com.teambitcoin.coinwallet.api.Address;
import com.teambitcoin.coinwallet.api.BlockchainAPI;
import com.teambitcoin.coinwallet.models.AddressContainer;
import com.teambitcoin.coinwallet.models.User;
import com.teambitcoin.coinwallet.models.UserWrapper;

/**
 * This class is responsible for displaying a list of addresses associated with
 * the {@code Account} of the currently logged-in {@code User}.
 */
public class AddressScreen extends Activity {
    private static final String ADDR_LABEL_MAP_KEY = "addrLabel";
    private static final String ADDR_VALUE_MAP_KEY = "address";
    
    private SimpleAdapter simpleAdapter;
    private AddressContainer addresses;
    private List<HashMap<String, String>> addressEntries;
    private Address selectedAddress = null;
    private boolean isViewingArchives;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        isViewingArchives = false;
        addressEntries = new ArrayList<HashMap<String, String>>();
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_main);
        
        User user = User.getLoggedInUser();
        addresses = new AddressContainer(user.getGUID());
        
        updateViewableList();
        
        // Address list UI set up
        ListView addrListView = (ListView) findViewById(R.id.address_list);
        
        simpleAdapter = new SimpleAdapter(this, addressEntries, 
                android.R.layout.simple_list_item_2, 
                new String[] { ADDR_LABEL_MAP_KEY, ADDR_VALUE_MAP_KEY }, 
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        
        addrListView.setAdapter(simpleAdapter);
        registerForContextMenu(addrListView);
        
        final Button archivedListBtn = (Button) findViewById(R.id.archived_addr_btn);
        archivedListBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                isViewingArchives = !isViewingArchives;
                if (isViewingArchives) {
                    archivedListBtn.setText("Show active");
                }
                else {
                    archivedListBtn.setText("Show archived");
                }
                updateViewableList();
                simpleAdapter.notifyDataSetChanged();
            }
        });
        
        // Add address button set up
        Button addNewAddrBtn = (Button) findViewById(R.id.add_new_addr_btn);
        addNewAddrBtn.setOnClickListener(new OnClickListener() {
            // Dialog prompt for address label
            public void onClick(View v) {
                final EditText addrLabelText = new EditText(AddressScreen.this);
                AlertDialog.Builder addNewAddrDialog = new AlertDialog.Builder(AddressScreen.this);
                addNewAddrDialog.setTitle("New address");
                addNewAddrDialog.setMessage("A new address will be generated. Please enter a label for this address.");
                addNewAddrDialog.setView(addrLabelText);
                addNewAddrDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Editable addrLabelInput = addrLabelText.getText();
                        // Label length check
                        if (addrLabelInput.length() > 255) {
                            popupLabelLengthTooLongWarning();
                            return;
                        }
                        // Generate addresses
                        Address generatedAddr = null;
                        try {
                            generatedAddr = apiCallToGenerateNewAddress(addrLabelInput);
                            // Dummy
                            // generatedAddr = new Address("addr",
                            // addrLabelInput.toString(), 0, 0);
                            
                            if (generatedAddr == null) {
                                Toast.makeText(AddressScreen.this,
                                        "Error: An error occurred. The address was not created.", Toast.LENGTH_LONG);
                                return;
                            }
                        }
                        catch (Exception e) {
                            // TODO: What to do if address generation fails?
                            e.printStackTrace();
                        }
                        addresses.createAddress(generatedAddr);
                        updateViewableList();
                        // Update adapter with newly generated address
                        simpleAdapter.notifyDataSetChanged();
                        Toast.makeText(AddressScreen.this, "Created address", Toast.LENGTH_SHORT).show();
                    }
                });
                addNewAddrDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                addNewAddrDialog.show();
            }
        });
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenuInfo info) {
        super.onCreateContextMenu(contextMenu, view, info);
        AdapterView.AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) info;
        List<Address> addresseList = getCurrentList();
        selectedAddress = addresseList.get(acmi.position);
        String archiveMode = (isViewingArchives) ? "Un-Archive" : "Archive";
        contextMenu.setHeaderTitle("Options");
        contextMenu.add(1, 1, 1, archiveMode);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 1) {
            if (selectedAddress != null) {
                if (isViewingArchives == false) {
                    addresses.archiveAddress(selectedAddress);
                }
                else {
                    addresses.unArchiveAddress(selectedAddress);
                }
                updateViewableList();
                simpleAdapter.notifyDataSetChanged();
            }
            Toast.makeText(this, "Archiving address", Toast.LENGTH_SHORT).show();
        }
        
        return true;
    }
    
    private void updateViewableList() {
        addressEntries.clear();
        List<Address> addresseList = getCurrentList();
        for (Address address : addresseList) {
            addToViewableList(address);
        }
    }
    
    private void addToViewableList(Address newAddress) {
        HashMap<String, String> newEntry = new HashMap<String, String>();
        newEntry.put(ADDR_LABEL_MAP_KEY, newAddress.getLabel());
        newEntry.put(ADDR_VALUE_MAP_KEY, newAddress.getAddress());
        addressEntries.add(newEntry);
    }
    
    private ArrayList<Address> getCurrentList() {
        ArrayList<Address> addressList;
        if (isViewingArchives) {
            addressList = addresses.getArchivedAddressList();
        }
        else {
            addressList = addresses.getActiveAddressList();
        }
        return addressList;
    }
    
    private void popupLabelLengthTooLongWarning() {
        Toast.makeText(this, "The label must contain between 0 and 255 characters.", Toast.LENGTH_LONG).show();
    }
    
    /* Makes a call to the Blockchain API to generate a new address */
    private Address apiCallToGenerateNewAddress(Editable addrLabelInput) throws Exception {
        User loggedInUser = User.getLoggedInUser();
        if (loggedInUser != null) {
            Account account = new UserWrapper().getUserAccount(loggedInUser);
            Address newAddress = new BlockchainAPI().generateNewAddress(account, addrLabelInput.toString());
            return newAddress;
        }
        return null;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, EditAccount.class));
                return true;
            case R.id.action_send_bitcoins:
                startActivity(new Intent(this, SendBitcoins.class));
                return true;
            case R.id.action_receive_bitcoins:
                startActivity(new Intent(this, ReceiveBitcoins.class));
                return true;
            case R.id.action_show_transactions:
                startActivity(new Intent(this, ShowTransactions.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lander, menu);
        return true;
    }
}
