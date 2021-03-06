package com.alexis.myanimecompanion.ui

import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.alexis.myanimecompanion.R
import com.alexis.myanimecompanion.domain.Anime
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

@BindingAdapter("imgUrl")
fun ImageView.setImage(url: String?) {
    url?.let {
        val imageURI = it.toUri().buildUpon().scheme("https").build()

        Glide.with(context)
            .load(imageURI)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_baseline_broken_image_24)
            )
            .into(this)
    }
}

@BindingAdapter("progress")
fun ProgressBar.setProgress(anime: Anime) {
    progress = if (anime.numEpisodes == null || anime.numEpisodes == 0) {
        0
    } else {
        val percentage = anime.episodesWatched.toDouble().div(anime.numEpisodes)
        percentage.times(100).toInt()
    }
}

@BindingAdapter("doubleToInt")
fun TextView.setValue(value: Double?) {
    value?.let {
        text = value.toString()
    }
}

@BindingAdapter("episodesWatchedFormatted")
fun TextView.setEpisodesWatched(anime: Anime?) {
    text = "${anime?.episodesWatched ?: 0}/${anime?.numEpisodes ?: "?"} eps"
}

@BindingAdapter("epsReleased")
fun TextView.setNumEpsReleased(anime: Anime?) {
    text = "Episodes: ${anime?.numEpisodes ?: "?"}"
}

@InverseBindingAdapter(attribute = "spinnerValue")
fun AppCompatSpinner.getValue(): Any? {
    return selectedItem
}

@BindingAdapter("spinnerValue")
fun AppCompatSpinner.setValue(value: Any?) {
    val valuePosition = (adapter as ArrayAdapter<Any>).getPosition(value)
    if (valuePosition != selectedItemPosition) {
        setSelection(valuePosition)
    }
}

@BindingAdapter("spinnerValueAttrChanged")
fun AppCompatSpinner.setValueListener(listener: InverseBindingListener) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            listener.onChange()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
}

@InverseBindingAdapter(attribute = "searchQuery")
fun AppCompatEditText.getValue(): String {
    return text.toString()
}

@BindingAdapter("searchQuery")
fun AppCompatEditText.setValue(value: String?) {
    if (value != null && value != text.toString()) {
        setText(value)
    }
}

@BindingAdapter("searchQueryAttrChanged")
fun AppCompatEditText.setValueListener(listener: InverseBindingListener) {
    doOnTextChanged { _, _, _, _ ->
        listener.onChange()
    }
}

@BindingAdapter("visible")
fun View.setVisibility(show: Boolean) {
    visibility = if (show) {
        View.VISIBLE
    } else {
        View.GONE
    }
}
