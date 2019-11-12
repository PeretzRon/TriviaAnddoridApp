package com.example.trivia_project.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trivia_project.R;
import com.example.trivia_project.classes.QuestionsDB;
import com.example.trivia_project.interfaces.ServerIP;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class StartGameActivity extends AppCompatActivity implements ServerIP {

    // global variable
    private ArrayList<String> categories_choose;
    private Button easyButton;
    private Button mediumButton;
    private Button hardButton;
    private Button logIn;
    private int level; // 1-Easy, 2-Medium, 3-Hard
    private TextView textViewNamePlayer;
    private TextView textViewPasswordPlayer;
    private boolean startGame;
    private boolean dataIsExist;
    private String questionsEasy; // test
    private String questionsMedium; // test
    private String questionsHard; // test
    private MediaPlayer gameMusic;
    private boolean musicOn; // for to know when to stop game music / start music (for nullPointerE)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game_acrtivity);

        //music
        gameMusic = MediaPlayer.create(StartGameActivity.this, R.raw.gameofthrones);
        gameMusic.setLooping(true);
        gameMusic.start();
        musicOn = true;

        categories_choose = new ArrayList<>(); // this array list save which categories has been
        // chosen
        categories_choose.add("Animals"); // default category
        startGame = false; // you can't play (activity playGame can't show)
        dataIsExist = false; // questions isn't arrive yet from DB


        logIn = findViewById(R.id.login_button);
        textViewNamePlayer = findViewById(R.id.editTextNameEnter);
        textViewPasswordPlayer = findViewById(R.id.editTextPasswordEnter);
        TextView textViewSignUp = findViewById(R.id.textViewSignUp);
        String text = "<font color=#FFFFFF>Not a member? </font> <font " +
                "color=#2ECCFA><u>Sign up</u></font>";
        textViewSignUp.setText(Html.fromHtml(text));

        //set the default level to be Easy. the other will be at gray color
        easyButton = findViewById(R.id.button_easy);
        mediumButton = findViewById(R.id.button_medium);
        hardButton = findViewById(R.id.button_hard);
        Button playButton = findViewById(R.id.playButton);
        Button categoriesButton = findViewById(R.id.categoriesButton);
        Button highScoreButton = findViewById(R.id.highScoreButton);
        easyButton.setBackgroundResource(R.drawable.ripple_effect_blue);
        mediumButton.setBackgroundResource(R.drawable.ripple_effect_yellow);
        hardButton.setBackgroundResource(R.drawable.ripple_effect_blue);
        level = 2; // default level is medium

        //set font to buttons
        Typeface font = Typeface.createFromAsset(getAssets(), "comic.ttf");
        easyButton.setTypeface(font);
        mediumButton.setTypeface(font);
        hardButton.setTypeface(font);
        playButton.setTypeface(font);
        highScoreButton.setTypeface(font);
        categoriesButton.setTypeface(font);
        logIn.setTypeface(font);
        textViewSignUp.setTypeface(font);
        getData(); // get questions from DB (Async Task)
    }

    public void level_button(View view) {
        //change the color and level of the game
        Button button = (Button) view;
        String pressed = button.getText().toString();
        switch (pressed) {
            case "Easy":
                easyButton.setBackgroundResource(R.drawable.ripple_effect_green); //color button
                // change to green
                mediumButton.setBackgroundResource(R.drawable.ripple_effect_blue); //color button
                // change to gray
                hardButton.setBackgroundResource(R.drawable.ripple_effect_blue); //color button
                // change to gray
                level = 1; // level set to Easy
                break;
            case "Medium":
                easyButton.setBackgroundResource(R.drawable.ripple_effect_blue); //color button
                // change to gray
                mediumButton.setBackgroundResource(R.drawable.ripple_effect_yellow); //color
                // button change to yellow
                hardButton.setBackgroundResource(R.drawable.ripple_effect_blue); //color button
                // change to gray
                level = 2; // level set to Medium
                break;
            case "Hard":
                easyButton.setBackgroundResource(R.drawable.ripple_effect_blue); //color button
                // change to gray
                mediumButton.setBackgroundResource(R.drawable.ripple_effect_blue); //color button
                // change to gray
                hardButton.setBackgroundResource(R.drawable.ripple_effect_red); //color button
                // change to red
                level = 3; // level set to Hard
                break;
        }

    }

    // Button "Play!" onClick
    public void playBut(View view) {
        if (!startGame) { //if start game is false the user didn't provide username and password
            showDialogWithMassage("You must login to play!"); // show dialog on screen - must
            // login to play
        } else if (!dataIsExist) {
            showErrorDataDialog(); // show dialog on screen - try to get the data again
        } else if (categories_choose.size() == 0) {
            showDialogWithMassage("You must select at least one category");
        } else {
            Intent intent = new Intent(this, PlayGameActivity.class); //go the game screen
            intent.putExtra("Category", categories_choose); // move to the activity the categories
            intent.putExtra("DataEasy", questionsEasy); // move to the activity the all questions
            // (String)
            intent.putExtra("DataMedium", questionsMedium); // move to the activity the all
            // questions (String)
            intent.putExtra("DataHard", questionsHard); // move to the activity the all questions
            // (String)
            intent.putExtra("Level", level); // move to the activity the level
            intent.putExtra("UserName", textViewNamePlayer.getText().toString());
            startActivity(intent);
        }
    }

    // Button "High Score" onClick
    public void highScoreButton(View view) {
        Intent intent2 = new Intent(this, HighScoreActivity.class); // go to high score screen
        intent2.putExtra("UserName", textViewNamePlayer.getText().toString());
        startActivity(intent2);
    }

    // Button "Login" onClick
    public void loginPressedButton(View view) {
        String name = textViewNamePlayer.getText().toString(); //get text from textPlain name
        String password = textViewPasswordPlayer.getText().toString(); //get text from textPlain
        // name
        if (name.equals("") || password.equals("")) { // check if the field is empty
            Toast.makeText(StartGameActivity.this, "You must fill username and password",
                    Toast.LENGTH_SHORT).show();
        } else { //check if username and password are valid (from DB)
            validationUser(name, password, "validUser");
        }
    }

    // Button "category" onClick
    public void categoryButton(View view) {
        Intent i = new Intent(this, CategoryActivity.class);
        i.putExtra("MyData", categories_choose);
        startActivityForResult(i, 2);
    }

    //onClick textView Sign up
    public void SignUp(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    //this method bring from the server all questions
    public String getData() {

        new AsyncTask<Void, Void, Void>() {
            String flag = "dataImport";

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
                    questionsEasy = inFromServer.readLine(); // read all data - json string
                    questionsMedium = inFromServer.readLine();
                    questionsHard = inFromServer.readLine();
                    dataIsExist = true; // the data is arrive. flag now is true
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    Log.e("StartGameActivity", "SocketTimeoutException - Data from DB isn't " +
                            "arrive");
                    dataIsExist = false;
                } catch (IOException e) {
                    Log.e("StartGameActivity", "IOException Exception - Data from DB isn't arrive");
                    dataIsExist = false;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void nothing) {
                super.onPostExecute(nothing);

            }
        }.execute();
        return null;
    }

    //this method approves user details
    public String validationUser(final String username, final String password, final String flag) {

        new AsyncTask<Void, Void, Void>() {
            String result = "False";
            Boolean serverUp;
            QuestionsDB questionsDB[];

            @Override
            protected Void doInBackground(Void... params) {
                Socket clientSocket = null;
                try {
                    clientSocket = new Socket();
                    clientSocket.connect(new InetSocketAddress(serverIP, 1246), 400);
                    serverUp = true;
                    DataOutputStream outToServer =
                            new DataOutputStream(clientSocket.getOutputStream());
                    BufferedReader inFromServer =
                            new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    outToServer.writeBytes(flag + "\n");  // sent to server the flag for him to
                    // know what to do next
                    outToServer.writeBytes(username + "\n"); // sent to server the username from
                    // the textView username
                    outToServer.writeBytes(password + "\n"); // sent to server the password from
                    // the textView password
                    result = inFromServer.readLine(); // the server return if the user ans
                    // password are correct

                } catch (SocketTimeoutException e) {
                    startGame = false;
                    serverUp = false;
                    Log.e("StartGameActivity", "SocketTimeoutException - Server might be down");
                    e.printStackTrace();
                } catch (Exception e) {
                    Log.e("StartGameActivity", "General Exception - validation User Fail");
                    startGame = false;
                    serverUp = false;
                    e.printStackTrace();


                }
                return null;
            }

            @Override
            protected void onPostExecute(Void nothing) {
                super.onPostExecute(nothing);
                if (serverUp == false) {
                    Toast.makeText(StartGameActivity.this, "Server connection refuse\nServer " +
                                    "might be down",
                            Toast.LENGTH_LONG).show();
                }
                if (result.equals("True")) {
                    logIn.setBackgroundResource(R.drawable.ripple_effect_green_round); // change
                    // the color of login to green
                    logIn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.correct2, 0);
                    // show image on button
                    closeKeyboard();

                    //create delay and show toast
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(StartGameActivity.this, "Login successfully",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }, 350); // delay time
                    startGame = true;
                } else {
                    logIn.setBackgroundResource(R.drawable.ripple_effect_red_round); // change
                    // the color of login to red
                    logIn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.incorrect2, 0); // show image on button
                    startGame = false;
                    if (serverUp) //serverUp = True if server is up, and False if down
                        Toast.makeText(StartGameActivity.this, "Wrong username or password",
                                Toast.LENGTH_SHORT).show();
                }


            }
        }.execute();
        return null;
    }

    // pop a dialog when data is not arrive from DB
    private void showErrorDataDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setIcon(R.drawable.ic_error_black_24dp);
        builder.setMessage("Error fetching data. Click OK to try again");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getData(); // try to get data (Table of questions from DB) again
                    }
                });

        builder.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        AlertDialog alert11 = builder.create();
        alert11.show();
    }

    // pop a dialog with massage
    private void showDialogWithMassage(String massage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setIcon(R.drawable.ic_error_black_24dp);
        builder.setMessage(massage);
        builder.setCancelable(true);
        builder.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog alert11 = builder.create();
        alert11.show();
    }

    // pop a dialog Exit Game
    private void showDialogExitGame() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit Trivia?");
        builder.setIcon(R.drawable.ic_keyboard_return_black_24dp);
        builder.setCancelable(true);
        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finishAffinity(); // kill ALL
                    }
                });
        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert11 = builder.create();
        alert11.show();
    }

    //hide keyboard (after user login)
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == 1) {
                if (data != null) {
                    categories_choose = data.getStringArrayListExtra("MyData");
                }
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (musicOn) {
            gameMusic.pause();
            gameMusic.release();
            musicOn = false;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!musicOn) {
            gameMusic = MediaPlayer.create(StartGameActivity.this, R.raw.gameofthrones);
            gameMusic.setLooping(true);
            gameMusic.start();
            musicOn = true;
        }
    }

    @Override
    public void onBackPressed() {
        showDialogExitGame();

    }

}

