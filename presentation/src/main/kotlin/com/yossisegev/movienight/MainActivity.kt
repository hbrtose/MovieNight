package com.yossisegev.movienight

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yossisegev.movienight.favorites.FavoriteMoviesFragment
import com.yossisegev.movienight.popularmovies.PopularMoviesFragment
import com.yossisegev.movienight.search.SearchFragment
import androidx.fragment.app.transaction
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var navigationBar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.transaction {
                replace(R.id.container, PopularMoviesFragment(), "popular")
            }
            title = getString(R.string.popular)
        }

        navigationBar = bottomNavigationView
        navigationBar.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == navigationBar.selectedItemId) {
            return false
        }

        when (item.itemId) {

            R.id.action_popular -> {
                supportFragmentManager.transaction {
                    replace(R.id.container, PopularMoviesFragment(), "popular")
                }
                title = getString(R.string.popular)
            }

            R.id.action_favorites -> {
                supportFragmentManager.transaction {
                    replace(R.id.container, FavoriteMoviesFragment(), "favorites")
                }
                title = getString(R.string.my_favorites)
            }

            R.id.action_search -> {
                supportFragmentManager.transaction {
                    replace(R.id.container, SearchFragment(), "search")
                }
                title = getString(R.string.search)
            }
        }

        return true
    }

}
