package com.andro.nishant.audiostreamer;


import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.ProgressDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity extends ActionBarActivity implements OnClickListener {

    AudioPlayerView audioPlayerViewObj;
    ProgressDialog pd;
    final String LogTag = "StreamAudioDemo";
    Button mainButton ;
    TextView errorTextView;
    String strJson= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainButton = (Button)findViewById(R.id.play);
        mainButton.setOnClickListener(this);

        errorTextView = (TextView)findViewById(R.id.errorTextViewId);
        errorTextView.setText("Click above button to start..");

        audioPlayerViewObj = new AudioPlayerView(getApplicationContext());
        audioPlayerViewObj.mainActivityObj = this;
        audioPlayerViewObj.audioSeekBar = (SeekBar) findViewById(R.id.audioSeekBarId);

//        audioPlayerViewObj.audioSeekBar.setProgress(40);
//        audioPlayerViewObj.audioSeekBar.setSecondaryProgress(75);
    }

    public void performNetworkOperationOnBackThread (View v){

        if (audioPlayerViewObj.mp != null){
            audioPlayerViewObj.mp.stop();
            audioPlayerViewObj.mp.release();
        }


        errorTextView.setText("Loading..");
        pd = new ProgressDialog(this);
        pd.setMessage("Loading..");
        pd.show();

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                loadJsonString();
            }
        });

        thread.start();
    }

    public void loadJsonString(){
        try {

//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);

            // Create a URL for the desired page
            URL url = new URL("https://iosish.iriscouch.com/nishant/TestNishant");

            Log.e(LogTag, "\nLoding data from server\n");

            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
            while ((str = in.readLine()) != null) {
                // str is one line of text; readLine() strips the newline character(s)
               // Log.e(LogTag, "\nData from server : "+str+"\n");
                strJson = str;
            }
            Log.e(LogTag, "\nAfter completion from server strJson: "+strJson+"\n");
           // strJson = str;
            in.close();

            if(strJson.length()>1){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        parseMyJson();
                    }
                });
            }
        } catch (Exception e) {
            String errStr = "Error in loadJsonString: " + e;
           // this.errorTextView.setText(errStr);
            Log.e(LogTag, errStr);
        }
    }

    public void parseMyJson() {
        pd.dismiss();
        TextView jsonOutputTextView = (TextView) findViewById(R.id.errorTextViewId);
        String data = "";
        try {
            JSONObject jsonRootObject = new JSONObject(strJson);

            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonArray = jsonRootObject.optJSONArray("Employee");

            //Iterate the jsonArray and print the info of JSONObjects
            for(int i=0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String id = jsonObject.optString("id");
                String name = jsonObject.optString("name");
                String salary = jsonObject.optString("salary");

                data += "Node"+i+" : \n id= "+ id +" \n Name= "+ name +" \n Salary= "+ salary +" \n ";
            }
            jsonOutputTextView.setText(data);

            jsonOutputTextView.setMovementMethod(new ScrollingMovementMethod());

            audioPlayerViewObj.audioSeekBar.setVisibility(View.GONE);

        } catch (JSONException e) {
            String errStr = "Error in parseMyJson: " + e.getMessage();
            this.errorTextView.setText(errStr);
            Log.e(LogTag, errStr);
        }
    }

    public void startAudioStreaming(View v){

        audioPlayerViewObj.audioSeekBar.setVisibility(View.VISIBLE);

        errorTextView.setText("");
        pd = new ProgressDialog(this);
        pd.setMessage("Buffering...");
        pd.show();

        audioPlayerViewObj.audioSeekBar.setProgress(0);
        audioPlayerViewObj.audioSeekBar.setSecondaryProgress(0);
        /*
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.parse(SongUrl), "audio/*");
            this.startActivity(i);
        }
        */

        try
        {
            if (audioPlayerViewObj.mp == null)
                audioPlayerViewObj.mp = new MediaPlayer();
            else
                audioPlayerViewObj.mp.reset();

            audioPlayerViewObj.mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            audioPlayerViewObj.mp.setOnPreparedListener(audioPlayerViewObj);
            audioPlayerViewObj.mp.setOnErrorListener(audioPlayerViewObj);
            audioPlayerViewObj.mp.setDataSource(audioPlayerViewObj.SongUrl);
            audioPlayerViewObj.mp.setOnBufferingUpdateListener(audioPlayerViewObj);
            audioPlayerViewObj.mp.setOnCompletionListener(audioPlayerViewObj);

            audioPlayerViewObj.mp.prepareAsync();
        }

        catch(Exception e) {
            String errStr = "Error : " + e.getMessage();
            this.errorTextView.setText(errStr);
            Log.e(LogTag, errStr);
            Toast.makeText(getApplicationContext(), errStr, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        startAudioStreaming(v);
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
}
