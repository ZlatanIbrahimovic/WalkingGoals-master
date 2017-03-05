package com.android_examples.slidingtab_android_examplescom;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.OnClickWrapper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.Activity.RESULT_OK;
import static com.android_examples.slidingtab_android_examplescom.GoalContract.GoalDbHelper.getGoalById;

public class Tab_1_Activity extends Fragment implements PopupMenu.OnMenuItemClickListener{

    View view;
    private static int selectionIndex;
    private ArrayList<GoalsListDisplay> goals;
    private SimpleAdapter adapter;
    private List<Map<String, String>> data;
    private GoalsListDisplay todaysGoal;
    private long systemTime;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.activity_tab_1, container, false);
        this.view = view;

        initialiseCreateGoalButton();
        initialiseAddProgressButton();
        setSystemTime();

        retrieveGoals();
        initialiseList();
        retreiveTodaysGoal();
        displayTodaysGoal();
        initialiseTimer();

        return view;
    }


    private void setSystemTime() {
        systemTime = -2;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        if (prefs.getBoolean("testMode", false)){
            Calendar cal = Calendar.getInstance();
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date dates = formatter.parse(prefs.getString("keyname",null));
                cal.setTime(dates);
                cal.add(Calendar.MONTH, 1);
                systemTime = cal.getTimeInMillis();
            } catch (ParseException e) {e.printStackTrace();}

        }
        else{
            systemTime = System.currentTimeMillis();
        }

        System.out.println(systemTime);
    }


    private void initialiseTimer() {
        Timer timer = new Timer();
        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                TextView goalExpired = (TextView) view.findViewById(R.id.goalExpired);
                checkGoalReached();
                if (todaysGoal != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(systemTime);
                    if (DateUtils.isSameDay(cal, todaysGoal.getCalendarDate())) {
//                        System.out.println("1");
                        goalExpired.setVisibility(View.GONE);
                    }
                    else{
//                        System.out.println("2");
//                        goalExpired.setVisibility(View.VISIBLE);
                        todaysGoal = null;
                    }
                }
            }
        };
        timer.scheduleAtFixedRate(t,1000,1000);
    }

    private void checkGoalReached() {
        if(todaysGoal != null) {
            if (todaysGoal.getProgress() >= Double.valueOf(todaysGoal.getDistance())) {
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getContext())
                                .setSmallIcon(R.drawable.notification_icon)
                                .setContentTitle("Daily goal reached")
                                .setContentText(todaysGoal.getTitle() + ". " + todaysGoal.getDistance() + " " + todaysGoal.getUnits() + " walked.");
                // Creates an explicit intent for an Activity in your app
                Intent resultIntent = new Intent(getContext(), Tab_1_Activity.class);
                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
                // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(getActivity());
                // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                mBuilder.setSound(alarmSound);
                mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
                mBuilder.setLights(Color.RED, 3000, 3000);

                NotificationManager mNotificationManager =
                        (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                // mId allows you to update the notification later on.
                int mId = 0;
                mNotificationManager.notify(mId, mBuilder.build());
            }
        }
    }


    private void initialiseAddProgressButton() {
        ImageButton addProgressBtn = (ImageButton) view.findViewById(R.id.addProgress);
        addProgressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (todaysGoal != null) {
                    Activity host = (Activity) view.getContext();
                    Context context = host.getApplicationContext();
                    Intent intent = new Intent(context, AddProgressActivity.class);
                    intent.putExtra("id", todaysGoal.getId());
                    intent.putExtra("units", todaysGoal.getUnits());
//                    startActivity(intent);
                    startActivityForResult(intent,0);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        //Do your work here in ActivityA

        if(resultCode==RESULT_OK){
            Bundle MBuddle = data.getExtras();
            String progressAdded = MBuddle.getString("progressAdded");
            String id = MBuddle.getString("id");
            System.out.println("BACK IN TAB_1_ACTIVITY" + progressAdded);
            System.out.println("BACK IN TAB_1_ACTIVITY" + id);

            UndoProgress undoProgress = new UndoProgress(view, id, progressAdded, Tab_1_Activity.this);
            OnClickWrapper onClickWrapper = new OnClickWrapper("superactivitytoast", undoProgress);

            SuperActivityToast superActivityToast = new SuperActivityToast(getActivity(), SuperToast.Type.BUTTON);
            superActivityToast.setDuration(SuperToast.Duration.EXTRA_LONG);
            superActivityToast.setText("Progress added");
            superActivityToast.setButtonIcon(SuperToast.Icon.Dark.UNDO, "UNDO");
            superActivityToast.setOnClickWrapper(onClickWrapper);
            superActivityToast.show();
        }

    }


    /* Sets the List with an adapter and an onClick listener which displays the popup menu when a list item is tapped.
    * Populates the list with the goals*/
    protected void initialiseList() {
        ListView listView = (ListView) view.findViewById(R.id.custom_list);
        data = new ArrayList<Map<String, String>>();

        if(goals != null){
            for (int i = 0; i < goals.size(); i++) {
                Map<String, String> datum = new HashMap<String, String>(2);
                datum.put("title", goals.get(i).getTitle());
                datum.put("distance", goals.get(i).getDistance() + " " + goals.get(i).getUnits());
                data.add(datum);
            }
        }
        else{
            Map<String, String> datum = new HashMap<String, String>(2);
            data.add(datum);
        }

        adapter = new SimpleAdapter(view.getContext(), data, R.layout.list_row_layout, new String[]{"title", "distance"}, new int[]{R.id.title, R.id.distance});

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                selectionIndex = position;
                showPopupMenu(view);
            }
        });
        listView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        setSystemTime();
        retrieveGoals();
        initialiseList();
        retreiveTodaysGoal();
        if (todaysGoal != null) {
            displayTodaysGoal();
        }
    }

    /*Add onClick listener to floating action button, which switches to AddGoalActivity when tapped*/
    private void initialiseCreateGoalButton() {
        ImageButton createGoalButton = (ImageButton) view.findViewById(R.id.addGoal);
        createGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity host = (Activity) view.getContext();
                Context context = host.getApplicationContext();
                startActivity(new Intent(context, AddGoalActivity.class));
            }
        });
    }


    public void showPopupMenu(View v){
        PopupMenu popup = new PopupMenu(getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popup.getMenu());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean goalsEditable = prefs.getBoolean("editGoals", true);

        if (!goalsEditable){
            popup.getMenu().findItem(R.id.edit).setEnabled(false);
        }
        else{
            popup.getMenu().findItem(R.id.edit).setEnabled(true);
        }

        popup.show();
        popup.setOnMenuItemClickListener(this);
    }


    public boolean onMenuItemClick(MenuItem item){
        switch(item.getItemId()){
            case R.id.setTodaysGoal:
                setTodaysGoal();
                return true;
            case R.id.edit:
                editGoal();
                return true;
            case R.id.delete: //http://stackoverflow.com/questions/5127407/how-to-implement-a-confirmation-yes-no-dialogpreference
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete goal?")
                        .setMessage("This goal will disappear from the 'Other Goals' section.")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                deleteGoal();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
            default: return false;
        }
    }

    private void editGoal() {
        Activity host = (Activity) getContext();
        Context context = host.getApplicationContext();
        Intent intent = new Intent(context, EditGoalActivity.class);
        intent.putExtra("id", goals.get(selectionIndex).getId());
        intent.putExtra("title", goals.get(selectionIndex).getTitle());
        intent.putExtra("distance", goals.get(selectionIndex).getDistance());
        intent.putExtra("units", goals.get(selectionIndex).getUnits());
        startActivity(intent);
    }



    private void setTodaysGoal() {
        int selectedGoalId = goals.get(selectionIndex).getId();
        Context context = getActivity();
        GoalContract.GoalDbHelper mDbHelper = new GoalContract.GoalDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        setSystemTime();
        // If there is an existing goal
        if (todaysGoal != null) {
            // Update new goal with current date and transfer progress

            Double convertedDistance = new DistanceConversion(todaysGoal.getProgress(), todaysGoal.getUnits(), GoalContract.GoalDbHelper.getGoalUnits(db, selectedGoalId), view.getContext()).convert();
//            System.out.println("CONVERTED DISTANCE: " + convertedDistance);

            ContentValues cv2 = new ContentValues();
            cv2.put(GoalContract.Goal.COLUMN_NAME_DATE, systemTime);
            cv2.put(GoalContract.Goal.COLUMN_NAME_PROGRESS, convertedDistance);
            db.update(GoalContract.Goal.TABLE_NAME, cv2, "_id=" + selectedGoalId, null);

            // Clear progress of existing goal and transfer it to new goal. Update new goal with current date
            ContentValues cv = new ContentValues();
            cv.put(GoalContract.Goal.COLUMN_NAME_DATE, "0");
            cv.put(GoalContract.Goal.COLUMN_NAME_PROGRESS, 0.0);
            db.update(GoalContract.Goal.TABLE_NAME, cv, "_id=" + todaysGoal.getId(), null);
        } else { // If no existing goal
            // update new goal with current date
            ContentValues cv = new ContentValues();

            cv.put(GoalContract.Goal.COLUMN_NAME_DATE, systemTime);
            db.update(GoalContract.Goal.TABLE_NAME, cv, "_id=" + selectedGoalId, null);
        }

        retrieveGoals();
        initialiseList();
        retreiveTodaysGoal();
        displayTodaysGoal();
    }

    protected void retreiveTodaysGoal() {
//        System.out.println("Today's time:     "  + System.currentTimeMillis());
        Calendar systemDateCalendar = Calendar.getInstance();
        systemDateCalendar.setTimeInMillis(systemTime);
        if (goals != null) {
            for (GoalsListDisplay goal : goals) {
                System.out.println(goal.getDate());
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(goal.getDate());
//                System.out.println("Goal Time " + goal.getTitle() + "     :" + goal.getDate());
//                if (DateUtils.isToday(calendar)) {
                    if (DateUtils.isSameDay(calendar,systemDateCalendar)) {
                    todaysGoal = goal;
                    System.out.println(todaysGoal.getId());
                }
            }
        }
    }


    protected void displayTodaysGoal(){
        if (todaysGoal != null) {
            // Set goal title
            TextView goalTitleTxt = (TextView) view.findViewById(R.id.goalTitleTxt);
            goalTitleTxt.setText(todaysGoal.getTitle());

            // Set goal distance with units
            TextView todaysGoalDistanceTxt = (TextView) view.findViewById(R.id.todaysGoalDistanceTxt);
            todaysGoalDistanceTxt.setText(todaysGoal.getDistance() + " " + todaysGoal.getUnits());

            // Set progress recorded
            TextView todaysGoalRecordedTxt = (TextView) view.findViewById(R.id.todaysGoalRecordedTxt);
            todaysGoalRecordedTxt.setText(todaysGoal.getProgress() + " " + todaysGoal.getUnits() + " walked");

            // Set % text above progress bar
            TextView todaysGoalPercentageTxt = (TextView) view.findViewById(R.id.todaysGoalPercentageTxt);
            todaysGoalPercentageTxt.setText(String.format("%.1f", todaysGoal.getPercentage()) + "%");

            // Set progress bar
            ProgressBar goalProgressBar = (ProgressBar) view.findViewById(R.id.goalProgressBar);
            goalProgressBar.setProgress((int) todaysGoal.getPercentage());
        }
    }


    private void deleteGoal() {

        Context context = getActivity();
        GoalContract.GoalDbHelper mDbHelper = new GoalContract.GoalDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String selectedGoalId = Integer.toString(goals.get(selectionIndex).getId());

        GoalsListDisplay goalToDelete = getGoalById(db, Integer.parseInt(selectedGoalId));
        System.out.println(goalToDelete.toString());
        ContentValues goalToDeleteValues = new ContentValues();
        goalToDeleteValues.put(GoalContract.Goal.COLUMN_NAME_TITLE, goalToDelete.getTitle());
        goalToDeleteValues.put(GoalContract.Goal.COLUMN_NAME_DISTANCE, goalToDelete.getDistance());
        goalToDeleteValues.put(GoalContract.Goal.COLUMN_NAME_UNITS, goalToDelete.getUnits());
        goalToDeleteValues.put(GoalContract.Goal.COLUMN_NAME_PROGRESS, goalToDelete.getProgress());
        goalToDeleteValues.put(GoalContract.Goal.COLUMN_NAME_DATE, goalToDelete.getDate());

        db = mDbHelper.getWritableDatabase();
        db.delete("goal", "_id=?", new String[]{selectedGoalId});
        retrieveGoals();
        initialiseList();

        undoDelete undoDelete = new undoDelete(view, goalToDeleteValues, Tab_1_Activity.this);
        OnClickWrapper onClickWrapper = new OnClickWrapper("superactivitytoast", undoDelete);

        SuperActivityToast superActivityToast = new SuperActivityToast(getActivity(), SuperToast.Type.BUTTON);
        superActivityToast.setDuration(SuperToast.Duration.EXTRA_LONG);
        superActivityToast.setText("Goal Deleted");
        superActivityToast.setButtonIcon(SuperToast.Icon.Dark.UNDO, "UNDO");
        superActivityToast.setOnClickWrapper(onClickWrapper);
        superActivityToast.show();

    }


    private void clearTodaysGoal(){
        TextView goalTitleTxt = (TextView)view.findViewById(R.id.goalTitleTxt);
        goalTitleTxt.setText("");

        TextView todaysGoalDistanceTxt = (TextView)view.findViewById(R.id.todaysGoalDistanceTxt);
        todaysGoalDistanceTxt.setText("");

        TextView todaysGoalRecordedTxt = (TextView)view.findViewById(R.id.todaysGoalRecordedTxt);
        todaysGoalRecordedTxt.setText("");

        TextView todaysGoalPercentageTxt = (TextView)view.findViewById(R.id.todaysGoalPercentageTxt);
        todaysGoalPercentageTxt.setText("");

        todaysGoal = null;
    }

    public void clear()
    {
        SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(getContext());; // here you get your prefrences by either of two methods
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }


    protected void retrieveGoals() {
        Context context = getActivity();
        GoalContract.GoalDbHelper mDbHelper = new GoalContract.GoalDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ArrayList<GoalsListDisplay> goals = mDbHelper.getAllGoals(db, GoalContract.Goal.TABLE_NAME);
        this.goals = goals;
    }

}

