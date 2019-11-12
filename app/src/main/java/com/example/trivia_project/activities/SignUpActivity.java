package com.example.trivia_project.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trivia_project.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import static com.example.trivia_project.interfaces.ServerIP.serverIP;

public class SignUpActivity extends AppCompatActivity {

    private TextView userName;
    private TextView password;
    private TextView rePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Button signUp = findViewById(R.id.sign_up_button);
        TextView title = findViewById(R.id.TextViewTitleCreateUser);
        userName = findViewById(R.id.editTextUserName);
        password = findViewById(R.id.editTextPassword);
        rePassword = findViewById(R.id.editTextRepeatPassword);


        //set and load fonts
        Typeface font = Typeface.createFromAsset(getAssets(), "midnightdrive.otf");
        Typeface font2 = Typeface.createFromAsset(getAssets(), "comic.ttf");
        title.setTypeface(font);
        signUp.setTypeface(font2);

    }

    public void SignUpPressedButton(View view) {
        String userNameFromTextView = userName.getText().toString(); // the user name from text box
        String passwordFromTextView = password.getText().toString(); // password from text box
        String rePasswordFromTextView = rePassword.getText().toString(); // repeat password text box
        if (userNameFromTextView.equals("") || passwordFromTextView.equals("") || rePasswordFromTextView.equals("")) {
            Toast.makeText(SignUpActivity.this, "Some fields are empty", // user name / password empty
                    Toast.LENGTH_SHORT).show();
        }
        else if(!passwordFromTextView.equals(rePasswordFromTextView)) // passwords don't match
            Toast.makeText(SignUpActivity.this, "passwords don't match",
                    Toast.LENGTH_SHORT).show();

        else{ //create user
            createUserInDB(userNameFromTextView,passwordFromTextView);
        }
    }

    private void createUserInDB(final String uName, final String pass) {

        new AsyncTask<Void, Void, Void>() {
            String flag = "newUser";
            String result = "";
            @Override
            protected Void doInBackground(Void... params) {
                Socket clientSocket = null;
                try {
                    clientSocket = new Socket();
                    clientSocket.connect(new InetSocketAddress(serverIP, 1246), 450);
                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    outToServer.writeBytes(flag + "\n");  // sent to server the flag for him to know what to do next
                    outToServer.writeBytes(uName + "\n"); // sent to server the username from the textView username
                    outToServer.writeBytes(pass + "\n"); // sent to server the password from the textView password
                    result = inFromServer.readLine(); // the server return if the user ans password are correct
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    Log.e("SignUpActivity", "SocketTimeoutException - Data from DB isn't arrive");
                } catch (IOException e) {
                    Log.e("SignUpActivity", "IOException Exception - Data from DB isn't arrive");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void nothing) {
                super.onPostExecute(nothing);
                if(result.equals("True")){
                    closeKeyboard();
                    showDialog();
                }
                else if(result.equals("False")) {
                    ShowToast("Error creating user\nUser already exists", Color.RED, R.color.black);
                }

                else
                    ShowToast("Server error! - User not created", Color.RED,R.color.black);

            }
        }.execute();

    }

    // show Toast with massage and different colors
    private void ShowToast(String massage, int colorText, int background) {
        Typeface font = Typeface.createFromAsset(getAssets(), "comic.ttf");
        Toast toast = Toast.makeText(SignUpActivity.this, massage,
                Toast.LENGTH_LONG);
        View view=toast.getView();
        TextView  view1= view.findViewById(android.R.id.message);
        view1.setTextColor(colorText);
        view.setBackgroundResource(background);
        view1.setTypeface(font);
        toast.show();
    }

    // pop a dialog when User created successfully
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("User created successfully");
        builder.setIcon(R.drawable.ic_shield);
        builder.setCancelable(true);
        builder.setPositiveButton(
                "Continue",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        AlertDialog alert11 = builder.create();
        alert11.show();
        Window window = alert11.getWindow();
        assert window != null;
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.y = 270;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
    }

    //hide keyboard
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void returnButton(View view) {
        onBackPressed();
    }
}
