package one.block.eospublicblockchain.network.api

import one.block.eospublicblockchain.data.model.EosBlockInfo
import java.math.BigInteger


interface EosApi {
    suspend fun getLatestBlock(): BigInteger
    suspend fun getBlockInfo(blockNumber: BigInteger): EosBlockInfo
}