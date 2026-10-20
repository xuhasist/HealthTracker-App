package ie.setu.domain.repository

import ie.setu.domain.Activity
import ie.setu.domain.db.Activities
import ie.setu.utils.mapToActivity
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update

class ActivityDAO {
    fun getAllActivities(): List<Activity> {
        val activityList: ArrayList<Activity> = arrayListOf()
        transaction {
            Activities.selectAll().map { activityList.add(mapToActivity(it)) }
        }
        return activityList
    }

    fun findActivityById(id: Int): Activity? {
        return transaction {
            Activities.selectAll().where { Activities.id eq id }
                .map { mapToActivity(it) }
                .firstOrNull()
        }
    }

    fun findActivityByUserId(userId: Int): List<Activity> {
        val activityList: ArrayList<Activity> = arrayListOf()
        transaction {
            Activities.selectAll().where { Activities.userId eq userId }
                .map { activityList.add(mapToActivity(it)) }
        }
        return activityList
    }

    fun saveActivity(activity: Activity): Activity {
        return transaction {
            val insertActivityId = Activities.insert {
                it[userId] = activity.userId
                it[description] = activity.description
                it[duration] = activity.duration
                it[calories] = activity.calories
                it[started] = activity.started
            }[Activities.id]

            activity.copy(id = insertActivityId) // create new Object
        }
    }

    fun updateActivity(activity: Activity): Activity? {
        return transaction {
            val updatedRow =
                Activities.update({ (Activities.id eq activity.id) and (Activities.userId eq activity.userId) }) {
                    it[userId] = activity.userId
                    it[description] = activity.description
                    it[duration] = activity.duration
                    it[calories] = activity.calories
                    it[started] = activity.started
                }
            if (updatedRow > 0) activity else null
        }
    }

    fun deleteActivityById(id: Int, ownerId: Int): Int {
        // Check ownership in SQL, so another user's record cannot be deleted.
        return transaction {
            Activities.deleteWhere { (Activities.id eq id) and (Activities.userId eq ownerId) }
        }
    }

    fun deleteActivitiesByUserId(userId: Int) {
        transaction { Activities.deleteWhere { Activities.userId eq userId } }
    }
}