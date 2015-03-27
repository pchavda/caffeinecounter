package com.detroitteatime.caffeinecounter;

import android.app.Activity;
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
import android.widget.Toast;

public class NewTypeDialog extends Activity implements AdapterView.OnItemSelectedListener {

    private EditText type;
    private EditText caffPer;
    private Button enter;
    private String typeString;
    private String amountString, name;
    private String[] items;
    private DataBaseHelper helper;
    private String category;
    private EditText[] editArray = new EditText[2];
    private double caffPerOz;
    private int kind;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_type);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString("DRINK_NAME");
            if (name == null)
                name = "Enter drink name";
            caffPerOz = extras.getDouble("CAFF_PER", 0);

            kind = extras.getInt(DataBaseHelper.QUERY_TYPE);

        }

        helper = new DataBaseHelper(this.getApplicationContext());

        Spinner spin = (Spinner) findViewById(R.id.editDrinkSpinner);
        spin.setOnItemSelectedListener(this);
        spin.getBackground().setColorFilter(Color.parseColor("#663033"),
                PorterDuff.Mode.MULTIPLY);

        helper.open(DataBaseHelper.READABLE);
        items = HelperMethodHolder.getCategoryList(this.getApplicationContext(), helper);
        helper.close();

        ArrayAdapter<String> aa = new ArrayAdapter<String>(this.getApplicationContext(), android.R.layout.simple_spinner_item, items);
        aa.setDropDownViewResource(R.layout.my_spinner);
        spin.setAdapter(aa);


        switch (kind) {


            case 1:
                spin.setSelection(1);
                break;

            case 2:
                spin.setSelection(4);
                break;

            case 3:
                spin.setSelection(0);
                break;

            case 4:
                spin.setSelection(2);
                break;

            case 5:
                spin.setSelection(3);
                break;


        }


        Button delete = (Button) findViewById(R.id.deleteType);
        delete.setVisibility(View.GONE);

        type = (EditText) findViewById(R.id.enterType);
        caffPer = (EditText) findViewById(R.id.enterAmount);

        type.setText(name);
        caffPer.setText(String.valueOf(caffPerOz));

        editArray[0] = type;
        editArray[1] = caffPer;

        enter = (Button) findViewById(R.id.enterNewType);
        enter.getBackground().setColorFilter(Color.parseColor("#663033"), PorterDuff.Mode.MULTIPLY);
        enter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                typeString = type.getText().toString();
                amountString = caffPer.getText().toString();

                double amnt = Double.valueOf(amountString);

                if (HelperMethodHolder.checkEmpty(editArray)) {
                    helper.open(DataBaseHelper.WRITEABLE);

                    String[] test = helper.getType(typeString);

                    if (test[0] == null) {
                        helper.insertType(typeString, amnt, category);

                    } else {
                        Toast.makeText(getApplicationContext(), "Name already used. Chose another.", Toast.LENGTH_SHORT).show();
                    }

                    helper.close();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                }

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
