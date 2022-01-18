package com.alexis.myanimecompanion

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

/**
 * Utility singleton to create a list of filter fields for api queries; e.g. Only get the objet's id and synopsis in
 * the JSON response. Sadly the API doesn't return the complete object. Fields must be specified.
 */

object QueryFieldsBuilder {
    private var queryList = mutableListOf<String>()

    fun fieldsForAnimeDetails(): QueryFieldsBuilder {
        this.addFields(
            Field.ID,
            Field.TITLE,
            Field.MAIN_PICTURE,
            Field.ALTERNATIVE_TITLES,
            Field.START_DATE,
            Field.END_DATE,
            Field.SYNOPSIS,
            Field.MEAN,
            Field.RANK,
            Field.POPULARITY,
            Field.NUM_LIST_USERS,
            Field.NUM_SCORING_USERS,
            Field.CREATED_AT,
            Field.NSFW,
            Field.MEDIA_TYPE,
            Field.STATUS,
            Field.GENRES,
            Field.MY_LIST_STATUS,
            Field.START_SEASON,
            Field.BROADCAST,
            Field.PICTURES,
            Field.BACKGROUND,
            Field.NUM_EPISODES,
            Field.RATING,
            Field.SOURCE
        )
        return this
    }

    fun addFields(vararg fields: Field): QueryFieldsBuilder {
        for (field in fields) {
            queryList.add(field.value)
        }
        return this
    }

    fun done(): String {
        return queryList.joinToString(",")
            .also {
                queryList.clear()
            }
    }
}

enum class Field(val value: String) {
    ID("id"),
    TITLE("title"),
    MAIN_PICTURE("main_picture"),
    ALTERNATIVE_TITLES("alternative_titles"),
    START_DATE("start_date"),
    END_DATE("end_date"),
    SYNOPSIS("synopsis"),
    MEAN("mean"),
    RANK("rank"),
    POPULARITY("popularity"),
    NUM_LIST_USERS("num_list_users"),
    NUM_SCORING_USERS("num_scoring_users"),
    CREATED_AT("created_at"),
    NSFW("nsfw"),
    MEDIA_TYPE("media_type"),
    STATUS("status"),
    GENRES("genres"),
    MY_LIST_STATUS("my_list_status"),
    START_SEASON("start_season"),
    BROADCAST("broadcast"),
    PICTURES("pictures"),
    BACKGROUND("background"),
    NUM_EPISODES("num_episodes"),
    RATING("rating"),
    SOURCE("source")
}

fun Activity.dismissKeyboard() {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (inputMethodManager.isAcceptingText)
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
}


fun createEncryptedSharedPreferences(context: Context, fileName: String): EncryptedSharedPreferences {
    val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    return EncryptedSharedPreferences
        .create(
            fileName,
            masterKeyAlias,
            context.applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as EncryptedSharedPreferences
}