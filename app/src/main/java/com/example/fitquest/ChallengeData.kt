package com.example.fitquest

import java.util.Date

data class ChallengeData(
    val id: String? = null,
    val title: String = "",
    val description: String? = null, //caso NAO tenha checkpoints
    val xp: Int = 0,
    val total_checkpoints: Int? = null, //caso tenha checkpoints
    var done_checkpoints: Int? = null, //caso tenha checkpoints
    var checkpoints : List<CheckpointData?> = emptyList(), //caso tenha checkpoints
    val goal: Int? = null, //caso NAO tenha checkpoints
    val type: String = "",
    val group: Boolean = false,
    val friend: UserProfile? = null, //caso seja group
    val begin_date: Date? = Date(),
    val end_date: Date? = Date(),
    var completed: Boolean = false,
)
