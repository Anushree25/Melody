package myecho.com.melody

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.app.NotificationCompat
import myecho.com.melody.Fragments.SongsPlayingFragment


class MusicBgService : Service() {

    private var myBinder = MyBinder()
    var trackNotificationBuilder:Notification?=null
    private lateinit var mediaSessionCompat: MediaSessionCompat
    public  var mediaPlayer: MediaPlayer? = null
    var notification :Notification? = null

    override fun onBind(intent: Intent): IBinder {
        mediaSessionCompat = MediaSessionCompat(baseContext,"Media Session")
        return myBinder
    }



    @SuppressLint("SuspiciousIndentation")
    fun sendNotification(playPauseButton : Int, currentPost:Int){

        val meataData = MediaMetadataCompat.Builder().apply {
                putString(MediaMetadata.METADATA_KEY_ARTIST, "title")
                .putString(MediaMetadata.METADATA_KEY_TITLE, "genre")
                    .putLong(
                    MediaMetadata.METADATA_KEY_DURATION,
                    mediaPlayer!!.duration.toLong()
                ) //Negative duration means the duration is unknown


        }

        val stateBuilder = PlaybackStateCompat.Builder()
            .setState(
                if (mediaPlayer!!.isPlaying()) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
              mediaPlayer!!.currentPosition.toLong(), 1f
            )

        mediaSessionCompat.setPlaybackState(stateBuilder.build())

        mediaSessionCompat.setMetadata( meataData.build())
        val prevIntent = Intent(baseContext, CaptureBroadcast::class.java).setAction(MyApplication.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext, 0,prevIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(baseContext, CaptureBroadcast::class.java).setAction(MyApplication.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext, 0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val playIntent = Intent(baseContext, CaptureBroadcast::class.java).setAction(MyApplication.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext, 0,playIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val exitIntent = Intent(baseContext, CaptureBroadcast::class.java).setAction(MyApplication.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext, 0,exitIntent,PendingIntent.FLAG_UPDATE_CURRENT)


        notification = androidx.core.app.NotificationCompat.Builder(baseContext,MyApplication.CHANNEL_ID)
            .setContentTitle(SongsPlayingFragment.Statified.currentSongPlayer!!.songTitle)
            .setContentText(SongsPlayingFragment.Statified.currentSongPlayer!!.songArtist)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.ic_launcher_foreground))
            .setStyle(NotificationCompat.MediaStyle().setMediaSession(mediaSessionCompat.sessionToken))
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.mipmap.play_previous_icon,getString(R.string.prev),prevPendingIntent)
            .addAction(playPauseButton,getString(R.string.play),playPendingIntent)
            .addAction(R.mipmap.play_next_icon,getString(R.string.next),nextPendingIntent)
            .addAction(android.R.drawable.ic_lock_power_off,getString(R.string.exit),exitPendingIntent)
            .build()

          startForeground(1,notification)


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
     return  super.onStartCommand(intent, flags, startId)

    }

    override fun onDestroy() {
        super.onDestroy()
//        if(!mediaPlayer!!.isPlaying){
//            mediaPlayer!!.stop()
//            stopForeground(true)
//        }
    }

    inner class MyBinder : Binder(){
        fun currentService () :MusicBgService{
            return this@MusicBgService
        }

    }
}