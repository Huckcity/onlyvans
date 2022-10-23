package org.adamgibbons.onlyvans.models

class VanMemStore : VanStore {

    private val vans = ArrayList<VanModel>()

    override fun findAll(): List<VanModel> {
        return vans
    }

    override fun create(van: VanModel) {
        van.id = 123
        println(van)
        vans.add(van)
        logAll()
    }

    override fun update(van: VanModel) {
        TODO("Not yet implemented")
    }

    private fun logAll() {
        vans.forEach { println(it) }
    }
}
