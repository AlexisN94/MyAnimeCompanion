package com.alexis.myanimecompanion.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.alexis.myanimecompanion.databinding.ListItemAnimeBinding
import com.alexis.myanimecompanion.domain.Anime

class ListAdapter(private val itemClickListener: ClickListener) :
    androidx.recyclerview.widget.ListAdapter<Anime, ListAdapter.ViewHolder>(MyAnimeDiffCallback()) {

    interface ClickListener {
        fun onItemClick(anime: Anime)
        fun onOptionsMenuClick(anime: Anime, view: View)
        fun onIncrementWatchedClick(anime: Anime, notifyDatasetChanged: () -> Unit)
        fun onDecrementWatchedClick(anime: Anime, notifyDatasetChanged: () -> Unit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val anime: Anime = getItem(position)
        holder.bind(anime, itemClickListener, this::notifyDataSetChanged)
    }

    class ViewHolder private constructor(private val binding: ListItemAnimeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(anime: Anime, clickListener: ClickListener, notifyDatasetChanged: () -> Unit) {
            binding.apply {
                this.anime = anime
                root.setOnClickListener {
                    clickListener.onItemClick(anime)
                }
                ibListItemMenu.setOnClickListener { view ->
                    clickListener.onOptionsMenuClick(anime, view)
                }
                ibListItemIncrement.setOnClickListener {
                    clickListener.onIncrementWatchedClick(anime, notifyDatasetChanged)
                }
                ibListItemDecrement.setOnClickListener {
                    clickListener.onDecrementWatchedClick(anime, notifyDatasetChanged)
                }
                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ListItemAnimeBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class MyAnimeDiffCallback : DiffUtil.ItemCallback<Anime>() {
    override fun areItemsTheSame(oldItem: Anime, newItem: Anime): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Anime, newItem: Anime): Boolean {
        return oldItem == newItem
    }
}