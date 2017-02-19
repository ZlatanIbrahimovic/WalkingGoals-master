package com.android_examples.slidingtab_android_examplescom;

/**
 * Created by liamdiamond on 19/02/2017.
 */

public class ActivityGoalStatistic {
    private GoalsListDisplay goal;
    private String statisticsHeading;

    public ActivityGoalStatistic(GoalsListDisplay goal, String statisticsHeading ){
        this.goal = goal;
        this.statisticsHeading = statisticsHeading;
    }

    public GoalsListDisplay getGoal() {
        return goal;
    }

    public String getStatisticsHeading() {
        return statisticsHeading;
    }
}
