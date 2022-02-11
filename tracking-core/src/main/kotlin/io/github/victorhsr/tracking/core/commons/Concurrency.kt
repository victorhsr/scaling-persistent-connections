package io.github.victorhsr.tracking.core.commons

import kotlinx.coroutines.sync.Mutex

suspend fun <T> runWithLock(lock: Mutex, runnable: suspend () -> T): T {
    lock.lock()
    try {
        return runnable()
    } finally {
        lock.unlock()
    }
}