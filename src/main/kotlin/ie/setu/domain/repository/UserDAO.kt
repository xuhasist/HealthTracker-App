package ie.setu.domain.repository

import ie.setu.domain.User
import ie.setu.domain.db.Users
import ie.setu.utils.mapToUser
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class UserDAO {
//    private val users = arrayListOf<User>(
//        User(name = "Alice", email = "alice@wonderland.com", id = 0),
//        User(name = "Bob", email = "bob@cat.ie", id = 1),
//        User(name = "Mary", email = "mary@contrary.com", id = 2),
//        User(name = "Carol", email = "carol@singer.com", id = 3)
//    )

    fun getAllUsers(): List<User> {
        val userList: ArrayList<User> = arrayListOf()
        transaction {
            Users.selectAll().map { userList.add(mapToUser(it)) }
        }
        return userList
    }

    fun findUserById(id: Int): User? {
        return transaction {
            Users.selectAll().where { Users.id eq id }
                .map { mapToUser(it) }
                .firstOrNull()
        }

        //return users.find { it.id == id }
    }

    fun findUserByEmail(email: String): User? {
        return transaction {
            Users.selectAll().where { Users.email eq email }
                .map { mapToUser(it) }
                .firstOrNull()
        }

        //return users.find { it.email == email }
    }

    fun saveUser(user: User): User {
        return transaction {
            val insertUserId = Users.insert {
                it[name] = user.name
                it[email] = user.email
            }[Users.id]

            user.copy(id = insertUserId) // create new Object
        }
        // id is auto-incremented

        //users.add(user)
    }

    fun deleteUserById(id: Int) {
        transaction { Users.deleteWhere { Users.id eq id } }
    }

    fun updateUser(id: Int, updatedUser: User) {
        transaction {
            Users.update({ Users.id eq id }) {
                it[name] = updatedUser.name
                it[email] = updatedUser.email
            }
        }
    }
}