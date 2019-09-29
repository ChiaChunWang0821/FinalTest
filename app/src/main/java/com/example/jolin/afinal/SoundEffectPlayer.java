package com.example.jolin.afinal;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundEffectPlayer implements MediaPlayer.OnCompletionListener {

    //播放音效
    //其中，sound 是 MediaPlayer.create(...) 指令所建立的多媒體播放物件
    // //而 R.raw.sound 是指向 raw子目錄中，主檔名為 sound 的聲音檔。
    // 而 sound.start() 就是利用所建立的 sound 多媒體物件來播放已經指定的聲音檔。
    MediaPlayer sound = null ;
    Context context ;

    public SoundEffectPlayer(Context mainContext) {
        context = mainContext ;
    }

    public void play( int fileID )
    {
        release() ;
        sound = MediaPlayer.create( context , fileID ) ;
        sound.setOnCompletionListener( this );
        sound.start() ;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        release() ;
    }

    private void release()
    {
        if ( sound != null )
        {
            sound.release();
            sound = null ;
        }
    }

    public void stop()
    {
        if (sound != null) {
            sound.release();
            sound = null;
        }
    }
}