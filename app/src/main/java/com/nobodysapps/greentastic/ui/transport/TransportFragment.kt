package com.nobodysapps.greentastic.ui.transport

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.nobodysapps.greentastic.R
import com.nobodysapps.greentastic.application.GreentasticApplication
import com.nobodysapps.greentastic.ui.ViewModelFactory
import kotlinx.android.synthetic.main.transport_fragment.view.*
import javax.inject.Inject

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [TransportFragment.OnListFragmentInteractionListener] interface.
 */
class TransportFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: TransportViewModel

    private var listener: OnListFragmentInteractionListener? = null
    private val transportRecyclerViewAdapter = TransportRecyclerViewAdapter()

    override fun onAttach(context: Context) {
        (requireActivity().application as GreentasticApplication).appComponent.inject(this)
        super.onAttach(context)
//        if (context is OnListFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.transport_fragment, container, false)

        Log.d(TAG, viewModelFactory.toString())
        viewModel = ViewModelProvider(this, viewModelFactory).get(TransportViewModel::class.java)
        arguments?.let {
            val source = it.getString(SOURCE_KEY, null) ?: ""
            val dest = it.getString(DESTINATION_KEY, null) ?: ""
            activity?.lifecycleScope?.let { scope ->
                viewModel.load(source, dest, scope)  // TODO check if okay to do this way
            }
        }
        view.rvTransport.adapter = transportRecyclerViewAdapter
        viewModel.vehicles.observe(viewLifecycleOwner, Observer { vehicles ->
            transportRecyclerViewAdapter.vehicles = vehicles
        })
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            view.pbLoading.visibility = if (it) View.VISIBLE else View.GONE
        })
        return view
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: Vehicle)
    }

    companion object {
        const val TAG = "TransportFragment"
        private const val SOURCE_KEY = "source"
        private const val DESTINATION_KEY = "dest"

        @JvmStatic
        fun newInstance(source: String, dest: String) =
            TransportFragment().apply {
                arguments = Bundle().apply {
                    putString(SOURCE_KEY, source)
                    putString(DESTINATION_KEY, dest)
                }
            }
    }
}
