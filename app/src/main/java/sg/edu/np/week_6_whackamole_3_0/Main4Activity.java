package sg.edu.np.week_6_whackamole_3_0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main4Activity extends AppCompatActivity {

    /* Hint:
        1. This creates the Whack-A-Mole layout and starts a countdown to ready the user
        2. The game difficulty is based on the selected level
        3. The levels are with the following difficulties.
            a. Level 1 will have a new mole at each 10000ms.
                - i.e. level 1 - 10000ms
                       level 2 - 9000ms
                       level 3 - 8000ms
                       ...
                       level 10 - 1000ms
            b. Each level up will shorten the time to next mole by 100ms with level 10 as 1000 second per mole.
            c. For level 1 ~ 5, there is only 1 mole.
            d. For level 6 ~ 10, there are 2 moles.
            e. Each location of the mole is randomised.
        4. There is an option return to the login page.
     */
    private static final String FILENAME = "Main4Activity.java";
    private static final String TAG = "Whack-A-Mole3.0!";
    CountDownTimer readyTimer;
    CountDownTimer moleCountDown;
    CountDownTimer moleTimer;
    private List<Button> buttonList = new ArrayList<>();
    private Integer score;
    private Integer level;
    private TextView scoring;
    private Button getRandomLocation;
    private Button getRandomLocation2;
    private Button backButton;
    private String username;
    MyDBHandler myDBHandler = new MyDBHandler(this, null,null, 1);


    private void readyTimer(){
        /*  HINT:
            The "Get Ready" Timer.
            Log.v(TAG, "Ready CountDown!" + millisUntilFinished/ 1000);
            Toast message -"Get Ready In X seconds"
            Log.v(TAG, "Ready CountDown Complete!");
            Toast message - "GO!"
            belongs here.
            This timer countdown from 10 seconds to 0 seconds and stops after "GO!" is shown.
         */
        moleCountDown = new CountDownTimer(10000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.v(TAG, "Ready Countdown!" + millisUntilFinished / 1000);
                Toast.makeText(getApplicationContext(), "Get Ready in " + millisUntilFinished / 1000 + " seconds!", Toast.LENGTH_SHORT).show();
            }

            public void onFinish() {
                Log.v(TAG, "Ready Countdown Complete!");
                Toast.makeText(getApplicationContext(), "GO!",Toast.LENGTH_SHORT).show();
                moleCountDown.cancel();
                setNewMole();
                placeMoleTimer();
            }
        };
        moleCountDown.start();
    }
    private void placeMoleTimer(){
        /* HINT:
           Creates new mole location each second.
           Log.v(TAG, "New Mole Location!");
           setNewMole();
           belongs here.
           This is an infinite countdown timer.
         */
        int msTime = (11 - level) * 1000;

        moleTimer = new CountDownTimer(msTime,msTime){
            @Override
            public void onTick(long millisUntilFinished) {

            }
            @Override
            public void onFinish() {
                Log.v(TAG,"New Mole Location!");
                reset();
                setNewMole();
                moleTimer.start();
            }
        };
        moleTimer.start();
    }
    private static final int[] BUTTON_IDS = {
            /* HINT:
                Stores the 9 buttons IDs here for those who wishes to use array to create all 9 buttons.
                You may use if you wish to change or remove to suit your codes.*/
            R.id.button1, R.id.button2, R.id.button3,
            R.id.button4, R.id.button5, R.id.button6,
            R.id.button7, R.id.button8, R.id.button9
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        /*Hint:
            This starts the countdown timers one at a time and prepares the user.
            This also prepares level difficulty.
            It also prepares the button listeners to each button.
            You may wish to use the for loop to populate all 9 buttons with listeners.
            It also prepares the back button and updates the user score to the database
            if the back button is selected.
         */
        scoring = (TextView) findViewById(R.id.textViewScore);
        score = 0;
        scoring.setText(String.valueOf(score));
        Intent receivingEnd = getIntent();
        level = receivingEnd.getIntExtra("sendLevel",0);
        username = receivingEnd.getStringExtra("sendUsername");
        backButton = findViewById(R.id.buttonBack);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserScore();
                Intent intent = new Intent(Main4Activity.this, Main3Activity.class);
                intent.putExtra("sendUsername", username);
                startActivity(intent);
            }
        });


        for(final int id : BUTTON_IDS){
            /*  HINT:
            This creates a for loop to populate all 9 buttons with listeners.
            You may use if you wish to remove or change to suit your codes.
            */
            final Button buttonListener = (Button) findViewById(id);
            buttonListener.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    doCheck(buttonListener);
                }
            });
            buttonList.add(buttonListener);
        }
        //readyTimer();
    }
    @Override
    protected void onStart(){
        super.onStart();
        readyTimer();
    }
    private void doCheck(Button checkButton)
    {
        /* Hint:
            Checks for hit or miss
            Log.v(TAG, FILENAME + ": Hit, score added!");
            Log.v(TAG, FILENAME + ": Missed, point deducted!");
            belongs here.
        */
        if (checkButton.getText().toString() == "*"){
            score +=1;
            scoring.setText(Integer.toString(score));
            Log.v(TAG, "Hit, score added!");

        }

        else{
            score -=1;
            scoring.setText(Integer.toString(score));
            Log.v(TAG, "Missed, point deducted!");
        }
        reset();
        setNewMole();
    }

    public void setNewMole()
    {
        /* Hint:
            Clears the previous mole location and gets a new random location of the next mole location.
            Sets the new location of the mole. Adds additional mole if the level difficulty is from 6 to 10.
         */
        Random ran = new Random();
        int randomLocation = ran.nextInt(9);
        int randomLocation2 = ran.nextInt(9);
        getRandomLocation = buttonList.get(randomLocation);
        getRandomLocation2 = buttonList.get(randomLocation2);
        if (level <= 5){
            getRandomLocation.setText("*");
        }
        else{
            getRandomLocation.setText("*");
            getRandomLocation2.setText("*");
        }
    }

    public void reset(){
        getRandomLocation.setText("O");
        getRandomLocation2.setText("O");
    }

    private void updateUserScore()
    {

     /* Hint:
        This updates the user score to the database if needed. Also stops the timers.
        Log.v(TAG, FILENAME + ": Update User Score...");
      */
        if (moleCountDown != null){
            moleCountDown.cancel();
        }
        if (moleTimer != null){
            moleTimer.cancel();
        }
        UserData userData = myDBHandler.findUser(username);
        int levels = userData.getLevels().indexOf(level);
        int scores = userData.getScores().get(levels);
        if (scores < score){
            Log.v(TAG, FILENAME + ": Update User Score...");
            userData.getScores().set(levels,score);
            myDBHandler.deleteAccount(username);
            myDBHandler.addUser(userData);
        }
    }

}
