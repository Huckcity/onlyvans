package org.adamgibbons.onlyvans.models

import java.util.*
import kotlin.collections.ArrayList

class VanMemStore : VanStore {

    private val vans = ArrayList<VanModel>()

    override fun findById(id: String): VanModel {
        return vans.find { v -> v.id == id }!!
    }

    override fun findAll(): List<VanModel> {
        return vans
    }

    override fun create(van: VanModel) {
        van.id = UUID.randomUUID().toString()
        vans.add(van)
        logAll()
    }

    override fun update(van: VanModel) {
        val existingVan: VanModel? = vans.find { v -> v.id == van.id }
        if (existingVan != null) {
            existingVan.title = van.title
            existingVan.description = van.description
        }
    }

    override fun delete(van: VanModel) {
        vans.remove(van)
    }

    private fun logAll() {
        vans.forEach { println(it) }
    }
}
