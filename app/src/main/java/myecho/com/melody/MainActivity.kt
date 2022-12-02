package myecho.com.melody


import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import myecho.com.melody.Adapters.NavigationDrawerAdapter
import myecho.com.melody.Fragments.MainScreenFragment


class MainActivity : AppCompatActivity() {

    object Statified {
        var drawerLayout: DrawerLayout? = null
    }

    var navArrayList: ArrayList<String> = ArrayList()
    var imagesfordrawer= intArrayOf(R.mipmap.navigation_allsongs,
            R.mipmap.navigation_favorites,
            R.mipmap.navigation_settings,
            R.mipmap.navigation_aboutus)

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var toolbar=findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        Statified.drawerLayout=findViewById(R.id.drawer_layout)
        navArrayList.add("All Songs")
        navArrayList.add("Favorites")
        navArrayList.add("Settings")
        navArrayList.add("About us")


        val toggle = ActionBarDrawerToggle(
                this, Statified.drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        Statified.drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()

        var detailsfragment= MainScreenFragment();
        this.supportFragmentManager.beginTransaction().add(R.id.details_fragment,detailsfragment,"MainScreenFragment").commit()

        var adapter=NavigationDrawerAdapter(navArrayList,imagesfordrawer,this)
        adapter.notifyDataSetChanged()

        var nav_view=findViewById<RecyclerView>(R.id.navigationrecyclerview)
        nav_view.layoutManager=
            LinearLayoutManager(this)
        nav_view.itemAnimator= DefaultItemAnimator()
        nav_view.adapter=adapter
        nav_view.setHasFixedSize(true)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment =supportFragmentManager.findFragmentByTag("MainScreenFragment")
        if (fragment != null) {

            fragment.onActivityResult(requestCode, resultCode, data)
        }

    }


}

