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

fun captureTooMuchCoroutine() = runBlocking {
    val model = CountingModelCoroutine()
    model.increment()
    println("I have ${model.count}")

    model.increment()
    println("I have ${model.count}") //We won't get here
}

class CountingModelCoroutine {
    var count = 0

    suspend fun increment() {
        count++
        withContext(Dispatchers.Default) {
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

fun returnDataCoroutine() = runBlocking {
    val sd = SomeData("Hello 🐶", 67)

    val result = makeData(sd)

    println("result: $result, is frozen ${result.isFrozen}")
}

private suspend fun makeData(sdIn: SomeData) = withContext(Dispatchers.Default) {
    SomeData("Hello again 🐶", sdIn.i + 55)
}