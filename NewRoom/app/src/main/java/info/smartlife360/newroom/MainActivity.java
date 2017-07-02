package info.smartlife360.newroom;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

//
//    https://newsapi.org/v1/articles?source=bbc-news&sortBy=top&apiKey=fa1eac0e786a4dc6a8a6dce075d26835
//    https://newsapi.org/v1/articles?source=business-insider-uk&sortBy=top&apiKey=fa1eac0e786a4dc6a8a6dce075d26835
//    https://newsapi.org/v1/articles?source=hacker-news&sortBy=top&apiKey=fa1eac0e786a4dc6a8a6dce075d26835
//    https://newsapi.org/v1/articles?source=the-hindu&sortBy=top&apiKey=fa1eac0e786a4dc6a8a6dce075d26835

    GridLayout iconGrid;
    ListView newsList;
    WebView browser;
    String description_arr[]=new String[10];
    String title_arr[]=new String[10];
    String links_arr[]=new String[10];
    String links[]={"https://newsapi.org/v1/articles?source=bbc-news&sortBy=top&apiKey=fa1eac0e786a4dc6a8a6dce075d26835",
            "https://newsapi.org/v1/articles?source=business-insider-uk&sortBy=top&apiKey=fa1eac0e786a4dc6a8a6dce075d26835",
            "https://newsapi.org/v1/articles?source=the-hindu&sortBy=top&apiKey=fa1eac0e786a4dc6a8a6dce075d26835",
            "https://newsapi.org/v1/articles?source=hacker-news&sortBy=top&apiKey=fa1eac0e786a4dc6a8a6dce075d26835"};



    @Override
    public void onBackPressed(){

        int b=newsList.getVisibility();//4means hide, 0 means show
        int c=iconGrid.getVisibility();
        if(b==0)
        {
            newsList.setVisibility(View.INVISIBLE);
            iconGrid.setVisibility(View.VISIBLE);

        }
        else{
            finish();
        }
        Log.i("Back: ",b+"Pressed");
    }


    public void channelClicked(View view)
    {
        int channelClicked=Integer.parseInt(view.getTag().toString());
        new DownloadData().execute(links[channelClicked]);
        iconGrid.setVisibility(View.INVISIBLE);
        newsList.setVisibility(View.VISIBLE);

    }


    public class DownloadData extends AsyncTask<String,Void,String>{

        URL url;
        HttpURLConnection connection;
        InputStream is;
        InputStreamReader ir;
        String result="";

        @Override
        protected String doInBackground(String... urls) {
            try{
                url=new URL(urls[0]);
                connection=(HttpURLConnection)url.openConnection();
                connection.connect();
                is=connection.getInputStream();
                ir=new InputStreamReader(is);
                int readed=ir.read();
                while (readed!=-1) {
                    char c=(char)readed;
                    result+=c;
                    readed=ir.read();
                }

                return result;

            }
            catch (Exception e)
            {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Something Gone Wrong.......", Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{

                JSONObject json=new JSONObject(s);
                JSONArray jsonArray=json.getJSONArray("articles");


                Log.i("Length is:",jsonArray.length()+"");
                for(int j=0;j<jsonArray.length();j++){
                    JSONObject jsonArticle=jsonArray.getJSONObject(j);

                    ///Fetching Data From API's
                    String title=jsonArticle.getString("title");
                    String description=jsonArticle.getString("description");
                    String author=jsonArticle.getString("author");
                    String url=jsonArticle.getString("url");
                    String publishedAt=jsonArticle.getString("publishedAt");
                    String urlToImage=jsonArticle.getString("urlToImage");
                    ///Fetching Complete

                    //Setting Data inside Arrays//
                    title_arr[j]=title;
                    description_arr[j]=description;
                    links_arr[j]=url;
                    newsList.setAdapter(new CustomeAdapter());

                    ///Setted//

//                    System.out.print(title+"\n"
//                            +author+"  "+publishedAt+"\n"
//                            +description+"\n"
//                            +url+"\n");

                    /////////////EveryThing Fine Here................. To be Continue...
                }

            }
            catch (Exception e){
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Something Gone Wrong.......", Toast.LENGTH_LONG).show();
            }

        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iconGrid=(GridLayout)findViewById(R.id.iconGrid);
        browser=(WebView)findViewById(R.id.browser);
        newsList=(ListView) findViewById(R.id.newsList);

        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                newsList.setVisibility(View.INVISIBLE);
                browser.setVisibility(View.VISIBLE);
                browser.loadUrl(links_arr[position]);
            }
        });

        Log.i("List ","Visible");
    }


    class CustomeAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return title_arr.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView=getLayoutInflater().inflate(R.layout.listitemlayout,null);
            TextView titleTextView=(TextView)convertView.findViewById(R.id.titleTextView);
            TextView descriptionTextView=(TextView)convertView.findViewById(R.id.descriptionTextView);
            titleTextView.setText(title_arr[position]);
            descriptionTextView.setText(description_arr[position]);
            Log.i("Result",title_arr[position]+"");
            return convertView;
        }
    }
}
