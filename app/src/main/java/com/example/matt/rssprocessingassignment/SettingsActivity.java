package com.example.matt.rssprocessingassignment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class SettingsActivity extends AppCompatActivity
{
    private SharedPreferences sharedPreferences;
    private RadioButton topNews;
    private RadioButton topSports;
    private SharedPreferences.Editor editor;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences("general prefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

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

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.numberOfArticles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
            {
                int selectedPositon = spinner.getSelectedItemPosition();
                editor.putInt("number of articles", selectedPositon);
                editor.apply();
            }

            public void onNothingSelected(AdapterView<?> adapterView)
            {
                editor.putInt("number of articles", 4);
                editor.apply();
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        loadRadioButtons();
        loadSpinner();
    }

    public void loadRadioButtons()
    {
        topNews.setChecked(sharedPreferences.getBoolean("top news", false));
        topSports.setChecked(sharedPreferences.getBoolean("top sports", false));
    }

    public void saveRadioButtons()
    {
        editor.putBoolean("top news", topNews.isChecked());
        editor.putBoolean("top sports", topSports.isChecked());
        editor.apply();
    }

    public void loadSpinner()
    {
        spinner.setSelection(sharedPreferences.getInt("number of articles", 4));
    }
}
