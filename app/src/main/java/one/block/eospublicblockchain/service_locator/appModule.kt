package one.block.eospublicblockchain.service_locator

import com.dropbox.android.external.store4.StoreBuilder

import one.block.eosiojavarpcprovider.implementations.EosioJavaRpcProviderImpl
import one.block.eospublicblockchain.Settings
import one.block.eospublicblockchain.data.model.EosBlockInfo
import one.block.eospublicblockchain.data.store.EosBlockInfoStore
import one.block.eospublicblockchain.data.store.LatestEosBlockNumberStore
import one.block.eospublicblockchain.network.api.EosApi
import one.block.eospublicblockchain.network.api.EosApiImpl
import one.block.eospublicblockchain.view_model.EosBlocksViewModel
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.math.BigInteger


fun Module.bodyModules(api: EosApi) {
    scope(named<EosBlocksViewModel>()) {
        scoped {
            LatestEosBlockNumberStore(StoreBuilder
                .fromNonFlow <Unit, BigInteger> {
                    api.getLatestBlock()
                }
                .build())
        }

        scoped {
            EosBlockInfoStore(StoreBuilder
                .fromNonFlow <BigInteger, EosBlockInfo> {
                    api.getBlockInfo(it)
                }
                .build())
        }
    }
}

val appModule = module {
    bodyModules(
        EosApiImpl(
            EosioJavaRpcProviderImpl(Settings.rpcBaseUrl)
        )
    )
}