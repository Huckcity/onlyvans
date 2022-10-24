package org.adamgibbons.onlyvans.models

interface VanStore {
    fun findById(id: String) : VanModel
    fun findAll(): List<VanModel>
    fun create(van: VanModel)
    fun update(van: VanModel)
    fun delete(van: VanModel)
}