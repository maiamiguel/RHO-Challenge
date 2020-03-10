package com.ua.rho_challenge.ui

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ua.rho_challenge.ConnectivityReceiver
import com.ua.rho_challenge.R
import com.ua.rho_challenge.databinding.FragmentOverviewBinding
import com.ua.rho_challenge.viewmodels.OverviewViewModel
import com.ua.rho_challenge.adapters.TweetsAdapter

/**
 * This fragment shows a list of tweets consumed through the Twitter Streaming API.
 */
class OverviewFragment : Fragment(), SearchView.OnQueryTextListener,
    ConnectivityReceiver.ConnectivityReceiverListener {
    private var isConnected: Boolean = false
    private lateinit var connectivityReceiver: ConnectivityReceiver
    private lateinit var searchView: SearchView

    /**
     * Lazily initialize [OverviewViewModel].
     */
    private val viewModel: OverviewViewModel by lazy {
        ViewModelProvider(this).get(OverviewViewModel::class.java)
    }

    /**
     * Inflates the layout with Data Binding, sets its lifecycle owner to the OverviewFragment
     * to enable Data Binding to observe LiveData, and sets up the RecyclerView with an adapter.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentOverviewBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel

        // Sets the adapter of the tweetList RecyclerView
        val adapter = TweetsAdapter()

        binding.tweetList.adapter = adapter

        viewModel.properties.observe(
            this.viewLifecycleOwner,
            Observer { t ->
                t?.let {
                    // Sets new Data to RecyclerView
                    adapter.setTweetsList(it)
                    // Scrolls down to last position of the list. Gives the UI flow perception
                    //binding.tweetList.scrollToPosition(t.size - 1)
                }
            })

        connectivityReceiver = ConnectivityReceiver()

        activity?.registerReceiver(
            connectivityReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        activity?.unregisterReceiver(connectivityReceiver)
    }

    /**
     * Inflates the search menu
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)

        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Listens to search text submission and passes it to viewModel to initiate the search
     */
    override fun onQueryTextSubmit(query: String?): Boolean {
        if ((!query.isNullOrBlank() or !query.isNullOrEmpty()) and isConnected) {
            searchView.clearFocus();
            displayToast(getString(R.string.start_search))

            query?.let { viewModel.searchStream(it) }
            return true
        }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (viewModel.isJobRunning()){
            viewModel.cancelJob()
        }
        return false
    }

    /**
     * Generic function to display toasts
     */
    private fun displayToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected) {
            Log.d("debug", "Device is connected.");
            this.isConnected = true;
            displayToast(getString(R.string.connection_yes))
        } else {
            Log.d("debug", "Device is not Connected");
            this.isConnected = false;
            displayToast(getString(R.string.no_connection))
            if (viewModel.isJobRunning()){
                viewModel.cancelJob()
            }
        }
    }
}
