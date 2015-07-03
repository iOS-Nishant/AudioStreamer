package com.andro.nishant.audiostreamer;


import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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

public class MainActivity extends ActionBarActivity implements OnClickListener {

    AudioPlayerView audioPlayerViewObj;
    ProgressDialog pd;
    final String LogTag = "StreamAudioDemo";
    Button mainButton ;
    TextView errorTextView;

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

        audioPlayerViewObj.audioSeekBar.setProgress(40);
        audioPlayerViewObj.audioSeekBar.setSecondaryProgress(75);

    }


    @Override
    public void onClick(View v) {
        errorTextView.setText("");
        pd = new ProgressDialog(this);
        pd.setMessage("Buffering.....");
        pd.show();
        errorTextView.setText("");

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
            Log.e(LogTag, e.getMessage());
            this.errorTextView.setText("Error : " + e.getMessage());
            Toast.makeText(getApplicationContext(), "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
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