class undoDelete implements SuperToast.OnClickListener{

    private View view;
    private ContentValues values;
    private Tab_1_Activity tab_1_activity;

    public undoDelete(View view, ContentValues values, Tab_1_Activity tab_1_activity){
        this.view = view;
        this.values = values;
        this.tab_1_activity = tab_1_activity;
    }

    @Override
    public void onClick(View view, Parcelable parcelable) {
        Context context = view.getContext();
        GoalContract.GoalDbHelper mDbHelper = new GoalContract.GoalDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.insert(GoalContract.Goal.TABLE_NAME, null, values);
        tab_1_activity.retrieveGoals();
        tab_1_activity.initialiseList();
    }
}

class UndoProgress implements SuperToast.OnClickListener{

    private View view;
    private double negatedProgress;
    private int id;
    private Tab_1_Activity tab_1_activity;

    public UndoProgress(View view, String id, String progressAdded, Tab_1_Activity tab_1_activity) {
        this.view = view;
        this.id = Integer.valueOf(id);
        this.negatedProgress = Double.valueOf(progressAdded) * -1.0;
        this.tab_1_activity = tab_1_activity;
    }

    @Override
    public void onClick(View view, Parcelable parcelable) {
        Context context = view.getContext();
        GoalContract.GoalDbHelper mDbHelper = new GoalContract.GoalDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        mDbHelper.increaseProgress(db, id, negatedProgress);
        tab_1_activity.retrieveGoals();
        tab_1_activity.initialiseList();
        tab_1_activity.retreiveTodaysGoal();
        tab_1_activity.displayTodaysGoal();
    }
}