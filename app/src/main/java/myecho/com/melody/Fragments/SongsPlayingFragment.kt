package myecho.com.melody.Fragments

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import kotlinx.android.synthetic.main.fragment_songs_playing.*
import myecho.com.melody.*
import myecho.com.melody.Fragments.SongsPlayingFragment.Statified.currentPosition
import myecho.com.melody.Fragments.SongsPlayingFragment.Statified.musicBgService
import myecho.com.melody.Fragments.SongsPlayingFragment.Statified.seekBar
import myecho.com.melody.utils.Constants
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 *
 */
class SongsPlayingFragment : Fragment(),ServiceConnection {


    var mAcceleration: Float = 0f
    var mAccelerationCurrent: Float = 0f
    var mAccelerationLast: Float = 0f



    object Statified {
        var count = 0
        var mActivity: Activity? = null
       // var mediaPlayer: MediaPlayer? = null
         var musicBgService:MusicBgService? = null
        var shuffleButton: ImageButton? = null
        var loopButton: ImageButton? = null
        var playpauseButton: ImageButton? = null
        var previousButton: ImageButton? = null
        var nextButton: ImageButton? = null
        var currentSongPlayer: CurrentSongPlayer? = null
        var currentPosition: Int = 0
        var fetchList: ArrayList<Songs>? = null
        var songTitle: TextView? = null
        var songArtist: TextView? = null
        var startTime: TextView? = null
        var endTime: TextView? = null
        var audioVisualization: AudioVisualization? = null
        var audioVisualizationView: GLAudioVisualizationView? = null
        var echoDatabase: EchoDatabase? = null
        var favButton: ImageButton? = null
        var seekBar: SeekBar? = null
        /*Sensor Variables*/
        var mSensorManager: SensorManager? = null
        var mSensorListener: SensorEventListener? = null
        var MY_PREFS_NAME = "ShakeFeature"



        var updateSongTime= object:Runnable {
            override fun run() {

                var getCurrent= musicBgService!!.mediaPlayer?.currentPosition
                startTime?.setText(String.format("%2d: %02d",
                        TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds
                        (getCurrent?.toLong()) -TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong()))))


