package org.adamgibbons.onlyvans.models

interface VanStore {
    fun findAll(): List<VanModel>
    fun create(van: VanModel)
    fun update(van: VanModel)
}