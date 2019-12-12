package sample

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.native.concurrent.isFrozen

fun basicBackgroundCoroutine() = runBlocking {
    println("(Coroutine) Is main thread $isMainThread")
    withContext(Dispatchers.Default) {
        println("(Coroutine) Is main thread $isMainThread")
    }
}

fun captureStateCoroutine() = runBlocking {
    val sd = SomeData("Hello 🥶", 67)
    println("(Coroutine) Am I frozen? ${sd.isFrozen}")
    withContext(Dispatchers.Default) {
        println("(Coroutine) Am I frozen now? ${sd.isFrozen}")
    }
}

fun captureTooMuchCoroutine() {
    val model = CountingModelCoroutine()
    model.increment()
    println("I have ${model.count}")

    model.increment()
    println("I have ${model.count}") //We won't get here
}

class CountingModelCoroutine {
    var count = 0

    fun increment() {
        count++
        background {
            saveToDb(count)
        }
    }

    private fun saveToDb(arg: Int) {
        //Do some db stuff
    }
}

fun captureArgsCoroutine() = runBlocking {
    val model = CountingModelSaferCoroutine()
    model.increment()
    println("I have ${model.count}")

    model.increment()
    println("I have ${model.count}")
}

class CountingModelSaferCoroutine {
    var count = 0

    suspend fun increment() {
        count++
        saveToDb(count)
    }

    private suspend fun saveToDb(arg: Int) = withContext(Dispatchers.Default) {
        println("Doing db stuff with $arg, in main $isMainThread")
    }
}