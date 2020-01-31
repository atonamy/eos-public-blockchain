package one.block.eospublicblockchain.view_model

import com.airbnb.mvrx.*
import com.dropbox.android.external.store4.StoreRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import one.block.eospublicblockchain.data.state.EosBlocksState
import one.block.eospublicblockchain.data.state.SingleEvent
import one.block.eospublicblockchain.data.store.EosBlockInfoStore
import one.block.eospublicblockchain.data.store.LatestEosBlockNumberStore
import org.koin.core.Koin
import org.koin.core.KoinComponent
import java.math.BigInteger

class EosBlocksViewModel(
    initialState: EosBlocksState,
    koin: Koin
): MvRxViewModel<EosBlocksState>(initialState, koin) {

    init {
        initKoinScope<EosBlocksViewModel>()
        asyncSubscribe(EosBlocksState::latestBlockNumber) {
            withState { state ->
                scope.launch {
                    for(i in 0 until state.totalBlocks) {
                        requestSpecificBlock(it.minus(BigInteger.valueOf(i.toLong())))
                        delay(250) //eos rpc throw error if load simultaneously
                    }
                }
            }
        }
    }

    private val latestBlockStore by koinScope.inject<LatestEosBlockNumberStore>()
    private val blockInfoStore by koinScope.inject<EosBlockInfoStore>()

    fun requestLatestBlocks(quantity: Short) {
        setState {
            copy(latestBlockNumber = Uninitialized, loadedBlocks = emptyMap(), totalBlocks = quantity)
        }

        latestBlockStore.value.stream(StoreRequest.fresh(Unit)).execute({
            latestBlockStore.value.clear(Unit)
        }) {
            copy(latestBlockNumber = it)
        }
    }

    fun requestSpecificBlock(blockNumber: BigInteger) {
        blockInfoStore.value.stream(
            StoreRequest.fresh(blockNumber))
            .execute({
                blockInfoStore.value.clear(blockNumber)
            }) { blockInfo ->
                copy(loadedBlocks = when(blockInfo) {
                    is Loading -> loadedBlocks + (blockNumber to Loading())
                    is Fail -> loadedBlocks + (blockNumber to Fail(blockInfo.error))
                    is Success -> loadedBlocks + (blockNumber to Success(blockInfo()))
                    else -> loadedBlocks + (blockNumber to Uninitialized)
                })
            }
    }

    fun resetActiveBlock() = setState {
        copy(currentBlock = null, rawData = "")
    }

    fun requestBlockDetails(blockNumber: BigInteger) = setState {
        if(loadedBlocks[blockNumber] == null)
            copy(goBack = SingleEvent(true))
        else {
            val block = loadedBlocks[blockNumber]
            if(block !is Success)
                copy(goBack = SingleEvent(true))
            else
                copy(currentBlock = block(), rawData = "")
        }
    }

    fun toggleRawData() = setState {
        if(rawData.isEmpty())
            copy(rawData = currentBlock?.rawData ?: "")
        else
            copy(rawData = "")
    }

    companion object : MvRxViewModelFactory<EosBlocksViewModel, EosBlocksState>, KoinComponent {
        override fun create(viewModelContext: ViewModelContext, state: EosBlocksState): EosBlocksViewModel  =
            EosBlocksViewModel(state, getKoin())
    }
}