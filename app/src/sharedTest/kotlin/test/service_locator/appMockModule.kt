package test.service_locator


import one.block.eospublicblockchain.service_locator.bodyModules
import org.koin.dsl.module
import test.mocks.EosApiMockImpl
import java.math.BigInteger

fun appMockModule(
    startMockBlock: BigInteger,
    mockServerResponseDelay: Long,
    getJsonFile: (String) -> String) = module {
    bodyModules(EosApiMockImpl(startMockBlock, mockServerResponseDelay) {
        getJsonFile("$it.json")
    })
}