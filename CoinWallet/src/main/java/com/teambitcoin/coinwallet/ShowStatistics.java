package com.teambitcoin.coinwallet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.teambitcoin.coinwallet.api.BlockchainAPI;
import com.teambitcoin.coinwallet.api.Statistics;

public class ShowStatistics extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_statistics);
        final Statistics stats = new BlockchainAPI().getStatistics();
		ListView txListView = (ListView) findViewById(R.id.statistics_list);
		List<Map<String, String>> txEntries = new ArrayList<Map<String, String>>();
		
		if(stats != null){
			txEntries.add(new HashMap<String, String>(){{
				put("name", "Total Number of BTC");
				put("value", stats.getTotalBTC().toString());
			}});
			txEntries.add(new HashMap<String, String>(){{
				put("name", "Network difficulty");
				put("value", Float.toString(stats.getDifficulty()));
			}});
			txEntries.add(new HashMap<String, String>(){{
				put("name", "Network hash rate");
				put("value", Float.toString(stats.getHashRate()));
			}});
			txEntries.add(new HashMap<String, String>(){{
				put("name", "Total number of blocks");
				put("value", Integer.toString(stats.getNumberOfBlocks()));
			}});
		}else{
			txEntries.add(new HashMap<String, String>(){{
				put("name", "No internet connection");
				put("value", "");
			}});
		}
		
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, txEntries,
				android.R.layout.simple_list_item_2, new String[] {
						"name", "value" }, new int[] {
						android.R.id.text1, android.R.id.text2 });
		txListView.setAdapter(simpleAdapter);
	}
}