package com.android_examples.slidingtab_android_examplescom;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by liamdiamond on 11/02/2017.
 */

public class DistanceConversion extends AppCompatActivity {

    private Double convertFromValue;
    private String convertFromUnits;
    private String convertToUnits;
    private double metersInStep;

    public DistanceConversion(Double convertFromValue, String convertFromUnits, String convertToUnits, Context context){
        this.convertFromValue = convertFromValue;
        this.convertFromUnits = convertFromUnits;
        this.convertToUnits = convertToUnits;
        getMetersInStep(context);
    }

    public double convert() {
        if (convertFromUnits.equals("Meters")){
            return convertFromMeters();
        }
        else if (convertFromUnits.equals("Kilometers")){
            return convertFromKilometers();
        }
        else if (convertFromUnits.equals("Yards")){
            return convertFromYards();
        }
        else if (convertFromUnits.equals("Feet")){
            return convertFromFeet();
        }
        else if (convertFromUnits.equals("Steps")){
            return convertFromSteps();
        }
        else{
            return 1.00;
        }
    }

    private double convertFromSteps() {
        if (convertToUnits.equals("Meters")){
            return round(convertFromValue*metersInStep, 2);
        }
        else if (convertToUnits.equals("Kilometers")){
            return round((convertFromValue*metersInStep)*1000, 2);
        }
        else if (convertToUnits.equals("Yards")){
            return round((convertFromValue*metersInStep)*1.09361, 2);
        }
        else if (convertToUnits.equals("Feet")){
            return round((convertFromValue*metersInStep)*3.28084, 2);
        }
        else{
            return 1.00;
        }
    }

    private double convertFromFeet() {
        if (convertToUnits.equals("Meters")){
            return round(convertFromValue*0.3048, 2);
        }
        else if (convertToUnits.equals("Kilometers")){
            return round(convertFromValue*0.0003048, 2);
        }
        else if (convertToUnits.equals("Yards")){
            return round(convertFromValue*0.333333, 2);
        }
        else if (convertToUnits.equals("Steps")){
            return round(convertFromValue*0.3048/metersInStep, 2);
        }
        else{
            return 1.00;
        }
    }

    private double convertFromYards() {
        if (convertToUnits.equals("Meters")){
            return round(convertFromValue*0.9144, 2);
        }
        else if (convertToUnits.equals("Kilometers")){
            return round(convertFromValue*0.0009144, 2);
        }
        else if (convertToUnits.equals("Feet")){
            return round(convertFromValue*3, 2);
        }
        else if (convertToUnits.equals("Steps")){
            return round((convertFromValue*0.9144)/metersInStep, 2);
        }
        else{
            return 0.00;
        }
    }

    private double convertFromKilometers() {
        if (convertToUnits.equals("Meters")){
            return round(convertFromValue*1000, 2);
        }
        else if (convertToUnits.equals("Yards")){
            return round(convertFromValue*1093.61, 2);
        }
        else if (convertToUnits.equals("Feet")){
            return round(convertFromValue*3280.84, 2);
        }
        else if (convertToUnits.equals("Steps")){
            return round((convertFromValue*1000/metersInStep), 2);
        }
        else{
            return 1.00;
        }
    }

    private double convertFromMeters() {
        if (convertToUnits.equals("Kilometers")){
            return round(convertFromValue*0.001, 2);
        }
        else if (convertToUnits.equals("Yards")){
            return round(convertFromValue*1.09361, 2);
        }
        else if (convertToUnits.equals("Feet")){
            return round(convertFromValue*3.28084, 2);
        }
        else if (convertToUnits.equals("Steps")){
            return round(convertFromValue/metersInStep, 2);
        }
        else{
            return 1.00;
        }
    }


    public void getMetersInStep(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String stepMapping = prefs.getString("stepMapping",null);
        try {
            metersInStep = Double.parseDouble(stepMapping);
        } catch (NumberFormatException e) {
            metersInStep = 0.762;
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
