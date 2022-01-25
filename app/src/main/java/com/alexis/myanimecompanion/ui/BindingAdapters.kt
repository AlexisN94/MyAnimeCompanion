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
    var animeDetails = anime.details
    progress =
        if (anime.myListStatus?.episodesWatched == null || animeDetails == null || animeDetails.numEpisodes == 0) {
            0
        } else {
            val percentage = anime.myListStatus.episodesWatched?.toDouble().div(animeDetails.numEpisodes)
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
    text = "${anime?.myListStatus?.episodesWatched ?: 0}/${anime?.details?.numEpisodes ?: "?"}"
}

@BindingAdapter("userScoreFormatted")
fun TextView.setUserScore(anime: Anime) {
    text = "${anime?.myListStatus?.score ?: 0}/10"
}

@BindingAdapter("epsReleased")
fun TextView.setNumEpsReleased(anime: Anime?) {
    text = "Episodes: ${anime?.details?.numEpisodes ?: "?"}"
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
    val a = text
    val b = text.toString()
    return b
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
