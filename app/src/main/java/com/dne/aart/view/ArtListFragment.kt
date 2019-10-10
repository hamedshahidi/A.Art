package com.dne.aart.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dne.aart.R
import com.dne.aart.util.AdoptiveListAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_art_list.*
import kotlinx.android.synthetic.main.fragment_art_list.view.*
import kotlinx.android.synthetic.main.fragment_expo_list.*
import kotlinx.android.synthetic.main.fragment_sign_up.view.*


class ArtListFragment : Fragment() {

    private var expoId = 0
    private var isAdmin: Boolean = false

    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val safeArgs: ArtListFragmentArgs by navArgs()
        expoId = safeArgs.expoId

        isAdmin = arguments!!.getBoolean("isAdmin")
        Log.d("DBGADMIN", "artlist admin: $isAdmin")

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_art_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // configuring theadapter for model list view
        linearLayoutManager = LinearLayoutManager(activity!!.applicationContext)
        rcv_models.layoutManager = linearLayoutManager
        rcv_models.adapter = AdoptiveListAdapter(context!!,expoId, isAdmin)

        // show divider between list items
        val dividerItemDecoration = DividerItemDecoration(
            rcv_models.context,
            linearLayoutManager.orientation
        )
        rcv_models.addItemDecoration(dividerItemDecoration)
    }
}
