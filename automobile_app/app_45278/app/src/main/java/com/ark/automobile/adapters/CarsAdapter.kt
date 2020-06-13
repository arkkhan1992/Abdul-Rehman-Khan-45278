package com.ark.automobile.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.ark.automobile.R
import com.ark.automobile.callbacks.CarCallback
import com.ark.automobile.commoners.AppUtils.setDrawable
import com.ark.automobile.databinding.ItemCarBinding
import com.ark.automobile.models.Car
import com.ark.automobile.utils.inflate
import com.ark.automobile.utils.setDrawable
import com.google.firebase.auth.FirebaseAuth
import com.mikepenz.ionicons_typeface_library.Ionicons

class CarsAdapter(private val context: Context, private val callback: CarCallback) : RecyclerView.Adapter<CarsAdapter.CarHolder>() {
    private val cars = mutableListOf<Car>()

    fun addCar(car: Car) {
        cars.add(car)
        notifyItemInserted(cars.size - 1)
    }

    fun addCars(cars: MutableList<Car>) {
        val initialPos = cars.size

        cars.addAll(cars)
        notifyItemRangeInserted(initialPos, cars.size)
    }

    fun updateCar(updatedCar: Car) {
        for ((index, car) in cars.withIndex()) {
            if (updatedCar.id == car.id) {
                cars[index] = updatedCar
                notifyItemChanged(index, updatedCar)
            }
        }
    }

    fun removeCar(removedCar: Car) {
        var indexToRemove: Int = -1

        for ((index, car) in cars.withIndex()) {
            if (removedCar.id == car.id) {
                indexToRemove = index
            }
        }

        cars.removeAt(indexToRemove)
        notifyItemRemoved(indexToRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarHolder {
        return CarHolder(parent.inflate(R.layout.item_car), callback, context)
    }

    override fun getItemCount(): Int = cars.size

    override fun onBindViewHolder(holder: CarHolder, position: Int) {
        holder.bind(cars[position])
    }

    class CarHolder(private val binding: ItemCarBinding, callback: CarCallback, context: Context) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.moreOptions.setImageDrawable(setDrawable(context, Ionicons.Icon.ion_android_more_vertical, R.color.secondaryText, 14))
            binding.location.setDrawable(setDrawable(context, Ionicons.Icon.ion_location, R.color.secondaryText, 12))
            binding.callback = callback
            binding.bullet = " \u2022 "
        }

        fun bind(car: Car) {
            binding.data = car
            binding.isMine = (car.sellerId == FirebaseAuth.getInstance().currentUser?.uid)
        }

    }

}