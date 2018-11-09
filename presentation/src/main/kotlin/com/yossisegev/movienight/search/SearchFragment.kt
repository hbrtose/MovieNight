package com.yossisegev.movienight.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.yossisegev.data.utils.onTextChanged
import com.yossisegev.movienight.R
import com.yossisegev.movienight.common.BaseFragment
import com.yossisegev.movienight.common.ImageLoader
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_search_movies.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

/**
 * Created by Yossi Segev on 11/11/2017.
 */
class SearchFragment : BaseFragment() {

    private val viewModel: SearchViewModel by viewModel()
    private val imageLoader: ImageLoader by inject()
    private lateinit var searchResultsAdapter: SearchResultsAdapter
    private lateinit var searchSubject: PublishSubject<String>
    private val compositeDisposable = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchSubject = PublishSubject.create()

        //TODO: Handle screen rotation during debounce
        val disposable = searchSubject.debounce(1, TimeUnit.SECONDS)
                .filter { it.length > 2 }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it != searchResultsAdapter.query) {
                        viewModel.search(it)
                    } else {
                        Log.i(javaClass.simpleName, "Same query -> aborting search")
                    }
                }

        compositeDisposable.add(disposable)
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

    private fun handleViewState(state: SearchViewState) {
        search_movies_progress.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        val movies = state.movies ?: listOf()
        if (state.showNoResultsMessage) {
            search_movies_no_results_message.visibility = View.VISIBLE
            search_movies_no_results_message.text = String.format(
                    getString(R.string.search_no_results_message,
                            state.lastSearchedQuery))
        } else {
            search_movies_no_results_message.visibility = View.GONE
        }
        searchResultsAdapter.setResults(movies, state.lastSearchedQuery)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.fragment_search_movies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        search_movies_edit_text.onTextChanged { text -> searchSubject.onNext(text) }
        searchResultsAdapter = SearchResultsAdapter(imageLoader) { movie, movieView ->
            showSoftKeyboard(false)
            navigateToMovieDetailsScreen(movie, movieView)
        }
        search_movies_recyclerview.layoutManager = LinearLayoutManager(activity)
        search_movies_recyclerview.adapter = searchResultsAdapter
        search_movies_edit_text.requestFocus()
        showSoftKeyboard(true)
        activity?.title = getString(R.string.search)
    }

    private fun showSoftKeyboard(show: Boolean) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (show) {
           imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0)
        } else {
            imm.hideSoftInputFromWindow(search_movies_edit_text.windowToken,0)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("lastSearch", search_movies_edit_text.text.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showSoftKeyboard(false)
        compositeDisposable.clear()
    }

}