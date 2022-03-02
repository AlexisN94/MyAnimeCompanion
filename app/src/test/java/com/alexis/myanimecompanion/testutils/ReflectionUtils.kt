package com.alexis.myanimecompanion.testutils

import kotlin.reflect.KClass
import kotlin.reflect.jvm.isAccessible

object ReflectionUtils {

    fun <T> invokeMethod(obj: Any, methodName: String): T? {
        val clazz = obj::class.java
        val method = clazz.getDeclaredMethod(methodName)
        method.isAccessible = true
        return method.invoke(obj) as T?
    }

    fun <T> invokeConstructor(clazz: KClass<*>): T {
        val constructor = clazz.constructors.first()
        constructor.isAccessible = true
        return constructor.call() as T
    }

    fun setField(obj: Any, fieldName: String, value: Any?) {
        val clazz = obj::class.java
        val field = clazz.getDeclaredField(fieldName)
        field.isAccessible = true
        field.set(obj, value)
    }
}
