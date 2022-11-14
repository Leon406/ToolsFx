package me.leon

import java.util.concurrent.Executors
import kotlinx.coroutines.asCoroutineDispatcher

/**
 *
 * @author Leon
 * @since 2022-11-14 13:06
 * @email: deadogone@gmail.com
 */
val DISPATCHER =
    Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2)
        .asCoroutineDispatcher()
