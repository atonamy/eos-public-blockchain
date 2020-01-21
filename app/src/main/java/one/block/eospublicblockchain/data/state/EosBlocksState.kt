package one.block.eospublicblockchain.data.state


import com.airbnb.mvrx.*
import one.block.eospublicblockchain.data.model.EosBlockInfo
import java.math.BigInteger

typealias EosBlocks = Map<BigInteger, Async<EosBlockInfo>>

data class EosBlocksState (
    val latestBlockNumber: Async<BigInteger> = Uninitialized,
    val loadedBlocks: EosBlocks = emptyMap(),
    val totalBlocks: Short = 0,
    val goBack: SingleEvent<Boolean> = SingleEvent(false),
    val currentBlock: EosBlockInfo? = null,
    val rawData: String = ""
): MvRxState {

    private val EosBlocks.allLoaded: Boolean
        get() {
            var loadedCounts = 0
            this.forEach {
                if(it.value is Success)
                    loadedCounts++
            }
            return loadedCounts == totalBlocks.toInt()
        }

    val canReload: Boolean
        get() = latestBlockNumber is Loading ||
                loadedBlocks.size  == totalBlocks.toInt() && loadedBlocks.allLoaded

}