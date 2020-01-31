package one.block.eospublicblockchain.service_locator

import one.block.eosiojavarpcprovider.implementations.EosioJavaRpcProviderImpl
import one.block.eospublicblockchain.Settings
import one.block.eospublicblockchain.network.api.EosApi
import one.block.eospublicblockchain.network.api.EosApiImpl
import one.block.eospublicblockchain.view_model.EosBlocksViewModel
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module



fun Module.bodyModules(api: EosApi) {
    scope(named<EosBlocksViewModel>()) {

        scoped {
            api
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