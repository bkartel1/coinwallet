package com.teambitcoin.coinwallet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.teambitcoin.coinwallet.api.Account;
import com.teambitcoin.coinwallet.api.Address;
import com.teambitcoin.coinwallet.api.BlockchainAPI;
import com.teambitcoin.coinwallet.models.AddressContainer;
import com.teambitcoin.coinwallet.models.User;
import com.teambitcoin.coinwallet.models.UserWrapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
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
        
        UpdateViewableList();
        
        // Address list UI set up
        ListView addrListView = (ListView) findViewById(R.id.address_list);
        
        simpleAdapter = new SimpleAdapter(this, addressEntries, 
                android.R.layout.simple_list_item_2, 
                new String[] { ADDR_LABEL_MAP_KEY, ADDR_VALUE_MAP_KEY }, 
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        
        addrListView.setAdapter(simpleAdapter);
        registerForContextMenu(addrListView);
        
        Button archivedListBtn = (Button) findViewById(R.id.archived_addr_btn);
        archivedListBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                isViewingArchives = !isViewingArchives;
                UpdateViewableList();
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
                        }
                        catch (Exception e) {
                            // TODO: What to do if address generation fails?
                            e.printStackTrace();
                        }
                        
                        addresses.CreateAddress(generatedAddr);
                        
                        UpdateViewableList();
                        
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
        
        List<Address> addresseList = GetCurrentList();
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
                    addresses.ArchiveAddress(selectedAddress);
                }
                else {
                    addresses.UnArchiveAddress(selectedAddress);
                }
                UpdateViewableList();
                simpleAdapter.notifyDataSetChanged();
            }
            Toast.makeText(this, "Archiving address", Toast.LENGTH_SHORT).show();
        }
        
        return true;
    }
    
    private void UpdateViewableList() {
        addressEntries.clear();
        
        List<Address> addresseList = GetCurrentList();
        
        for (Address address : addresseList) {
            AddToViewableList(address);
        }
    }
    
    private void AddToViewableList(Address newAddress) {
        HashMap<String, String> newEntry = new HashMap<String, String>();
        newEntry.put(ADDR_LABEL_MAP_KEY, newAddress.getLabel());
        newEntry.put(ADDR_VALUE_MAP_KEY, newAddress.getAddress());
        addressEntries.add(newEntry);
    }
    
    private ArrayList<Address> GetCurrentList() {
        ArrayList<Address> addresseList;
        
        if (isViewingArchives) {
            addresseList = addresses.getArchivedAddressList();
        }
        else {
            addresseList = addresses.getActiveAddressList();
        }
        return addresseList;
    }
    
    private void popupLabelLengthTooLongWarning() {
        Toast.makeText(this, "The label must be between 0 and 255 characters.", Toast.LENGTH_LONG).show();
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
}
