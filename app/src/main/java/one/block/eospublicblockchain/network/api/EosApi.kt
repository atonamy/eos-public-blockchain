package one.block.eospublicblockchain.network.api

import kotlinx.coroutines.flow.Flow
import one.block.eospublicblockchain.data.model.EosBlockInfo
import java.math.BigInteger


interface EosApi {
    fun getLatestBlock(): Flow<BigInteger>
    fun getBlockInfo(blockNumber: BigInteger): Flow<EosBlockInfo>
}