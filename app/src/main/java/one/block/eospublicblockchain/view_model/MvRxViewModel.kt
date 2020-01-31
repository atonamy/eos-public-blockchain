package one.block.eospublicblockchain.view_model

import com.airbnb.mvrx.*
import com.dropbox.android.external.store4.StoreResponse
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import org.koin.core.Koin
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import java.util.concurrent.TimeoutException

abstract class MvRxViewModel<S : MvRxState>(initialState: S, private val koin: Koin) :
    BaseMvRxViewModel<S>(initialState, debugMode = BuildConfig.DEBUG) {

    protected val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    protected lateinit var koinScope: Scope


    @Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
    protected inline fun <reified T> initKoinScope() {
        koinScope = koin.getOrCreateScope("ViewModel", named<T>())
    }

    protected fun <T> Flow<StoreResponse<T>>.execute(completed: (suspend () -> Unit)? = null,
                                                     stateReducer: S.(Async<T>) -> S) =
        scope.launch {
            try {
                setState {
                    stateReducer(this, Loading())
                }
                catch {
                    setState {
                        stateReducer(this, Fail(it))
                    }
                    completed?.invoke()
                }
                collect {
                    val error = it.errorOrNull()
                    val state = when {
                        it is StoreResponse.Loading -> Loading<T>()
                        error != null -> Fail(error)
                        else -> Success(it.requireData())
                    }
                    setState {
                        stateReducer(
                            this,
                            state
                        )
                    }
                    if(state !is Loading)
                        completed?.invoke()
                }
            } catch (e: Exception) {
                setState {
                    stateReducer(this, Fail(e))
                }
                completed?.invoke()
            }
        }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
        if(::koinScope.isInitialized)
            koinScope.close()
    }
}
