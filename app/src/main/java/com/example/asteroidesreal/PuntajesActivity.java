package com.example.asteroidesreal;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.EdgeToEdge;

import java.util.List;

public class PuntajesActivity extends AppCompatActivity {
    
    private ScoreDatabaseHelper dbHelper;
    private ListView listViewPuntajes;
    private TextView tvSinPuntajes;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_puntajes);
        
        // Ajustar padding por sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // Fondo animado
        setupBackgroundAnimation();
        
        // Botón de regresar
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        
        listViewPuntajes = findViewById(R.id.listViewPuntajes);
        tvSinPuntajes = findViewById(R.id.tvSinPuntajes);
        
        dbHelper = new ScoreDatabaseHelper(this);
        cargarPuntajes();
    }
    
    private void cargarPuntajes() {
        List<Score> scores = dbHelper.getAllScores();
        
        if (scores.isEmpty()) {
            listViewPuntajes.setVisibility(View.GONE);
            tvSinPuntajes.setVisibility(View.VISIBLE);
        } else {
            listViewPuntajes.setVisibility(View.VISIBLE);
            tvSinPuntajes.setVisibility(View.GONE);
            
            ScoreAdapter adapter = new ScoreAdapter(this, scores);
            listViewPuntajes.setAdapter(adapter);
        }
    }
    
    private void setupBackgroundAnimation() {
        androidx.constraintlayout.widget.ConstraintLayout layout = findViewById(R.id.main);
        android.animation.ArgbEvaluator evaluator = new android.animation.ArgbEvaluator();
        final int[] colors = new int[]{
                android.graphics.Color.parseColor("#4B0082"), // morado
                android.graphics.Color.parseColor("#000033"), // azul oscuro
                android.graphics.Color.parseColor("#FF00FF")  // fucsia
        };

        android.graphics.drawable.GradientDrawable gradientDrawable = new android.graphics.drawable.GradientDrawable(
                android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{colors[0], colors[1]}
        );
        layout.setBackground(gradientDrawable);

        android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(8000);
        animator.setRepeatCount(android.animation.ValueAnimator.INFINITE);
        animator.setRepeatMode(android.animation.ValueAnimator.REVERSE);
        animator.addUpdateListener(valueAnimator -> {
            float fraction = valueAnimator.getAnimatedFraction();
            int colorStart = (Integer) evaluator.evaluate(fraction, colors[0], colors[1]);
            int colorEnd = (Integer) evaluator.evaluate(fraction, colors[1], colors[2]);
            gradientDrawable.setColors(new int[]{colorStart, colorEnd});
        });
        animator.start();
    }
}







