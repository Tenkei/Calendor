package com.esbati.keivan.persiancalendar.components

import kotlin.reflect.KClass

class ServiceLocator {

    private val registry: HashMap<KClass<*>, Instance<*>> = hashMapOf()

    companion object {
        lateinit var instance: ServiceLocator

        fun init(serviceLocator: ServiceLocator) {
            instance = serviceLocator
        }
    }

    fun <T> set(clazz: KClass<*>, definition: () -> T){
        registry[clazz] = Instance(definition)
    }

    fun <T> get(clazz: KClass<*>): T = registry[clazz]?.get() as T ?: error("Couldn't retrieve instance")
}

class Instance<T> (val definition: () -> T) {

    private var instance: T? = null

    @Suppress("UNCHECKED_CAST")
    fun <T> get(): T {
        if (instance == null)
            instance = definition()

        return instance as T
    }
}