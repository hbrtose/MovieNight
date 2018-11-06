package com.yossisegev.movienight.details

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.yossisegev.movienight.R
import com.yossisegev.movienight.common.ImageLoader
import com.yossisegev.movienight.common.SimpleTransitionEndedCallback
import com.yossisegev.movienight.entities.Video
import kotlinx.android.synthetic.main.activity_movie_details.*
import kotlinx.android.synthetic.main.details_overview_section.*
import kotlinx.android.synthetic.main.details_video_section.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MovieDetailsActivity : AppCompatActivity() {

    private val detailsViewModel: MovieDetailsViewModel by viewModel()
    private val imageLoader: ImageLoader by inject()

    companion object {
        private const val MOVIE_ID: String = "extra_movie_id"
        private const val MOVIE_POSTER_URL: String = "extra_movie_poster_url"

        fun newIntent(context: Context, movieId: Int, posterUrl: String?): Intent {
            val i = Intent(context, MovieDetailsActivity::class.java)
            i.putExtras(bundleOf(MOVIE_ID to movieId, MOVIE_POSTER_URL to posterUrl))
            return i
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)
        postponeEnterTransition()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE and View.SYSTEM_UI_FLAG_FULLSCREEN
        details_back_button.setOnClickListener { finish() }
        details_favorite_fab.setOnClickListener { detailsViewModel.favoriteButtonClicked(intent.getIntExtra(MOVIE_ID, 0)) }

        intent.getStringExtra(MOVIE_POSTER_URL)?.let {
            imageLoader.load(it, details_poster) {
                startPostponedEnterTransition()
            }
        } ?: run {
            startPostponedEnterTransition()
        }

        window.sharedElementEnterTransition.addListener(SimpleTransitionEndedCallback {
            observeViewState()
        })

        // If we don't have any entering transition
        if (savedInstanceState != null) {
            observeViewState()
        } else {
            detailsViewModel.getMovieDetails(intent.getIntExtra(MOVIE_ID, 0))
        }
    }

    private fun observeViewState() {
        detailsViewModel.viewState.observe(this, Observer { viewState -> handleViewState(viewState) })
        detailsViewModel.favoriteState.observe(this, Observer { favorite -> handleFavoriteStateChange(favorite) })
        detailsViewModel.errorState.observe(this, Observer { throwable ->
            throwable?.let {
                toast(throwable.message?:"")
            }
        })
    }

    private fun onVideoSelected(video: Video) {
        video.url?.let {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
            startActivity(browserIntent)
        }
    }

    private fun handleViewState(state: MovieDetailsViewState?) {
        state?.let {
            details_title.text = state.title
            details_overview.text = state.overview
            details_release_date.text = String.format(getString(R.string.release_date_template, state.releaseDate))
            details_score.text = if (state.votesAverage == 0.0) getString(R.string.n_a) else state.votesAverage.toString()
            state.genres?.let { details_tags.tags = state.genres }

            val transition = Slide()
            transition.excludeTarget(details_poster, true)
            transition.duration = 750
            TransitionManager.beginDelayedTransition(details_root_view, transition)
            details_title.visibility = View.VISIBLE
            details_release_date.visibility = View.VISIBLE
            details_score.visibility = View.VISIBLE
            details_release_date_layout.visibility = View.VISIBLE
            details_score_layout.visibility = View.VISIBLE
            details_overview_section.visibility = View.VISIBLE
            details_video_section.visibility = View.VISIBLE
            details_tags.visibility = View.VISIBLE

            state.backdropUrl?.let { backdrop ->  imageLoader.load(backdrop, details_backdrop) }

            state.videos?.let { videos ->
                val videosAdapter = VideosAdapter(videos, this::onVideoSelected)
                details_videos.layoutManager = LinearLayoutManager(this)
                details_videos.adapter = videosAdapter

            } ?: run {
                details_video_section.visibility = View.GONE
            }
        }
    }

    private fun handleFavoriteStateChange(favorite: Boolean?) {
        favorite?.let {
            (details_favorite_fab as View).visibility = View.VISIBLE
            details_favorite_fab.setImageDrawable(
                    if (favorite)
                        ContextCompat.getDrawable(this, R.drawable.ic_favorite_white_36dp)
                    else
                        ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_white_36dp))
        }
    }
}
