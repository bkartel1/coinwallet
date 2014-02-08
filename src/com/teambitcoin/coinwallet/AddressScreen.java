package com.teambitcoin.coinwallet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.teambitcoin.coinwallet.api.Address;
import com.teambitcoin.coinwallet.api.BlockchainAPI;
import com.teambitcoin.coinwallet.models.User;

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
						// TODO: generate the address w/label
						Address generatedAddr = null;
						try {
							//generatedAddr = new BlockchainAPI().generateNewAddress(null);
							generatedAddr = new Address("addr", addrLabelInput.toString(), 0, 0);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						addresses.CreateAddress(generatedAddr);
						
						AddToViewableList(generatedAddr);
						
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
		contextMenu.add(1, 1, 1, "Edit");
		contextMenu.add(1, 2, 2, "Archive");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
		//Toast.makeText(this, "Not yet implemented!", Toast.LENGTH_SHORT).show();
		if(menuItem.getItemId() == 2)
		{
			Toast.makeText(this, "Archiving address", Toast.LENGTH_SHORT).show();
		}
		return true;
	}
	
	// oye oye.
	private void CreateViewableList(){		
		addressEntries = new ArrayList<HashMap<String, String>>();
		for(Address address : addresses.getActiveAddressList())
		{
			AddToViewableList(address);
		}
	}
	
	private void AddToViewableList(Address newAddress){
		HashMap<String,String> newEntry = new HashMap<String,String>();
		newEntry.put("address",newAddress.getLabel());
		addressEntries.add(newEntry);
	}
	
}
