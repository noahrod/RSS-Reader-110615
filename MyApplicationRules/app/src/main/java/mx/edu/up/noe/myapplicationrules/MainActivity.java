package mx.edu.up.noe.myapplicationrules;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.DocumentsContract;
import org.w3c.dom.Element;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class MainActivity extends ActionBarActivity {
    ListView rssListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rssListView = (ListView) findViewById(R.id.listViewfeed);
        rssListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>()));
        new GetFeed().execute("http://adeektos.mx/feed");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public InputStream downloadXML(String xmlString) throws IOException{
        URL url = new URL(xmlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(10000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        return conn.getInputStream();
    }

    class GetFeed extends AsyncTask<String,String,String>{
        private ArrayAdapter<String> adapter;
        protected void onPreExecute(){
            adapter = (ArrayAdapter<String>) rssListView.getAdapter();
        }
        protected String doInBackground(String... feedurl){

            try {
                DocumentBuilderFactory  factory= DocumentBuilderFactory.newInstance();
                DocumentBuilder db = factory.newDocumentBuilder();
                Document dom = db.parse(downloadXML(feedurl[0]));
                NodeList nl = dom.getElementsByTagName("item");
                if(nl != null && nl.getLength() > 0){
                    for(int i=0; i< nl.getLength(); i++){
                        Element el = (Element) nl.item(i);
                        String title = el.getElementsByTagName("title").item(0).getTextContent();
                        String description = el.getElementsByTagName("description").item(0).getTextContent();
                        //
                        //String content = el.getElementsByTagName("content").item(0).getTextContent();
                        //String itunes_image = el.getElementsByTagName("itunes:image").item(0).getTextContent();
                        //
                        Pattern p = Pattern.compile("src=\"(.*?)\"");
                        Matcher m = p.matcher(description);
                        if(m.find()){
                            Log.d("imagen", m.group(1));
                            //InputStream a = downloadXML(m.group(1));
                            //Bitmap bmp = BitmapFactory.decodeStream(a);
                        }
                        publishProgress(title);
                    }
                }


            }catch (Throwable t){

            }
            return null;
        }
        protected void onProgressUpdate(String... values){
            adapter.add(values[0]);
        }
        protected void onPostExecute(String result){

        }

    }

}
















