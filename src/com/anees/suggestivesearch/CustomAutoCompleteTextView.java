package com.anees.suggestivesearch;

import java.util.HashMap;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/**
 * Customizing AutoCompleteTextView to return Search suggestions
 */
public class CustomAutoCompleteTextView extends AutoCompleteTextView {

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
	super(context, attrs);
    }

    /** Returns the search suggestion corresponding to the selected item */
    @Override
    protected CharSequence convertSelectionToString(Object selectedItem) {
	/**
	 * Each item in the autocompetetextview suggestion list is a hashmap
	 * object
	 */
	@SuppressWarnings("unchecked")
	HashMap<String, String> hm = (HashMap<String, String>) selectedItem;
	return hm.get("suggestion");
    }

}
