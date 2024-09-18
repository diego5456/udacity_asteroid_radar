package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private var adapter: MainListAdapter? = null

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel
        adapter= MainListAdapter(AsteroidClickListener { asteroid ->
            findNavController().navigate(MainFragmentDirections.actionShowDetail(asteroid))
        })

        binding.asteroidRecycler.adapter = adapter

        viewModel.pictureOfDay.observe(viewLifecycleOwner){
            Picasso.with(requireContext()).load(it?.url).into(binding.activityMainImageOfTheDay)
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.show_week_menu -> {
                viewModel.updateFilter(AsteroidsFilter.WEEK)
                true
            }
            R.id.show_today_menu -> {
                viewModel.updateFilter(AsteroidsFilter.TODAY)
                true
            }
            R.id.show_saved_menu -> {
                viewModel.updateFilter(AsteroidsFilter.ALL)
                true
            }
            else -> false

        }
    }

}
