package com.android.projects.ultimatesentinal;

/**
 * Created by snehakannan on 27/11/18.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetUpCode extends AppCompatActivity {

    String secretCode;
    Button startCode, stopCode;
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setupcode);
        prefs = getSharedPreferences(getApplicationContext().getString(R.string.app_name), Context.MODE_PRIVATE);

        startCode = (Button)findViewById(R.id.startCode);
        stopCode = (Button)findViewById(R.id.stopCode);

        startCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStartPassword();
            }
        });

    }

    private void setStartPassword() {
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_enter_password, null);
        SharedPreferences.Editor editor = prefs.edit();

        new AlertDialog.Builder(this)
                .setTitle(R.string.enter_password_stop)
                .setView(dialogView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText passwordEdit = (EditText) dialogView.findViewById(R.id.password);
                        Toast.makeText(getApplicationContext(), passwordEdit.getText().toString() , Toast.LENGTH_LONG).show();
                        secretCode = passwordEdit.getText().toString();
                        SharedPreferences.Editor preferencesEditor = prefs.edit();
                        preferencesEditor.putString("secretCode", secretCode);
                        preferencesEditor.commit();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
        editor.putString("secretCode", secretCode);
        editor.commit();
        editor.apply();

    }
    private void setStopPassword() {
        SharedPreferences.Editor preferencesEditor = prefs.edit();
        preferencesEditor.putString("secretCode", secretCode);

        preferencesEditor.putString("secretCode", "-1");
        preferencesEditor.commit();

    }
    @Override
    protected void onPause(){
        super.onPause();
        SharedPreferences.Editor preferencesEditor = prefs.edit();
        preferencesEditor.putString("secretCode", secretCode);
        preferencesEditor.apply();
    }
}