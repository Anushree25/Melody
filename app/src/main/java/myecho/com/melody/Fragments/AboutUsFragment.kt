package myecho.com.melody.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.*
import myecho.com.melody.MainActivity

import myecho.com.melody.R


class AboutUsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_about_us, container, false)


        if (container != null) {
            container.removeAllViews();
        }
        (activity as MainActivity)
                .title = getString(R.string.about_us_string)

        return view

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
