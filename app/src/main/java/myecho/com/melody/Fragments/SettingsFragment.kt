package myecho.com.melody.Fragments


import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.*
import android.widget.Switch
import myecho.com.melody.MainActivity

import myecho.com.melody.R
import myecho.com.melody.utils.Constants

class SettingsFragment : Fragment() {

    var shakeSwitch: Switch? = null
    var mActivity: Activity? = null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_settings, container, false)
        shakeSwitch = view.findViewById(R.id.shakeSwitch)
        (activity as MainActivity)
                .title=getString(R.string.settings)
        this.setHasOptionsMenu(true)
        if (container != null) {
            container.removeAllViews();
        }

        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        mActivity == activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val prefs = mActivity?.getSharedPreferences(Constants.MY_PREFS_NAME, Context.MODE_PRIVATE)
        val isAllowed = prefs?.getBoolean(Constants.FEATURE, false)

        //checking the value

        if (isAllowed as Boolean) {

            shakeSwitch?.isChecked=true

        }else{

            shakeSwitch?.isChecked=false
        }


        shakeSwitch?.setOnCheckedChangeListener({CompoundButton,b->

            if(b){
                val editor=mActivity?.getSharedPreferences(Constants.MY_PREFS_NAME,Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("feature",true)
                editor?.apply()

            }else{
                val editor=mActivity?.getSharedPreferences(Constants.MY_PREFS_NAME,Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("feature",false)
                editor?.apply()
            }
        })
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
