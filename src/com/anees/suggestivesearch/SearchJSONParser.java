package com.anees.suggestivesearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class SearchJSONParser {

    /** Receives a JSONObject and returns a list */
    public List<HashMap<String, String>> parse(JSONObject jObject) {

	JSONArray jSuggestions = null;
	try {
	    /** Retrieves all the elements in the 'places' array */
	    jSuggestions = jObject.getJSONArray("tags");
	    
	} catch (JSONException e) {
	    e.printStackTrace();
	    return new ArrayList<HashMap<String, String>>();
	}
	return getSuggestions(jSuggestions);
    }

    private List<HashMap<String, String>> getSuggestions(JSONArray jSuggestions) {
	int suggestionCount = jSuggestions.length();
	List<HashMap<String, String>> suggestionList = new ArrayList<HashMap<String, String>>();
	/** Taking each suggestion, parses and adds to list object */
	for (int i = 0; i < suggestionCount; i++) {
	    try {
		HashMap<String, String> suggestion = new HashMap<String, String>();
		suggestion.put("suggestion", (String) jSuggestions.get(i));
		suggestionList.add(suggestion);
	    } catch (JSONException e) {
		e.printStackTrace();
	    }
	}
	
	return suggestionList;
    }
}
