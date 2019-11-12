package com.example.trivia_project.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.trivia_project.R;
import com.example.trivia_project.interfaces.ServerIP;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class EndGameActivity extends AppCompatActivity implements ServerIP {

    private String userName;
    private int score;
    private int level;
    private String isSucceeded;
    private Button serverIsUpdate;
    private boolean serverIsUpdateBoll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);


        //get data form the game activity
        Intent intent = getIntent();
        score = intent.getIntExtra("Score", 0);
        int correctAns = intent.getIntExtra("CorrectAns", 0);
        userName = intent.getStringExtra("UserName");
        level = intent.getIntExtra("Level", 1);

        //Code Style - Using Html
        String userNameFix =
                String.format("<font color=#4da6ff><u>User Name:</u> </font> <font " + "color" +
                        "=#FFFFFF>%s" +
                "</font>", userName);
        String scoreFix = String.format("<font color=#4da6ff><u>Score:</u> </font> <font " +
                "color=#FFFFFF>%s</font>", String.valueOf(score));
        String correctAnsFix = String.format("<font color=#4da6ff><u>Correct answers:</u> </font>" +
                " <font color=#FFFFFF>%s</font>", String.valueOf(correctAns));

        //getting the id of the object into variables
        TextView textViewSummery = findViewById(R.id.summery_textView);
        textViewSummery.setPaintFlags(textViewSummery.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        TextView textViewScore = findViewById(R.id.scoreTextView);
        TextView textViewCorrectAns = findViewById(R.id.correctAnsTextView);
        TextView textViewUserName = findViewById(R.id.textViewUserName);
        serverIsUpdate = findViewById(R.id.serverIsUpdateButton);

        //set and load fonts
        Typeface font1 = Typeface.createFromAsset(getAssets(), "midnightdrive.otf");
        Typeface font2 = Typeface.createFromAsset(getAssets(), "comic.ttf");
        textViewSummery.setTypeface(font1);
        textViewScore.setTypeface(font2);
        textViewCorrectAns.setTypeface(font2);
        textViewUserName.setTypeface(font2);
        textViewSummery.setTextColor(Color.CYAN);
        serverIsUpdate.setTypeface(font2);

        //Set the text on text Views
        textViewScore.setText(Html.fromHtml(scoreFix)); // convert from Html code to String
        textViewCorrectAns.setText(Html.fromHtml(correctAnsFix));
        textViewUserName.setText(Html.fromHtml(userNameFix));
        updateScoreInHighScoreTable();

        isSucceeded = "F";
        String serverIsUpdateString = String.format("<font color=#4da6ff><u>Server Update:</u> " +
                "</font>" + "<font color=#4da6ff>  </font>");
        serverIsUpdate.setText(Html.fromHtml(serverIsUpdateString));
        serverIsUpdateBoll = false;
        serverIsUpdate.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                R.drawable.ic_restore_black_24dp, 0);
    }


    @Override
    public void onBackPressed() {
        //do nothing.
        //user must click to button to continue. (no back option)
    }


    public void goToHighScoreTable(View view) {
        Intent intent = new Intent(this, HighScoreActivity.class); //go the high Score Activity
        intent.putExtra("SendFromEndGame", true); // send flag to high Score that the
        // EndGameActivity Created the activity
        intent.putExtra("UserName", userName);
        startActivity(intent);

    }

    public void homeScreenButton(View view) {
        Intent intent = new Intent(this, StartGameActivity.class); //go to home screen
        startActivity(intent);
    }

    private String updateScoreInHighScoreTable() {

        new AsyncTask<Void, Void, Void>() {
            String flag = "highScoreUpdate";

            @Override
            protected Void doInBackground(Void... params) {
                Socket clientSocket = null;
                try {
                    clientSocket = new Socket();
                    clientSocket.connect(new InetSocketAddress(serverIP, 1246), 450);
                    DataOutputStream outToServer =
                            new DataOutputStream(clientSocket.getOutputStream());
                    BufferedReader inFromServer =
                            new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    outToServer.writeBytes(flag + "\n"); // sent to server the flag for him to
                    // know what to do next
                    outToServer.writeBytes(userName + "\n"); // send to server the name to bring
                    // the high score
                    outToServer.writeBytes(String.valueOf(score) + "\n"); // send to server the
                    // score of the username
                    outToServer.writeBytes(String.valueOf(level) + "\n"); // send to server the
                    // level that user select
                    isSucceeded = inFromServer.readLine();
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    Log.e("EndGameActivity", "SocketTimeoutException - Data from DB isn't arrive");
                } catch (IOException e) {
                    Log.e("EndGameActivity", "IOException Exception - Data from DB isn't arrive");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void nothing) {
                super.onPostExecute(nothing);
                if (isSucceeded.equals("T")) {
                    serverIsUpdateBoll = true;
                    serverIsUpdate.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            R.drawable.ic_thumb_up_black_24dp, 0);
                } else {
                    serverIsUpdateBoll = false;
                    serverIsUpdate.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            R.drawable.ic_restore_black_24dp, 0);
                }
            }
        }.execute();
        return null;
    }


    public void serverIsUpdateButton(View view) {
        if (!serverIsUpdateBoll) { // the score is not update on DB
            updateScoreInHighScoreTable(); // try again to update
        }
    }
}
