package com.example.matt.rssprocessingassignment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingsActivity extends AppCompatActivity
{
    private SharedPreferences sharedPreferences;
    private RadioButton topNews;
    private RadioButton topSports;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences("general prefs", MODE_PRIVATE);

        RadioGroup defaultFeed = (RadioGroup)findViewById(R.id.rgDefaultFeed);
        topNews = (RadioButton)findViewById(R.id.rbTopNews);
        topSports = (RadioButton)findViewById(R.id.rbTopSports);
        defaultFeed.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                saveRadioButtons();
            }
        });


    }

    @Override
    protected void onResume()
    {
        super.onResume();
        loadRadioButtons();
    }

    public void loadRadioButtons()
    {
        topNews.setChecked(sharedPreferences.getBoolean("top news", false));
        topSports.setChecked(sharedPreferences.getBoolean("top sports", false));
    }

    public void saveRadioButtons()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("top news", topNews.isChecked());
        editor.putBoolean("top sports", topSports.isChecked());
        editor.apply();
    }
}
