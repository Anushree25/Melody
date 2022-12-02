package myecho.com.melody.Adapters

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi

import myecho.com.melody.Fragments.SongsPlayingFragment
import myecho.com.melody.R
import myecho.com.melody.Songs
import myecho.com.melody.utils.Constants
import myecho.com.melody.utils.ContentResolver

class  MainScreenAdapter(content_list:ArrayList<Songs>,context:Context):
    RecyclerView.Adapter<MainScreenAdapter.SongsViewHolder>(){

    var content_list:ArrayList<Songs>?=null
    var mcontext:Context?=null
    var position = 0

    init {
        this.content_list=content_list
        this.mcontext=context

    }

    class SongsViewHolder(itemview:View?):
        RecyclerView.ViewHolder(itemview!!){

        var title: TextView?=null
        var artist:TextView?=null
        var layout:RelativeLayout?=null

        init {
            title=itemview?.findViewById(R.id.tracktitle)
            artist=itemview?.findViewById(R.id.artist)
            layout=itemview?.findViewById(R.id.content_view)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongsViewHolder {

        var itemview=LayoutInflater.from(parent?.context).inflate(R.layout.custom_main_screen_adapter,parent,false)
        var view= SongsViewHolder(itemview)

        return  view

    }

    override fun getItemCount(): Int {
        if(content_list!=null) {
            return (content_list as ArrayList<Songs>).size
        }else{
            return 0
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: SongsViewHolder, position: Int) {
        var songobject=content_list?.get(position)
        holder?.title?.setText(songobject?.songTitle)
        holder?.artist?.setText(songobject?.artist)

        holder?.layout?.setOnClickListener {
            this.position =  holder.adapterPosition
            val songsPlayingFragment= SongsPlayingFragment()
            var obj=Bundle()
            obj.putString(Constants.ARTIST,songobject?.artist)
            obj.putString(Constants.PATH,songobject?.details)
            obj.putString(Constants.TITLE,songobject?.songTitle)
            obj.putInt(Constants.ID,songobject?.songId?.toInt() as Int)
            obj.putInt(Constants.SONG_POSITION,position)
            obj.putParcelableArrayList(Constants.SONG_LIST,content_list)
            songsPlayingFragment.arguments=obj
            (mcontext as FragmentActivity).supportFragmentManager.beginTransaction().
                    replace(R.id.details_fragment,songsPlayingFragment)
                    .addToBackStack((mcontext as FragmentActivity).getString(R.string.song_fragment))
                    .commit()

        }

        holder?.layout?.setOnLongClickListener{
            this.position =  holder.adapterPosition
            ContentResolver.deleteSong(mcontext!!,songobject!!)
            true

        }


    }

}


