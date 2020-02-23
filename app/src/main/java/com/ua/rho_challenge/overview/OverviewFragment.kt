package com.ua.rho_challenge.overview

import android.content.Context
import android.net.ConnectivityManager
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
 * This fragment shows the list of tweets consumed through the Twitter Streaming API.
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
                        binding.tweetList.scrollToPosition(t.size - 1)
                    }
                }
            })

        setHasOptionsMenu(true)
        return binding.root
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val connection = connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected
        if (connection){
            Log.d("debug","true")
        }
        else{
            Log.d("debug","false")
        }
        return connection
    }

    /**
     * Inflates the search menu.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        this!!.context?.let { isNetworkAvailable(it) }
        if (!query.isNullOrBlank() or !query.isNullOrEmpty()) {
            searchView.clearFocus();
            displayToast("Search started..")
            query?.let { viewModel.searchStream(it) }

        }
        return true
    }

    fun displayToast(msg: String) {
        val t = Toast.makeText(context, msg, Toast.LENGTH_LONG)
        t.show()
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }
}
