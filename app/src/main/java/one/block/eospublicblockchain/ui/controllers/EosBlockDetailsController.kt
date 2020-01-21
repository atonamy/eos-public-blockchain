package one.block.eospublicblockchain.ui.controllers

import android.view.View
import com.airbnb.epoxy.EpoxyController
import com.airbnb.mvrx.withState
import one.block.eospublicblockchain.*
import one.block.eospublicblockchain.data.model.EosAction
import one.block.eospublicblockchain.view_model.EosBlocksViewModel

class EosBlockDetailsController(
                                private val viewModel: EosBlocksViewModel,
                                private val cutOffMessage: String): EpoxyController() {

    override fun buildModels() = withState(viewModel) {
        val block = it.currentBlock ?: return@withState

        rowBlockItem {
            id("block_${block.hash}")
            model(block)
        }

        rowRawData {
            id("raw_${block.hash}")
            rawData(
                // This shortcut. Proper implementation would be lazy UI rendering chunks of raw data
                if(it.rawData.length > 20480)
                    it.rawData.substring(0, 20480) + " ...$cutOffMessage"
                else it.rawData
            )
            onClick(View.OnClickListener {
                viewModel.toggleRawData()
            })
        }

        block.transactions.forEach { transaction ->
            rowTransactionItem {
                id(transaction.id)
                model(transaction)
            }
            transaction.actions.distinctBy { i -> i.hex }.forEach {  action ->
                when(action) {
                    is EosAction.Transfer -> rowTransferActionItem {
                        id(action.hex)
                        model(action)
                    }
                    is EosAction.Execution -> rowExecuteActionItem {
                        id(action.hex)
                        model(action)
                    }
                }
            }
        }


    }


}