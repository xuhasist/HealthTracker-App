package ie.setu.utils

import ie.setu.domain.Activity
import ie.setu.domain.db.Activities
import org.jetbrains.exposed.sql.ResultRow


fun mapToActivity(it: ResultRow) = Activity(
    id = it[Activities.id],
    description = it[Activities.description],
    duration = it[Activities.duration],
    started = it[Activities.started],
    calories = it[Activities.calories],
    userId = it[Activities.userId]
)