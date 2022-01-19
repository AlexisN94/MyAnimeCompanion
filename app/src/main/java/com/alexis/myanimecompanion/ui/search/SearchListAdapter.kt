package com.alexis.myanimecompanion.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alexis.myanimecompanion.databinding.ListItemSearchResultBinding
import com.alexis.myanimecompanion.domain.Anime
import com.alexis.myanimecompanion.ui.list.MyAnimeDiffCallback

class SearchListAdapter(private val clickListener: ClickListener) :
    ListAdapter<Anime, SearchListAdapter.SearchResultViewHolder>(MyAnimeDiffCallback()) {

    interface ClickListener {
        fun onItemClick(anime: Anime)
        fun onStatusChange(anime: Anime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        return SearchResultViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val anime = getItem(position)
        holder.bind(anime, clickListener, this::notifyDataSetChanged)
    }

    class SearchResultViewHolder private constructor(private val binding: ListItemSearchResultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(anime: Anime, clickListener: ClickListener, notifyDatasetChanged: () -> Unit) {
            binding.apply {
                this.anime = anime
                root.setOnClickListener { clickListener.onItemClick(anime) }
                // TODO setOnAnimeStatusChangeListener { clickListener.onStatusChange(anime) }
                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup): SearchResultViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ListItemSearchResultBinding.inflate(inflater, parent, false)
                return SearchResultViewHolder(binding)
            }
        }
    }
}
