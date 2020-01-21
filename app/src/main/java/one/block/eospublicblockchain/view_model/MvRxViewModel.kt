package one.block.eospublicblockchain.view_model

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.*
import com.dropbox.android.external.store4.StoreResponse
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import org.koin.core.Koin
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import kotlin.reflect.KProperty1

abstract class MvRxViewModel<S : MvRxState>(initialState: S, private val koin: Koin) :
    BaseMvRxViewModel<S>(initialState, debugMode = BuildConfig.DEBUG) {

    private val compositeDisposable = CompositeDisposable()
    protected lateinit var koinScope: Scope


    @Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
    protected inline fun <reified T> initKoinScope() {
        koinScope = koin.getOrCreateScope("ViewModel", named<T>())
    }

    @Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
    protected inline fun <T> Flow<StoreResponse<T>>.execute(crossinline stateReducer: S.(Async<T>) -> S) =
        viewModelScope.launch {
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

                        val error = it.errorOrNull()
                        stateReducer(this,
                            when {
                                it is StoreResponse.Loading -> Loading()
                                error != null -> Fail(error)
                                else -> Success(it.requireData())
                            }
                        )
                    }
                }
            } catch (e: Exception) {
                setState {
                    stateReducer(this, Fail(e))
                }
            }
        }

    private fun Disposable?.toDispose() {
        if(this != null) {
            if(!this.isDisposed)
                this.dispose()
            compositeDisposable.remove(this)
        }
    }

    private fun Disposable.track(): Disposable? {
        if(!this.isDisposed)
            compositeDisposable.add(this)
        return this
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
        if(::koinScope.isInitialized)
            koinScope.close()
    }

    protected fun <T> singleAsyncSubscribe(asyncProp: KProperty1<S, Async<T>>,
                                          onSuccess: ((T) -> Unit)? = null) {
        var subscription: Disposable? = null

        subscription = asyncSubscribe(asyncProp, {
            subscription.toDispose()
        }) {
            subscription.toDispose()
            onSuccess?.invoke(it)
        }.track()
    }
}
