package com.android_examples.slidingtab_android_examplescom;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by liamdiamond on 01/02/2017.
 */

public class GoalsListDisplay {
    private int id;
    private String title;
    private String distance;
    private String units;
    private double progress;
    private long date;
    private float percentage;
    private Calendar calendarDate;


    public GoalsListDisplay (int id, String title, String distance, String units, double progress, long date) {
        this.id = id;
        this.title = title;
        this.distance = distance;
        this.units = units;
        this.progress = progress;
        this.date = date;
        calendarDate = Calendar.getInstance();
        calendarDate.setTimeInMillis(date);
    }

    public String toString(){
        return "_id " + id + " Title "+ title + " Distance " + distance + " Units " + units + " Progress " + progress + " Date " + date;
    }

    public String getTitle(){
        return title;
    }

    public String getDistance(){
        return distance;
    }

    public Calendar getCalendarDate(){
        return calendarDate;
    }

    public String readableDateFormat(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("EE");
        String dayOfWeek = dateFormat.format(calendarDate.getTime());

        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        String day = dayFormat.format(calendarDate.getTime());

        String month = new SimpleDateFormat("MMM").format(calendarDate.getTime());
        return dayOfWeek + ", " + month + " " + day;
    }

    public String getUnits(){
        return units;
    }

    public int getId(){
        return id;
    }

    public double getPercentage(){
        return (float) ((100  * progress) / Float.parseFloat(distance));
    }

    public long getDate(){
        return date;
    }

    public double getProgress(){
        return progress;
    }

    public void setProgress(double progress){
        this.progress = progress;
    }

    public void setDate(long date){
        this.date = date;
    }

}
