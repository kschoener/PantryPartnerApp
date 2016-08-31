package net.pantrypartner.pantrypartner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.util.Calendar;

public class FoodCreator extends Activity {

    private EditText name , count;
    private NumberPicker month, day, year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_creator);

        name = (EditText) findViewById(R.id.inputGroceryTitle);

        count = (EditText) findViewById(R.id.groceryCount);

        month = (NumberPicker) findViewById(R.id.expMonthPicker);
        month.setMinValue(1);
        month.setMaxValue(12);

        day = (NumberPicker) findViewById(R.id.expDayPicker);
        day.setMinValue(1);
        day.setMaxValue(31);

        Calendar cal = Calendar.getInstance();
        year = (NumberPicker) findViewById(R.id.expYrPicker);
        year.setMinValue(cal.get(Calendar.YEAR));
        year.setMaxValue(cal.get(Calendar.YEAR)+25);

        Button cancelButton = (Button) findViewById(R.id.cancelGroceryButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button saveButton = (Button) findViewById(R.id.saveGroceryButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = name.getText().toString() + "," + Integer.parseInt(count.getText().toString())
                        + "," + month.getValue() + "," + day.getValue() + "," + year.getValue();

                //now store this on Azure

                Intent mainActivity = new Intent(v.getContext(), MainActivity.class);
                mainActivity.putExtra("newGroceryItem", data);
                Log.d("PP", "Sent extra is: " + data);
                startActivity(mainActivity);

            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_food_creator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
