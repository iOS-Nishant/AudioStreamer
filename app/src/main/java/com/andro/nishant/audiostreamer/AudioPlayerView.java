package com.andro.nishant.audiostreamer;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

/**
 * Created by NishantThite on 01/07/15.
 */
public class AudioPlayerView extends View implements View.OnClickListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener{

    MediaPlayer mp;
    SeekBar audioSeekBar;
    final String SongUrl = "http://appstore.creoinvent.co.in/Audio/WalkRain.mp3";
    final String LogTag = "StreamAudioDemo";
    Context context;
    public AudioPlayerView(Context context) {
        super(context);
        this.context = context;
        audioSeekBar = (SeekBar) findViewById(R.id.audioSeekBarId);
        audioSeekBar.setProgress(0);
        audioSeekBar.setOnSeekBarChangeListener(this);


    }

    @Override
    public void onClick(View v) {

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
            //errorTextView.setText("Error : "+e.getMessage());
            Toast.makeText(context, "Error : "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onPrepared(MediaPlayer mp) {

        audioSeekBar.setMax(mp.getDuration());
        audioSeekBar.postDelayed(onEverySecond, 1000);

        Toast.makeText(context, "Prepare finished", Toast.LENGTH_LONG).show();
        Log.i(LogTag, "Prepare finished");
       // pd.setMessage("Playing.....");
        mp.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        String errorStr = "onError MediaPlayer : "+ what+" and "  + extra;
       // errorTextView.setText(errorStr);
        Toast.makeText(context, errorStr, Toast.LENGTH_LONG).show();
       // pd.dismiss();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
       // pd.dismiss();
        //Toast.makeText(getApplicationContext(), "Completed", Toast.LENGTH_LONG).show();
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
