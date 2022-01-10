package com.alexis.myanimecompanion.data.remote.models

/**
 * General anime statistics for the domainUser
 */
data class AnimeStatistics(
    val mean_score: Double = 0.0,
    val num_days: Double = 0.0,
    val num_days_completed: Double = 0.0,
    val num_days_dropped: Int = 0,
    val num_days_on_hold: Int = 0,
    val num_days_watched: Double = 0.0,
    val num_days_watching: Double = 0.0,
    val num_episodes: Int = 0,
    val num_items: Int = 0,
    val num_items_completed: Int = 0,
    val num_items_dropped: Int = 0,
    val num_items_on_hold: Int = 0,
    val num_items_plan_to_watch: Int = 0,
    val num_items_watching: Int = 0,
    val num_times_rewatched: Int = 0
)