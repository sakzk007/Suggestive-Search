package com.anees.suggestivesearch;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckInternet {
    private Context _context;

    public CheckInternet(Context context) {
	this._context = context;
    }

    /** Checking whether Internet is present or not */
    public boolean isConnectingToInternet() {
	ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
	if (connectivity != null) {
	    NetworkInfo[] info = connectivity.getAllNetworkInfo();
	    if (info != null) {
		int length_connections = info.length;
		for (int i = 0; i < length_connections; i++)
		    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
			return true;
		    }
	    }
	}
	return false;
    }
}
