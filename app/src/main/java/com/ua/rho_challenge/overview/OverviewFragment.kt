package com.ua.rho_challenge.overview

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ua.rho_challenge.R
import com.ua.rho_challenge.databinding.FragmentOverviewBinding
import com.ua.rho_challenge.network.Tweet

/**
 * This fragment shows a list of tweets consumed through the Twitter Streaming API.
 */
class OverviewFragment : Fragment(), SearchView.OnQueryTextListener {
    private lateinit var searchView: SearchView

    /**
     * Lazily initialize [OverviewViewModel].
     */
    private val viewModel: OverviewViewModel by lazy {
        ViewModelProviders.of(this).get(OverviewViewModel::class.java)
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
        binding.setLifecycleOwner(this)

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel

        // Sets the adapter of the tweetList RecyclerView
        val adapter = TweetsAdapter()

        binding.tweetList.adapter = adapter

        viewModel.properties.observe(
            this.viewLifecycleOwner,
            object : Observer<ArrayList<Tweet>> {
                override fun onChanged(t: ArrayList<Tweet>?) {
                    t?.let {
                        // Sets new Data to RecyclerView
                        adapter.setEmployeeList(it)
                        // Scrolls down to last position of the list. Gives the UI flow perception
                        //binding.tweetList.scrollToPosition(t.size - 1)
                    }
                }
            })

        if (!context?.let { isNetworkAvailable(it) }!!){
            viewModel.unavailableInternetConnection()
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    /**
     * Checks if there is an internet connection
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        if (!isConnected) {
            Log.d("debug", "Device is not connected!")
            displayToast(getString(R.string.no_connection))
        }
        else{
            Log.d("debug", "Device is connected!")
        }
        return isConnected
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
        if (context?.let { isNetworkAvailable(it) }!!){
            if (!query.isNullOrBlank() or !query.isNullOrEmpty()) {
                searchView.clearFocus();
                displayToast(getString(R.string.start_search))
                query?.let { viewModel.searchStream(it) }

            }
            return true
        }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    /**
     * Generic function to display toasts
     */
    fun displayToast(msg: String) {
        val t = Toast.makeText(context, msg, Toast.LENGTH_LONG)
        t.show()
    }
}
