package com.esbati.keivan.persiancalendar.components

import kotlin.reflect.KClass

class ServiceLocator {

    private val registry: HashMap<KClass<*>, Any> = hashMapOf()

    companion object {
        lateinit var instance: ServiceLocator

        fun init(serviceLocator: ServiceLocator) {
            instance = serviceLocator
        }
    }

    fun <T> set(clazz: KClass<*>, definition: T){
        registry[clazz] = definition as Any
    }

    fun <T> get(clazz: KClass<*>): T = registry[clazz] as T
}