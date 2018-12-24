package com.example.kabicin.rhythmkk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    private ImageButton startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get handle on Start Button
        startButton = findViewById(R.id.start_button);
        setStartButtonListener();
    }


    /**
     * Sets the OnClickListener for the Start Button
     */
    private void setStartButtonListener() {
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Switch to GameActivity on click.
                Intent gameIntent = new Intent(MainActivity.this, GameActivity.class);
                gameIntent.putExtra("username", "kabicin");
                startActivity(gameIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }


}
