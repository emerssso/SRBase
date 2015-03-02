//This Software is distributed under The Apache License, Version 2.0
//The License is available at http://www.apache.org/licenses/LICENSE-2.0
package com.gmail.emerssso.srbase;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.gmail.emerssso.srbase.database.PartTable;
import com.gmail.emerssso.srbase.database.SRContentProvider;
import com.gmail.emerssso.srbase.models.Part;

/**
 * The EditPartActivity class implements an activity which provides
 * a form for users to enter new parts to be associated with a particular
 * SR, and certain information associated with that part.
 */
public class EditPartActivity extends EditSubItemActivity {

    /**
     * The part number.
     */
    private EditText partNumber;

    /**
     * The number of parts in question.
     */
    private EditText partQuantity;

    /**
     * The part source (i.e. work, home, Japan).
     */
    private EditText partSource;

    /**
     * The part description.
     */
    private EditText partDescription;

    /**
     * Indicates whether the part was used or not.
     */
    private CheckBox partUsed;
    
    private Part part = new Part();

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.edit_part_activity);

        partNumber = (EditText) findViewById(R.id.part_number);
        partQuantity = (EditText) findViewById(R.id.part_quantity);
        partSource = (EditText) findViewById(R.id.part_source);
        partDescription = (EditText) findViewById(R.id.part_description);
        partUsed = (CheckBox) findViewById(R.id.part_used);
        /* The confirm button to save data. */
        Button confirm = (Button) findViewById(R.id.part_confirm);

        Bundle extras = getIntent().getExtras();

        savedUri = (bundle == null) ? null :
                (Uri) bundle.getParcelable(
                        SRContentProvider.PART_CONTENT_ITEM_TYPE);

        if (extras != null) {
            part.setSrId(extras.getString(PartTable.COLUMN_SR_ID));

            savedUri = extras
                    .getParcelable(SRContentProvider.PART_CONTENT_ITEM_TYPE);
        }

        if (savedUri != null)
            fillData(savedUri);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (partNumber.getText().toString().length() == 0) {
                    Toast.makeText(EditPartActivity.this, getString(R.string.part_number_missing),
                            Toast.LENGTH_LONG).show();
                } else {
                    saveState();
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    /**
     * Fill data from the database entry at Uri into the form.
     *
     * @param uri the Source of data to be loaded.
     */
    private void fillData(Uri uri) {
        Cursor cursor = getContentResolver()
                .query(uri, PartTable.COLUMNS, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();

            Part part = Part.fromCursor(cursor);

            partNumber.setText(part.getPartNumber());

            if (part.quantityAvailable())
                partQuantity.setText(part.getQuantity());

            if (part.sourceAvailable())
                partSource.setText(part.getSource());

            if (part.descriptionAvailable())
                partDescription.setText(part.getDescription());

            partUsed.setChecked(part.isUsed());

            cursor.close();
        }
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putParcelable(
                SRContentProvider.PART_CONTENT_ITEM_TYPE, savedUri);
    }

    /**
     * Save the state of the form into the database.
     */
    private void saveState() {

        if (partNumber.length() == 0)
            return;

        part.setPartNumber(partNumber.getText().toString());
        part.setQuantity(partQuantity.getText().toString());
        part.setSource(partSource.getText().toString());
        part.setDescription(partDescription.getText().toString());
        part.setUsed(partUsed.isChecked());

        if (savedUri == null) {
            savedUri = getContentResolver()
                    .insert(SRContentProvider.PART_CONTENT_URI, part.toContentValues());
        } else {
            getContentResolver().update(savedUri, part.toContentValues(), null, null);
        }
    }

    @Override
    protected Intent createIntentForParent() {
        Intent i = new Intent(this, ViewSRActivity.class);
        Uri todoUri = Uri.parse(SRContentProvider.SR_CONTENT_URI + "/" + part.getSrId());
        i.putExtra(SRContentProvider.SR_CONTENT_ITEM_TYPE, todoUri);
        return i;
    }
}