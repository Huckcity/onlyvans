package org.adamgibbons.onlyvans.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.adamgibbons.onlyvans.MainApp
import org.adamgibbons.onlyvans.R
import org.adamgibbons.onlyvans.adapters.VanAdapter
import org.adamgibbons.onlyvans.adapters.VanListener
import org.adamgibbons.onlyvans.databinding.FragmentVanListBinding
import org.adamgibbons.onlyvans.helpers.SwipeToDeleteCallback
import org.adamgibbons.onlyvans.models.VanModel

class VanListFragment : Fragment(), VanListener {

    private lateinit var app: MainApp
    private var _binding: FragmentVanListBinding? = null
    private val binding get() = _binding!!
    private lateinit var vanList: List<VanModel>
    private var shouldRefreshOnResume: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVanListBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        vanList = app.vans.findAll()

        val swipeHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.recyclerView.adapter as VanAdapter
                val van = vanList[viewHolder.adapterPosition]
                adapter.removeAt(viewHolder.adapterPosition)
                app.vans.delete(van)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.van_list_action_bar, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                val navController = findNavController()
                navController.run {
                    navigate(R.id.vanFragment)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onVanClick(van: VanModel) {
        openAddEditVan(van)
    }

    private fun openAddEditVan(van: VanModel?) {
        val fragment: Fragment = VanFragment.newInstance()
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        val args = Bundle()
        args.putBoolean("van_edit", (van != null))
        if (van != null) {
            args.putString("van_id", van.id)
        }
        fragment.arguments = args
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            VanListFragment().apply {
                arguments = Bundle().apply { }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        binding.recyclerView.adapter = VanAdapter(app.vans.findAll(), this)
        activity?.title = "All Vans"
    }
}