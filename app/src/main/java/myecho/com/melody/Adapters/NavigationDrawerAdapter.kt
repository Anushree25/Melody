package myecho.com.melody.Adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import myecho.com.melody.Fragments.AboutUsFragment
import myecho.com.melody.Fragments.FavoriteFragment
import myecho.com.melody.MainActivity
import myecho.com.melody.Fragments.MainScreenFragment
import myecho.com.melody.Fragments.SettingsFragment


class  NavigationDrawerAdapter(content_list:ArrayList<String>,images_list:IntArray,context:Context):
    RecyclerView.Adapter<NavigationDrawerAdapter.NavViewHolder>(){

    var content_list:ArrayList<String>?=null
    var  images_list:IntArray?=null
    var mcontext:Context?=null

    init {
        this.content_list=content_list
        this.images_list=images_list
        this.mcontext=context

    }

    class NavViewHolder(itemview:View?):
        RecyclerView.ViewHolder(itemview!!){

        var icon: ImageView?=null
        var text:TextView?=null
        var layout:RelativeLayout?=null

        init {
            icon=itemview?.findViewById(myecho.com.melody.R.id.nav_item_image)
            text=itemview?.findViewById(myecho.com.melody.R.id.nav_item_text)
            layout=itemview?.findViewById(myecho.com.melody.R.id.navigation_drawer_item)


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavViewHolder {

        var itemview=LayoutInflater.from(parent?.context).inflate(myecho.com.melody.R.layout.custom_row_drawer,parent,false)
        var view= NavViewHolder(itemview)

        return  view

    }

    override fun getItemCount(): Int {
        return  (content_list as ArrayList ).size

    }

    override fun onBindViewHolder(holder: NavViewHolder, position: Int) {
        holder?.icon?.setBackgroundResource(images_list?.get(position) as Int)
        holder?.text?.setText(content_list?.get(position))

        holder?.layout?.setOnClickListener {

            if(position==0){
                val mainScreenFragment= MainScreenFragment()
                (mcontext as MainActivity).supportFragmentManager.beginTransaction().replace(myecho.com.melody.R.id.details_fragment,mainScreenFragment).commit()

            }
            else if(position==1){
                val mainScreenFragment= FavoriteFragment()
                (mcontext as MainActivity).supportFragmentManager.beginTransaction().replace(myecho.com.melody.R.id.details_fragment,mainScreenFragment).commit()

            }
            if(position==2){
                val mainScreenFragment= SettingsFragment()
                (mcontext as MainActivity).supportFragmentManager.beginTransaction().replace(myecho.com.melody.R.id.details_fragment,mainScreenFragment)
                        .addToBackStack(null).commit()

            }

            if(position==3){
                val aboutUsFragment= AboutUsFragment()
                (mcontext as MainActivity).supportFragmentManager.beginTransaction().replace(myecho.com.melody.R.id.details_fragment,aboutUsFragment).addToBackStack(null).commit()


            }

            MainActivity.Statified.drawerLayout?.closeDrawers()

        }

    }
}