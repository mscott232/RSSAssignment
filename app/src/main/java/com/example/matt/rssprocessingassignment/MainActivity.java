package com.example.matt.rssprocessingassignment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
    private ArrayList<String> title;
    private ArrayList<String> pubDate;
    private StringBuilder builder;
    private String urlAddress = "http://www.winnipegsun.com/g00/3_c-6bbb.bnssnujlx78zs.htr_/c-6RTWJUMJZX77x24myyux3ax2fx2fbbb.bnssnujlx78zs.htrx2fsjbx78x2fwx78x78.crq_$/$/$";
    private NewsAdapter newsAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listView = (ListView)findViewById(R.id.lvFeed);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        processRss(null);
    }

    private boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void processRss(View view)
    {
            RssProcessorTask rssTask = new RssProcessorTask();
            rssTask.execute();
    }

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

            newsAdapter = new NewsAdapter(MainActivity.this, R.layout.list_item, title, "title");
            //newsAdapter = new NewsAdapter(MainActivity.this, R.layout.list_item, pubDate, "pubDate");
            listView.setAdapter(newsAdapter);
        }

        class SunHandler extends DefaultHandler
        {
            private boolean inTitle, inPubDate;

            // Initialization block
            {
                title = new ArrayList<String>();
                pubDate = new ArrayList<String>();

            }

            @Override
            public void startDocument() throws SAXException
            {
                super.startDocument();
                Log.d("Matt", "startDocument");
            }

            @Override
            public void endDocument() throws SAXException
            {
                super.endDocument();
                Log.d("Matt", "endDocument");

                for (String t : pubDate)
                {
                    Log.d("Matt", t);
                }
            }

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
            {
                super.startElement(uri, localName, qName, attributes);
                Log.d("Matt", "startElement: " + qName);

                builder = new StringBuilder();

                if (qName.equals("title"))
                {
                    inTitle = true;
                } else if (qName.equals("pubDate"))
                {
                    inPubDate = true;
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException
            {
                super.endElement(uri, localName, qName);
                Log.d("Matt", "endElement: " + qName);

                if (qName.equals("title"))
                {
                    inTitle = false;
                } else if (qName.equals("pubDate"))
                {
                    inPubDate = false;
                }
            }

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException
            {
                super.characters(ch, start, length);
                String s = new String(ch, start, length);
                Log.d("Matt", "characters: " + s);
                builder.append(ch, start, length);

                if (inTitle)
                {
                    title.add(builder.toString());
                } else if (inPubDate)
                {
                    pubDate.add(s);
                }
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class NewsAdapter extends ArrayAdapter<String>
    {
        private ArrayList<String> items;
        private String typeOfList;

        public NewsAdapter(Context context, int textViewResourceId, ArrayList<String> items, String typeOfList)
        {
            super(context, textViewResourceId, items);
            this.items = items;
            this.typeOfList = typeOfList;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            View v = convertView;

            if(v == null)
            {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item, null);
            }

            String o = items.get(position);

            if(o != null)
            {
                TextView tt = (TextView)v.findViewById(R.id.toptext);
                TextView bt = (TextView)v.findViewById(R.id.bottomtext);

                if(typeOfList.equals("title"))
                {
                    if (tt != null)
                    {
                        tt.setText(o.toString());
                    }
                }
                if(typeOfList.equals("pubDate"))
                {
                    if (bt != null)
                    {
                        bt.setText(o.toString());
                    }
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

        public NewsArticle(String articleTitle, String publishDate, String description)
        {
            this.articleTitle = articleTitle;
            this.publishDate = publishDate;
            this.description = description;
        }

        public String getArticleTitle() { return articleTitle; }

        public String getPublishDate() { return publishDate; }

        public String getDescription() { return description; }
    }
}
