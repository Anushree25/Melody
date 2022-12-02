package myecho.com.melody.Fragments


import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.*
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import myecho.com.melody.Adapters.FavoriteAdapter
import myecho.com.melody.EchoDatabase
import myecho.com.melody.MainActivity

import myecho.com.melody.R
import myecho.com.melody.Songs
import myecho.com.melody.utils.Constants


class FavoriteFragment : Fragment() {

    var mActivity:Activity?=null
    var songs_list:ArrayList<Songs>?=null
    var recyclerView: RecyclerView?=null
    var noFavorites:TextView?=null
    var nowplayingBottomBar:RelativeLayout?=null
    var songTitle:TextView?=null
    var playPauseButton :ImageButton?=null
    var trackPosition:Int=0
    var fav_content:EchoDatabase?=null

    var refresh_list:ArrayList<Songs>?=null
    var getSongsFromDatabase:ArrayList<Songs>?=null


    object  Statified{

     var mediaPlayer:MediaPlayer?=null

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_favorite, container, false)
        recyclerView=view.findViewById(R.id.favorite_recycler)
        noFavorites=view.findViewById(R.id.no_favorites)
        nowplayingBottomBar=view.findViewById(R.id.music_player_menu_fav)
        songTitle=view.findViewById(R.id.song_title)
        playPauseButton=view.findViewById(R.id.play_pause_button)
        fav_content=EchoDatabase(mActivity as Context)
        setHasOptionsMenu(true)

        (activity as MainActivity)
                .title=getString(R.string.favorites_text)


        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mActivity=context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        mActivity=activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bottomBarSetup()
        display_favorites_by_searching()



    }

    fun getSongsFromThePhone():ArrayList<Songs>{

        var songs=ArrayList<Songs>()
        var contentResolver=mActivity?.contentResolver
        var songuri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songcursor=contentResolver?.query(songuri,null,null,null,null)

        if(songcursor!=null && songcursor.moveToFirst()) {
            var songId = songcursor.getColumnIndex(MediaStore.Audio.Media._ID)
            var songtitle = songcursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            var songArtist = songcursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            var songDetails = songcursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            var songeDate = songcursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)

            while (songcursor.moveToNext()) {
                var id = songcursor.getLong(songId)
                var title = songcursor.getString(songtitle)
                var artist = songcursor.getString(songArtist)
                var details = songcursor.getString(songDetails)
                var date = songcursor.getLong(songeDate)

                songs.add(Songs(id, title, artist, details, date,"null","null"))

            }


        }


        return songs
    }


    fun bottomBarSetup(){

        bottomBarClickHandler()
        try {

          songTitle?.setText(SongsPlayingFragment.Statified.currentSongPlayer?.songTitle)

            if( SongsPlayingFragment.Statified.musicBgService!!.mediaPlayer!=null) {
                SongsPlayingFragment.Statified.musicBgService!!.mediaPlayer?.setOnCompletionListener {

                    songTitle?.setText(SongsPlayingFragment.Statified.currentSongPlayer?.songTitle)
                    SongsPlayingFragment.Staticated.onSongComplete()

                }
                if (SongsPlayingFragment.Statified.musicBgService!!.mediaPlayer!!.isPlaying as Boolean) {

                    nowplayingBottomBar?.visibility = View.VISIBLE
                } else {

                    nowplayingBottomBar?.visibility = View.INVISIBLE
                }
            }
        }catch (e:Exception){

            e.printStackTrace()

        }


    }

    fun bottomBarClickHandler(){


        nowplayingBottomBar?.setOnClickListener {

            Statified.mediaPlayer=SongsPlayingFragment.Statified.musicBgService!!.mediaPlayer

            val songsPlayingFragment= SongsPlayingFragment()
          var obj=Bundle()
          obj.putString(Constants.ARTIST,SongsPlayingFragment.Statified.currentSongPlayer?.songArtist)
          obj.putString(Constants.PATH,SongsPlayingFragment.Statified.currentSongPlayer?.songPath)
          obj.putString(Constants.TITLE,SongsPlayingFragment.Statified.currentSongPlayer?.songTitle)
          obj.putInt(Constants.ID,SongsPlayingFragment.Statified.currentSongPlayer?.songId?.toInt() as Int)
          obj.putInt(Constants.SONG_POSITION,SongsPlayingFragment.Statified.currentSongPlayer?.songPosition?.toInt() as Int)
          obj.putParcelableArrayList(Constants.SONG_LIST,songs_list)
          songsPlayingFragment.arguments=obj

          obj.putString(getString(R.string.fav_bottom_bar),getString(R.string.success_text))

          fragmentManager?.beginTransaction()?.replace(R.id.details_fragment,songsPlayingFragment)
                  ?.addToBackStack(getString(R.string.song_fragment))!!.commit()



      }

        playPauseButton?.setOnClickListener({
            if(SongsPlayingFragment.Statified.musicBgService!!.mediaPlayer?.isPlaying as Boolean){
                SongsPlayingFragment.Statified.musicBgService!!.mediaPlayer?.pause()

                trackPosition=SongsPlayingFragment.Statified.currentSongPlayer?.songPosition?.toInt() as Int

                playPauseButton?.setBackgroundResource(R.mipmap.play_icon)
            }else{

                SongsPlayingFragment.Statified.musicBgService!!.mediaPlayer?.seekTo(trackPosition)
                SongsPlayingFragment.Statified.musicBgService!!.mediaPlayer?.start()
                playPauseButton?.setBackgroundResource(R.mipmap.pause_icon)
            }

        })


    }


    fun display_favorites_by_searching(){

        if(fav_content?.checkSizeOfDb() as Int>0 ){

            refresh_list=ArrayList<Songs>()

            getSongsFromDatabase=fav_content?.fetchDbList()

            val fetchListFromDevice=getSongsFromThePhone()

            //if there is song present in phone storage
            if(fetchListFromDevice!=null && getSongsFromDatabase!=null){

                for(i in 0..fetchListFromDevice?.size-1){

                    for(j in 0..getSongsFromDatabase?.size as Int-1){

                        //if fav songs are present
                        if(fetchListFromDevice?.get(i).songId === getSongsFromDatabase?.get(j)?.songId){

                            refresh_list?.add(getSongsFromDatabase?.get(j)as Songs)

                        }
                    }

                }
             }
            //If there are no fav
            if(refresh_list==null){

                recyclerView?.visibility=View.INVISIBLE
                noFavorites?.visibility=View.VISIBLE
            }else{

                //Show fav songs
                var adapter=FavoriteAdapter(refresh_list as ArrayList<Songs>,context as Activity)
                val mLayoutManager=
                    LinearLayoutManager(activity)
                recyclerView?.layoutManager=mLayoutManager
                recyclerView?.itemAnimator=
                    DefaultItemAnimator()
                recyclerView?.adapter=adapter
                recyclerView?.setHasFixedSize(true)
            }

        }else{
            //If count is 0
            recyclerView?.visibility=View.INVISIBLE
            noFavorites?.visibility=View.VISIBLE

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
            item?.isVisible = false
        }



}
