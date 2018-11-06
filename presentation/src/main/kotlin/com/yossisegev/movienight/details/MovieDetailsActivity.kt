package com.yossisegev.movienight.details

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.lujun.androidtagview.TagContainerLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
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

    private lateinit var backdropImage: ImageView
    private lateinit var posterImage: ImageView
    private lateinit var title: TextView
    private lateinit var overview: TextView
    private lateinit var releaseDate: TextView
    private lateinit var score: TextView
    private lateinit var videos: RecyclerView
    private lateinit var videosSection: View
    private lateinit var backButton: View
    private lateinit var tagsContainer: TagContainerLayout
    private lateinit var favoriteButton: FloatingActionButton

    companion object {
        private const val MOVIE_ID: String = "extra_movie_id"
        private const val MOVIE_POSTER_URL: String = "extra_movie_poster_url"

        fun newIntent(context: Context, movieId: Int, posterUrl: String?): Intent {
            val i = Intent(context, MovieDetailsActivity::class.java)
            i.putExtra(MOVIE_ID, movieId)
            i.putExtra(MOVIE_POSTER_URL, posterUrl)
            return i
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)
        postponeEnterTransition()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE and View.SYSTEM_UI_FLAG_FULLSCREEN

        backButton = details_back_button
        backButton.setOnClickListener { finish() }
        favoriteButton = details_favorite_fab
        favoriteButton.setOnClickListener { detailsViewModel.favoriteButtonClicked(intent.getIntExtra(MOVIE_ID, 0)) }
        backdropImage = details_backdrop
        posterImage = details_poster
        title = details_title
        overview = details_overview
        releaseDate = details_release_date
        tagsContainer = details_tags
        score = details_score
        videos = details_videos
        videosSection = details_video_section

        intent.getStringExtra(MOVIE_POSTER_URL)?.let {
            imageLoader.load(it, posterImage) {
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
        if (state == null) return

        title.text = state.title
        overview.text = state.overview
        releaseDate.text = String.format(getString(R.string.release_date_template, state.releaseDate))
        score.text = if (state.votesAverage == 0.0) getString(R.string.n_a) else state.votesAverage.toString()
        state.genres?.let { tagsContainer.tags = state.genres }

        val transition = Slide()
        transition.excludeTarget(posterImage, true)
        transition.duration = 750
        TransitionManager.beginDelayedTransition(details_root_view, transition)
        title.visibility = View.VISIBLE
        releaseDate.visibility = View.VISIBLE
        score.visibility = View.VISIBLE
        details_release_date_layout.visibility = View.VISIBLE
        details_score_layout.visibility = View.VISIBLE
        details_overview_section.visibility = View.VISIBLE
        videosSection.visibility = View.VISIBLE
        tagsContainer.visibility = View.VISIBLE

        state.backdropUrl?.let { imageLoader.load(it, backdropImage) }

        state.videos?.let {
            val videosAdapter = VideosAdapter(it, this::onVideoSelected)
            videos.layoutManager = LinearLayoutManager(this)
            videos.adapter = videosAdapter

        } ?: run {
            videosSection.visibility = View.GONE
        }
    }

    private fun handleFavoriteStateChange(favorite: Boolean?) {
        if (favorite == null) return
        favoriteButton.visibility = View.VISIBLE
        favoriteButton.setImageDrawable(
                if (favorite)
                    ContextCompat.getDrawable(this, R.drawable.ic_favorite_white_36dp)
                else
                    ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_white_36dp))
    }
}
