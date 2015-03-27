package com.detroitteatime.caffeinecounter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DrinkDialogNonLinear extends Activity {

    private Button shortSize, tall, grande, venti, info;
    protected String type = null;
    private int[] mgCaff;
    private int ventiValue = 20;
    private boolean isFrozen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_starbucks);

        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        mgCaff = intent.getIntArrayExtra(DataBaseHelper.MGCAFF);
        isFrozen = intent.getBooleanExtra(DataBaseHelper.IS_FROZEN, false);

        info = (Button) findViewById(R.id.infoS);
        info.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DrinkDialogNonLinear.this, InfoView.class);
                intent.putExtra("drink_type", type);

                startActivity(intent);

            }
        });


        shortSize = (Button) findViewById(R.id.shortSize);
        shortSize.getBackground().setColorFilter(Color.parseColor(HelperMethodHolder.BUTTON_COLOR), PorterDuff.Mode.MULTIPLY);

        if (mgCaff[0] == -1) {
            shortSize.setVisibility(View.GONE);

        } else {

            shortSize.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    HelperMethodHolder.addDrink(8, type, mgCaff[0], new DataBaseHelper(DrinkDialogNonLinear.this.getApplicationContext()));
                    finish();
                }
            });

        }

        tall = (Button) findViewById(R.id.tall);
        tall.getBackground().setColorFilter(Color.parseColor(HelperMethodHolder.BUTTON_COLOR), PorterDuff.Mode.MULTIPLY);

        if (mgCaff[1] == -1) {
            tall.setVisibility(View.GONE);

        } else {

            tall.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    HelperMethodHolder.addDrink(12, type, mgCaff[1], new DataBaseHelper(DrinkDialogNonLinear.this.getApplicationContext()));
                    finish();
                }
            });
        }


        grande = (Button) findViewById(R.id.grande);
        grande.getBackground().setColorFilter(Color.parseColor(HelperMethodHolder.BUTTON_COLOR), PorterDuff.Mode.MULTIPLY);

        if (mgCaff[2] == -1) {
            grande.setVisibility(View.GONE);

        } else {


            grande.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    HelperMethodHolder.addDrink(16, type, mgCaff[2], new DataBaseHelper(DrinkDialogNonLinear.this.getApplicationContext()));
                    finish();
                }
            });

        }

        venti = (Button) findViewById(R.id.venti);
        venti.getBackground().setColorFilter(Color.parseColor(HelperMethodHolder.BUTTON_COLOR), PorterDuff.Mode.MULTIPLY);

        if (mgCaff[3] == -1) {
            venti.setVisibility(View.GONE);

        } else {

	        	/*if (mgCaff[0] == -1){

		        	RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
		        	        ViewGroup.LayoutParams.WRAP_CONTENT);

		        	p.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		        	p.addRule(RelativeLayout.ALIGN_BASELINE, R.id.grande);
		        	
		        	
		        	
		        	venti.setLayoutParams(p);
	        	}*/


            if (isFrozen) {
                venti.setText("24 oz (Venti)");

            }


            venti.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    HelperMethodHolder.addDrink(ventiValue, type, mgCaff[3], new DataBaseHelper(DrinkDialogNonLinear.this.getApplicationContext()));
                    finish();
                }
            });

        }

    }//end onCreate()


}
