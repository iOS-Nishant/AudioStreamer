package com.andro.nishant.audiostreamer;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;
import android.media.MediaPlayer.OnBufferingUpdateListener;

/**
 * Created by NishantThite on 01/07/15.
 */
public class AudioPlayerView extends View implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,
        SeekBar.OnSeekBarChangeListener, OnBufferingUpdateListener{

    public int bufferPercent=0;
    MainActivity mainActivityObj;
    MediaPlayer mp;
    SeekBar audioSeekBar;
    final String SongUrl = "http://appstore.creoinvent.co.in/Audio/WalkRain";
    final String LogTag = "StreamAudioDemo";
    Context context;
    public AudioPlayerView(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        audioSeekBar.setOnSeekBarChangeListener(this);
        audioSeekBar.setProgress(0);
        audioSeekBar.setSecondaryProgress(bufferPercent);
        audioSeekBar.setMax(mp.getDuration());
        audioSeekBar.postDelayed(onEverySecond, 1000);

        Toast.makeText(context, "Prepare finished", Toast.LENGTH_LONG).show();
        Log.i(LogTag, "Prepare finished");
        mainActivityObj.pd.setMessage("Playing.....");
        mainActivityObj.pd.dismiss();
        mp.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        String errorStr = "onError MediaPlayer : "+ what+" and "  + extra;
        mainActivityObj.errorTextView.setText(errorStr);
        Toast.makeText(context, errorStr, Toast.LENGTH_LONG).show();
        mainActivityObj.pd.dismiss();

        mp.release();
        mp = null;
        audioSeekBar.setProgress(0);
        bufferPercent = 0 ;
        audioSeekBar.setSecondaryProgress( bufferPercent);
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
                audioSeekBar.setSecondaryProgress( bufferPercent);
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

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

        bufferPercent = percent;

        Log.d(LogTag, "\n MediaPlayer buffer Percent: " + bufferPercent + "\n\n");


        mainActivityObj.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                audioSeekBar.setSecondaryProgress( bufferPercent);
                mainActivityObj.errorTextView.setText("buffer Percent: " + bufferPercent);
            }
        });


    }
}
