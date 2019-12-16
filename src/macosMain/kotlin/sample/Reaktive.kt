package sample

import com.badoo.reaktive.observable.*
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import kotlin.native.concurrent.isFrozen

val values = mutableListOf<Any>()
var isFinished = false

fun basicObservable(){
    observable<SomeData> { emitter ->
        println("From io thread, is main thread? ${isMainThread}")
        emitter.onNext(SomeData("arst", 43))
    }
        .subscribeOn(ioScheduler)
        .observeOn(mainScheduler)
        .threadLocal()
        .doOnBeforeNext { values += it } // Callback is not frozen, we can updated the mutable list
        .doOnBeforeFinally { isFinished = true } // Callback is not frozen, we can change the flag
        .subscribe {
            println("In main thread $isMainThread, is data frozen ${it.isFrozen}")
        }
}

fun willFreeze(){
    val sd = SomeData("arst", 43)
    println("Frozen here? ${sd.isFrozen}")
    observable<SomeData> { emitter ->
        println("How about here? ${sd.isFrozen}")
        emitter.onNext(sd)
    }
        .subscribeOn(ioScheduler)
        .observeOn(mainScheduler)
        .threadLocal()
        .doOnBeforeNext { values += it } // Callback is not frozen, we can updated the mutable list
        .doOnBeforeFinally { isFinished = true } // Callback is not frozen, we can change the flag
        .subscribe {
            println("Obviously frozen here? ${sd.isFrozen}")
        }
}