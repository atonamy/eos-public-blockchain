package one.block.eospublicblockchain


import com.airbnb.mvrx.*
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.setMain
import one.block.eospublicblockchain.data.state.EosBlocksState
import one.block.eospublicblockchain.view_model.EosBlocksViewModel
import org.awaitility.kotlin.await
import org.junit.Test

import org.junit.Before
import test.BaseTest
import java.io.File
import java.math.BigInteger
import java.util.concurrent.TimeUnit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class EosViewModelTest : BaseTest() {

    companion object {
        const val testDelay = 5000L
        const val mockedLatestBlock = "101226457"
    }

    private val viewModel: EosBlocksViewModel by lazy {
        EosBlocksViewModel(EosBlocksState(), koin)
    }

    @Before
    fun setupTest() {
        setupKoin(BigInteger(mockedLatestBlock), 500)
        RxAndroidPlugins.setInitMainThreadSchedulerHandler {
            Schedulers.newThread()
        }
        RxAndroidPlugins.setMainThreadSchedulerHandler {
            Schedulers.newThread()
        }
        Dispatchers.setMain(newSingleThreadContext("Mocking UI thread"))
    }


    @Test
    fun testInitialState_success() {
        verifyStateWithPending {
            latestBlockNumber is Uninitialized &&
                    loadedBlocks.isEmpty() &&
                    totalBlocks == 0.toShort() &&
                    goBack.value == false &&
                    currentBlock == null &&
                    rawData.isEmpty()
        }
    }

    @Test
    fun testLatestBlockLoading_success() {
        viewModel.requestLatestBlocks(5)
        verifyStateWithPending {
            latestBlockNumber is Loading
        }
    }



    @Test
    fun testParticularBlockLoading_success() {
        viewModel.requestSpecificBlock(BigInteger(mockedLatestBlock))
        verifyStateWithPending  {
            loadedBlocks[BigInteger(mockedLatestBlock)] is Loading
        }
    }

    @Test
    fun testAllBlocksLoading_success() {
        for(i in 0 .. 4) {
            viewModel.requestLatestBlocks(5)
            val testBlock = BigInteger(mockedLatestBlock).minus(BigInteger("$i"))
            verifyStateWithPending {
                loadedBlocks[testBlock] is Loading
            }
        }

    }

    @Test
    fun testLoadedBlockNumber_success() {
        viewModel.requestLatestBlocks(5)
        verifyStateWithPending {
            latestBlockNumber is Success && latestBlockNumber() == BigInteger(mockedLatestBlock)
        }
    }

    @Test
    fun testLoadedBlockInfo_success() {
        viewModel.requestSpecificBlock(BigInteger(mockedLatestBlock))
        verifyStateWithPending {
            loadedBlocks[BigInteger(mockedLatestBlock)] is Success &&
                    loadedBlocks[BigInteger(mockedLatestBlock)]?.invoke()?.number ==
                    BigInteger(mockedLatestBlock)
        }
    }

    @Test
    fun testAllLoadedBlocks_success() {
        for(i in 0 .. 4) {
            viewModel.requestLatestBlocks(5)
            val testBlock = BigInteger(mockedLatestBlock).minus(BigInteger("$i"))
            verifyStateWithPending {
                loadedBlocks[testBlock] is Success &&
                        loadedBlocks[testBlock]?.invoke()?.number ==  testBlock
            }
        }

    }

    @Test
    fun testLatestBlockLoading_fail() {
        setupKoin(BigInteger("0"), 500)
        viewModel.requestLatestBlocks(1)
        verifyStateWithPending {
            latestBlockNumber is Fail
        }
    }

    @Test
    fun testGetBlockInfo_fail() {
        viewModel.requestSpecificBlock(BigInteger("0"))
        verifyStateWithPending {
            loadedBlocks[BigInteger("0")] is Fail
        }
    }

    @Test
    fun testGetAllBlocks_fail() {
        setupKoin(BigInteger("1"), 500)
        for(i in 0 .. 4) {
            viewModel.requestLatestBlocks(5)
            val testBlock = BigInteger("1").minus(BigInteger("$i"))
            verifyStateWithPending {
                loadedBlocks[testBlock] is Fail
            }
        }
    }

    @Test
    fun testBlockDetails_success() {
        viewModel.requestSpecificBlock(BigInteger(mockedLatestBlock))
        verifyStateWithPending {
            loadedBlocks[BigInteger(mockedLatestBlock)] is Success
        }

        viewModel.requestBlockDetails(BigInteger(mockedLatestBlock))
        verifyStateWithPending {
            currentBlock != null
        }
    }

    @Test
    fun testToggleRawData_success() {
        verifyStateWithPending {
            rawData.isEmpty()
        }
        testBlockDetails_success()
        viewModel.toggleRawData()
        verifyStateWithPending {
            rawData.isNotEmpty()
        }
    }

    @Test
    fun testResetActiveBlock_success() {
        testToggleRawData_success()
        verifyStateWithPending {
            currentBlock != null && rawData.isNotEmpty()
        }
        viewModel.resetActiveBlock()
        verifyStateWithPending {
            currentBlock == null && rawData.isEmpty()
        }
    }

    override fun getFileFromResources(file: String): String {
        val classLoader = javaClass.classLoader
        val resource = classLoader!!.getResource(file)
        return String(File(resource.path).readBytes())
    }

    private inline fun verifyStateWithPending(awaitMili: Long = testDelay,
                                              crossinline condition: EosBlocksState.() -> Boolean) {

        await.atMost(awaitMili, TimeUnit.MILLISECONDS).until {
            withState(viewModel) { condition(it) }
        }
    }
}
