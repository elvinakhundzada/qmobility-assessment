package ae.qmobility.kmp.presentation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher

fun <T> TestScope.collectValues(flow: Flow<T>): List<T> {
    val values = mutableListOf<T>()
    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
        flow.toList(values)
    }
    return values
}
