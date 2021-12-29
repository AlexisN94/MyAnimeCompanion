package com.alexis.myanimecompanion.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.alexis.myanimecompanion.databinding.ListItemAnimeBinding
import com.alexis.myanimecompanion.domain.Anime

class ListAdapter(
    private val animeClickListener: AnimeClickListener,
    private val animeMenuClickListener: AnimeMenuClickListener
) :
    androidx.recyclerview.widget.ListAdapter<Anime, ListAdapter.ViewHolder>(MyAnimeDiffCallback()) {

    private val _eventItemMenuClicked = MutableLiveData<Boolean>()
    val eventItemMenuClicked: LiveData<Boolean>
        get() = _eventItemMenuClicked

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val anime: Anime = getItem(position)
        holder.bind(anime, animeClickListener, animeMenuClickListener)
    }

    class ViewHolder private constructor(private val binding: ListItemAnimeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(anime: Anime, animeClickListener: AnimeClickListener, animeMenuClickListener: AnimeMenuClickListener) {
            binding.anime = anime
            binding.root.setOnClickListener { animeClickListener.onAnimeClick(anime) }
            binding.ibListItemMenu.setOnClickListener { animeMenuClickListener.onAnimeMenuClick(it, anime) }
            binding.executePendingBindings()
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

interface AnimeClickListener {
    fun onAnimeClick(anime: Anime)
}

interface AnimeMenuClickListener {
    fun onAnimeMenuClick(view: View, anime: Anime)
}