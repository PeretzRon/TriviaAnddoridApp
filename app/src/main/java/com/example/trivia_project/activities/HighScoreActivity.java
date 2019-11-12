package com.example.trivia_project.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.trivia_project.R;
import com.example.trivia_project.classes.Score;
import com.example.trivia_project.interfaces.ServerIP;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class HighScoreActivity extends AppCompatActivity implements ServerIP {

    private boolean startActivityFromGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        startActivityFromGame = intent.getBooleanExtra("SendFromEndGame", false);
        highScoreImportAndShow();
    }

    @Override
    public void onBackPressed() {
        if (startActivityFromGame) {
            Intent intent = new Intent(this, StartGameActivity.class); //go the game screen
            startActivity(intent);
        } else
            finish();
    }

    private void highScoreImportAndShow() {
        Intent intent = getIntent();
        final String userNameFromMainPage = intent.getStringExtra("UserName"); // from previous
        // activity get the username
        new AsyncTask<Void, Void, Boolean>() {
            String flag = "highScore"; //for server to know what to do --> 2 is for get the score
            // and username
            Score scores[];

            @Override
            protected Boolean doInBackground(Void... params) {
                Socket clientSocket = null;

                try {
                    clientSocket = new Socket();
                    clientSocket.connect(new InetSocketAddress(serverIP, 1246), 250);
                    DataOutputStream outToServer =
                            new DataOutputStream(clientSocket.getOutputStream());
                    BufferedReader inFromServer =
                            new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    outToServer.writeBytes(flag + "\n"); // for the server to know what to do
                    switch (flag) {
                        case "highScore":
                            String tableHighScore = inFromServer.readLine(); //read from the
                            // server the high score table as string
                            Gson gson = new Gson();
                            scores = gson.fromJson(tableHighScore, Score[].class); // convert the
                            // string to object
                    }
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean serverIsUp) {
                super.onPostExecute(serverIsUp);
                if (serverIsUp) { //if the server is running
                    setContentView(R.layout.activity_high_score);
                    TextView title = findViewById(R.id.highScoreTitle);

                    TextView placeEasy = findViewById(R.id.placeTextRowEasy);
                    TextView userNameEasy = findViewById(R.id.userNameTextRowEasy);
                    TextView scoreEasy = findViewById(R.id.ScoreTextRowEasy);
                    TableLayout tableEasy = findViewById(R.id.tableScoresEasy1);
                    TextView placeEasy1 = findViewById(R.id.placeTextRowEasy1);
                    TextView userNameEasy1 = findViewById(R.id.userNameTextRowEasy1);
                    TextView scoreEasy1 = findViewById(R.id.ScoreTextRowEasy1);

                    TextView placeMedium = findViewById(R.id.placeTextRowMedium);
                    TextView userNameMedium = findViewById(R.id.userNameTextRowMedium);
                    TextView scoreMedium = findViewById(R.id.ScoreTextRowMedium);
                    TableLayout tableMedium = findViewById(R.id.tableScoresMedium1);
                    TextView placeMedium1 = findViewById(R.id.placeTextRowMedium1);
                    TextView userNameMedium1 = findViewById(R.id.userNameTextRowMedium1);
                    TextView scoreMedium1 = findViewById(R.id.ScoreTextRowMedium1);

                    TextView placeHard = findViewById(R.id.placeTextRowHard);
                    TextView userNameHard = findViewById(R.id.userNameTextRowHard);
                    TextView scoreHard = findViewById(R.id.ScoreTextRowHard);
                    TableLayout tableHard = findViewById(R.id.tableScoresHard1);
                    TextView placeHard1 = findViewById(R.id.placeTextRowHard1);
                    TextView userNameHard1 = findViewById(R.id.userNameTextRowHard1);
                    TextView scoreHard1 = findViewById(R.id.ScoreTextRowHard1);


                    //Load fonts
                    Typeface font1 = Typeface.createFromAsset(getAssets(), "midnightdrive.otf");
                    Typeface font2 = Typeface.createFromAsset(getAssets(), "GOODDP.TTF");
                    Typeface font3 = Typeface.createFromAsset(getAssets(), "comic.ttf");

                    //set fonts and frame to Easy Table
                    title.setTypeface(font1);
                    userNameEasy.setTypeface(font2);
                    scoreEasy.setTypeface(font2);
                    placeEasy.setTypeface(font2);
                    userNameEasy1.setTypeface(font2);
                    scoreEasy1.setTypeface(font2);
                    placeEasy1.setTypeface(font2);
                    userNameEasy.setBackgroundResource(R.drawable.table_cell_bg);
                    scoreEasy.setBackgroundResource(R.drawable.table_cell_bg);
                    placeEasy.setBackgroundResource(R.drawable.table_cell_bg);

                    //set fonts and frame to Medium Table
                    userNameMedium.setTypeface(font2);
                    scoreMedium.setTypeface(font2);
                    placeMedium.setTypeface(font2);
                    userNameMedium1.setTypeface(font2);
                    scoreMedium1.setTypeface(font2);
                    placeMedium1.setTypeface(font2);
                    userNameMedium.setBackgroundResource(R.drawable.table_cell_bg);
                    scoreMedium.setBackgroundResource(R.drawable.table_cell_bg);
                    placeMedium.setBackgroundResource(R.drawable.table_cell_bg);

                    //set fonts and frame to Hard Table
                    userNameHard.setTypeface(font2);
                    scoreHard.setTypeface(font2);
                    placeHard.setTypeface(font2);
                    userNameHard1.setTypeface(font2);
                    scoreHard1.setTypeface(font2);
                    placeHard1.setTypeface(font2);
                    userNameHard.setBackgroundResource(R.drawable.table_cell_bg);
                    scoreHard.setBackgroundResource(R.drawable.table_cell_bg);
                    placeHard.setBackgroundResource(R.drawable.table_cell_bg);


                    int rateEasy = 1; // rate for easy
                    int rateMedium = 1; // rate for medium
                    int rateHard = 1; // rate for hard

                    //for every row in the table score from DB
                    // build a text view and lines -> build a table
                    for (int i = 0; i < scores.length && i < 50; i++) {
                        String username = scores[i].getName();
                        int userScore = scores[i].getScore();
                        if (userScore == 0) {
                            continue;
                        }
                        switch (scores[i].getLevel()) {
                            case 1:
                                TableRow rowEasy = new TableRow(HighScoreActivity.this);
                                TextView myRateEasy = new TextView(HighScoreActivity.this);
                                TextView myNameEasy = new TextView(HighScoreActivity.this);
                                TextView myScoreEasy = new TextView(HighScoreActivity.this);
                                myNameEasy.setTextColor(Color.WHITE);

                                myScoreEasy.setTextSize(15); // define size of font
                                myNameEasy.setTextSize(15);// define size of font
                                myRateEasy.setTextSize(15);// define size of font
                                myScoreEasy.setTextColor(Color.WHITE);

                                myRateEasy.setLayoutParams(new TableRow.LayoutParams(50, 100));
                                myRateEasy.setGravity(Gravity.CENTER); // focus the text to center of the box
                                myRateEasy.setTextColor(Color.BLACK); // text color black


                                myNameEasy.setLayoutParams(new TableRow.LayoutParams(200, 100));
                                myNameEasy.setGravity(Gravity.CENTER); // focus the text to center of the box
                                myNameEasy.setTextColor(Color.BLACK); // text color black

                                myScoreEasy.setLayoutParams(new TableRow.LayoutParams(50, 100));
                                myScoreEasy.setGravity(Gravity.CENTER); // focus the text to center of the box
                                myScoreEasy.setTextColor(Color.BLACK); // text color black

                                //this is for coloring the specif username in other color
                                //marker the username that logon on table score
                                if (username.equalsIgnoreCase(userNameFromMainPage)) {
                                    rowEasy.setBackgroundResource(R.color.greenLight);
                                } else {
                                    rowEasy.setBackgroundResource(R.color.green);
                                }

                                myRateEasy.setText(String.valueOf(rateEasy)); // for rate column
                                myRateEasy.setBackgroundResource(R.drawable.table_cell_bg);

                                myNameEasy.setText(username); // set the username at the text box
                                myNameEasy.setBackgroundResource(R.drawable.table_cell_bg);

                                myScoreEasy.setText(String.valueOf(userScore)); // set the score
                                // at the
                                // text box
                                myScoreEasy.setBackgroundResource(R.drawable.table_cell_bg);

                                myRateEasy.setTypeface(font3);
                                myNameEasy.setTypeface(font3);
                                myScoreEasy.setTypeface(font3);

                                rowEasy.addView(myRateEasy);
                                rowEasy.addView(myNameEasy);
                                rowEasy.addView(myScoreEasy);

                                tableEasy.addView(rowEasy); // apply all to the table
                                rateEasy++;
                                break;


                            case 2:
                                TableRow rowMedium = new TableRow(HighScoreActivity.this);
                                TextView myRateMedium = new TextView(HighScoreActivity.this);
                                TextView myNameMedium = new TextView(HighScoreActivity.this);
                                TextView myScoreMedium = new TextView(HighScoreActivity.this);
                                myNameMedium.setTextColor(Color.WHITE);

                                myScoreMedium.setTextSize(15); // define size of font
                                myNameMedium.setTextSize(15);// define size of font
                                myRateMedium.setTextSize(15);// define size of font
                                myScoreMedium.setTextColor(Color.WHITE);

                                myRateMedium.setLayoutParams(new TableRow.LayoutParams(50, 100));
                                myRateMedium.setGravity(Gravity.CENTER); // focus the text to center of the box
                                myRateMedium.setTextColor(Color.BLACK); // text color black


                                myNameMedium.setLayoutParams(new TableRow.LayoutParams(200, 100));
                                myNameMedium.setGravity(Gravity.CENTER); // focus the text to center of the box
                                myNameMedium.setTextColor(Color.BLACK); // text color black

                                myScoreMedium.setLayoutParams(new TableRow.LayoutParams(50, 100));
                                myScoreMedium.setGravity(Gravity.CENTER); // focus the text to center of the box
                                myScoreMedium.setTextColor(Color.BLACK); // text color black

                                //this is for coloring the specif username in other color
                                //marker the username that logon on table score
                                if (username.equalsIgnoreCase(userNameFromMainPage)) {
                                    rowMedium.setBackgroundResource(R.color.yellowLight);
                                } else {
                                    rowMedium.setBackgroundResource(R.color.yellow2);
                                }

                                myRateMedium.setText(String.valueOf(rateMedium)); // for rate column
                                myRateMedium.setBackgroundResource(R.drawable.table_cell_bg);

                                myNameMedium.setText(username); // set the username at the text box
                                myNameMedium.setBackgroundResource(R.drawable.table_cell_bg);

                                myScoreMedium.setText(String.valueOf(userScore)); // set the score
                                myScoreMedium.setBackgroundResource(R.drawable.table_cell_bg);

                                //set fonts to the text boxes
                                myRateMedium.setTypeface(font3);
                                myNameMedium.setTypeface(font3);
                                myScoreMedium.setTypeface(font3);

                                // add the text box to row
                                rowMedium.addView(myRateMedium);
                                rowMedium.addView(myNameMedium);
                                rowMedium.addView(myScoreMedium);

                                tableMedium.addView(rowMedium); // apply all to the table
                                rateMedium++;
                                break;

                            case 3:
                                TableRow rowHard = new TableRow(HighScoreActivity.this);
                                TextView myRateHard = new TextView(HighScoreActivity.this);
                                TextView myNameHard = new TextView(HighScoreActivity.this);
                                TextView myScoreHard = new TextView(HighScoreActivity.this);
                                myNameHard.setTextColor(Color.WHITE);

                                myScoreHard.setTextSize(15); // define size of font
                                myNameHard.setTextSize(15);// define size of font
                                myRateHard.setTextSize(15);// define size of font
                                myScoreHard.setTextColor(Color.WHITE);

                                myRateHard.setLayoutParams(new TableRow.LayoutParams(50, 100));
                                myRateHard.setGravity(Gravity.CENTER); // focus the text to center of the box
                                myRateHard.setTextColor(Color.BLACK); // text color black


                                myNameHard.setLayoutParams(new TableRow.LayoutParams(200, 100));
                                myNameHard.setGravity(Gravity.CENTER); // focus the text to center of the box
                                myNameHard.setTextColor(Color.BLACK); // text color black

                                myScoreHard.setLayoutParams(new TableRow.LayoutParams(50, 100));
                                myScoreHard.setGravity(Gravity.CENTER); // focus the text to center of the box
                                myScoreHard.setTextColor(Color.BLACK); // text color black

                                //this is for coloring the specif username in other color
                                //marker the username that logon on table score
                                if (username.equalsIgnoreCase(userNameFromMainPage)) {
                                    rowHard.setBackgroundResource(R.color.redLight);
                                } else {
                                    rowHard.setBackgroundResource(R.color.red);
                                }

                                myRateHard.setText(String.valueOf(rateHard)); // for rate column
                                myRateHard.setBackgroundResource(R.drawable.table_cell_bg);

                                myNameHard.setText(username); // set the username at the text box
                                myNameHard.setBackgroundResource(R.drawable.table_cell_bg);

                                myScoreHard.setText(String.valueOf(userScore)); // set the score
                                myScoreHard.setBackgroundResource(R.drawable.table_cell_bg);

                                //set fonts to the text boxes
                                myRateHard.setTypeface(font3);
                                myNameHard.setTypeface(font3);
                                myScoreHard.setTypeface(font3);

                                // add the text box to row
                                rowHard.addView(myRateHard);
                                rowHard.addView(myNameHard);
                                rowHard.addView(myScoreHard);

                                tableHard.addView(rowHard); // apply all to the table
                                rateHard++;
                                break;

                        }
                    }
                    MediaPlayer ring = MediaPlayer.create(HighScoreActivity.this,
                            R.raw.high_score_music);
                    ring.start(); // play sound
                } else {
                    showErrorMassage(); // if there no connection so server - will show popup
                    // massage

                }


            }

        }.execute();
    }

    // pop a dialog when server is down - HighScore can't to be showed
    private void showErrorMassage() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Server might be down! High score table can't load from server");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish(); // close the activity
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
