package com.example.matt.rssprocessingassignment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ArticleDetailsActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_details);

        Intent intent = getIntent();

        String title = intent.getStringExtra("title");
        TextView textViewTitle = (TextView)findViewById(R.id.tvTitle);
        textViewTitle.setText(title);

        String description = intent.getStringExtra("description");
        TextView textViewDescription = (TextView)findViewById(R.id.tvDescription);
        textViewDescription.setText(description);

        String pubDate = intent.getStringExtra("pubDate");
        TextView textViewPubDate = (TextView)findViewById(R.id.tvPubDate);
        textViewPubDate.setText(pubDate);

        String link = intent.getStringExtra("link");
        TextView textViewLink = (TextView)findViewById(R.id.tvLink);
        textViewLink.setText(link);
    }
}
