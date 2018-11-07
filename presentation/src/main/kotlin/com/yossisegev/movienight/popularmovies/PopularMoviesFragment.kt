package com.yossisegev.movienight.popularmovies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.yossisegev.movienight.R
import com.yossisegev.movienight.common.BaseFragment
import com.yossisegev.movienight.common.ImageLoader
import kotlinx.android.synthetic.main.fragment_popular_movies.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Created by Yossi Segev on 11/11/2017.
 */
class PopularMoviesFragment : BaseFragment() {

    private val viewModel: PopularMoviesViewModel by viewModel()
    private val imageLoader: ImageLoader by inject()
    private lateinit var popularMoviesAdapter: PopularMoviesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.getPopularMovies()
        }
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

    private fun handleViewState(state: PopularMoviesViewState) {
        popular_movies_progress.visibility = if (state.showLoading) View.VISIBLE else View.GONE
        state.movies?.let { popularMoviesAdapter.addMovies(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.fragment_popular_movies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        popularMoviesAdapter = PopularMoviesAdapter(imageLoader) { movie, v ->
            navigateToMovieDetailsScreen(movie, v)
        }
        popular_movies_recyclerview.layoutManager = GridLayoutManager(activity, 2)
        popular_movies_recyclerview.adapter = popularMoviesAdapter
        activity?.title = getString(R.string.popular)
    }
}