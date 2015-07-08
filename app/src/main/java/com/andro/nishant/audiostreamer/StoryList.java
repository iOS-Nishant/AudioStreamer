package com.andro.nishant.audiostreamer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by NishantThite on 08/07/15.
 */
public class StoryList extends Activity {

    ListView storyListView;
    ProgressDialog pdInStoryList;
    String strJson;
    ArrayList<String> storyListValues=new ArrayList<String>();
    ArrayAdapter<String> storyArrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_list);

        String stringData = "";
        Intent intent = getIntent();
        if (null != intent) {
            stringData = intent.getStringExtra("USERNAME");

        }

        Log.d(MainActivity.LogTag,"stringData");

        storyListView = (ListView) findViewById(R.id.storyListViewId);

        // Defined Array values to show in ListView
        storyListValues.add("Test - 1");
        storyListValues.add("Test - 2");
        storyListValues.add("Test - 3");
        storyListValues.add("Test - 4");
        storyListValues.add("Test - 5");
        storyListValues.add("Test - 6");
        storyListValues.add("Test - 7");
        storyListValues.add("Test - 9");
        storyListValues.add("Test - 10");
        storyListValues.add("Test - 11");
        storyListValues.add("Test - 12");
        storyListValues.add("Test - 13");
        storyListValues.add("Test - 14");
        storyListValues.add("Test - 15");


        storyArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, storyListValues);

        // Assign adapter to ListView
        storyListView.setAdapter(storyArrayAdapter);

        // ListView Item Click Listener
        storyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = (String) storyListView.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
                        .show();

            }

        });
    }

    public void loadStoryDataOnBackThread (View v){

        pdInStoryList = new ProgressDialog(this);
        pdInStoryList.setMessage("Loading story list..");

        pdInStoryList.show();

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                loadJsonString();
            }
        });

        thread.start();
    }

    public void loadStoryAsAsyncTask(View v){
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            loadStoryDataOnBackThread(v);
        } else {
            // display error
            String errMsg = "";
            if (networkInfo != null){
                errMsg = "ExtraInfo: " + networkInfo.getExtraInfo() + ". Reason: " + networkInfo.getReason();
            }else{
                errMsg = "No connection.. ";
            }
            String[] errArr = {errMsg};
            updateListViewWithDataAndBackGroundColor(errArr, Color.RED);
        }
    }

    int newBackGrounfColorForStoryList =0 ;
    public void updateListViewWithDataAndBackGroundColor(String[] detailsArr, int newBgColor){
        storyListValues.clear();

        for (int j=0; j<detailsArr.length ; j++){
            storyListValues.add(detailsArr[j]);
        }

        newBackGrounfColorForStoryList = newBgColor;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run( ) {
                if (pdInStoryList != null){
                    pdInStoryList.dismiss();
                }
                storyListView.setBackgroundColor(newBackGrounfColorForStoryList);
                storyArrayAdapter.notifyDataSetChanged();
            }
        });
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {

            }
            return "Nishnat doInBackground";
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            String[] newValueArr = {result};
            updateListViewWithDataAndBackGroundColor(newValueArr, Color.LTGRAY);
        }
    }

    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(MainActivity.LogTag, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    public void loadJsonString(){
        try {

//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);

            // Create a URL for the desired page
            URL url = new URL("https://iosish.iriscouch.com/nishant/TestNishant");

            Log.e(MainActivity.LogTag, "\nLoding Story data from server\n");

            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String str;
            while ((str = in.readLine()) != null) {
                // str is one line of text; readLine() strips the newline character(s)
                // Log.e(LogTag, "\nData from server : "+str+"\n");
                strJson = str;
            }
            Log.e(MainActivity.LogTag, "\nStory data: " + strJson + "\n");
            // strJson = str;
            in.close();


            if(strJson.length()>1){
                parseMyJson();
            }
        } catch (Exception e) {
            String errStr = "Error in load Story JsonString: " + e;
            // this.errorTextView.setText(errStr);
            Log.e(MainActivity.LogTag, errStr);
        }
    }

    public void parseMyJson() {
        //String data = "";
        try {
            JSONObject jsonRootObject = new JSONObject(strJson);

            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonArray = jsonRootObject.optJSONArray("Employee");

            String[] newValueArr = new String[jsonArray.length()];
            //Iterate the jsonArray and print the info of JSONObjects
            for(int i=0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                //String id = jsonObject.optString("id");
                String name = jsonObject.optString("name");
                newValueArr[i]=name;

                //Log.e(MainActivity.LogTag, name);

                //String salary = jsonObject.optString("salary");

                //data += "Node"+i+" : \n id= "+ id +" \n Name= "+ name +" \n Salary= "+ salary +" \n ";
            }

            updateListViewWithDataAndBackGroundColor(newValueArr, Color.LTGRAY);

        } catch (JSONException e) {
            String errStr = "Error in parseMyJson in StoryList: " + e.getMessage();
            //this.errorTextView.setText(errStr);
            Log.e(MainActivity.LogTag, errStr);
        }
    }



}
