package com.example.trivia_project.activities;


import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trivia_project.R;
import com.example.trivia_project.classes.QuestionsDB;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class PlayGameActivity extends AppCompatActivity {


    private ArrayList<QuestionsDB> currentGame = null;
    private ArrayList<String> categories_choose;
    private ArrayList<QuestionsDB> arrayListEasy;
    private ArrayList<QuestionsDB> arrayListMedium;
    private ArrayList<QuestionsDB> arrayListHard;
    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView textViewQuestionCount;
    private TextView textViewLevelGame;
    private TextView textViewCategory;
    private TextView feedback;
    private Button goToSummery;
    private RadioGroup rbGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private RadioButton rb4;
    private Button buttonConfirmNext;
    private ColorStateList textColorDefaultRB; // for color radio button
    private int level;
    private int easyQuestionsSizeArray;
    private int mediumQuestionsSizeArray;
    private int hardQuestionsSizeArray;
    private int numberQuestion;
    private int score;
    private String userName;
    private Map<Integer, Integer> radioButtonToIdHashMap;
    private MediaPlayer tickTack;
    private MediaPlayer hurryUp;
    private MediaPlayer timeOver;
    private MediaPlayer feedbackSound;
    private MediaPlayer handsClap;
    private int correct_ans;
    private Drawable progressBarRed;
    private Drawable progressBarNone;
    private Drawable progressBarGreen;
    private Drawable getProgressBarOrange;
    private int progress;
    private int endTime = 250;
    private ProgressBar progressBarView;
    private TextView tv_time;
    private CountDownTimer countDownTimer;
    private int seconds; // for timer
    private boolean startWithoutOnResume; // first time don't do OnResume
    private boolean InThisActivity; // to know if the the activity stop / or not
    private boolean isFeedbackSoundPlay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);
        startWithoutOnResume = true;

        //timer
        progressBarView = findViewById(R.id.view_progress_bar);
        tv_time = findViewById(R.id.tv_timer);
        progressBarRed = getResources().getDrawable(R.drawable.circularprogressbar_red);
        progressBarNone = getResources().getDrawable(R.drawable.circularprogressbar_none);
        progressBarGreen = getResources().getDrawable(R.drawable.circularprogressbar_green);
        getProgressBarOrange = getResources().getDrawable(R.drawable.circularprogressbar_orange);

        //Animation for timer
        RotateAnimation makeVertical = new RotateAnimation(0, -90,
                RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        makeVertical.setFillAfter(true);
        progressBarView.startAnimation(makeVertical);
        progressBarView.setSecondaryProgress(endTime);
        progressBarView.setProgress(0);

        //sound
        tickTack = MediaPlayer.create(PlayGameActivity.this, R.raw.tick_tock); //load sound
        hurryUp = MediaPlayer.create(PlayGameActivity.this, R.raw.hurry); //load sound
        tickTack.setLooping(true);
        hurryUp.setLooping(true);
        feedbackSound = null;
        isFeedbackSoundPlay = false;

        Intent intent = getIntent();
        categories_choose = intent.getStringArrayListExtra("Category"); // get the categories
        userName = intent.getStringExtra("UserName"); // get the Username from last activity
        level = intent.getIntExtra("Level", 1); // get the level from last activity

        String questionsEasy = intent.getStringExtra("DataEasy"); // get questions - level
        // easyQuestionsSizeArray
        String questionsMedium = intent.getStringExtra("DataMedium"); // get questions - level
        // mediumQuestionsSizeArray
        String questionsHard = intent.getStringExtra("DataHard"); // get questions - level
        // hardQuestionsSizeArray
        parseJsonToArrayList(questionsEasy, questionsMedium, questionsHard); // convert from
        // string json to arrayList
        removeUnnecessaryCategories(arrayListEasy); // remove the questions that not in the
        // selected categories
        removeUnnecessaryCategories(arrayListMedium); // remove the questions that not in the
        // selected categories
        removeUnnecessaryCategories(arrayListHard); // remove the questions that not in the
        // selected categories


        textViewQuestion = findViewById(R.id.text_view_question);
        textViewQuestion.setMovementMethod(new ScrollingMovementMethod()); // make text box scroll
        textViewQuestionCount = findViewById(R.id.text_view_question_count);
        textViewScore = findViewById(R.id.text_view_score);
        textViewCategory = findViewById(R.id.text_view_category);
        textViewLevelGame = findViewById(R.id.text_view_level);
        feedback = findViewById(R.id.feedBackView);
        rbGroup = findViewById(R.id.radio_group);
        rb1 = findViewById(R.id.radio_button1);
        rb2 = findViewById(R.id.radio_button2);
        rb3 = findViewById(R.id.radio_button3);
        rb4 = findViewById(R.id.radio_button4);
        buttonConfirmNext = findViewById(R.id.button_confirm_next);
        goToSummery = findViewById(R.id.go_to_summery);
        goToSummery.setVisibility(View.GONE); // disappear button
        score = 0; // start the score with 0 points
        InThisActivity = true; // we are in this activity

        //Set and load font
        Typeface font = Typeface.createFromAsset(getAssets(), "comic.ttf");
        Typeface font2 = Typeface.createFromAsset(getAssets(), "MAIAN.TTF");
        Typeface font3 = Typeface.createFromAsset(getAssets(), "midnightdrive.otf");
        textViewQuestion.setTypeface(font);
        textViewQuestionCount.setTypeface(font2);
        textViewCategory.setTypeface(font2);
        textViewLevelGame.setTypeface(font2);
        textViewScore.setTypeface(font2);
        buttonConfirmNext.setTypeface(font);
        goToSummery.setTypeface(font);
        feedback.setTypeface(font3);

        textColorDefaultRB = rb1.getTextColors();
        numberQuestion = 0;

        // get the size of the arrayList of each level
        easyQuestionsSizeArray = arrayListEasy.size();
        mediumQuestionsSizeArray = arrayListMedium.size();
        hardQuestionsSizeArray = arrayListHard.size();

        //this hashMap know if the user select the correct answer by id of radio button
        radioButtonToIdHashMap = new HashMap<>();
        radioButtonToIdHashMap.put(rb1.getId(), 1);
        radioButtonToIdHashMap.put(rb2.getId(), 2);
        radioButtonToIdHashMap.put(rb3.getId(), 3);
        radioButtonToIdHashMap.put(rb4.getId(), 4);

        // in this arrayList there is the questions for the
        // current game
        currentGame = new ArrayList<>();
        createCurrentListQuestions(level); // create a 20 questions list via selected level
        String levelGame =
                String.format("<font color=#4da6ff><u>Level:</u> </font> <font " + "color" +
                        "=#FFFFFF> %s" +
                        "</font>", convertLevel(level));
        textViewLevelGame.setText(Html.fromHtml(levelGame));
        buttonConfirmNext.setText("Confirm");
        numberQuestion++;
        String scoreCurrent =
                String.format("<font color=#4da6ff><u>Score:</u> </font> <font " + "color" +
                        "=#FFFFFF> %s" +
                        "</font>", String.valueOf(score));
        textViewScore.setText(Html.fromHtml(scoreCurrent));

        showQuestion(); // show the next question (in this case the first)

    }

    //this method show the next question ans start the timer
    private void showQuestion() {
        try {
            Random random = new Random();
            int randomIndex = random.nextInt(currentGame.size()); //get random index from arrayList
            // currentGame
            enableDisableRadioButton(true); // set radio button to be clickable
            feedback.setText(""); // disappear feedback
            textViewQuestion.setText(currentGame.get(randomIndex).getQuestion());
            String category =
                    String.format("<font color=#4da6ff><u>Category:</u> </font> <font " + "color" +
                            "=#FFFFFF> %s" +
                            "</font>", currentGame.get(randomIndex).getCategory());
            textViewCategory.setText(Html.fromHtml(category));
            rb1.setTextColor(textColorDefaultRB);
            rb2.setTextColor(textColorDefaultRB);
            rb3.setTextColor(textColorDefaultRB);
            rb4.setTextColor(textColorDefaultRB);
            rbGroup.clearCheck(); // clear selection from radio group
            rb1.setText(currentGame.get(randomIndex).getAns1());
            rb2.setText(currentGame.get(randomIndex).getAns2());
            rb3.setText(currentGame.get(randomIndex).getAns3());
            rb4.setText(currentGame.get(randomIndex).getAns4());
            String QuestionNumber =
                    String.format("<font color=#4da6ff><u>Question:</u> </font> <font " + "color" +
                            "=#FFFFFF> %s" +
                            "</font>", String.valueOf(numberQuestion) + "/20");
            textViewQuestionCount.setText(Html.fromHtml(QuestionNumber));
            numberQuestion++;
            correct_ans = currentGame.get(randomIndex).getCorrect_ans();
            currentGame.remove(randomIndex);
            tickTack.start();
            fn_countdown();
            countDownTimer.start(); // start the timer
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToSummery(View view) {
        textViewQuestion.setText("");
        buttonConfirmNext.setVisibility(View.GONE);
        feedback.setText("");
        rbGroup.setVisibility(View.GONE);
        vibrate(); // phone is vibrate
        final KonfettiView konfettiView = findViewById(R.id.viewKonfetti); // make konfetti
        konfettiView.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(new Size(12, 5f))
                .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                .streamFor(300, 5450L);
        handsClap = MediaPlayer.create(PlayGameActivity.this, R.raw.audience_clapping);
        handsClap.start(); // feedbackSound sound

        //move all this variables to the next activity
        Intent intent = new Intent(PlayGameActivity.this, EndGameActivity.class); // summery screen
        intent.putExtra("Score", score);
        intent.putExtra("CorrectAns", score / 5);
        intent.putExtra("UserName", userName);
        intent.putExtra("Level", level);
        startActivity(intent);
    }

    // convert to arrayList
    private void parseJsonToArrayList(String questionsEasy, String questionsMedium,
                                      String questionsHard) {
        Gson gson = new Gson();
        QuestionsDB questionsDBEasy[] = gson.fromJson(questionsEasy, QuestionsDB[].class);
        arrayListEasy = new ArrayList<>(Arrays.asList(questionsDBEasy));

        QuestionsDB questionsDBMedium[] = gson.fromJson(questionsMedium, QuestionsDB[].class);
        arrayListMedium = new ArrayList<>(Arrays.asList(questionsDBMedium));

        QuestionsDB questionsDBHard[] = gson.fromJson(questionsHard, QuestionsDB[].class);
        arrayListHard = new ArrayList<>(Arrays.asList(questionsDBHard));
    }

    // remove categories that user did not select at the CategoriesActivity
    private void removeUnnecessaryCategories(ArrayList<QuestionsDB> array) {

        Iterator<QuestionsDB> k = array.iterator();
        while (k.hasNext()) {
            QuestionsDB elem = k.next();
            if (!categories_choose.contains(elem.getCategory()))
                k.remove();
        }
    }

    // this method for select from the all questions only 20 by the selected level game
    private void createCurrentListQuestions(int level) {
        Random random = new Random();
        int randomIndex;
        int max_easy = 0;
        int max_med = 0;
        int max_hard = 0;

        switch (level) {
            case 1: //level easyQuestionsSizeArray
                max_easy = 10;
                max_med = 7;
                max_hard = 3;
                break;
            case 2: //level mediumQuestionsSizeArray
                max_easy = 5;
                max_med = 12;
                max_hard = 3;
                break;
            case 3: //level hardQuestionsSizeArray
                max_easy = 2;
                max_med = 8;
                max_hard = 10;
                break;
        }

        for (int i = 0; i < max_easy; i++) {
            randomIndex = random.nextInt(easyQuestionsSizeArray);
            currentGame.add(arrayListEasy.get(randomIndex));
            arrayListEasy.remove(randomIndex);
            easyQuestionsSizeArray -= 1;
        }
        for (int i = 0; i < max_med; i++) {
            randomIndex = random.nextInt(mediumQuestionsSizeArray);
            currentGame.add(arrayListMedium.get(randomIndex));
            arrayListMedium.remove(randomIndex);
            mediumQuestionsSizeArray -= 1;
        }

        for (int i = 0; i < max_hard; i++) {
            randomIndex = random.nextInt(hardQuestionsSizeArray);
            currentGame.add(arrayListHard.get(randomIndex));
            arrayListHard.remove(randomIndex);
            hardQuestionsSizeArray -= 1;
        }
    }

    // this method check id the user select the correct answer
    @SuppressWarnings("ConstantConditions")
    private void checkAnswer() {
        tickTack.pause(); // stop tick tack timer sound
        if (hurryUp.isPlaying()) {
            hurryUp.pause();
        }
        changeColorRadioButton(); // change correct ans to green other to red
        enableDisableRadioButton(false); // radio button can't press now
        int rbSelected = findViewById(rbGroup.getCheckedRadioButtonId()).getId();
        if (correct_ans == radioButtonToIdHashMap.get(rbSelected)) { // correct ans
            playFeedbackSound(1); // feedbackSound sound correct answer (random sound)
            score += 5; // score update in +5
            String scoreCurrent =
                    String.format("<font color=#4da6ff><u>Score:</u> </font> <font " + "color" +
                            "=#FFFFFF> %s" +
                            "</font>", String.valueOf(score));
            textViewScore.setText(Html.fromHtml(scoreCurrent));
            feedback.setText("Good :)");
            feedback.setTextColor(Color.GREEN);
        } else {// incorrect ans
            playFeedbackSound(2);// feedbackSound sound wrong answer (random sound)
            feedback.setText("Wrong   Answer :(");
            feedback.setTextColor(Color.RED);
        }
    }

    //this method allow or prevent to click on radio button
    private void enableDisableRadioButton(Boolean bool) {
        rb1.setClickable(bool);
        rb2.setClickable(bool);
        rb3.setClickable(bool);
        rb4.setClickable(bool);
    }

    // this method change the color of radio button via the correct answer
    private void changeColorRadioButton() {
        rb1.setTextColor(Color.RED);
        rb2.setTextColor(Color.RED);
        rb3.setTextColor(Color.RED);
        rb4.setTextColor(Color.RED);
        switch (correct_ans) {
            case 1:
                rb1.setTextColor(Color.GREEN);
                break;
            case 2:
                rb2.setTextColor(Color.GREEN);
                break;
            case 3:
                rb3.setTextColor(Color.GREEN);
                break;
            case 4:
                rb4.setTextColor(Color.GREEN);
                break;
        }
    }

    // vibrate the phone at the end of the game
    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        long[] mVibratePattern = new long[]{500, 100, 50, 100, 50, 100, 50, 100, 50, 100, 50
                , 100, 50, 100, 50, 100, 50, 100, 50, 600, 50};
        int[] mAmplitudes = new int[]{0, 250, 0, 250, 0, 250, 0, 250, 0, 250, 0, 250, 0, 250, 0, 250, 0,
                250, 0, 250, 0};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // support other device
            VibrationEffect effect = VibrationEffect.createWaveform(mVibratePattern, mAmplitudes,
                    -1);
            v.vibrate(effect);
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }
    }

    public void confirmAndNextButton(View view) {
        Button b = (Button) view;
        String ButtonPressed = b.getText().toString();
        if (ButtonPressed.equals("Confirm")) {
            if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked()) {
                checkAnswer(); // check if the correct answer is right.
                fn_countdown();
                ChangeButtonRoll(); // check if the game is over and change button roll
            } else {
                Toast.makeText(PlayGameActivity.this, "Please Select Answer",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            if (isFeedbackSoundPlay) {
                feedbackSound.pause();
                isFeedbackSoundPlay = false;
            }
            if (feedbackSound != null)
                feedbackSound.release();
            showQuestion(); // show next question
            buttonConfirmNext.setText("Confirm");
        }
    }

    // this method check if the the game is over (yes -> disappear items, no-> change button)
    private void ChangeButtonRoll() {
        if (currentGame.size() != 0) {
            buttonConfirmNext.setText("Next");
        } else { // pass 20 questions -> game is over
            buttonConfirmNext.setVisibility(View.GONE); //disappear button
            goToSummery.setVisibility(View.VISIBLE); //set button appear

            //create animation of blinking button
            final ObjectAnimator animator =
                    (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.blink);
            animator.setTarget(goToSummery);
            animator.start();

            //disappear text on screen
            textViewQuestionCount.setText("");
            textViewCategory.setText("");
            textViewScore.setText("");
            textViewLevelGame.setText("");
            progressBarView.setProgressDrawable(progressBarNone);
            tv_time.setText("");
        }
    }

    // convert from level(int) to String (Easy,Medium,Hard)
    private String convertLevel(int level) {
        String levelString = null;
        switch (level) {
            case 1:
                levelString = "Easy";
                break;
            case 2:
                levelString = "Medium";
                break;
            case 3:
                levelString = "Hard";
                break;
        }
        return levelString;
    }

    private void fn_countdown() {
        int myProgress = 0;
        try {
            countDownTimer.cancel();

        } catch (Exception e) {
            e.printStackTrace();
        }
        String timeInterval = "21";
        progress = 1;
        endTime = Integer.parseInt(timeInterval); // up to finish time
        countDownTimer = new CountDownTimer(endTime * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                progressBarView.setProgressDrawable(progressBarGreen);
                tv_time.setTextColor(Color.GREEN);
                setProgress(progress, endTime);
                progress = progress + 1;
                seconds = (int) (millisUntilFinished / 1000) % 60;
                if (seconds < 10) {
                    tv_time.setTextColor(Color.parseColor("#FFFF6332"));
                    progressBarView.setProgressDrawable(getProgressBarOrange);
                }
                if (seconds < 5) {
                    tv_time.setTextColor(Color.RED);
                    progressBarView.setProgressDrawable(progressBarRed);

                    if (tickTack.isPlaying()) {
                        tickTack.pause();
                        hurryUp.start();
                    }
                }
                tv_time.setText("00:" + seconds);
            }

            @Override
            public void onFinish() { //time is over
                ChangeButtonRoll();
                changeColorRadioButton();
                if (tickTack.isPlaying())
                    tickTack.pause(); // pause tick tack sound
                if (hurryUp.isPlaying())
                    hurryUp.pause(); // pause hurryUp sound
                if (InThisActivity) { //if activity is still alive (not outside)
                    timeOver = MediaPlayer.create(PlayGameActivity.this, R.raw.wrong_1);
                    timeOver.start();  // feedbackSound sound time over
                }
                setProgress(progress, endTime);
                tv_time.setTextColor(Color.RED);
                tv_time.setText("00:0");
                enableDisableRadioButton(false); // radio button not clickable
                buttonConfirmNext.setText("Next");
                feedback.setText("Time   Over :(");
                feedback.setTextColor(Color.RED);

            }
        };
    }

    public void setProgress(int startTime, int endTime) {
        progressBarView.setMax(endTime);
        progressBarView.setSecondaryProgress(endTime);
        progressBarView.setProgress(startTime);

    }

    @Override
    public void onBackPressed() {
        showErrorDataDialog();
    }


    // pop a dialog exit the game
    private void showErrorDataDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit from current game?");
        builder.setIcon(R.drawable.ic_error_black_24dp);
        builder.setCancelable(true);
        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(PlayGameActivity.this, StartGameActivity.class);
                        if (tickTack.isPlaying())
                            tickTack.stop(); // stop tick tack sound
                        if (hurryUp.isPlaying())
                            hurryUp.stop();
                        fn_countdown(); // pause the timer (if the timer continue error sound
                        // feedbackSound on end)
                        finish();  //Kill the activity from which you will go to next activity
                        startActivity(i);
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

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (tickTack.isPlaying()) {
            tickTack.pause(); // stop tick tack sound
        }

        if (hurryUp.isPlaying()) {
            hurryUp.pause();
        }
        InThisActivity = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!startWithoutOnResume) { //this bool for only the first time that onCreate call to
            // onResume
            if (seconds > 5)
                tickTack.start();
            else if (seconds > 0)
                hurryUp.start();
        }
        startWithoutOnResume = false;
        InThisActivity = true;

    }

    // play random feedback sound
    // n=1: feedbackSound correct sound, n=2(else)
    // feedbackSound wrong sound
    private void playFeedbackSound(int n) {
        Random random = new Random();
        int randomMusic = random.nextInt(5);
        isFeedbackSoundPlay = true;
        switch (randomMusic) {
            case 0:
                if (n == 1) {
                    feedbackSound = MediaPlayer.create(PlayGameActivity.this, R.raw.correct_1);
                    feedbackSound.start();
                } else {
                    feedbackSound = MediaPlayer.create(PlayGameActivity.this, R.raw.wrong_1);
                    feedbackSound.start();
                }
                break;
            case 1:
                if (n == 1) {
                    feedbackSound = MediaPlayer.create(PlayGameActivity.this, R.raw.correct_2);
                    feedbackSound.start();
                } else {
                    feedbackSound = MediaPlayer.create(PlayGameActivity.this, R.raw.wrong_2);
                    feedbackSound.start();
                }
                break;
            case 2:
                if (n == 1) {
                    feedbackSound = MediaPlayer.create(PlayGameActivity.this, R.raw.correct_3);
                    feedbackSound.start();
                } else {
                    feedbackSound = MediaPlayer.create(PlayGameActivity.this, R.raw.wrong_3);
                    feedbackSound.start();
                }
                break;
            case 3:
                if (n == 1) {
                    feedbackSound = MediaPlayer.create(PlayGameActivity.this, R.raw.correct_4);
                    feedbackSound.start();
                } else {
                    feedbackSound = MediaPlayer.create(PlayGameActivity.this, R.raw.wrong_4);
                    feedbackSound.start();
                }
                break;
            case 4:
                if (n == 1) {
                    feedbackSound = MediaPlayer.create(PlayGameActivity.this, R.raw.correct_6);
                    feedbackSound.start();
                } else {
                    feedbackSound = MediaPlayer.create(PlayGameActivity.this, R.raw.wrong_5);
                    feedbackSound.start();
                }
                break;
        }
    }
}


