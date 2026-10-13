package ie.setu.domain.repository

import ie.setu.domain.User
import ie.setu.domain.db.Users
import ie.setu.utils.mapToUser
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

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
        return null
        //return users.find { it.email == email }
    }

    fun saveUser(user: User) {
        //users.add(user)
    }

    fun deleteUserById(id: Int) {
//        val user = findUserById(id)
//        if (user != null) {
//            users.remove(user)
//        }
    }

    fun updateUser(id: Int, updatedUser: User) {
//        val userIndex = users.indexOfFirst { it.id == id }
//        if (userIndex != -1) {
//            users[userIndex] = updatedUser.copy(id = id)    // Keep the same id for the updated user
//        }
    }
}