package com.ua.rho_challenge.overview

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.ua.rho_challenge.R
import com.ua.rho_challenge.databinding.FragmentOverviewBinding

/**
 * This fragment shows the the status of the list of tweets consumed through the Twitter Streaming API.
 */
class OverviewFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var searchView: SearchView
    /**
     * Lazily initialize our [OverviewViewModel].
     */
    private val viewModel: OverviewViewModel by lazy {
        ViewModelProviders.of(this).get(OverviewViewModel::class.java)
    }

    /**
     * Inflates the layout with Data Binding, sets its lifecycle owner to the OverviewFragment
     * to enable Data Binding to observe LiveData, and sets up the RecyclerView with an adapter.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentOverviewBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.setLifecycleOwner(this)

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel

        // Sets the adapter of the tweetList RecyclerView
        binding.tweetList.adapter = TweetsAdapter()

        setHasOptionsMenu(true)
        return binding.root
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
        if (!query.isNullOrBlank() or !query.isNullOrEmpty()){
            //searchView.clearFocus();
            query?.let { viewModel.searchStream(it) }

        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }
}
