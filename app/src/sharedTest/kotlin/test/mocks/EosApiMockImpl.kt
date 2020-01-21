package test.mocks

import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import one.block.eosiojava.models.rpcProvider.response.GetBlockResponse
import one.block.eospublicblockchain.data.model.EosBlockInfo
import one.block.eospublicblockchain.data.parsers.RpcBlockParser
import one.block.eospublicblockchain.network.api.EosApi
import org.threeten.bp.LocalDateTime
import java.math.BigInteger

class EosApiMockImpl(private val startBlock: BigInteger,
                     private val delayInMilis: Long,
                     private val getBlockRawData: (BigInteger) -> String) : EosApi, RpcBlockParser {

    override suspend fun getLatestBlock(): BigInteger {
        delay(delayInMilis)
        if(startBlock == BigInteger.ZERO)
            throw Exception("Wrong block number")
        return startBlock
    }

    override suspend fun getBlockInfo(blockNumber: BigInteger): EosBlockInfo  {
        delay(delayInMilis)
        val block = Gson().fromJson(getBlockRawData(blockNumber), GetBlockResponse::class.java)
        return parseBlock(block)
    }
}