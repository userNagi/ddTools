package com.nagi.ddtools.ui.widget

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SeekBar
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.nagi.ddtools.R

class ChooseWhoDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_choose_who, null)

            val spinnerPrimary: Spinner = view.findViewById(R.id.spinnerPrimary)
            val primaryAdapter = ArrayAdapter(
                it, android.R.layout.simple_spinner_item, listOf("Category 1", "Category 2")
            )
            primaryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPrimary.adapter = primaryAdapter

            val spinnerSecondary: Spinner = view.findViewById(R.id.spinnerSecondary)
            val secondaryAdapter = ArrayAdapter(
                it, android.R.layout.simple_spinner_item, listOf("Subcategory 1", "Subcategory 2")
            )
            secondaryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSecondary.adapter = secondaryAdapter

            val listViewMultiChoice: ListView = view.findViewById(R.id.listViewMultiChoice)
            val listViewAdapter = ArrayAdapter(
                it, android.R.layout.simple_list_item_multiple_choice, listOf("Item 1", "Item 2", "Item 3")
            )
            listViewMultiChoice.adapter = listViewAdapter
            listViewMultiChoice.choiceMode = ListView.CHOICE_MODE_MULTIPLE

            val selectionSeekBar: SeekBar = view.findViewById(R.id.selectionSeekBar)
            selectionSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })

            builder.setView(view)
                .setPositiveButton("OK") { dialog, id ->
                }
                .setNegativeButton("Cancel") { dialog, id ->
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