                //TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long))))
                seekBar?.setProgress(getCurrent?.toInt() as Int)
                Handler().postDelayed(this,1000)
            }

        }
    }

    object Staticated{
        var MY_PREFS_SHUFFLE="Shuffle feature"
        var MY_PREFS_LOOP="Loop feature"



        fun playNext(check:String){

            seekBar?.setProgress(0);
            if(check.equals("PlayNextNormal",true))
            {
                Statified.currentPosition =  Statified.currentPosition + 1

            } else if (check.equals("PlayNextNormalShuffle",true)){

                var randomObject=Random()
                var randomPosition=randomObject.nextInt(Statified.fetchList?.size?.plus(1)as Int)
                Statified.currentPosition=randomPosition


            }

            Statified.currentSongPlayer?.isLoop=false
            Statified.loopButton?.setBackgroundResource(R.mipmap.loop_white_icon)

            if(Statified.currentPosition==Statified.fetchList?.size){

                Statified. currentPosition=0
            }
            var nextSong=Statified.fetchList?.get(Statified.currentPosition)
            Statified.currentSongPlayer?.songPath=nextSong?.details
            Statified.currentSongPlayer?.songTitle=nextSong?.songTitle
            Statified.currentSongPlayer?.songArtist=nextSong?.artist
            Statified.currentSongPlayer?.songId=nextSong?.songId as Long
            updateTextviews(Statified.currentSongPlayer?.songTitle as String,Statified.currentSongPlayer?.songArtist as String)
            musicBgService!!.mediaPlayer?.reset()

            try {
                musicBgService!!.mediaPlayer?.setDataSource(Statified.mActivity!!.baseContext, Uri.parse(Statified.currentSongPlayer?.songPath))
                musicBgService!!.mediaPlayer?.prepare()
                ProcessInformation( musicBgService!!.mediaPlayer as MediaPlayer)
                musicBgService!!.mediaPlayer!!.start()

            }catch (e:Exception){

                e.printStackTrace()
            }

            if(Statified.echoDatabase?.checkIfIdExists(Statified.currentSongPlayer?.songId?.toInt() as Int) as Boolean){

                Statified.favButton?.setBackgroundResource(R.mipmap.favorite_on)

            }else{

                Statified.favButton?.setBackgroundResource(R.mipmap.favorite_off)

            }

            Statified.musicBgService!!.sendNotification(R.drawable.pause,SongsPlayingFragment.Statified.currentPosition)
            Statified.playpauseButton!!.setBackgroundResource(R.drawable.pause_icon)

        }

        fun playPrevious(){

            seekBar?.setProgress(0);
            Statified.currentPosition=Statified.currentPosition-1

            if(Statified.currentPosition==-1){

                Statified.currentPosition=0
            }

            if(Statified.currentSongPlayer?.isPlaying as Boolean){

                Statified. playpauseButton?.setBackgroundResource(R.mipmap.pause_icon)
            }else{

                Statified.playpauseButton?.setBackgroundResource(R.mipmap.play_icon)
            }

            Statified.currentSongPlayer?.isLoop=false
            Statified.loopButton?.setBackgroundResource(R.mipmap.loop_white_icon)

            var prevSong=Statified.fetchList?.get(Statified.currentPosition)
            Statified.currentSongPlayer?.songPosition= Statified.currentPosition
            Statified. currentSongPlayer?.songPath=prevSong?.details
            Statified.currentSongPlayer?.songTitle=prevSong?.songTitle
            Statified.currentSongPlayer?.songArtist=prevSong?.artist
            Statified.currentSongPlayer?.songId=prevSong?.songId as Long
           updateTextviews(  Statified.currentSongPlayer?.songTitle as String, Statified. currentSongPlayer?.songArtist as String)
            musicBgService!!.mediaPlayer!!.reset()

            try {
                musicBgService!!.mediaPlayer!!.setDataSource(Statified.mActivity!!.baseContext, Uri.parse(Statified.currentSongPlayer?.songPath))
                musicBgService!!.mediaPlayer!!.prepare()
                ProcessInformation( musicBgService!!.mediaPlayer as MediaPlayer)
                musicBgService!!.mediaPlayer!!.start()

            }catch (e:Exception){

                e.printStackTrace()
            }

            if(Statified.echoDatabase?.checkIfIdExists(Statified.currentSongPlayer?.songId?.toInt() as Int) as Boolean){

                Statified.favButton?.setBackgroundResource(R.mipmap.favorite_on)

            }else{

                Statified.favButton?.setBackgroundResource(R.mipmap.favorite_off)

            }

            if(Statified.currentSongPlayer!!.isPlaying){
                Statified.musicBgService!!.sendNotification(R.drawable.pause,Statified.currentPosition)
            }else{
                Statified.musicBgService!!.sendNotification(R.drawable.play,Statified.currentPosition)
            }


        }

        fun pauseMusic(){
            musicBgService!!.mediaPlayer!!.pause()
            Statified. currentSongPlayer?.isPlaying=false
            Statified.playpauseButton?.setBackgroundResource(R.mipmap.play_icon)
            Statified.musicBgService!!.sendNotification(R.drawable.play,Statified.currentPosition)
        }

        fun playMusic(){
            musicBgService!!.mediaPlayer!!.start()
            Statified.currentSongPlayer?.isPlaying=true
            Statified.playpauseButton?.setBackgroundResource(R.mipmap.pause_icon)
            Statified.musicBgService!!.sendNotification(R.drawable.pause,Statified.currentPosition)


        }

        fun updateTextviews(title:String,artist:String){
            var updatedTitle=title
            var updatedArtist=artist
            if(title.equals("<unknown>",true)){
                updatedTitle="unknown"

            }
            if(artist.equals("<unknown>",true)){

                updatedArtist="unknown"

            }
            Statified.songTitle?.setText(updatedTitle)
            Statified.songArtist?.setText(updatedArtist)
        }

        fun ProcessInformation(mediaPlayer: MediaPlayer){

            val finalTime=mediaPlayer?.duration
            val firstTime=mediaPlayer?.currentPosition
            Statified.seekBar?.max=finalTime

            Statified.startTime?.setText(String.format("%2d: %02d",
                    TimeUnit.MILLISECONDS.toMinutes(firstTime?.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds
                    (firstTime?.toLong()) -TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(firstTime?.toLong()))))

            Statified.endTime?.setText(String.format("%2d: %02d",
                    TimeUnit.MILLISECONDS.toMinutes(finalTime?.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds
                    (finalTime?.toLong()) -TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime?.toLong()))))

            Statified.seekBar?.setProgress(firstTime)
            Handler().postDelayed(Statified.updateSongTime,1000)


        }

        fun onSongComplete(){

            seekBar?.setProgress(0);
            if(Statified.currentSongPlayer?.shuffleSong as Boolean)
            {
                Statified.currentSongPlayer?.isPlaying=true
                playNext("PlayNextNormalShuffle")
            }else {
                if(Statified.currentSongPlayer?.isLoop as Boolean){

                    Statified. currentSongPlayer?.isPlaying=true
                    var nextSong=Statified.fetchList?.get(Statified.currentPosition)
                    Statified.currentSongPlayer?.songPath=nextSong?.details
                    Statified.currentSongPlayer?.songTitle=nextSong?.songTitle
                    Statified.currentSongPlayer?.songArtist=nextSong?.artist
                    Statified.currentSongPlayer?.songId=nextSong?.songId as Long
                    musicBgService!!. mediaPlayer?.reset()

                    try {
                        var isLoopingCountCompleted = false
                        musicBgService!!. mediaPlayer?.setDataSource(Statified.mActivity!!.baseContext, Uri.parse(Statified.currentSongPlayer?.songPath))
                        musicBgService!!.mediaPlayer?.prepare()
                        ProcessInformation( musicBgService!!.mediaPlayer as MediaPlayer)

                        if(Statified.count>0) {

                            musicBgService!!.mediaPlayer?.start()
                            Statified.count = Statified.count -1
                            if(Statified.count == 0){
                          //  isLoopingCountCompleted = true
                            Statified. currentSongPlayer?.isLoop= false
                            Statified.loopButton?.setBackgroundResource(R.mipmap.loop_white_icon)
                            musicBgService!!.mediaPlayer?.stop()

                            }

                        }

                        if(Statified.count == 10){
                            musicBgService!!.mediaPlayer?.start()
                        }


                    }catch (e:Exception){

                        e.printStackTrace()
                    }

                }else{

                    Statified.currentSongPlayer?.isPlaying=true
                    playNext("PlayNextNormal")
                }
            }

            if(Statified.echoDatabase?.checkIfIdExists(Statified.currentSongPlayer?.songId?.toInt() as Int) as Boolean){

                Statified.favButton?.setBackgroundResource(R.mipmap.favorite_on)

            }else{

                Statified.favButton?.setBackgroundResource(R.mipmap.favorite_off)

            }
        }

    }

    //Gets called at the start
    override fun onAttach(context: Context?) {
        super.onAttach(context)

        Statified.mActivity=context as Activity
        val intent=   Intent(Statified.mActivity,MusicBgService::class.java)
        context.bindService(intent,this, AppCompatActivity.BIND_AUTO_CREATE)
        context.startService(intent)
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        Statified.mActivity=activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*Sensor service is activate when the fragment is created*/
        Statified.mSensorManager = Statified.mActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        /*Default values*/
        mAcceleration = 0.0f
        /*We take earth's gravitational value to be default, this will give us good results*/
        mAccelerationCurrent = SensorManager.GRAVITY_EARTH
        mAccelerationLast = SensorManager.GRAVITY_EARTH
        /*Here we call the function*/
        bindShakeListener()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view= inflater.inflate(R.layout.fragment_songs_playing, container, false)
        setHasOptionsMenu(true)
        Statified.shuffleButton=view.findViewById(R.id.shuffle)
        Statified.loopButton=view.findViewById(R.id.loop)
        Statified.playpauseButton=view.findViewById(R.id.play_image)
        Statified.previousButton=view.findViewById(R.id.previous_image)
        Statified.nextButton=view.findViewById(R.id.next_image)
        Statified.songTitle=view.findViewById(R.id.titlesong)
        Statified.songArtist=view.findViewById(R.id.artistsong)
        Statified.startTime=view.findViewById(R.id.start_time)
        Statified.endTime=view.findViewById(R.id.end_time)
        Statified.favButton=view.findViewById(R.id.favorite_icon)
        Statified.favButton?.alpha=0.8f
        Statified.seekBar=view.findViewById(R.id.seekbar)

        Statified.fetchList= ArrayList<Songs>()
        Statified.audioVisualizationView=view.findViewById(R.id.visualizer_view)

        (activity as MainActivity)
                .title="" +
                "Now Playing"


        return  view
    }


    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         Statified. audioVisualization=Statified.audioVisualizationView as AudioVisualization

    }


    fun clickHandler()
    {
        //Fav Button functionality
        Statified.favButton?.setOnClickListener({


            if(Statified.echoDatabase?.checkIfIdExists(Statified.currentSongPlayer?.songId?.toInt() as Int) as Boolean){

                Statified.favButton?.setBackgroundResource(R.mipmap.favorite_off)
                Statified.echoDatabase?.deleteValueFromTheDatabase(Statified.currentSongPlayer?.songId?.toInt() as Int)
                Toast.makeText(Statified.mActivity,getString(R.string.delted_from_favorites),Toast.LENGTH_SHORT).show()

            }else{

                Statified.favButton?.setBackgroundResource(R.mipmap.favorite_on)
                Statified.echoDatabase?.storeAsFavorite(Statified.currentSongPlayer?.songId?.toInt(),Statified.currentSongPlayer?.songTitle,Statified.currentSongPlayer?.songArtist
                        ,Statified.currentSongPlayer?.songPath)

                Toast.makeText(Statified.mActivity,getString(R.string.added_to_favorites),Toast.LENGTH_SHORT).show()

            }
        })

        //Shuffle Button functionality
        Statified.shuffleButton?.setOnClickListener({

            var editorShuffle=Statified.mActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE,Context.MODE_PRIVATE)?.edit()
            var editorLoop=Statified.mActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP,Context.MODE_PRIVATE)?.edit()


            if(Statified.currentSongPlayer?.shuffleSong as Boolean){
                Statified.currentSongPlayer?.shuffleSong=false
                Statified.shuffleButton?.setBackgroundResource(R.mipmap.shuffle_white_icon)
                editorShuffle?.putBoolean("feature",false)
                editorShuffle?.apply()

            }else{
                Statified.currentSongPlayer?.shuffleSong=true
                Statified.currentSongPlayer?.isLoop=true
                Statified.loopButton?.setBackgroundResource(R.mipmap.loop_white_icon)
                Statified.shuffleButton?.setBackgroundResource(R.mipmap.shuffle_icon)
                editorShuffle?.putBoolean(Constants.FEATURE,true)
                editorShuffle?.apply()
                editorLoop?.putBoolean(Constants.FEATURE,false)
                editorLoop?.apply()

            }
        })

        //Loop button functionality
        Statified.loopButton?.setOnClickListener({

            var editorShuffle=Statified.mActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE,Context.MODE_PRIVATE)?.edit()
            var editorLoop=Statified.mActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP,Context.MODE_PRIVATE)?.edit()

            if(Statified.currentSongPlayer?.isLoop as Boolean){

                Statified.currentSongPlayer?.isLoop=false
                Statified.loopButton?.setBackgroundResource(R.mipmap.loop_white_icon)
                editorLoop?.putBoolean(Constants.FEATURE,false)
                editorLoop?.apply()

            }else{
                showLoopCount()
                Statified. currentSongPlayer?.shuffleSong=false

                Statified.shuffleButton?.setBackgroundResource(R.mipmap.shuffle_white_icon)
//                if(Statified.count == 0) {
//                    editorLoop?.putBoolean("feature", false)
//                    Statified. currentSongPlayer?.isLoop=false
//                    Statified.loopButton?.setBackgroundResource(R.mipmap.loop_white_icon)
//                    editorLoop?.apply()
//                }else{
                    editorLoop?.putBoolean(Constants.FEATURE, false)
                    Statified. currentSongPlayer?.isLoop=true
                    Statified.loopButton?.setBackgroundResource(R.mipmap.loop_icon)
                    editorLoop?.apply()
               // }
                editorShuffle?.putBoolean(Constants.FEATURE,false)
                editorShuffle?.apply()

            }

        })
        // Play Pause Button
        Statified.playpauseButton?.setOnClickListener({
            if(  musicBgService!!.mediaPlayer!!.isPlaying as Boolean){

                   Staticated.pauseMusic()

            }else{

                  Staticated.playMusic()
            }

        })
        //Previous Button
        Statified.previousButton?.setOnClickListener({
            Statified.currentSongPlayer?.isPlaying=true

            if(Statified.currentSongPlayer?.isLoop as Boolean){

                Statified.loopButton?.setBackgroundResource(R.mipmap.loop_white_icon)


            }
            Staticated.playPrevious()


        })
        //Next Button
        Statified.nextButton?.setOnClickListener({

            Statified.currentSongPlayer?.isPlaying=true
            if(Statified.currentSongPlayer?.shuffleSong as Boolean){
                Staticated.playNext("PlayNextNormalShuffle")
            }else{
                Staticated.playNext("PlayNextNormal")
            }

        })


        //Seek Bar
        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    musicBgService!!.mediaPlayer!!.seekTo(progress)
                    seekBar.setProgress(progress)
                }
                var  nextSong=Statified.fetchList?.get(Statified.currentPosition)
                Statified.currentSongPlayer?.songPath=nextSong?.details
                Statified.currentSongPlayer?.songTitle=nextSong?.songTitle
                Statified.currentSongPlayer?.songArtist=nextSong?.artist
                Statified.currentSongPlayer?.songId=nextSong?.songId as Long
                Staticated.updateTextviews(
                    Statified.currentSongPlayer?.songTitle as String,
                    Statified.currentSongPlayer?.songArtist as String
                )
              //  musicBgService!!.mediaPlayer?.reset()



            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is started.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is stopped.

            }
        })


    }


    fun showLoopCount(){
        val popupMenu: PopupMenu = PopupMenu(context,Statified.loopButton)
        popupMenu.menuInflater.inflate(R.menu.menu_loop,popupMenu.menu)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when(item.itemId) {
                R.id.redirect1 -> {

                     Statified.count = item.title.toString().toInt() + 1
                    Toast.makeText(context, "You Clicked : " + item.title, Toast.LENGTH_SHORT)
                        .show()
                }
                R.id.redirect2 -> {
                    Statified.count = item.title.toString().toInt() + 1
                    Toast.makeText(context, "You Clicked : " + item.title, Toast.LENGTH_SHORT)
                        .show()
                }
                R.id.redirect3 -> {
                    Statified.count = item.title.toString().toInt() + 1
                    Toast.makeText(context, "You Clicked : " + item.title, Toast.LENGTH_SHORT)
                        .show()
                }
                R.id.redirect4 -> {
                    Statified.count = item.title.toString().toInt()+ 1
                    Toast.makeText(context, "You Clicked : " + item.title, Toast.LENGTH_SHORT)
                        .show()
                }
                R.id.redirect5 -> {
                    Statified.count = item.title.toString().toInt()+ 1
                    Toast.makeText(context, "You Clicked : " + item.title, Toast.LENGTH_SHORT)
                        .show()
                }
                R.id.redirect -> {
                    Statified.count = 10 + 1
                    Toast.makeText(context, "You Clicked : " + item.title, Toast.LENGTH_SHORT)
                        .show()
                }
            }
            true
        })
        popupMenu.show()

    }

    override fun onPause() {
        Statified.audioVisualization?.onPause()
        super.onPause()

        /*When fragment is paused, we remove the sensor to prevent the battery drain*/
        Statified.mSensorManager?.unregisterListener(Statified.mSensorListener)
    }



    override fun onDestroy() {
        Statified.audioVisualization?.release()
        Handler().removeCallbacks(Statified.updateSongTime)
        super.onDestroy()

//        if(Statified.currentSongPlayer!!.isPlaying){
//           Statified.mActivity!!.stopService(intent)
//
//
//        }
    }


    /*This function handles the shake events in order to change the songs when we shake the phone*/
    fun bindShakeListener() {

        /*The sensor listener has two methods used for its implementation i.e. OnAccuracyChanged() and onSensorChanged*/
        Statified.mSensorListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

                /*We do noot need to check or work with the accuracy changes for the sensor*/
            }

            override fun onSensorChanged(event: SensorEvent) {

                /*We need this onSensorChanged function
                * This function is called when there is a new sensor event*/
                /*The sensor event has 3 dimensions i.e. the x, y and z in which the changes can occur*/
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                /*Now lets see how we calculate the changes in the acceleration*/
                /*Now we shook the phone so the current acceleration will be the first to start with*/
                mAccelerationLast = mAccelerationCurrent

                /*Since we could have moved the phone in any direction, we calculate the Euclidean distance to get the normalized distance*/
                mAccelerationCurrent = Math.sqrt(((x * x + y * y + z * z).toDouble())).toFloat()

                /*Delta gives the change in acceleration*/
                val delta = mAccelerationCurrent - mAccelerationLast

                /*Here we calculate thelower filter
                * The written below is a formula to get it*/
                mAcceleration = mAcceleration * 0.9f + delta

                /*We obtain a real number for acceleration
                * and we check if the acceleration was noticeable, considering 12 here*/
                if (mAcceleration > 12) {

                    /*If the accel was greater than 12 we change the song, given the fact our shake to change was active*/
                    val prefs = Statified.mActivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
                    val isAllowed = prefs?.getBoolean("feature", false)
                    if (isAllowed as Boolean) {
                        Staticated.playNext("PlayNextNormal")
                    }
                }
            }
        }
    }


    //This function is used to create the menu
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
            menu?.clear()
            inflater?.inflate(R.menu.song_playing_menu, menu)
            super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
            val item: MenuItem? = menu?.findItem(R.id.redirect)
            item?.isVisible = true
    }
    ///Here we handle the click event of the menu item
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {

            R.id.redirect -> {
                Statified.mActivity?.onBackPressed()
                return false
            }
        }
        return false
    }

    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        val binder =  p1 as MusicBgService.MyBinder
        musicBgService = binder.currentService()

        createMediaPlayer()
        Statified.currentSongPlayer?.isPlaying = true
        musicBgService!!.sendNotification(R.drawable.pause, currentPosition)

    }

    fun createMediaPlayer(){

        Statified.echoDatabase= EchoDatabase(Statified.mActivity as Context)
        Statified.currentSongPlayer= CurrentSongPlayer()

        Statified.currentSongPlayer?.isPlaying = true
        Statified.currentSongPlayer?.isLoop = false
        Statified.currentSongPlayer?.shuffleSong = false
        Statified.currentPosition=arguments?.getInt(Constants.SONG_POSITION) as Int
        Statified.fetchList=arguments?.getParcelableArrayList(Constants.SONG_LIST)
        Statified.currentSongPlayer!!.isPlaying=arguments?.getBoolean(Constants.SONG_PLAYING)!!
        //Check song is topped from bottom bar or song

        var path:String?=null
        var title:String?=null
        var artist:String?=null
        var id:Long?=0
        var position:Int?=0

        try {

            path=arguments?.getString(Constants.PATH)
            title= arguments?.getString(Constants.TITLE)
            artist=arguments?.getString(Constants.ARTIST)
            id=arguments?.getInt(Constants.ID)?.toLong()
            position= arguments?.getInt(Constants.SONG_POSITION) as Int

            Statified.currentSongPlayer?.songPath=path
            Statified.currentSongPlayer?.songTitle=title
            Statified.currentSongPlayer?.songArtist=artist
            Statified.currentSongPlayer?.songId=id as Long
            Statified.currentSongPlayer?.songPosition = position

            Staticated.updateTextviews(Statified.currentSongPlayer?.songTitle as String,Statified.currentSongPlayer?.songArtist as String)


        }catch (e:Exception){

            e.printStackTrace()
        }


        var favBootomBar=arguments?.getString("FavBottomBar")

        if(favBootomBar!=null){


            musicBgService!!.mediaPlayer=FavoriteFragment.Statified.mediaPlayer!!
        }else {

            if(musicBgService!!.mediaPlayer == null) {
                musicBgService!!.mediaPlayer = MediaPlayer()
            }
            musicBgService!!.mediaPlayer!!.reset()
            musicBgService!!.mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)

            try {
                musicBgService!!.mediaPlayer!!.setDataSource(Statified.mActivity!!.baseContext, Uri.parse(path))
                musicBgService!!.mediaPlayer!!.prepare()

            } catch (e: Exception) {

                e.printStackTrace()
            }

            musicBgService!!.mediaPlayer!!.start()
        }

        Staticated.ProcessInformation(  musicBgService!!.mediaPlayer as MediaPlayer)
        musicBgService!!.mediaPlayer!!.setOnCompletionListener {
            Staticated. onSongComplete()
        }

        clickHandler()

        var visualizationHandler=DbmHandler.Factory.newVisualizerHandler(Statified.mActivity as Context,0)
        Statified.audioVisualization?.linkTo(visualizationHandler)

        var prefsForShuffle=Statified. mActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE,Context.MODE_PRIVATE)
        var isShuffleAllowed=prefsForShuffle?.getBoolean("feature",false)
        if(isShuffleAllowed as Boolean){
            Statified.currentSongPlayer?.shuffleSong=true
            Statified.currentSongPlayer?.isLoop=false
            Statified.shuffleButton?.setBackgroundResource(R.mipmap.shuffle_icon)
            Statified.loopButton?.setBackgroundResource(R.mipmap.loop_white_icon)

        }else{
            Statified.currentSongPlayer?.shuffleSong=false
            Statified.shuffleButton?.setBackgroundResource(R.mipmap.shuffle_white_icon)
        }

        var prefsForLoop=Statified. mActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP,Context.MODE_PRIVATE)
        var isLoopAllowed=prefsForLoop?.getBoolean("feature",false)
        if(isLoopAllowed as Boolean){
            Statified.currentSongPlayer?.shuffleSong=false
            Statified.currentSongPlayer?.isLoop=true
            Statified.shuffleButton?.setBackgroundResource(R.mipmap.shuffle_white_icon)
            Statified.loopButton?.setBackgroundResource(R.mipmap.loop_icon)

        }else{
            Statified.currentSongPlayer?.isLoop=false
            Statified.loopButton?.setBackgroundResource(R.mipmap.loop_white_icon)
        }

        if(Statified.echoDatabase?.checkIfIdExists(Statified.currentSongPlayer?.songId?.toInt() as Int) as Boolean){

            Statified.favButton?.setBackgroundResource(R.mipmap.favorite_on)

        }else{

            Statified.favButton?.setBackgroundResource(R.mipmap.favorite_off)

        }
    }


    override fun onServiceDisconnected(p0: ComponentName?) {
        //SongsPlayingFragment.Statified.musicBgService!!.stopForeground(true)
        musicBgService = null
    }


}
