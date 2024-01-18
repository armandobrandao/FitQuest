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
    val isGroup: Boolean = false,
    val friend: String? = null, //caso seja group, algo que consiga identificar o amigo
    val begin_date: Date? = Date(),
    val end_date: Date? = Date(),
    val isCompleted: Boolean = false,
)

// TODO verificar como associar ao amigo
// dá para associar como os dailyQuests e associar tmb ao documento do outro??

// Exemplo Challenge com Checkpoints:
// ChallengeData(title = "Urban Explorer", description = null, xp = 200,
// total_checkpoints = 3, done_checkpoints = 1,
// checkpoints = [CheckpointData(), CheckpointData()], goal = null
// type = "Location", isGroup = false, friend = null),

// Exemplo Challenge sem Checkpoints:
// ChallengeData(title = "Insane Walker", description = "Complete 40 000 steps", xp = 100,
// total_checkpoints = null, done_checkpoints = null,
// checkpoints = null, goal = 40000,
// type = "Steps", isGroup = false, friend = null),

// Outro possible type -> Minutes

//Exemplo Challenge de Grupo
// ChallengeData(title = "Insane Walkers", description = "Complete 85 000 steps", xp = 150,
// total_checkpoints = null, done_checkpoints = null,
// checkpoints = null, goal = 40000,
// type = "Steps", isGroup = true, friend = "Algo que identifique o amigo"),
