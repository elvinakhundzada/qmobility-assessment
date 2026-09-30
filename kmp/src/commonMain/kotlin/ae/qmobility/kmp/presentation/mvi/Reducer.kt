package ae.qmobility.kmp.presentation.mvi

fun interface Reducer<State, Transition> {
    fun reduce(state: State, transition: Transition): State
}
