package org.adamgibbons.onlyvans.fragments

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import org.adamgibbons.onlyvans.MainApp
import org.adamgibbons.onlyvans.R
import org.adamgibbons.onlyvans.activities.MapsActivity
import org.adamgibbons.onlyvans.databinding.FragmentVanBinding
import org.adamgibbons.onlyvans.helpers.decodeImage
import org.adamgibbons.onlyvans.helpers.encodeImage
import org.adamgibbons.onlyvans.helpers.showImagePicker
import org.adamgibbons.onlyvans.models.Location
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
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    var location = Location(52.245696, -7.139102, 15f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        setHasOptionsMenu(true)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            killFragment()
        }
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
        activity?.title = "Add Van"

        val thisYear: Int = Calendar.getInstance().get(Calendar.YEAR)
        val years: IntArray = (1900..thisYear).toList().toIntArray()
        val stringArray = years.map { it.toString() }.toTypedArray()
        (binding.yearPickerTextField.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(stringArray)

        if (edit) {
            activity?.title = van.title
            binding.vanTitle.setText(van.title)
            binding.vanDescription.setText(van.description)
            binding.btnAdd.setText(R.string.update_van)
            binding.chooseImage.setText(R.string.change_image)
            if (van.image64.isNotEmpty()) {
                binding.vanImage.setImageBitmap(decodeImage(van.image64))
            }
            binding.colorPicker.setText(van.color, false)
            binding.enginePicker.setText(van.engine.toString(), false)
            binding.yearPicker.setText(van.year.toString(), false)
            location = van.location
        }

        registerImagePickerCallback()
        registerMapCallback()

        binding.btnAdd.setOnClickListener {
            addVan()
        }

        binding.chooseImage.setOnClickListener {
            addVanImage()
        }

        binding.vanLocation.setOnClickListener {
            val launcherIntent = Intent(context, MapsActivity::class.java)
                .putExtra("location", location)
            mapIntentLauncher.launch(launcherIntent)
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.van_action_bar, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                killFragment()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu){
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.item_add)
        if (item != null)
            item.isVisible = false
    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when (result.resultCode) {
                    RESULT_OK -> {
                        if (result.data != null) {
                            location = result.data!!.extras?.getParcelable("location")!!
                        }
                    }
                    RESULT_CANCELED -> { } else -> { }
                }
            }
    }


    private fun addVan() {
        try {
            van.title = binding.vanTitle.text.toString()
            van.description = binding.vanDescription.text.toString()
            van.color = binding.colorPicker.text.toString()
            van.engine = binding.enginePicker.text.toString().toDouble()
            van.year = binding.yearPicker.text.toString().toInt()
            van.location = location

            if (van.title.isEmpty() || van.description.isEmpty() || van.color.isEmpty() || van.engine.toString().isEmpty() || van.year.toString().isEmpty()) {
                view?.let { Snackbar.make(it, "All fields are required!", Snackbar.LENGTH_LONG).show() }
            } else {
                if (edit) {
                    app.vans.update(van.copy())
                } else {
                    app.vans.create(van.copy())
                }
                view?.let { Snackbar.make(it, "Van Updated!", Snackbar.LENGTH_LONG).show() }
                killFragment()
            }
        } catch (exception: java.lang.Exception) {
            view?.let { Snackbar.make(it, "All fields are required!", Snackbar.LENGTH_LONG).show() }
        }

    }

    private fun killFragment() {
        val fragmentTransaction: FragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.remove(this).commit()
        val navController = findNavController()
        navController.run {
            popBackStack()
            navigate(R.id.vanListFragment)
        }
    }

    private fun addVanImage() {
        showImagePicker(imageIntentLauncher)
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