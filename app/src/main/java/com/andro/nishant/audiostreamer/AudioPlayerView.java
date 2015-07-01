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
public class AudioPlayerView extends View implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener{

    MainActivity mainActivityObj;
    MediaPlayer mp;
    SeekBar audioSeekBar;
    final String SongUrl = "http://appstore.creoinvent.co.in/Audio/WalkRain.mp3";
    final String LogTag = "StreamAudioDemo";
    Context context;
    public AudioPlayerView(Context context) {
        super(context);
        this.context = context;
        audioSeekBar = (SeekBar) findViewById(R.id.audioSeekBarId);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        audioSeekBar.setOnSeekBarChangeListener(this);
        audioSeekBar.setProgress(0);
        audioSeekBar.setMax(mp.getDuration());
        audioSeekBar.postDelayed(onEverySecond, 1000);

        Toast.makeText(context, "Prepare finished", Toast.LENGTH_LONG).show();
        Log.i(LogTag, "Prepare finished");
        mainActivityObj.pd.setMessage("Playing.....");
        mp.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        String errorStr = "onError MediaPlayer : "+ what+" and "  + extra;
        mainActivityObj.errorTextView.setText(errorStr);
        Toast.makeText(context, errorStr, Toast.LENGTH_LONG).show();
        mainActivityObj.pd.dismiss();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mainActivityObj.pd.dismiss();
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
