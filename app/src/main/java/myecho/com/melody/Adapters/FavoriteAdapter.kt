package myecho.com.melody.Adapters

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import myecho.com.melody.Fragments.SongsPlayingFragment
import myecho.com.melody.R
import myecho.com.melody.Songs
import myecho.com.melody.utils.Constants

class FavoriteAdapter(songDetails: ArrayList<Songs>,context: Context):
    RecyclerView.Adapter<FavoriteAdapter.FavSongsViewHolder>(){

    var songs_list:ArrayList<Songs>?=null
    var mcontext:Context?=null

    init {
        this.songs_list=songDetails
        this.mcontext=context

    }

    override fun onBindViewHolder(holder: FavSongsViewHolder, position: Int) {

        var songobject=songs_list?.get(position)
        holder?.title?.setText(songobject?.songTitle)
        holder?.artist?.setText(songobject?.artist)
        holder?.layout?.setOnClickListener {

            val songsPlayingFragment= SongsPlayingFragment()
            var obj=Bundle()
            obj.putString(Constants.ARTIST,songobject?.artist)
            obj.putString(Constants.PATH,songobject?.details)
            obj.putString(Constants.TITLE,songobject?.songTitle)
            obj.putInt(Constants.ID,songobject?.songId?.toInt() as Int)
            obj.putInt(Constants.SONG_POSITION,position)
            obj.putParcelableArrayList(Constants.SONG_LIST,songs_list)
            songsPlayingFragment.arguments=obj
            (mcontext as FragmentActivity).supportFragmentManager.beginTransaction().replace(myecho.com.melody.R.id.details_fragment,songsPlayingFragment)
                    .addToBackStack((mcontext as FragmentActivity).getString(R.string.song_fragment)).commit()

        }

    }



    class FavSongsViewHolder(itemview:View?):
        RecyclerView.ViewHolder(itemview!!){

        var title: TextView?=null
        var artist:TextView?=null
        var layout:RelativeLayout?=null

        init {
            title=itemview?.findViewById(myecho.com.melody.R.id.tracktitle)
            artist=itemview?.findViewById(myecho.com.melody.R.id.artist)
            layout=itemview?.findViewById(myecho.com.melody.R.id.content_view)


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteAdapter.FavSongsViewHolder {

        var itemview=LayoutInflater.from(parent?.context).inflate(myecho.com.melody.R.layout.custom_main_screen_adapter,parent,false)
        var view=  FavoriteAdapter.FavSongsViewHolder(itemview)

        return  view

    }

    override fun getItemCount(): Int {
        if(songs_list!=null) {
            return (songs_list as ArrayList<Songs>).size
        }else{
            return 0
        }


    }

}