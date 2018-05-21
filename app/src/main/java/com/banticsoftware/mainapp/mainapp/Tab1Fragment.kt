package com.banticsoftware.mainapp.mainapp


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.model.LatLng

import kotlinx.android.synthetic.main.fragment_tab1.*


class Tab1Fragment : Fragment() {



    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_tab1, container, false)



        rdbOne.visibility= View.INVISIBLE
        rdbTwo.visibility= View.INVISIBLE
        rdbThree.visibility = View.INVISIBLE




    }



}// Required empty public constructor
