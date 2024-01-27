package com.nagi.ddtools.ui.widget.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.nagi.ddtools.R
import com.nagi.ddtools.database.AppDatabase
import com.nagi.ddtools.database.activityList.ActivityList
import com.nagi.ddtools.ui.toolpage.tools.groupwho.ChooseWhoActivity
import com.nagi.ddtools.utils.UiUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IncludeFromActivityDialog : DialogFragment() {
    private lateinit var spinnerLocation: AppCompatSpinner
    private lateinit var spinnerDate: AppCompatSpinner
    private lateinit var spinnerName: AppCompatSpinner
    private lateinit var buttonConfirm: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dialog_choose_activity, container, false)
        spinnerLocation = view.findViewById(R.id.spinnerLocation)
        spinnerDate = view.findViewById(R.id.spinnerDate)
        spinnerName = view.findViewById(R.id.spinnerName)
        buttonConfirm = view.findViewById(R.id.buttonConfirm)
        buttonConfirm.setOnClickListener {
            dismiss()
            UiUtils.openPage(
                requireActivity(),
                ChooseWhoActivity::class.java,
                false,
                Bundle().apply {
                    putString("name", spinnerName.selectedItem as String)
                }
            )
        }
        setupSpinners()
        return view
    }

    private fun setupSpinners() {
        lifecycleScope.launch {
            val data = withContext(Dispatchers.IO) {
                AppDatabase.getInstance().activityListDao().getAll()
            }

            withContext(Dispatchers.Main) {
                val locations = data.map { it.location }.distinct()
                val dates = data.map { it.duration_date }.distinct()
                spinnerLocation.adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, locations)
                spinnerDate.adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dates)

                spinnerLocation.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View,
                            position: Int,
                            id: Long
                        ) {
                            updateSpinnerName(data)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }
                spinnerDate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        updateSpinnerName(data)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
                updateSpinnerName(data)
            }
        }
    }

    private fun updateSpinnerName(data: List<ActivityList>) {
        val selectedLocation = spinnerLocation.selectedItem as? String
        val selectedDate = spinnerDate.selectedItem as? String
        val filteredData = data.filter {
            (selectedLocation == null || it.location == selectedLocation) && (selectedDate == null || it.duration_date == selectedDate)
        }
        val names = filteredData.map { it.name }
        spinnerName.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
    }
}
