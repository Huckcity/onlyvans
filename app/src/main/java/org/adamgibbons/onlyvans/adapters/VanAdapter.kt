package org.adamgibbons.onlyvans.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.adamgibbons.onlyvans.databinding.CardVanBinding
import org.adamgibbons.onlyvans.models.VanModel

interface VanListener {
    fun onVanClick(van: VanModel)
}

class VanAdapter constructor(private var vans: List<VanModel>,
                             private val listener: VanListener) : RecyclerView.Adapter<VanAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardVanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val van = vans[holder.adapterPosition]
        holder.bind(van, listener)
    }

    override fun getItemCount(): Int = vans.size

    class MainHolder(private val binding : CardVanBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(van: VanModel, listener: VanListener) {
            binding.vanTitle.text = van.title
            binding.vanDescription.text = van.description
            binding.root.setOnClickListener { listener.onVanClick(van) }
        }
    }

}