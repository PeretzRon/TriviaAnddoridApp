package com.example.trivia_project.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.trivia_project.R;

import java.util.ArrayList;
import java.util.HashMap;


public class CategoryActivity extends AppCompatActivity {

    private ArrayList<String> categories_choose; //this arrayList using as "Shared Preferences"
    // to save data
    private HashMap<String, Integer> buttonsId;
    private MediaPlayer gameMusic;
    private boolean MusicOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //music
        gameMusic = MediaPlayer.create(CategoryActivity.this, R.raw.gameofthrones); //load
        gameMusic.setLooping(true); // loop sound when it over
        gameMusic.start(); // start play music
        MusicOn = true;

        setContentView(R.layout.activity_category);
        Button animalsButton = findViewById(R.id.animiles_button);
        Button sportsButton = findViewById(R.id.sport_button);
        Button celebritiesButton = findViewById(R.id.celebrities_button);
        Button vehiclesButton = findViewById(R.id.vehicles_button);
        Button generalKnowledgeButton = findViewById(R.id.general_Knowledge_button);
        Button computersButton = findViewById(R.id.science_Computers_button);
        Button geographyButton = findViewById(R.id.geography_Knowledge_button);
        Button musicButton = findViewById(R.id.Music_button);
        Button moviesButton = findViewById(R.id.Movies_button);
        Button historyButton = findViewById(R.id.History_button);

        animalsButton.setTextColor(Color.WHITE);
        sportsButton.setTextColor(Color.WHITE);
        celebritiesButton.setTextColor(Color.WHITE);
        vehiclesButton.setTextColor(Color.WHITE);
        vehiclesButton.setTextColor(Color.WHITE);
        generalKnowledgeButton.setTextColor(Color.WHITE);
        computersButton.setTextColor(Color.WHITE);
        geographyButton.setTextColor(Color.WHITE);
        musicButton.setTextColor(Color.WHITE);
        moviesButton.setTextColor(Color.WHITE);
        historyButton.setTextColor(Color.WHITE);


        buttonsId = new HashMap<>();
        buttonsId.put("Animals", animalsButton.getId());
        buttonsId.put("General Knowledge", generalKnowledgeButton.getId());
        buttonsId.put("Celebrities", celebritiesButton.getId());
        buttonsId.put("Sports", sportsButton.getId());
        buttonsId.put("Vehicles", vehiclesButton.getId());
        buttonsId.put("Geography", geographyButton.getId());
        buttonsId.put("Science Computers", computersButton.getId());
        buttonsId.put("Movies", moviesButton.getId());
        buttonsId.put("Music", musicButton.getId());
        buttonsId.put("History", historyButton.getId());


        //load and set fonts to buttons
        Typeface font = Typeface.createFromAsset(getAssets(), "midnightdrive.otf");
        Typeface font2 = Typeface.createFromAsset(getAssets(), "GOODDP.TTF");
        TextView textViewTitle = findViewById(R.id.Categories_Text);
        textViewTitle.setTypeface(font); // this font is fore the title - > high score
        animalsButton.setTypeface(font2);
        sportsButton.setTypeface(font2);
        celebritiesButton.setTypeface(font2);
        vehiclesButton.setTypeface(font2);
        generalKnowledgeButton.setTypeface(font2);
        computersButton.setTypeface(font2);
        geographyButton.setTypeface(font2);
        musicButton.setTypeface(font2);
        moviesButton.setTypeface(font2);
        historyButton.setTypeface(font2);

        Intent intent = getIntent();
        categories_choose = intent.getStringArrayListExtra("MyData");
        setColorToButtons(categories_choose);
    }


    @SuppressWarnings("ConstantConditions")
    public void setColorToButtons(ArrayList<String> categories_choose) {
        // color the categories that the user selected
        for (String elem : categories_choose) {
            int idd = buttonsId.get(elem);
            Button b = findViewById(idd);
            b.setTextColor(Color.GREEN);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void categories_click(View view) {
        Button buttonPressed = (Button) view;
        String buttonPressedName = buttonPressed.getText().toString().trim();
        buttonPressed = findViewById(buttonsId.get(buttonPressedName));
        if (buttonPressed.getCurrentTextColor() == Color.GREEN) {
            buttonPressed.setTextColor(Color.WHITE); // color is white = not selected
            categories_choose.remove(buttonPressedName); // remove the category from arrayList
        } else {
            buttonPressed.setTextColor(Color.GREEN); // color is green = selected
            categories_choose.add(buttonPressedName); // add the category from arrayList
        }
    }

    @Override
    public void onBackPressed() { //when back buttons is pressed
        if (gameMusic.isPlaying()) {
            gameMusic.pause();
            gameMusic.release();
            MusicOn = false;
        }
        Intent intent = new Intent();
        intent.putExtra("MyData", categories_choose); // share the ArrayList to Start Game Activity
        setResult(1, intent);
        finish();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        gameMusic.pause();
        gameMusic.release();
        MusicOn = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!MusicOn) {
            gameMusic = MediaPlayer.create(CategoryActivity.this, R.raw.gameofthrones);
            gameMusic.setLooping(true);
            gameMusic.start();
            MusicOn = true;
        }
    }
}
