package ie.setu.utils

import ie.setu.domain.Activity
import ie.setu.domain.db.Activities
import org.jetbrains.exposed.sql.ResultRow

fun mapToActivity(it: ResultRow) = Activity(
    id = it[Activities.id],
    userId = it[Activities.userId],
    description = it[Activities.description],
    duration = it[Activities.duration],
    calories = it[Activities.calories],
    started = it[Activities.started],
)