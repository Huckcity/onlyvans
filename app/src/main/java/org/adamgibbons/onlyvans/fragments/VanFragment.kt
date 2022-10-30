package org.adamgibbons.onlyvans.fragments

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import org.adamgibbons.onlyvans.MainApp
import org.adamgibbons.onlyvans.R
import org.adamgibbons.onlyvans.databinding.FragmentVanBinding
import org.adamgibbons.onlyvans.helpers.decodeImage
import org.adamgibbons.onlyvans.helpers.encodeImage
import org.adamgibbons.onlyvans.helpers.showImagePicker
import org.adamgibbons.onlyvans.models.VanModel
import java.io.InputStream
import java.util.*

class VanFragment : Fragment() {

    private var van = VanModel()
    private var _binding: FragmentVanBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var app: MainApp
    private var edit = false

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
        _binding = FragmentVanBinding.inflate(layoutInflater)

        val args = arguments
        edit = args?.getBoolean("van_edit", false) == true

        if (edit) {
            if (args != null) {
                van = args.getString("van_id")?.let { app.vans.findById(it) }!!
            }
        }
        activity?.title = van.title

        val colorPickerAdapter = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.colors_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.colorPicker.adapter = adapter
        }

        val enginePickerAdapter = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.engines_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.enginePicker.adapter = adapter
        }

        val years = ArrayList<String>()
        val thisYear: Int = Calendar.getInstance().get(Calendar.YEAR)
        for (i in 1900..thisYear) {
            years.add(i.toString())
        }

        val yearPickerAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            years
        ).also {
                adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            binding.yearPicker.adapter = adapter
        }

        if (edit) {
            println("WE MADE IT")
            binding.vanTitle.setText(van.title)
            binding.vanDescription.setText(van.description)
            binding.btnAdd.setText(R.string.update_van)
            binding.chooseImage.setText(R.string.change_image)
            binding.vanImage.setImageBitmap(decodeImage(van.image64))
            binding.colorPicker.setSelection(colorPickerAdapter.getPosition(van.color))
            binding.enginePicker.setSelection(enginePickerAdapter.getPosition(van.engine.toString()))
            binding.yearPicker.setSelection(yearPickerAdapter.getPosition(van.year.toString()))
        }

        registerImagePickerCallback()

        binding.btnAdd.setOnClickListener {
            addVan()
        }

        binding.chooseImage.setOnClickListener {
            addVanImage()
        }

        return binding.root
    }
    private fun addVan() {
        van.title = binding.vanTitle.text.toString()
        van.description = binding.vanDescription.text.toString()
        van.color = binding.colorPicker.selectedItem.toString()
        van.engine = binding.enginePicker.selectedItem.toString().toDouble()
        van.year = binding.yearPicker.selectedItem.toString().toInt()

        if (van.title.isEmpty()) {
            view?.let { Snackbar.make(it, "You must enter a title", Snackbar.LENGTH_LONG).show() }
        } else {
            if (edit) {
                app.vans.update(van.copy())
            } else {
                app.vans.create(van.copy())
            }
            view?.let { Snackbar.make(it, "Van Updated!", Snackbar.LENGTH_LONG).show() }

            val fragmentTransaction: FragmentTransaction? = fragmentManager?.beginTransaction()
            fragmentTransaction?.remove(this)?.commit()
            val navController = findNavController()
            navController.run {
                popBackStack()
                navigate(R.id.vanListFragment)
            }
        }
    }

    private fun addVanImage() {
        showImagePicker(imageIntentLauncher)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_van_add, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                parentFragmentManager.popBackStack()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            VanFragment().apply {
                arguments = Bundle().apply {}
            }
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        if (result.data != null) {
                            val imageStream: InputStream? =
                                requireActivity().contentResolver.openInputStream(result.data!!.data!!)
                            val selectedImage = BitmapFactory.decodeStream(imageStream)
                            val encodedImage: String? = encodeImage(selectedImage)

                            binding.vanImage.setImageBitmap(selectedImage)
                            binding.chooseImage.setText(R.string.change_image)
                            println(encodedImage)
                            if (encodedImage != null) {
                                van.image64 = encodedImage
                            }
                        } // end of if
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }
}