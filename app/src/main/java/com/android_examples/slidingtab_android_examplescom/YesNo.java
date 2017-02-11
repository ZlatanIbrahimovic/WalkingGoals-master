package com.android_examples.slidingtab_android_examplescom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.widget.Toast;

public class YesNo extends DialogPreference
{
    public YesNo(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    protected void onClick()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Delete goal history?");
        dialog.setMessage("This action will delete all your goals. Are you sure you want to continue?");
        dialog.setCancelable(true);
        dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //reset database
                Toast.makeText(getContext(), "Application reset!", Toast.LENGTH_SHORT).show();
                GoalContract.GoalDbHelper mDbHelper = new GoalContract.GoalDbHelper(getContext());
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                mDbHelper.clearHistory(db);
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dlg, int which)
            {
                dlg.cancel();
            }
        });

        AlertDialog al = dialog.create();
        al.show();
    }
}