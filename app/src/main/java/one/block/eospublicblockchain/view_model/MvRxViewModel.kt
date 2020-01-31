package one.block.eospublicblockchain.view_model

import com.airbnb.mvrx.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import org.koin.core.Koin
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope

abstract class MvRxViewModel<S : MvRxState>(initialState: S, private val koin: Koin) :
    BaseMvRxViewModel<S>(initialState, debugMode = BuildConfig.DEBUG) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    protected lateinit var koinScope: Scope


    @Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
    protected inline fun <reified T> initKoinScope() {
        koinScope = koin.getOrCreateScope("ViewModel", named<T>())
    }

    protected fun <T> Flow<T>.execute(stateReducer: S.(Async<T>) -> S) =
        scope.launch {
            try {
                setState {
                    stateReducer(this, Loading())
                }
                catch {
                    setState {
                        stateReducer(this, Fail(it))
                    }
                }
                collect {
                    setState {
                        stateReducer(
                            this,
                            Success(it)
                        )
                    }
                }
            } catch (e: Exception) {
                setState {
                    stateReducer(this, Fail(e))
                }
            }
        }


    override fun onCleared() {
        super.onCleared()
        scope.cancel()
        if(::koinScope.isInitialized)
            koinScope.close()
    }
}
