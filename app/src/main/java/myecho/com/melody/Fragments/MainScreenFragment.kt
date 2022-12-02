package myecho.com.melody.Fragments


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import myecho.com.melody.*
import myecho.com.melody.Adapters.MainScreenAdapter
import myecho.com.melody.utils.ContentResolver
import java.util.*


class MainScreenFragment : Fragment() {

    var musicplayerlayout:RelativeLayout?=null
    object Statified {
        var recyclerViewSongs: RecyclerView? = null
    }
    var songsplayingvisiblelayout:RelativeLayout?=null
    var songsnotavailablelayout:RelativeLayout?=null
    var songTitle:TextView?=null
    var playpausebutton:ImageButton?=null
    var mActivity:Activity?=null
    var mainScreenAdapter: MainScreenAdapter? = null

    var trackPosition=0
    var getSongsList:ArrayList<Songs>?=null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view=inflater.inflate(R.layout.fragment_main_screen, container, false)
        setHasOptionsMenu(true)
        musicplayerlayout=view.findViewById(R.id.music_player_menu)
        Statified.recyclerViewSongs=view.findViewById(R.id.songs_list)
        songsplayingvisiblelayout=view.findViewById(R.id.visiblelayout)
        songsnotavailablelayout=view.findViewById(R.id.invisiblelayout)
        playpausebutton=view.findViewById(R.id.pause_button)
        songTitle=view.findViewById(R.id.song_title)


        (activity as MainActivity)
                .title=getString(R.string.all_songs)


       // enableSwipeToDeleteAndUndo()
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

