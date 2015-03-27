package com.detroitteatime.caffeinecounter;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class SodaList extends ListActivity {

    //make sure these are in the same order as in Drink.java
    private String[] items;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.soda_list);

        items = HelperMethodHolder.setDrinkList(this.getApplicationContext());


        setListAdapter(new ArrayAdapter<String>(this,
                R.layout.drink_list_row,
                items));

    }


    public void onListItemClick(ListView parent, View v, int position,
                                long id) {

        TextView tv = (TextView) v.findViewById(R.id.type_name);
        String type = tv.getText().toString();

        Intent intent = new Intent(SodaList.this, DrinkDialog.class);
        intent.putExtra("type", type);
        startActivity(intent);
        finish();

    }


}
