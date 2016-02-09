package com.example.niklasgame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

public class GraphActivity extends Activity
{
    private static final int ASK_FOR_ANSWER = 10;
    String[] start_names = {"ACE", "NIK", "JIM"};
    String[] start_highscores = {"25", "15", "5"};
    String alertvalue = "Nik";
    String score_points = "0";
    String filename = "game_score";
    int thirdPointScore = 0;

    @Override
     public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.main);

        /**String[][] newscores = new String[3][2];
        newscores[0][0] = alertvalue; newscores[0][1] = score_points;
        newscores[1][0] = alertvalue; newscores[1][1] = score_points;
        newscores[2][0] = alertvalue; newscores[2][1] = score_points;    
        
        writeFile(newscores);    /**/
        String[][] scores = readFile();
        if(scores != null) { setTextViews(scores);}

    }

    private String[][] readFile () {
        String[][] values = new String[3][2];
        values[0][0] = start_names[0]; values[0][1] = start_highscores[0];
        values[1][0] = start_names[1]; values[1][1] = start_highscores[1];
        values[2][0] = start_names[2]; values[2][1] = start_highscores[2];

        try {
            File aFile = new File(getFilesDir(), filename);
            
            if(aFile.canRead() && aFile.exists()) {

                BufferedReader in = new BufferedReader(new FileReader(aFile));
                String line;
                int id = 0;
                while ((line = in.readLine()) != null && id <3) {
                    String[] v = line.split("#;");
                    values[id][0] = v[0];
                    values[id][1] = v[1];
                    id++;
                }
                in.close();
            }

        } catch (FileNotFoundException fnfe) {
             Log.i("/************************/ File not found", "Not found");
             writeFile(values);

        } catch (Exception e) {
             Log.i("File read problem :", e.getMessage());
        }
        return values;
    }

    public void writeFile(String[][]  scores) {
        try {
            File aFile = new File(getFilesDir(), filename);
            BufferedWriter out = new BufferedWriter(new FileWriter(aFile));

            int place = 0;
            int the_score =  Integer.parseInt(score_points);
            int first_score = Integer.parseInt(scores[0][1].trim());
            int second_score = Integer.parseInt(scores[1][1].trim());
            int third_score = Integer.parseInt(scores[2][1].trim());

            if(the_score >= first_score) { place = 1;
            } else if(the_score >= second_score) { place = 2;
            } else if(the_score >= third_score) { place = 3; }
            
            if (place == 0) { out.close(); return; }
            
            if(place == 1){
                scores[2][0] = scores[1][0]; scores[2][1] = scores[1][1];
                scores[1][0] = scores[0][0]; scores[1][1] = scores[0][1];
                scores[0][0] = alertvalue; scores[0][1] = score_points;
            } else if(place == 2){
                scores[2][0] = scores[1][0]; scores[2][1] = scores[1][1];
                scores[1][0] = alertvalue; scores[1][1] = score_points;
            } else if(place == 3){
                scores[2][0] = alertvalue; scores[2][1] = score_points;
            }
            
            out.write(scores[0][0] + "#;" + scores[0][1] + "\n");
            out.write(scores[1][0] + "#;" + scores[1][1] + "\n");
            out.write(scores[2][0] + "#;" + scores[2][1] + "\n");
            out.close();
        } catch (java.io.IOException e) {
            //do something if an IOException occurs.
        }
    }
    
    public void setTextViews(String[][] scores) {
        TextView tvscore = (TextView) findViewById(R.id.first_place);
        tvscore.setText(scores[0][0] + " " + scores[0][1]);

        tvscore = (TextView) findViewById(R.id.second_place);
        tvscore.setText(scores[1][0] + " " + scores[1][1]);

        tvscore = (TextView) findViewById(R.id.third_place);
        tvscore.setText(scores[2][0] + " " + scores[2][1]);

        thirdPointScore = Integer.parseInt(scores[2][1].trim());
    }
    
    public void alertInput() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Enter Name:");
        //alert.setMessage("Enter Name :");// Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                alertvalue = input.getText().toString();
                String[][] scores = readFile();
                writeFile(scores);
                scores = readFile();
                setTextViews(scores);
                return;
            }
        });
        alert.show();
    }

    public void startGameActivity (View v) {
        //startActivity(new Intent(this, GraphGame.class));
        startActivityForResult(new Intent(this, GraphGame.class), ASK_FOR_ANSWER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case ASK_FOR_ANSWER:
                score_points = data.getStringExtra("Points");
                TextView response = (TextView) findViewById(R.id.points);
                response.setText("Points: " + score_points);

                int the_score =  Integer.parseInt(score_points);
                if(the_score >= thirdPointScore) {
                    alertInput();
                }
                //startActivityForResult(new Intent(this, GraphGame.class), ASK_FOR_ANSWER);
                break;
        }
    }
}