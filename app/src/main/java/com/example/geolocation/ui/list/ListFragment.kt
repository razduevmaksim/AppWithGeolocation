package com.example.geolocation.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.geolocation.adapter.GeolocationAdapter
import com.example.geolocation.databinding.FragmentListBinding
import com.example.geolocation.model.GeolocationModel

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GeolocationAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val listViewModel =
            ViewModelProvider(this)[ListViewModel::class.java]

        _binding = FragmentListBinding.inflate(inflater, container, false)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){
        val viewModel = ViewModelProvider(this)[ListViewModel::class.java]
        viewModel.initDatabase()
        recyclerView = binding.recyclerViewLocation
        adapter = GeolocationAdapter()
        recyclerView.adapter = adapter
        viewModel.getAll().observe(viewLifecycleOwner) { listGeolocation ->
            listGeolocation.asReversed()
            adapter.setList(listGeolocation)
        }
            val title = "New point"
            val latitude = 0.0.toString()
            val longitude = 0.0.toString()
            viewModel.insert(GeolocationModel(title = title, latitude = latitude, longitude = longitude)){}

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}