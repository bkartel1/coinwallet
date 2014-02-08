package com.teambitcoin.coinwallet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.teambitcoin.coinwallet.api.Account;
import com.teambitcoin.coinwallet.api.Address;
import com.teambitcoin.coinwallet.api.BlockchainAPI;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class AddressScreen extends Activity {
	SimpleAdapter simpleAdapter;
	AddressContainer addresses;
	List<HashMap<String, String>> addressEntries;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.address_main);
		
		// TODO: should this be the current logged-in User?
		User user = new User("me","1");
		addresses = new AddressContainer(user.getGUID());
		
		CreateViewableList();

		// Address list UI set up
		ListView addrListView = (ListView) findViewById(R.id.address_list);
		
		simpleAdapter = new SimpleAdapter(this, addressEntries, 
				android.R.layout.simple_list_item_1, new String[] {"address"}, 
				new int[] {android.R.id.text1});
		
		addrListView.setAdapter(simpleAdapter);
		registerForContextMenu(addrListView);
		
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
							//generatedAddr = apiCallToGenerateNewAddress(addrLabelInput);
							generatedAddr = new Address("addr", addrLabelInput.toString(), 0, 0);
						} catch (Exception e) {
							// TODO: What to do when address generation fails? 
							e.printStackTrace();
						}
												
						addresses.CreateAddress(generatedAddr);
						
						AddToViewableList(generatedAddr);
						
						// Update adapter with newly generated address
						simpleAdapter.notifyDataSetChanged();					
						
						Toast.makeText(AddressScreen.this, "Created address", Toast.LENGTH_SHORT).show();
					}
				});
				addNewAddrDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO: cancel operation
					}
				});
				
				addNewAddrDialog.show();
			}
		});
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenuInfo info) {
		super.onCreateContextMenu(contextMenu, view, info);
		
		// TODO: change all hard coded strings to resources 
		contextMenu.setHeaderTitle("Options");
		contextMenu.add(1, 1, 1, "Archive");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case 1:
			Toast.makeText(this, "Archiving address", Toast.LENGTH_SHORT).show();
			break;
		}
		
		return true;
	}

	private void CreateViewableList(){		
		addressEntries = new ArrayList<HashMap<String, String>>();
		for(Address address : addresses.getActiveAddressList())	{
			AddToViewableList(address);
		}
	}
	
	private void AddToViewableList(Address newAddress){
		HashMap<String,String> newEntry = new HashMap<String,String>();
		newEntry.put("address", newAddress.getLabel());
		addressEntries.add(0, newEntry);
	}
	
	private void popupLabelLengthTooLongWarning() {
		AlertDialog.Builder addNewAddrDialog = new AlertDialog.Builder(AddressScreen.this);
		addNewAddrDialog.setTitle("Error");
		addNewAddrDialog.setMessage("The label must be between 0 and 255 characters.");
	}
	
	// TODO: Is this necessary? Move this somewhere else
	private Address apiCallToGenerateNewAddress(Editable addrLabelInput) throws Exception {
		User loggedInUser = User.getLoggedInUser();
		if (loggedInUser != null) {
			Account account = new UserWrapper().getUserAccount(loggedInUser);
			return new BlockchainAPI().generateNewAddress(account, addrLabelInput.toString());
		}
		return null;
	}
}
