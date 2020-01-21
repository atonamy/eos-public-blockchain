package one.block.eospublicblockchain.network.api


import one.block.eosiojava.interfaces.IRPCProvider
import one.block.eosiojava.models.rpcProvider.request.GetBlockRequest
import one.block.eospublicblockchain.data.model.EosBlockInfo
import one.block.eospublicblockchain.data.parsers.RpcBlockParser
import java.math.BigInteger


class EosApiImpl(private val rpc: IRPCProvider) : EosApi,
    RpcBlockParser {

    override suspend fun getLatestBlock(): BigInteger = rpc.info.headBlockNum

    override suspend fun getBlockInfo(blockNumber: BigInteger): EosBlockInfo =
        parseBlock(rpc.getBlock(GetBlockRequest(blockNumber.toString())))

}