        getSongsList=ContentResolver.getSongsFromThePhone(mActivity!!)
       //Declaring the preferences to save the sorting order which we select*/
        val prefs = activity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)
        val action_sort_ascending = prefs?.getString("action_sort_ascending", "true")
        val action_sort_recent = prefs?.getString("action_sort_recent", "false")
        //If there are no songs we do not display the list instead we display no songs message*/
        if (getSongsList == null) {
            songsplayingvisiblelayout?.visibility = View.INVISIBLE
            songsnotavailablelayout?.visibility = View.VISIBLE
        }
        //If there are songs in the device, we display the list*/
        else {

            mainScreenAdapter = MainScreenAdapter(getSongsList as ArrayList<Songs>,context!!)

            val mLayoutManager =
                LinearLayoutManager(mActivity)
            Statified.recyclerViewSongs?.layoutManager = mLayoutManager
            Statified.recyclerViewSongs?.itemAnimator =
                DefaultItemAnimator()
            Statified.recyclerViewSongs?.adapter = mainScreenAdapter
        }
        /*If the songs list is not empty, then we check whether applied any comparator
        * And we use that comparator and sort the list accordingly*/
        if (getSongsList != null) {
            if (action_sort_ascending!!.equals("true", ignoreCase = true)) {
                Collections.sort(getSongsList, Songs.Statified.nameComparator)
                mainScreenAdapter?.notifyDataSetChanged()
            } else if (action_sort_recent!!.equals("true", ignoreCase = true)) {
                Collections.sort(getSongsList, Songs.Statified.dateComparator)
                mainScreenAdapter?.notifyDataSetChanged()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        bottomBarSetup()
    }



    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.main, menu)

        return
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)


    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val switcher = item?.itemId
        if (switcher == R.id.sort_by_name) {
            /*Whichever action item is selected, we save the preferences and perform the operation of comparison*/
            val editor = mActivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()
            editor?.putString("action_sort_ascending", "true")
            editor?.putString("action_sort_recent", "false")
            editor?.apply()
            if (getSongsList != null) {
                Collections.sort(getSongsList, Songs.Statified.nameComparator)
            }
            mainScreenAdapter?.notifyDataSetChanged()
            return false
        } else if (switcher == R.id.sort_recent_items) {
            val editortwo = mActivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()
            editortwo?.putString("action_sort_recent", "true")
            editortwo?.putString("action_sort_ascending", "false")
            editortwo?.apply()
            if (getSongsList != null) {
                Collections.sort(getSongsList, Songs.Statified.dateComparator)
            }
            mainScreenAdapter?.notifyDataSetChanged()
            return false
        }
        return super.onOptionsItemSelected(item)
    }

    fun bottomBarSetup(){

        bottomBarClickHandler()
        try {

            songTitle?.setText(SongsPlayingFragment.Statified.currentSongPlayer?.songTitle)

            if (SongsPlayingFragment.Statified.musicBgService!! ===null){
                return
            }
            if( SongsPlayingFragment.Statified.musicBgService!!.mediaPlayer !=null) {
                SongsPlayingFragment.Statified.musicBgService!!.mediaPlayer?.setOnCompletionListener {

                    songTitle?.setText(SongsPlayingFragment.Statified.currentSongPlayer?.songTitle)
                    SongsPlayingFragment.Staticated.onSongComplete()

                }
                if (SongsPlayingFragment.Statified.musicBgService!!.mediaPlayer?.isPlaying as Boolean) {

                    musicplayerlayout?.visibility = View.VISIBLE
                } else {

                    musicplayerlayout?.visibility = View.INVISIBLE
                }
            }
        }catch (e:Exception){

            e.printStackTrace()

        }


    }

    fun bottomBarClickHandler(){

        musicplayerlayout?.setOnClickListener {

            FavoriteFragment.Statified.mediaPlayer=SongsPlayingFragment.Statified.musicBgService!!.mediaPlayer
            val songsPlayingFragment= SongsPlayingFragment()
            var obj=Bundle()
            obj.putString("artist",SongsPlayingFragment.Statified.currentSongPlayer?.songArtist)
            obj.putString("path",SongsPlayingFragment.Statified.currentSongPlayer?.songPath)
            obj.putString("title",SongsPlayingFragment.Statified.currentSongPlayer?.songTitle)
            obj.putInt("id",SongsPlayingFragment.Statified.currentSongPlayer?.songId?.toInt() as Int)
            obj.putInt("songPosition",SongsPlayingFragment.Statified.currentSongPlayer?.songPosition?.toInt() as Int)
            obj.putBoolean("songPlaying",SongsPlayingFragment.Statified.currentSongPlayer?.isPlaying!!)

            obj.putParcelableArrayList("songslist",getSongsList)
            songsPlayingFragment.arguments=obj

            obj.putString("FavBottomBar","success")

            fragmentManager?.beginTransaction()?.replace(R.id.details_fragment,songsPlayingFragment)
                    ?.addToBackStack("SongsPlayingFragment")?.commit()

         }

        playpausebutton?.setOnClickListener({
            if(SongsPlayingFragment.Statified.musicBgService!!.mediaPlayer?.isPlaying as Boolean){
                SongsPlayingFragment.Statified.musicBgService!!.mediaPlayer?.pause()
                trackPosition=SongsPlayingFragment.Statified.currentSongPlayer?.songPosition?.toInt() as Int
                playpausebutton?.setBackgroundResource(R.mipmap.play_icon)

            }else{
                SongsPlayingFragment.Statified.musicBgService!!.mediaPlayer?.seekTo(trackPosition)
                SongsPlayingFragment.Statified.musicBgService!!.mediaPlayer?.start()
                playpausebutton?.setBackgroundResource(R.mipmap.pause_icon)
            }

        })

    }


    private fun enableSwipeToDeleteAndUndo() {
        val swipeToDeleteCallback: SwipeControllerEditCallback = object : SwipeControllerEditCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                val position = viewHolder.adapterPosition
                val item = mainScreenAdapter!!.content_list!!.get(position)
                showDialog(item)
            }

        }
        val itemTouchhelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchhelper.attachToRecyclerView(Statified.recyclerViewSongs)
    }


    fun showDialog(song : Songs){
        val taskEditText = EditText(context)
        val dialog: AlertDialog = AlertDialog.Builder(context)
            .setTitle(getString(R.string.update_song))
            .setMessage(getString(R.string.update_song_desc))
            .setView(taskEditText)
            .setPositiveButton(getString(R.string.update), DialogInterface.OnClickListener { dialog, which ->

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentResolver.updateSong(mActivity!!,song)
                    mainScreenAdapter!!.notifyDataSetChanged()
                }

            })
            .setNegativeButton(getString(R.string.cancel_text), null)
            .create()

        dialog.show()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9) {
            if (resultCode != 0) {
                val pos = mainScreenAdapter!!.position
                ContentResolver.deleteSong(context!!,mainScreenAdapter!!.content_list!!.get(pos))
                mainScreenAdapter!!.notifyItemRemoved(pos);
                Toast.makeText(context, getString(R.string.song_delete_message),Toast.LENGTH_SHORT).show()

            }
        }
        //This is still in progress
        if (requestCode == 8) {
            val pos = mainScreenAdapter!!.position
            ContentResolver.updateSong(context!!,mainScreenAdapter!!.content_list!!.get(pos))
            mainScreenAdapter!!.notifyDataSetChanged()
            Toast.makeText(context, getString(R.string.updated_song),Toast.LENGTH_SHORT).show()
        }

    }





}
