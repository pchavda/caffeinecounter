package com.detroitteatime.caffeinecounter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class EditTypeStarbucks extends Activity implements AdapterView.OnItemSelectedListener {

    private TextView type, shrtPrompt, tallPrompt, grandePrompt, ventiPrompt;
    private EditText shrt, tall, grande, venti;
    private Button enter;
    private Button delete;
    private String typeString;
    private String amountString;
    private DataBaseHelper helper;
    private String typeName;
    private String category;
    private int[] mgCaff;
    private String[] items;
    private ArrayList<EditText> editTexts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_type_starbucks);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        typeName = extras.getString("type");
        mgCaff = extras.getIntArray(DataBaseHelper.MGCAFF);
        helper = new DataBaseHelper(EditTypeStarbucks.this.getApplicationContext());

        helper.open(DataBaseHelper.READABLE);
        items = HelperMethodHolder.getCategoryList(this.getApplicationContext(), helper);
        category = HelperMethodHolder.getCategory(typeName, helper);
        helper.close();


        shrtPrompt = (TextView) findViewById(R.id.shortPrompt);
        tallPrompt = (TextView) findViewById(R.id.tallPrompt);
        grandePrompt = (TextView) findViewById(R.id.grandePrompt);
        ventiPrompt = (TextView) findViewById(R.id.ventiPrompt);

        type = (TextView) findViewById(R.id.typePrompt);
        type.setText(typeName);

        editTexts = new ArrayList<EditText>();

        shrt = (EditText) findViewById(R.id.enterShort);

        if (mgCaff[0] != -1) {
            shrt.setText(String.valueOf(mgCaff[0]));
            editTexts.add(shrt);

        } else {
            shrt.setVisibility(View.INVISIBLE);
            shrtPrompt.setVisibility(View.INVISIBLE);
        }


        tall = (EditText) findViewById(R.id.enterTall);

        if (mgCaff[1] != -1) {
            tall.setText(String.valueOf(mgCaff[1]));
            editTexts.add(tall);
        } else {
            tall.setVisibility(View.INVISIBLE);
            tallPrompt.setVisibility(View.INVISIBLE);
        }

        grande = (EditText) findViewById(R.id.enterGrande);

        if (mgCaff[2] != -1) {
            grande.setText(String.valueOf(mgCaff[2]));
            editTexts.add(grande);
        } else {
            grande.setVisibility(View.INVISIBLE);
            grandePrompt.setVisibility(View.INVISIBLE);
        }

        venti = (EditText) findViewById(R.id.enterVenti);

        if (mgCaff[3] != -1) {
            venti.setText(String.valueOf(mgCaff[3]));
            editTexts.add(venti);
        } else {
            venti.setVisibility(View.INVISIBLE);
            ventiPrompt.setVisibility(View.INVISIBLE);
        }

        enter = (Button) findViewById(R.id.enterNewType);
        enter.getBackground().setColorFilter(Color.parseColor("#663033"), PorterDuff.Mode.MULTIPLY);
        delete = (Button) findViewById(R.id.deleteType);
        delete.getBackground().setColorFilter(Color.parseColor("#663033"), PorterDuff.Mode.MULTIPLY);


        Spinner spin = (Spinner) findViewById(R.id.spinnerStarbucks);
        spin.setOnItemSelectedListener(this);
        ArrayAdapter<String> aa = new ArrayAdapter<String>(this.getApplicationContext(), R.layout.my_spinner, items);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);
        spin.getBackground().setColorFilter(Color.parseColor("#663033"), PorterDuff.Mode.MULTIPLY);

        int position = HelperMethodHolder.getPosition(items, category);
        spin.setSelection(position);

        enter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                helper.open(DataBaseHelper.WRITEABLE);
                typeString = type.getText().toString();


                if (HelperMethodHolder.checkEmpty(editTexts)) {

                    if (editTexts.contains(shrt))
                        mgCaff[0] = Integer.valueOf(shrt.getText().toString());


                    if (editTexts.contains(tall))
                        mgCaff[1] = Integer.valueOf(tall.getText().toString());


                    if (editTexts.contains(grande))
                        mgCaff[2] = Integer.valueOf(grande.getText().toString());


                    if (editTexts.contains(venti))
                        mgCaff[3] = Integer.valueOf(venti.getText().toString());

                    helper.updateStarbucksType(typeName, category, mgCaff);
                    helper.close();
                    finish();

                } else {
                    Toast.makeText(EditTypeStarbucks.this, "Cannot have empty fields.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(EditTypeStarbucks.this);
                builder.setMessage("Are you sure you want to delete this entry?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                helper.open(DataBaseHelper.WRITEABLE);
                                helper.deleteType(typeName);
                                helper.close();
                                finish();

                                dialog.cancel();
                                EditTypeStarbucks.this.finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                               long arg3) {
        category = items[arg2];

    }


    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

}

