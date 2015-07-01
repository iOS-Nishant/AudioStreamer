package com.andro.nishant.audiostreamer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnClickListener,
        OnPreparedListener, OnErrorListener, OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    MediaPlayer mp;
    ProgressDialog pd;
    final String SongUrl = "http://appstore.creoinvent.co.in/Audio/WalkRain.mp3";
    final String LogTag = "StreamAudioDemo";
    Button mainButton ;
    TextView errorTextView;
    SeekBar audioSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainButton = (Button)findViewById(R.id.play);
        mainButton.setOnClickListener(this);

        errorTextView = (TextView)findViewById(R.id.errorTextViewId);
        errorTextView.setText("Click above button to start..");

        audioSeekBar = (SeekBar) findViewById(R.id.audioSeekBarId);
        audioSeekBar.setProgress(0);
        audioSeekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        audioSeekBar.setMax(mp.getDuration());
        audioSeekBar.postDelayed(onEverySecond, 1000);

        Toast.makeText(getApplicationContext(), "Prepare finished", Toast.LENGTH_LONG).show();
        Log.i(LogTag, "Prepare finished");
        pd.setMessage("Playing.....");
        mp.start();
    }

    private Runnable onEverySecond=new Runnable() {
        @Override
        public void run() {

            if(audioSeekBar != null) {
                audioSeekBar.setProgress(mp.getCurrentPosition());
            }

            if(mp.isPlaying()) {
                audioSeekBar.postDelayed(onEverySecond, 1000);
            }
        }
    };

    @Override
    public void onClick(View v) {
        errorTextView.setText("");
        audioSeekBar.setProgress(0);

        /*
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.parse(SongUrl), "audio/*");
            this.startActivity(i);
        }
        */

        try
        {
            pd = new ProgressDialog(this);
            pd.setMessage("Buffering.....");
            pd.show();
            mp = new MediaPlayer();
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setOnPreparedListener(this);
            mp.setOnErrorListener(this);
            mp.setDataSource(SongUrl);
            mp.prepareAsync();
            mp.setOnCompletionListener(this);
        }


        catch(Exception e)
        {
            Log.e(LogTag, e.getMessage());
            errorTextView.setText("Error : "+e.getMessage());
            Toast.makeText(getApplicationContext(), "Error : "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        String errorStr = "onError MediaPlayer : "+ what+" and "  + extra;
        errorTextView.setText(errorStr);
        Toast.makeText(getApplicationContext(), errorStr, Toast.LENGTH_LONG).show();
        pd.dismiss();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        pd.dismiss();
        //Toast.makeText(getApplicationContext(), "Completed", Toast.LENGTH_LONG).show();
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser) {
            // this is when actually seekbar has been seeked to a new position
            mp.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
