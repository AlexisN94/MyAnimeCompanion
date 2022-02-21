package com.alexis.myanimecompanion

import kotlin.reflect.KClass
import kotlin.reflect.jvm.isAccessible

object ReflectionUtils {

    fun invokeMethod(className: String, methodName: String) {
        val clazz = Class.forName(className)
        val method = clazz.getDeclaredMethod(methodName)
        method.isAccessible = true
        method.invoke(null)
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
