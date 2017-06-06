// MineField.java
// Author:  Alex Chochia
// Date:    05/30/2015
// Purpose:
//      The view of the game. Creates an environment for the user to play MineSweeper
//      Uses a controller to handle all logic


package alex_chochia.minesweeper;

// MARK - imports
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MineField extends ActionBarActivity
{
    // MARK - instance variables
    private GameMaster controller;
    private SharedPreferences prefs;

    public static String USER_LEARNED_GAME = "USER_LEARNED_GAME";

    // MARK - Life cycle methods
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_field);

        this.controller = new GameMaster(getApplicationContext(), 10);

        prefs = getSharedPreferences(getPackageName(), 0);
        if (!prefs.getBoolean(USER_LEARNED_GAME, false))
        {
            displayInfo(null);
        }

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        setMineField();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_mine_field, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy()
    {
        this.controller.message.cancel();
        super.onDestroy();
    }

    // MARK - onClick methods
    public void restart(MenuItem menuItem)
    {
        controller.restart();
    }

    public void displayInfo(MenuItem menuItem)
    {
        if (menuItem != null && !prefs.getBoolean(USER_LEARNED_GAME, false))
        {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(USER_LEARNED_GAME, true);
            edit.apply();
        }

        InstructionSet is = new InstructionSet();
        is.show(getFragmentManager(), "InstructionSetDialogFragment");
    }

    // MARK - view creation
    private void setMineField()
    {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService
                (Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        int size = dm.widthPixels / 9;

        TableLayout layout =  (TableLayout) findViewById(R.id.mine_field);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) layout.getLayoutParams();
        params.setMargins(size/2, size/2, size/2, size/2);
        layout.setLayoutParams(params);
        params.setMargins(5,5,5,5);

        for(int i = 0; i < controller.getArrayLength(); i++)
        {
            TableRow tr = new TableRow(this);
            tr.setGravity(Gravity.CENTER);

            for (int q = 0; q < controller.getArrayLength(); q++)
            {
                tr.addView(controller.getButton(q, i), size, size);
            }

            layout.addView(tr);
        }
    }

    // MARK - Inner classes
    public static class InstructionSet extends DialogFragment
    {
        @Override
        public Dialog onCreateDialog(Bundle savedInstance)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.instructions).setItems(R.array.instructions, null)
                    .setPositiveButton(R.string.dismiss_instructions,
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dismiss();
                                }
                            });

            return builder.create();
        }
    }
}
