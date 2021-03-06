package com.klekchyan.asteroidsnews.view.list

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.klekchyan.asteroidsnews.R
import com.klekchyan.asteroidsnews.databinding.FragmentListBinding
import com.klekchyan.asteroidsnews.repository.DownloadingState
import com.klekchyan.asteroidsnews.view.filter.FilterViewModel

class ListFragment : Fragment() {

    private var binding: FragmentListBinding? = null
    private val listViewModel: ListViewModel by viewModels()
    private val filterViewModel: FilterViewModel by viewModels({ requireActivity() })
    private var toast: Toast? = null
    private var snackbar: Snackbar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentListBinding.inflate(inflater)
        setHasOptionsMenu(true)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = AsteroidsAdapter(AsteroidsAdapterClickListener { asteroid, id ->
            when(id){
                0 -> listViewModel.onAsteroidClicked(asteroid)
                1 -> {
                    listViewModel.onAddAsteroidToFavoriteClicked(asteroid)
                    showToast(getString(R.string.adding_to_favorite_toast))
                }
                2 -> {
                    listViewModel.onDeleteAsteroidFromFavoriteClicked(asteroid)
                    showToast(getString(R.string.delete_from_favorite_toast))
                }
            }
        })

        binding?.lifecycleOwner = this
        binding?.viewModel = listViewModel
        binding?.asteroidsRecyclerView?.adapter  = adapter
        binding?.floatingActionButton?.setOnClickListener {
            listViewModel.onFilterClicked()
        }

        //ListViewModel observation
        listViewModel.listOfAsteroids.observe(viewLifecycleOwner, { list ->
            adapter.changeList(list, listViewModel.shownList.value)

        })

        listViewModel.isEmptyList.observe(viewLifecycleOwner, { isEmpty ->
            if(isEmpty) showEmptyListView() else showNotEmptyListView()
        })

        listViewModel.navigateToSpecificAsteroid.observe(viewLifecycleOwner, { asteroid ->
            asteroid?.let {
                findNavController().navigate(
                    ListFragmentDirections
                        .actionListFragmentToSpecificAsteroidFragment(it.id))
                listViewModel.onSpecificAsteroidNavigateDone()
            }
        })

        listViewModel.navigateToFilterFragment.observe(viewLifecycleOwner, { isClicked ->
            if (isClicked){
                findNavController().navigate(ListFragmentDirections.actionListFragmentToFilterFragment())
                listViewModel.onFilterNavigateDone()
            }
        })

        listViewModel.navigateToInfoFragment.observe(viewLifecycleOwner, { isClicked ->
            if (isClicked){
                findNavController().navigate(ListFragmentDirections.actionListFragmentToInfoFragment())
                listViewModel.onInfoNavigateDone()
            }
        })

        listViewModel.shownList.observe(viewLifecycleOwner, { shownList ->
            when(shownList){
                ShownList.ALL -> selectAll()
                else -> selectFavorite()
            }
        })

        listViewModel.progressIndicatorState.observe(viewLifecycleOwner, { state ->
            when(state){
                DownloadingState.START -> { binding?.progressIndicator?.isVisible = true }
                DownloadingState.FINISH -> { binding?.progressIndicator?.visibility = View.GONE }
                else -> showSnackBar(R.string.disconnected)
            }
        })

        //FilterViewModel observation
        filterViewModel.dateRange.observe(viewLifecycleOwner, { newDateRange ->
            listViewModel.changeDateRange(newDateRange)
        })

        filterViewModel.isHazardousFilter.observe(viewLifecycleOwner, { isHazardous ->
            listViewModel.changeFilterByHazardous(isHazardous)
        })

        filterViewModel.averageSizeFilter.observe(viewLifecycleOwner, { range ->
            listViewModel.changeFilterByAverageSize(range)
        })
    }

    private fun selectAll(){
        val (textAll, textFavorite) = getAllAndFavoriteStrings()

        binding?.allAsteroids?.text = getSpannableString(textAll ?: "")
        binding?.favoriteAsteroids?.text = textFavorite
    }

    private fun selectFavorite(){
        val (textAll, textFavorite) = getAllAndFavoriteStrings()

        binding?.allAsteroids?.text = textAll
        binding?.favoriteAsteroids?.text = getSpannableString(textFavorite ?: "")
    }

    private fun getAllAndFavoriteStrings(): Pair<String?, String?>{
        return context?.getString(R.string.all_asteroids) to context?.getString(R.string.favorite_asteroids)
    }

    private fun getSpannableString(text: String): SpannableString {
        val spannableFavorite = SpannableString(text)
        spannableFavorite.setSpan(UnderlineSpan(), 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableFavorite
    }

    private fun showEmptyListView(){
        binding?.emptyListText?.isVisible = true
        binding?.asteroidsRecyclerView?.visibility = View.GONE
    }

    private fun showNotEmptyListView(){
        binding?.emptyListText?.visibility = View.GONE
        binding?.asteroidsRecyclerView?.isVisible = true
    }

    private fun showSnackBar(stringId: Int){
        snackbar?.dismiss()
        snackbar = Snackbar.make(binding?.floatingActionButton!!,
            stringId,
            Snackbar.LENGTH_LONG)
            .setAnchorView(binding?.floatingActionButton!!)
        snackbar?.show()
    }

    private fun showToast(text: String){
        toast?.cancel()
        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
        toast?.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_bar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.info_item){
            listViewModel.onInfoClicked()
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}