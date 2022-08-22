package com.emeraldblast.p6.ui.common.compose

import androidx.compose.runtime.*

typealias Ms<T> = MutableState<T>
typealias St<T> = State<T>

object StateUtils {
    /**
     * a convenient function for mutableStateOf
     */
    inline fun <T> ms(f: () -> T): Ms<T> {
        return mutableStateOf(f())
    }
    /**
     * a convenient function for mutableStateOf
     */
    fun <T> ms(o: T): Ms<T> {
        return mutableStateOf(o)
    }
    fun <T : Any?> T.toMs(): Ms<T> {
        return ms(this)
    }
    fun <T : Any?> T.toSt(): St<T> {
        return ms(this)
    }

    /**
     * a convenient function for remember + mutableStateOf
     */
    @Composable
    fun <T> rms(o: T): Ms<T> {
        return remember { mutableStateOf(o) }
    }

    /**
     * a convenient function for remember + mutableStateOf
     */
    @Composable
    fun <T> rms(f: () -> T): Ms<T> {
        return remember { mutableStateOf(f()) }
    }
}
