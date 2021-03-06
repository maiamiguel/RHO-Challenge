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
import com.google.android.material.snackbar.Snackbar
import com.ua.rho_challenge.network.ConnectivityReceiver
import com.ua.rho_challenge.R
import com.ua.rho_challenge.viewmodels.OverviewViewModel
import com.ua.rho_challenge.adapters.TweetsAdapter
import com.ua.rho_challenge.databinding.FragmentOverviewBinding
import com.ua.rho_challenge.models.Tweet
import com.ua.rho_challenge.utils.TTLList
import kotlinx.android.synthetic.main.fragment_overview.*

/**
 * This fragment shows a list of tweets consumed through the Twitter Streaming API.
 */
class OverviewFragment : Fragment(), SearchView.OnQueryTextListener,
    ConnectivityReceiver.ConnectivityReceiverListener {
    private var isConnected: Boolean = false
    private lateinit var connectivityReceiver: ConnectivityReceiver
    private lateinit var searchView: SearchView
    private lateinit var mSnackBar: Snackbar
    private lateinit var searchQuery : String

    /**
     * Lazily initialize [OverviewViewModel].
     */
    private lateinit var viewModel: OverviewViewModel

    /**
     * Inflates the layout with Data Binding, sets its lifecycle owner to the OverviewFragment
     * to enable Data Binding to observe LiveData, and sets up the RecyclerView with an adapter.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(
            this
        ).get(OverviewViewModel::class.java)

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
                t.let {
                    // Sets new Data to RecyclerView
                    Log.d("debug", "setTweetsList changed")
                    adapter.setTweetsList(it)
                }
            })

        viewModel.tweetsDB.observe(
            this.viewLifecycleOwner,
            Observer { t ->
                t.let {
                    // Sets new Data to RecyclerView from Room DB
                    Log.d("debug", "DB CHANGED DATA")
                    if (!isConnected) {
                        adapter.setTweetsList(it)
                    }
                }
            })

        connectivityReceiver =
            ConnectivityReceiver()

        activity?.registerReceiver(
            connectivityReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(connectivityReceiver)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d("debug", "onSaveInstanceState - $searchQuery")
        outState.putString("searchQuery", searchQuery)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d("debug", "onActivityCreated")

        searchQuery = savedInstanceState?.getString("searchQuery").toString()

        Log.d("debug", "onActivityCreated Bundle - $searchQuery")

        super.onActivityCreated(savedInstanceState)
    }

    /**
     * Inflates the search menu
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)

        if (!isConnected && searchQuery != "null"){
            searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                    // it is important to call this before we set our own query text.
                    searchView.onActionViewExpanded()
                    searchView.setQuery(searchQuery, false)
                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem?) = true
            })
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Listens to search text submission and passes it to viewModel to initiate the search
     */
    override fun onQueryTextSubmit(query: String?): Boolean {
        if ((!query.isNullOrBlank() or !query.isNullOrEmpty())) {
            if (isConnected) {
                searchView.clearFocus();
                displayToast(getString(R.string.start_search))

                query?.let {
                    searchQuery = it
                    viewModel.searchStream(it)
                }
                return true
            } else {
                displayToast(getString(R.string.no_connection))
            }
        }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (viewModel.isJobRunning()) {
            viewModel.cancelJob()
            displayToast(getString(R.string.stop_stream))
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
        Log.d("debug", "onNetworkConnectionChanged")
        if (isConnected) {
            Log.d("debug", "Device is connected.");
            this.isConnected = true;

            mSnackBar =
                Snackbar.make(rootLayout, getString(R.string.connection_yes), Snackbar.LENGTH_LONG)
            mSnackBar.show()
        } else {
            Log.d("debug", "Device is not Connected");
            this.isConnected = false;

            mSnackBar =
                Snackbar.make(rootLayout, getString(R.string.no_connection), Snackbar.LENGTH_LONG)
            mSnackBar.show()

            if (viewModel.isJobRunning()) {
                viewModel.cancelJob()
            }
        }
    }
}
