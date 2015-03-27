package com.detroitteatime.caffeinecounter;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class EditType extends Activity implements
        AdapterView.OnItemSelectedListener {

    private TextView type, amountPrompt;
    private EditText caffPer, editType;

    private Button enter;
    private Button delete;
    private String typeString;
    private String amountString;
    private DataBaseHelper helper;
    private String typeName;
    private String category;
    private String[] items;
    private EditText[] editArray = new EditText[2];
    private boolean isMetric;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_type);

        SharedPreferences settings = getSharedPreferences("prefs", 0);
        isMetric = settings.getBoolean(DrinkDialog.METRIC, false);

        if (isMetric) {
            amountPrompt = (TextView) findViewById(R.id.amountPrompt);
            amountPrompt.setText("Caffeine per ml");
        }

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        typeName = extras.getString("type");

        type = (TextView) findViewById(R.id.enterType);
        editType = (EditText) findViewById(R.id.enterType);
        caffPer = (EditText) findViewById(R.id.enterAmount);

        editArray[0] = editType;
        editArray[1] = caffPer;

        enter = (Button) findViewById(R.id.enterNewType);
        enter.getBackground().setColorFilter(Color.parseColor("#663033"),
                PorterDuff.Mode.MULTIPLY);
        delete = (Button) findViewById(R.id.deleteType);
        delete.getBackground().setColorFilter(Color.parseColor("#663033"),
                PorterDuff.Mode.MULTIPLY);

        helper = new DataBaseHelper(EditType.this.getApplicationContext());

        helper.open(DataBaseHelper.READABLE);

        double caff = helper.getCaffPer(typeName);

        if (isMetric)
            caff = caff / 29.57353;


        caffPer.setText(String.valueOf(caff));
        items = HelperMethodHolder.getCategoryList(
                this.getApplicationContext(), helper);
        category = HelperMethodHolder.getCategory(typeName, helper);
        helper.close();

        type.setText("Enter Type");
        editType.setText(typeName);

        Spinner spin = (Spinner) findViewById(R.id.editDrinkSpinner);
        spin.setOnItemSelectedListener(this);

        ArrayAdapter<String> aa = new ArrayAdapter<String>(
                this.getApplicationContext(), R.layout.my_spinner, items);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);
        spin.getBackground().setColorFilter(Color.parseColor("#663033"),
                PorterDuff.Mode.MULTIPLY);

        int position = HelperMethodHolder.getPosition(items, category);
        spin.setSelection(position);

        enter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (HelperMethodHolder.checkEmpty(editArray)) {
                    typeString = type.getText().toString();
                    amountString = caffPer.getText().toString();
                    double amnt = Double.valueOf(amountString);
                    String newType = editType.getText().toString();

                    helper = new DataBaseHelper(EditType.this
                            .getApplicationContext());
                    helper.open(DataBaseHelper.WRITEABLE);

                    if (isMetric) amnt = amnt * 29.57353;


                    helper.updateType(typeName, amnt, newType, category);
                    helper.close();
                    finish();

                } else {
                    Toast.makeText(EditType.this, "Fields cannot be empty",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        EditType.this);
                builder.setMessage(
                        "Are you sure you want to delete this entry?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {

                                        helper.open(DataBaseHelper.WRITEABLE);
                                        helper.deleteType(typeName);
                                        helper.close();
                                        finish();

                                        dialog.cancel();
                                        EditType.this.finish();
                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
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