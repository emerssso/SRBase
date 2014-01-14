/*
 * TODO: Figure out Copyright stuff
 */

package com.gmail.emerssso.srbase;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

//TODO: Write Some more in the Javadoc header
/**
 * This activity manages the creation and modification
 * of SR elements in the database.
 *
 * @author Conner Kasten
 */
public class EditSRActivity extends Activity {
	EditText mSRNumber;
	EditText mCustomer;
	EditText mModelNumber;
	EditText mSerialNumber;
	EditText mDescription;
	Button mEnter;
	
	Uri savedUri;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		mSRNumber = (EditText) findViewById(R.id.SRNumber);
		mCustomer = (EditText) findViewById(R.id.customerName);
		mModelNumber = (EditText) findViewById(R.id.modelNumber);
		mSerialNumber = (EditText) findViewById(R.id.serialNumber);
		mDescription = (EditText) findViewById(R.id.description);
		mEnter = (Button) findViewById(R.id.enter);
		
		//Bundle extras = getIntent().getExtras();
		
		//TODO: Make it possible to retrieve a saved instance from
		//the content provider. Example from vogella's tutorial:
		//savedUri = (savedInstanceState == null) ? null : 
		//	(Uri) savedInstanceState
	    //    .getParcelable(MyTodoContentProvider.CONTENT_ITEM_TYPE);
		
		// Or passed from the other activity
	    //if (extras != null) {
	    //  todoUri = extras
	    //      .getParcelable(MyTodoContentProvider.CONTENT_ITEM_TYPE);
		//
	    //  fillData(todoUri);
	    //}
		
		mEnter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_OK);
				finish();
			}
		});
	}
	
//TODO: Add methods for handling and interacting with the content provider

}
