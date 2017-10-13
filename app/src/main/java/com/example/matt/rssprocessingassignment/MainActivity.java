package com.example.matt.rssprocessingassignment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity
{
    private ArrayList<NewsArticle> newsArticle;
    private String urlAddress;
    private NewsAdapter newsAdapter;
    private ListView listView;
    private SharedPreferences sharedPreferences;
    private int numberOfArticles;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("general prefs", MODE_PRIVATE);

        listView = (ListView)findViewById(R.id.lvFeed);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent intent = new Intent(MainActivity.this, ArticleDetailsActivity.class);
                intent.putExtra("title", newsArticle.get(i).getArticleTitle());
                intent.putExtra("link", newsArticle.get(i).getLink());
                intent.putExtra("description", newsArticle.get(i).getDescription());
                intent.putExtra("pubDate", newsArticle.get(i).getPublishDate());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        boolean topNews;
        boolean topSports;

        setNumberOfArticles();

        topNews = sharedPreferences.getBoolean("top news", false);
        topSports = sharedPreferences.getBoolean("top sports", false);

        if(topNews)
        {
            urlAddress = "http://www.winnipegsun.com/g00/3_c-6bbb.bnssnujlx78zs.htr_/c-6RTWJUMJZX77x24myyux3ax2fx2fbbb.bnssnujlx78zs.htrx2fsjbx78x2fwx78x78.crq_$/$/$";
        }
        else if(topSports)
        {
            urlAddress = "http://www.winnipegsun.com/g00/3_c-6bbb.bnssnujlx78zs.htr_/c-6RTWJUMJZX77x24myyux3ax2fx2fbbb.bnssnujlx78zs.htrx2fx78utwyx78x2fwx78x78.crq_$/$/$";
        }
        else
        {
            urlAddress = "http://www.winnipegsun.com/g00/3_c-6bbb.bnssnujlx78zs.htr_/c-6RTWJUMJZX77x24myyux3ax2fx2fbbb.bnssnujlx78zs.htrx2fsjbx78x2fwx78x78.crq_$/$/$";
        }

        processRss(null);
    }

    /**
     * Method to begin processing rss feed
     * @param view
     */
    public void processRss(View view)
    {
            RssProcessorTask rssTask = new RssProcessorTask();
            rssTask.execute();
    }

    /**
     * Custom class that extends AsyncTask class
     */
    class RssProcessorTask extends AsyncTask
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            Log.d("Matt", "onPreExecute");
        }

        @Override
        protected Object doInBackground(Object[] objects)
        {
            URL rssUrl = null;

            try
            {
                rssUrl = new URL(urlAddress);
            } catch (MalformedURLException e)
            {
                e.printStackTrace();
            }

            SAXParser saxParser = null;

            try
            {
                saxParser = SAXParserFactory.newInstance().newSAXParser();
            } catch (ParserConfigurationException e)
            {
                e.printStackTrace();
            } catch (SAXException e)
            {
                e.printStackTrace();
            }

            SunHandler sunHandler = new SunHandler();

            HttpURLConnection localNewsConnection = null;

            try
            {
                localNewsConnection = (HttpURLConnection) rssUrl.openConnection();
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            try
            {
                saxParser.parse(localNewsConnection.getInputStream(), sunHandler);
            } catch (SAXException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            Log.d("Matt", "doInBackground");

            return null;
        }

        @Override
        protected void onPostExecute(Object o)
        {
            super.onPostExecute(o);
            Log.d("Matt", "onPostExecute");

            newsAdapter = new NewsAdapter(MainActivity.this, R.layout.list_item, newsArticle);
            listView.setAdapter(newsAdapter);
        }

        class SunHandler extends DefaultHandler
        {
            private boolean inTitle, inPubDate, inDescription, inLink;
            private StringBuilder builder;
            private NewsArticle article;
            @Override
            public void startDocument() throws SAXException
            {
                super.startDocument();
                Log.d("Matt", "startDocument");
                builder = new StringBuilder();
                newsArticle = new ArrayList<NewsArticle>();
            }

            @Override
            public void endDocument() throws SAXException
            {
                super.endDocument();
                Log.d("Matt", "endDocument");
            }

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
            {
                super.startElement(uri, localName, qName, attributes);
                Log.d("Matt", "startElement: " + qName);

                // Create a new instance of news article when element item is parsed
                if (qName.equalsIgnoreCase("item"))
                {
                    this.article = new NewsArticle();
                }

                // Determine if article is null and if it isn't determine which element is being parsed and change it's boolean to true
                if(this.article != null)
                {
                    if (qName.equalsIgnoreCase("title"))
                    {
                        inTitle = true;
                    }
                    else if (qName.equalsIgnoreCase("link"))
                    {
                        inLink = true;
                    }
                    else if (qName.equalsIgnoreCase("description"))
                    {
                        inDescription = true;
                    }
                    else if (qName.equalsIgnoreCase("pubDate"))
                    {
                        inPubDate = true;
                    }
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException
            {
                super.endElement(uri, localName, qName);
                Log.d("Matt", "endElement: " + qName);

                // Determine if article is null
                if(this.article != null)
                {
                    // Determine what element is being parsed and then set the articles property based on that
                    if (qName.equalsIgnoreCase("title"))
                    {
                        inTitle = false;

                        // If the current article title is null set it
                        if(article.getArticleTitle() == null)
                        {
                            article.setArticleTitle(builder.toString().trim());
                        }
                    }
                    else if(qName.equalsIgnoreCase("link"))
                    {
                        inLink = false;
                        article.setLink(builder.toString());
                    }
                    else if (qName.equalsIgnoreCase("description"))
                    {
                        inDescription = false;
                        article.setDescription(builder.toString());
                    }
                    else if (qName.equalsIgnoreCase("pubDate"))
                    {
                        inPubDate = false;
                        article.setPublishDate(builder.toString());
                    }
                    else if(qName.equalsIgnoreCase("source"))
                    {
                        // Set the number of articles to be displayed
                        if(newsArticle.size() <= numberOfArticles - 1)
                        {
                            newsArticle.add(article);
                        }
                    }
                    builder.setLength(0);
                }
            }

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException
            {
                super.characters(ch, start, length);

                if(this.article != null)
                {
                    if (inTitle)
                    {
                        builder.append(ch, start, length);
                    }
                    else if (inLink)
                    {
                        builder.append(ch, start, length);
                    }
                    else if (inDescription)
                    {
                        builder.append(ch, start, length);
                    }
                    else if (inPubDate)
                    {
                        builder.append(ch, start, length);
                    }
                }

                Log.d("Matt", "characters: " + builder.toString());
            }

            @Override
            public void warning(SAXParseException e) throws SAXException
            {
                super.warning(e);
                Log.d("Matt", "warning");
            }

            @Override
            public void error(SAXParseException e) throws SAXException
            {
                super.error(e);
                Log.d("Matt", "error");
            }

            @Override
            public void fatalError(SAXParseException e) throws SAXException
            {
                super.fatalError(e);
                Log.d("Matt", "fatalError");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.localNews:
                urlAddress = "http://www.winnipegsun.com/g00/3_c-6bbb.bnssnujlx78zs.htr_/c-6RTWJUMJZX77x24myyux3ax2fx2fbbb.bnssnujlx78zs.htrx2fsjbx78x2fwx78x78.crq_$/$/$";
                processRss(null);
                return true;
            case R.id.localSports:
                urlAddress = "http://www.winnipegsun.com/g00/3_c-6bbb.bnssnujlx78zs.htr_/c-6RTWJUMJZX77x24myyux3ax2fx2fbbb.bnssnujlx78zs.htrx2fx78utwyx78x2fwx78x78.crq_$/$/$";
                processRss(null);
                return true;
            case R.id.settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.refresh:
                processRss(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class NewsAdapter extends ArrayAdapter<NewsArticle>
    {
        private ArrayList<NewsArticle> items;

        public NewsAdapter(Context context, int textViewResourceId, ArrayList<NewsArticle> items)
        {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            View v = convertView;

            if(v == null)
            {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item, null);
            }

            NewsArticle o = items.get(position);

            if(o != null)
            {
                TextView tt = (TextView)v.findViewById(R.id.toptext);
                TextView bt = (TextView)v.findViewById(R.id.bottomtext);

                if (tt != null)
                {
                    tt.setText(o.getArticleTitle());
                }
                if (bt != null)
                {
                    bt.setText(o.getPublishDate());
                }
            }

            return v;
        }
    }
    class NewsArticle
    {
        private String articleTitle;
        private String publishDate;
        private String description;
        private String link;

        public NewsArticle() {}

        public NewsArticle(String articleTitle, String publishDate, String description, String link)
        {
            this.articleTitle = articleTitle;
            this.publishDate = publishDate;
            this.description = description;
            this.link = link;
        }

        public String getArticleTitle() { return articleTitle; }

        public void setArticleTitle(String articleTitle) { this.articleTitle = articleTitle; }

        public String getPublishDate() { return publishDate; }

        public void setPublishDate(String publishDate) { this.publishDate = publishDate; }

        public String getDescription() { return description; }

        public void setDescription(String description) { this.description = description; }

        public String getLink() { return link;}

        public void setLink(String link) { this.link = link; }
    }

    public void setNumberOfArticles()
    {
        int articlesIndex = sharedPreferences.getInt("number of articles", 4);

        if(articlesIndex == 4)
        {
            numberOfArticles = 100;
        }
        else
        {
            String[] stringArray = getResources().getStringArray(R.array.numberOfArticles);
            numberOfArticles = Integer.parseInt(stringArray[articlesIndex]);
        }
    }
}
