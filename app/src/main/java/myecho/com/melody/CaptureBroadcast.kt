package myecho.com.melody

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import myecho.com.melody.Fragments.SongsPlayingFragment

class CaptureBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
         when(intent?.action){
             MyApplication.PREVIOUS-> {
                 SongsPlayingFragment.Staticated.playPrevious()

                 Toast.makeText(context,"Previous Clicked",Toast.LENGTH_SHORT).show()
                 if(SongsPlayingFragment.Statified.currentSongPlayer!!.isPlaying){
                     SongsPlayingFragment.Statified.musicBgService!!.sendNotification(R.drawable.pause,SongsPlayingFragment.Statified.currentPosition)
                 }else{
                     SongsPlayingFragment.Statified.musicBgService!!.sendNotification(R.drawable.play,SongsPlayingFragment.Statified.currentPosition)
                 }

             }
             MyApplication.NEXT-> {
                 SongsPlayingFragment.Staticated.playNext("PlayNextNormal")
             }
             MyApplication.PLAY-> {

                 if(SongsPlayingFragment.Statified.currentSongPlayer!!.isPlaying){
                     SongsPlayingFragment.Staticated.pauseMusic()
                 }else{
                     SongsPlayingFragment.Staticated.playMusic()
                 }
                 Toast.makeText(context,"Play Clicked",Toast.LENGTH_SHORT).show()

             }
             MyApplication.EXIT-> {
                 SongsPlayingFragment.Statified.musicBgService!!.stopForeground(true)
                 SongsPlayingFragment.Statified.musicBgService = null
             }


         }


//
//        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
//        /*Here we check whether the user has an outgoing call or not*/
//        if (intent?.action == Intent.ACTION_NEW_OUTGOING_CALL) {
//            try {
//                MainActivity.Statified.notificationManager?.cancel(1978)
//            }catch (e:Exception){
//                e.printStackTrace()
//
//            }
//            try {
//                /*If the media player was playing we pause it and change the play/pause button*/
//                if (SongsPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean){
//                    SongsPlayingFragment.Statified.mediaPlayer?.pause()
//                    SongsPlayingFragment.Statified.playpauseButton?.setBackgroundResource(R.mipmap.play_icon)
//                }
//            } catch (e: Exception){
//                e.printStackTrace()
//            }
//        } else {
//            /*Here we use the telephony manager to get the access for the service*/
//            val tm: TelephonyManager = context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//            when (tm?.callState) {
//            /*We check the call state and if the call is ringing i.e. the user has an incoming call
//            * then also we pause the media player*/
//                TelephonyManager.CALL_STATE_RINGING -> {
//                    try {
//                        MainActivity.Statified.notificationManager?.cancel(1978)
//                    }catch (e:Exception){
//                        e.printStackTrace()
//
//                    }
//                    try {
//                        if (SongsPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
//                            SongsPlayingFragment.Statified.mediaPlayer?.pause()
//                            SongsPlayingFragment.Statified.playpauseButton?.setBackgroundResource(R.mipmap.play_icon)
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//            /*Else we do nothing*/
//                else -> {
//                }
//            }
//        }
    }








}
