package com.nobodysapps.greentastic.ui.transport


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nobodysapps.greentastic.R
import com.nobodysapps.greentastic.ui.dummy.DummyContent.DummyItem
import com.nobodysapps.greentastic.ui.transport.TransportFragment.OnListFragmentInteractionListener
import com.nobodysapps.greentastic.utils.setTint
import kotlinx.android.synthetic.main.transport_list_item.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class TransportRecyclerViewAdapter : RecyclerView.Adapter<TransportRecyclerViewAdapter.ViewHolder>() {
    var vehicles = emptyList<Vehicle>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }
    private var mListener: OnListFragmentInteractionListener? = null
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
        val vehicle = vehicles[position]
        holder.vehicleView.setImageResource(vehicle.type.resourceValue)
        holder.vehicleView.setTint(vehicle.total.color)

        val critImages = listOf(R.drawable.ic_co2, R.drawable.ic_cal, R.drawable.ic_toxic, R.drawable.ic_money, R.drawable.ic_time)
        val critValues = listOf(vehicle.emission, vehicle.calories, vehicle.toxicity, vehicle.price, vehicle.duration)
        val critViews = listOf(holder.co2Views, holder.calViews, holder.toxViews, holder.priceViews, holder.durationViews)

        critViews.forEachIndexed { index, (imageView, textView) ->
            val vehicleValue = critValues[index]
            imageView.setImageResource(critImages[index])
            imageView.setTint(vehicleValue.color)
            textView.text = vehicleValue.unit.evaluate(vehicleValue.absoluteValue)
            textView.setTextColor(vehicleValue.color)
        }

        with(holder.mView) {
            tag = vehicle
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = vehicles.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val vehicleView: ImageView = mView.ivVehicle
        val co2Views = Pair<ImageView, TextView>(mView.ivCO2, mView.tvCO2)
        val durationViews = Pair<ImageView, TextView>(mView.ivDuration, mView.tvDuration)
        val toxViews = Pair(mView.ivToxic, mView.tvToxic)
        val priceViews = Pair(mView.ivMoney, mView.tvMoney)
        val calViews = Pair(mView.ivCalories, mView.tvCalories)
    }
}
