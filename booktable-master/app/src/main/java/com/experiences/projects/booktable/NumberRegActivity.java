package com.experiences.projects.booktable;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseUser;


public class NumberRegActivity extends ActionBarActivity {
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_reg);

        context = this;

        ParseUser user = ParseUser.getCurrentUser();

        if (user != null) {
            if (user.getEmail() == "" || user.get("Phone").toString() == "") {
                setupRegForm(user);
            }
        } else {
            Intent homeIntent = new Intent(this, TimePrefActivity.class);
            this.startActivity(homeIntent);
        }
    }

    public void setupRegForm(final ParseUser user) {
        final EditText emailText = (EditText) findViewById(R.id.nr_et_email),
                numText = (EditText) findViewById(R.id.nr_et_number);
        if (user.getEmail() == "") {
            emailText.setVisibility(View.VISIBLE);
        }

        if (user.get("Phone").toString() == "") {
            numText.setVisibility(View.VISIBLE);
        }

        Button btn_reg = (Button) findViewById(R.id.nr_btn_register);
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getEmail() == "") {
                    user.setEmail(emailText.getText().toString());
                }

                if (user.get("Phone").toString() == "") {
                    user.put("Phone", numText.getText().toString());
                }

                user.put("CompletedSignup", true);

                user.saveInBackground();
                Intent homeIntent = new Intent(context, TimePrefActivity.class);
                context.startActivity(homeIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_number_reg, menu);
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
