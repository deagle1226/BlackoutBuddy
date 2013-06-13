package com.blackoutbuddy.android.app;

import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.blackoutbuddy.android.data.BlackoutData;
import com.blackoutbuddy.android.data.DataSource;


public class BlackoutFragment extends SherlockListFragment {
	
	public static final String TAG = BlackoutFragment.class.getSimpleName();
	
	private DataSource datasource;
	private BlackoutData selected;
	
	public BlackoutFragment(){
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ListView list = new ListView(getSherlockActivity());
		list.setId(android.R.id.list);
		list.setAdapter(new ListViewAdapter<String>(getSherlockActivity(),
				android.R.layout.simple_list_item_1, new String[]{}));
		registerForContextMenu(list);
		selected = null;
		return list;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateList();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		BlackoutData blackout = (BlackoutData) l.getItemAtPosition(position);
		long group = blackout.getId();
		SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity()).edit();
		prefs.putLong("selectedBlackout", group);
		prefs.commit();
//		Intent i = new Intent(getSherlockActivity(), LocationsList.class);
//		i.putExtra("group", group);
//		startActivity(i);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterView.AdapterContextMenuInfo info;
	    try {
	        info = (AdapterView.AdapterContextMenuInfo) menuInfo;
	    } catch (ClassCastException e) {
	        Log.e(TAG, "bad menuInfo", e);
	    return;
	    }
		selected = (BlackoutData) getListAdapter().getItem(info.position);
		menu.setHeaderTitle(selected.toString());
		menu.add("Delete");
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		if (item.getTitle().toString().equalsIgnoreCase("Delete")){
			datasource.open();
			datasource.deleteBlackout(selected);
			datasource.close();
			Log.d(TAG, "deleted blackout at: " + selected.getId());
			updateList();
		}
		return super.onContextItemSelected(item);
	}

	public void updateList(){
		datasource = new DataSource(getSherlockActivity());
		datasource.open();
		List<BlackoutData> blackouts = datasource.getAllBlackouts();
		datasource.close();
		ArrayAdapter<BlackoutData> adapter = new ArrayAdapter<BlackoutData>(getSherlockActivity(), android.R.layout.simple_list_item_1, blackouts);
        setListAdapter(adapter);
	}


}
