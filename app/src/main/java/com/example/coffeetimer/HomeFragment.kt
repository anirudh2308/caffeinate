package com.example.coffeetimer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val coffeeSpinner: Spinner = view.findViewById(R.id.coffeeSpinner)
        // Populate the Spinner with coffee types
        val coffeeTypes = arrayOf("Espresso", "Latte", "Cappuccino", "Macchiato", "Americano", "Mocha")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, coffeeTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        coffeeSpinner.adapter = adapter

        val brewTimePicker: NumberPicker = view.findViewById(R.id.brewTimePicker)
        val startButton: Button = view.findViewById(R.id.startButton)

        // Set up NumberPicker for brew time
        brewTimePicker.minValue = 1
        brewTimePicker.maxValue = 60

        startButton.setOnClickListener {
            val selectedCoffee = coffeeSpinner.selectedItem.toString()
            val brewTime = brewTimePicker.value

            // Create a bundle to pass the data to TimerFragment
            val bundle = Bundle().apply {
                putString("COFFEE_TYPE", selectedCoffee)
                putInt("BREW_TIME", brewTime)
            }

            // Use NavController to navigate to TimerFragment
            findNavController().navigate(R.id.action_homeFragment_to_timerFragment, bundle)
        }

        return view
    }
}
