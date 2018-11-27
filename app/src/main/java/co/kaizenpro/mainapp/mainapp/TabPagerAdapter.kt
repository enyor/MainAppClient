package co.kaizenpro.mainapp.mainapp

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter


class TabPagerAdapter(fm: FragmentManager, private var tabCount: Int) :
        FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {

        when (position) {
            0 -> return Tab4Fragment()
            1 -> return Tab2Fragment()
            2 -> return Tab3Fragment()
        //2 -> return Tab3Fragment()
           // 3 -> return Tab3Fragment() //Tab1Fragment()
            else -> return null
        }
    }

    override fun getCount(): Int {
        return tabCount
    }
}