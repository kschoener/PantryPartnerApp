package net.pantrypartner.pantrypartner;

import android.util.Log;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by kschoener on 3/5/16.
 */
public class GroceryItem{

    private String _title;
    private int[] _exp; // [MM , DD , YYYY]
    private int _count;
    private int _dateIntRep;


    public GroceryItem(String title, int[] exp, int count){
        _title = title;
        _exp = exp;
        _count = count;
//        Log.d("PP", "Title is: "+title);
//        Log.d("PP", "Expiration is: "+ Arrays.toString(exp));
//        Log.d("PP", "Count is: "+count);
//        Log.d("PP", "Attempting to assign Month...");
        String month = (_exp[0] < 10) ? ("0"+_exp[0]) : (""+_exp[0]);
//        Log.d("PP", "Month is: "+month+". Now attempting to assign Day...");
        String day = (_exp[1] < 10) ? ("0"+_exp[1]) : (""+_exp[1]);
//        Log.d("PP", "Day is: "+day+". Now attempting to assign temp...");
        String temp = ""+_exp[2]+month+day;

//        Log.d("PP", "_dateIntRep = "+Integer.parseInt(temp));
        _dateIntRep = Integer.parseInt(temp);
    }

    public String get_title(){return _title;}
    public int[] get_exp(){return _exp;}
    public int get_count(){return _count;}

    public int date_to_compare(){
        return _dateIntRep;
    }

    public String expToString(){
        return ""+_exp[0]+" / "+_exp[1]+" / "+_exp[2];
    }

    public String toString(){
        return _title;
    }
}
