package com.yossisegev.movienight.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yossisegev.movienight.R
import com.yossisegev.movienight.common.BaseFragment
import com.yossisegev.movienight.common.ImageLoader
import kotlinx.android.synthetic.main.fragment_favorite_movies.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Created by Yossi Segev on 11/11/2017.
 */
class FavoriteMoviesFragment : BaseFragment() {

    private val viewModel: FavoriteMoviesViewModel by viewModel()
    private val imageLoader: ImageLoader by inject()

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyMessage: TextView
    private lateinit var favoriteMoviesAdapter: FavoriteMoviesAdapter

    override fun onResume() {
        super.onResume()
        viewModel.getFavorites()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.viewState.observe(this, Observer {
            if (it != null) handleViewState(it)
        })
        viewModel.errorState.observe(this, Observer { throwable ->
            throwable?.let {
                context?.toast(throwable.message?:"")
            }
        })
    }

    private fun handleViewState(state: FavoritesMoviesViewState) {
        progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        emptyMessage.visibility = if (!state.isLoading && state.isEmpty) View.VISIBLE else View.GONE
        state.movies?.let { favoriteMoviesAdapter.setMovies(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.fragment_favorite_movies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = favorite_movies_progress
        favoriteMoviesAdapter = FavoriteMoviesAdapter(imageLoader) { movie, v ->
            navigateToMovieDetailsScreen(movie, v)
        }
        recyclerView = favorite_movies_recyclerview
        emptyMessage = favorite_movies_empty_message
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = favoriteMoviesAdapter

    }
}