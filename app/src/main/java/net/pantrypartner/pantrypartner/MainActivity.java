package net.pantrypartner.pantrypartner;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import com.microsoft.windowsazure.mobileservices.*;


public class MainActivity extends Activity {

    private final String fileName = "pantryItems.dat";
    private ArrayList<GroceryItem> mGroceryList;
    private StableArrayAdapter<GroceryItem> mStableGroceryList;
    private AlarmManager alarmManager;


    //private MobileServiceClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /*try{
            mClient = new MobileServiceClient(
                    "https://pantrypartnerbackend.azurewebsites.net",
                    this
            );
        }catch (Exception e){
            Log.e("PP", "Couldn't initialize client");
        }*/

        //the data base for all of the items
        mGroceryList = new ArrayList<>();
        //alarm manager to notify when something is expired
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        //create random dates for items
        this.randomPopulation();
        //check if anything was passed on
        this.checkExtra();
        //if there are items saved, they are added
        this.loadLocal();

        this.orderThePantry();

        mStableGroceryList = new StableArrayAdapter(this, R.layout.list_item, mGroceryList);

        setContentView(R.layout.activity_main);

        this.setUpListView();


        Button addGrocery = (Button) findViewById(R.id.addGroceryButton);
        addGrocery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addActivity = new Intent(v.getContext(), FoodCreator.class);
                startActivity(addActivity);
            }
        });

    }




    private void randomPopulation() {
        String[] names = {"Cocoa Puffs", "Cheerios", "Pringles", "Pizza", "Chicken", "Ham",
                "Turkey", "Oatmeal", "Beans", "Milk", "Water", "Ketchup", "Pretzel", "Cookies", "Sushi"};
        Random rand = new Random();
        for (int i = 0; i < names.length; i++) {
            int[] randomDate = {rand.nextInt(32), rand.nextInt(32), 2017};
//            Log.d("PP", Arrays.toString(randomDate));
//            Log.d("PP", "Attempting to create GroceryItem...");
            mGroceryList.add(new GroceryItem(names[i], randomDate, 1));
//            Log.d("PP", "Created GroceryItem!");
        }
    }




    private void checkExtra(){
        Intent myIntent = getIntent();
        if (myIntent.hasExtra("newGroceryItem")){
//            Log.d("PP", "Received extra is: "+myIntent.getStringExtra("newGroceryItem"));
            this.addGroceryItem(myIntent.getStringExtra("newGroceryItem"));
        }
    }




    private void orderThePantry() {
        int size = mGroceryList.size();
        for (int i = 0; i < size; i++) {
            GroceryItem tempMin = null;
            int index = -1;
            for (int j = i; j < mGroceryList.size(); j++) {
                if (tempMin == null) {
                    tempMin = mGroceryList.get(j);
                    index = j;
                } else if (mGroceryList.get(j).date_to_compare() < tempMin.date_to_compare()) {
                    tempMin = mGroceryList.get(j);
                    index = j;
                }
            }
            mGroceryList.add(i, mGroceryList.remove(index));
        }
    }





    private void loadLocal(){
//        StringBuffer datax = new StringBuffer("");
        try{
            FileInputStream fis = openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis) ;
            BufferedReader buffreader = new BufferedReader(isr);

            String readString = buffreader.readLine();
            while ( readString != null ) {
                //datax.append(readString);
                this.addGroceryItem(readString);
                readString = buffreader.readLine();
            }
            fis.close();

        }catch (Exception e){
           return;
        }
    }




    private void setUpListView(){
        ListView mListView = (ListView) findViewById(R.id.pantryList);
        mListView.setAdapter(mStableGroceryList);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder adb=new AlertDialog.Builder(MainActivity.this);
                adb.setTitle("Remove Item");
                adb.setMessage("Are you sure you want to remove?");
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mGroceryList.remove(positionToRemove);
                        mStableGroceryList.notifyDataSetChanged();
                    }});
                adb.show();

                return false;
            }
        });
    }




    private void addGroceryItem(String complexData){
        String[] data = complexData.split(",");
//        Log.d("PP", "addGroceryItem received: "+data.toString());
        //0 is title, 1 is count, 2 is month, 3 is day, 4 is year
        int count = Integer.parseInt(data[1]);
        int[] date = {Integer.parseInt(data[2]),Integer.parseInt(data[3]),Integer.parseInt(data[4])};
        GroceryItem temp = new GroceryItem(data[0], date, count);
        mGroceryList.add(temp);

        Calendar calendar = Calendar.getInstance();
        //sets Year, Month, Day
        calendar.set(Integer.parseInt(data[4]), Integer.parseInt(data[2]), Integer.parseInt(data[3]));

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        // cal.add(Calendar.SECOND, 5);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

    }




    @Override
    public void onStop(){
        String save = "";
        for(int i = 0; i < mGroceryList.size(); i++){
            save += mGroceryList.get(i).get_title() + "," + mGroceryList.get(i).get_count()
                + "," + mGroceryList.get(i).get_exp()[0] + "," + mGroceryList.get(i).get_exp()[1] +
                   "," + mGroceryList.get(i).get_exp()[2] + '\n';
        }
        try{
        FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
        fos.write(save.getBytes());
        fos.close();
        }catch (Exception e){
            super.onStop();
            return;
        }
        super.onStop();
    }









    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
