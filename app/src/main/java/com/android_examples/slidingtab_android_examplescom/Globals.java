package com.android_examples.slidingtab_android_examplescom;

import android.app.Application;

public class Globals extends Application {

    private int currentGoalId =0;
    private String currentGoalUnits = "";


    public int getCurrentGoalId(){
        return this.currentGoalId;
    }

    public void setCurrentGoalId(int currentGoalId){
        this.currentGoalId = currentGoalId;
    }

    public void setCurrentGoalUnits(String currentGoalUnits){
        this.currentGoalUnits = currentGoalUnits;
    }

    public String getCurrentGoalUnits(){
        return currentGoalUnits;
    }

}