package one.block.eospublicblockchain.ui.controllers

import android.view.View
import com.airbnb.epoxy.EpoxyController
import com.airbnb.mvrx.*
import one.block.eospublicblockchain.*
import one.block.eospublicblockchain.data.state.EosBlocks
import one.block.eospublicblockchain.view_model.EosBlocksViewModel
import java.math.BigInteger

class EosBlocksController(
    private val viewModel: EosBlocksViewModel,
    private val unknownError: String,
    private val totalLoadBlocks: Short
    ) : EpoxyController() {

    private var onErrorHandler: ((Throwable) -> Unit)? = null
    private var onBlockClickHandler: ((BigInteger) -> Unit)? = null

    fun onError(listener: (Throwable) -> Unit) {
        onErrorHandler = listener
    }

    fun onBlockClick(listener: (BigInteger) -> Unit) {
        onBlockClickHandler = listener
    }

    override fun buildModels() = withState(viewModel) { state ->

        when (state.latestBlockNumber) {
            is Uninitialized -> rowIntro {
                id("intro")
                onDisplayBlocks(View.OnClickListener {
                    viewModel.requestLatestBlocks(totalLoadBlocks)
                })
            }
            is Fail -> onErrorHandler?.invoke(state.latestBlockNumber.error)
            else -> renderBlocks(state.latestBlockNumber, state.loadedBlocks)
        }

        Unit
    }


    private fun renderBlocks(currentBlock: Async<BigInteger>, blocks: EosBlocks) {
        if(currentBlock !is Success)
            return

        blocks.forEach { item ->
            when(val blockState = item.value) {
                is Fail -> rowBlockError {
                    id(item.key)
                    err(blockState.error.message ?: unknownError)
                    onRetry (View.OnClickListener {
                        viewModel.requestSpecificBlock(item.key)
                    })
                }
                is Success -> rowBlockItem {
                    id(item.key)
                    model(blockState())
                    onBlockClick (View.OnClickListener {
                        onBlockClickHandler?.invoke(item.key)
                    })
                }
                else -> rowLoading {
                    id(item.key)
                }
            }
        }

    }

}