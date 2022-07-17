package com.example.geolocation.ui.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.geolocation.R
import com.example.geolocation.adapter.GeolocationAdapter
import com.example.geolocation.databinding.FragmentListBinding

class ListFragment : Fragment() {
    private var _binding: FragmentListBinding? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GeolocationAdapter

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentListBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_action_bar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.mybutton) {
            deleteAll()
        }
        return super.onOptionsItemSelected(item)

    }
    fun deleteAll(){
        val viewModel = ViewModelProvider(this)[ListViewModel::class.java]
//        val builder = AlertDialog.Builder(requireContext())
//        builder.setPositiveButton("Yes"){
//            _,_ ->
//            viewModel.deleteAll()
//        }
        viewModel.initDatabase()
        adapter = GeolocationAdapter()
        recyclerView.adapter = adapter
        viewModel.deleteAll()
    }
    private fun init() {
        val viewModel = ViewModelProvider(this)[ListViewModel::class.java]
        viewModel.initDatabase()
        recyclerView = binding.recyclerViewLocation
        adapter = GeolocationAdapter()
        recyclerView.adapter = adapter
        viewModel.getAll().observe(viewLifecycleOwner) { listGeolocation ->
            adapter.setList(listGeolocation.asReversed())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}