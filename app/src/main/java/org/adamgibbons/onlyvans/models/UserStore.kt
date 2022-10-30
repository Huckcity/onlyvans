package org.adamgibbons.onlyvans.models

interface UserStore {
    fun findById(id: String) : UserModel
    fun findAll(): List<UserModel>
    fun create(user: UserModel)
    fun update(user: UserModel)
    fun delete(user: UserModel)
    fun findByUsername(username: String): UserModel?
    fun verifyPassword(user: UserModel, password: String): Boolean
}