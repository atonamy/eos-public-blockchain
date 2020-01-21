package test

import org.koin.core.Koin
import org.koin.dsl.koinApplication
import test.service_locator.appMockModule
import java.math.BigInteger

// can be reuse for instrumented tests as well
abstract class BaseTest {

    protected lateinit var koin: Koin

    protected fun setupKoin(startMockBlock: BigInteger,
                            mockServerResponseDelay: Long) {
        if(::koin.isInitialized)
            koin.close()
        koin = koinApplication {
            modules(
               appMockModule(startMockBlock, mockServerResponseDelay) {
                   getFileFromResources(it)
               }
            )
        }.koin
    }

    protected abstract fun getFileFromResources(file: String): String
}