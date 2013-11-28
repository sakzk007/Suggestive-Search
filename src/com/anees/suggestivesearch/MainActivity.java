package com.anees.suggestivesearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.SimpleAdapter;

public class MainActivity extends Activity {

    private AutoCompleteTextView atvSuggestion;
    private SuggestionTask suggestionTask;
    private ParserTask parserTask;
    private CheckInternet ci;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);

	ci = new CheckInternet(getApplicationContext());
	if (!ci.isConnectingToInternet()) {
	    showAlert();
	}

	atvSuggestion = (AutoCompleteTextView) findViewById(R.id.atv_search);
	atvSuggestion.setThreshold(1);

	atvSuggestion.addTextChangedListener(new TextWatcher() {

	    @Override
	    public void onTextChanged(CharSequence s, int start, int before, int count) {
		suggestionTask = new SuggestionTask();
		suggestionTask.execute(s.toString());
	    }

	    @Override
	    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub
	    }

	    @Override
	    public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
	    }
	});
    }

    // Alert If Internet Not Present
    private void showAlert() {
	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
	// set title
	alertDialogBuilder.setTitle("No Internet!");
	// set dialog message
	alertDialogBuilder.setMessage("Suggestive Search won't work!").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int id) {
		dialog.cancel();
	    }
	});
	// create alert dialog
	AlertDialog alertDialog = alertDialogBuilder.create();
	// show it
	alertDialog.show();
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
	String data = "";
	InputStream iStream = null;
	HttpURLConnection urlConnection = null;
	try {
	    URL url = new URL(strUrl);

	    // Creating an http connection to communicate with url
	    urlConnection = (HttpURLConnection) url.openConnection();

	    // Connecting to url
	    urlConnection.connect();

	    // Reading data from url
	    iStream = urlConnection.getInputStream();

	    BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

	    StringBuffer sb = new StringBuffer();

	    String line = "";
	    while ((line = br.readLine()) != null) {
		sb.append(line);
	    }

	    data = sb.toString();

	    br.close();

	} catch (Exception e) {
	    Log.d("Exception while downloading url", e.toString());
	} finally {
	    iStream.close();
	    urlConnection.disconnect();
	}
	return data;
    }

    // Fetches all places from GooglePlaces AutoComplete Web Service
    private class SuggestionTask extends AsyncTask<String, Void, String> {

	@Override
	protected String doInBackground(String... suggest) {
	    // For storing data from web service
	    String data = "";

	    String input = "";

	    try {
		input = URLEncoder.encode(suggest[0], "utf-8");
	    } catch (UnsupportedEncodingException e1) {
		e1.printStackTrace();
	    }
	    // Building the url to the web service
	    String url = "http://ec2-54-213-110-181.us-west-2.compute.amazonaws.com:8080/offers/tags/?q=" + input;

	    try {
		// Fetching the data from web service in background
		data = downloadUrl(url);

	    } catch (Exception e) {
		Log.d("Background Task", e.toString());
	    }
	    return data;
	}

	@Override
	protected void onPostExecute(String result) {
	    super.onPostExecute(result);
	     Log.w("O/p ", result);
	    // Creating ParserTask
	    parserTask = new ParserTask();

	    // Starting Parsing the JSON string returned by Web Service
	    parserTask.execute(result);
	}
    }

    /** A class to parse the Suggestions in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

	JSONObject jObject;

	@Override
	protected List<HashMap<String, String>> doInBackground(String... jsonData) {

	    List<HashMap<String, String>> suggestions = null;

	    SearchJSONParser suggestionsJsonParser = new SearchJSONParser();

	    try {
		jObject = new JSONObject(jsonData[0]);

		// Getting the parsed data as a List construct
		suggestions = suggestionsJsonParser.parse(jObject);

	    } catch (Exception e) {
		Log.d("Exception", e.toString());
	    }
	    return suggestions;
	}

	@Override
	protected void onPostExecute(List<HashMap<String, String>> result) {

	    String[] from = new String[] { "suggestion" };
	    int[] to = new int[] { android.R.id.text1 };

	    try {
		// Creating a SimpleAdapter for the AutoCompleteTextView
		SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);
		// Setting the adapter
		atvSuggestion.setAdapter(adapter);
	    } catch (Exception e) {
		Log.e("Main Breaking!", e.getMessage());
	    }

	}
    }

}