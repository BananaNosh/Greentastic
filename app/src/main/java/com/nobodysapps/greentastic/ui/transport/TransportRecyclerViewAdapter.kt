package com.nobodysapps.greentastic.ui.transport

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.nobodysapps.greentastic.R


import com.nobodysapps.greentastic.ui.transport.TransportFragment.OnListFragmentInteractionListener
import com.nobodysapps.greentastic.ui.dummy.DummyContent.DummyItem

import kotlinx.android.synthetic.main.transport_list_item.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class TransportRecyclerViewAdapter(
    private val mValues: List<Vehicle>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<TransportRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Vehicle
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
//            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transport_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.vehicleView.setImageResource(item.type.resourceValue)

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val vehicleView: ImageView = mView.ivVehicle
    }
}
