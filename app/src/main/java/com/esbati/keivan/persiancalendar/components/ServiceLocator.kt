package com.esbati.keivan.persiancalendar.components

import kotlin.reflect.KClass

inline fun <reified T> locate(): Lazy<T> = lazy { ServiceLocator.getInstance().get<T>() }

class ServiceLocator {

    @PublishedApi
    internal val registry: HashMap<KClass<*>, Instance<*>> = hashMapOf()

    companion object {
        
        private var instance: ServiceLocator? = null

        fun init(serviceLocator: ServiceLocator) {
            instance = serviceLocator
        }

        fun getInstance() = instance ?: error("Service Locator has not been initiated")
    }

    /**
     * Register a Single instance dependency
     */
    inline fun <reified T> single(noinline definition: () -> T){
        registry[T::class] = SingleInstance(definition)
    }

    /**
     * Register a Factory instance dependency
     */
    inline fun <reified T> factory(noinline definition: () -> T){
        registry[T::class] = FactoryInstance(definition)
    }

    /**
     * Retrieve an instance of the given dependency
     */
    inline fun <reified T> get(): T = registry[T::class]?.get() as T ?: error("No instance of the given type is found")
}

/**
 * Instance Holder
 * create and retrieve instance of the given type
 */
abstract class Instance<T> (val definition: () -> T) {

    /**
     * Retrieve an instance
     * @return T
     */
    abstract fun <T> get(): T

    /**
     * Create an instance according to definition
     * @return T
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> create(): T {
        return definition() as T
    }
}

/**
 * Singleton Instance Holder
 * create an instance of the given type only once and return it when needed
 */
class SingleInstance<T> (definition: () -> T): Instance<T>(definition) {

    private var instance: T? = null

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(): T {
        if (instance == null)
            instance = create()

        return instance as T
    }
}

/**
 * Factory Instance Holder
 * create and return a new instance of the given type every time needed
 */
class FactoryInstance<T> (definition: () -> T): Instance<T>(definition) {

    override fun <T> get(): T {
        return create()
    }
}