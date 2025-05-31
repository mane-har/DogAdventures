package com.enam.gamedog;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView aboutText = findViewById(R.id.aboutText);
        aboutText.setText("LosTail is a 2D adventure game that tells the heartfelt story of Buddy, a small black-and-white dog lost far from home. As Buddy, you'll travel through cities and forests, facing challenges and exploring the world in search of the one place that matters most — home.\n\n" +
                "Features:\n" +
                "• 6 exciting levels across 2 different worlds\n" +
                "• City and Forest environments\n" +
                "• Collect treats and avoid obstacles\n" +
                "• Simple one-touch controls\n\n" +
                "With simple controls, a touching atmosphere, and a journey full of hope, LosTail is a game about loyalty, love, and never giving up — no matter how far away you are.\n\n" +
                "Join Buddy on this emotional journey and help him find his way back to where he truly belongs.");
    }
} 