package one.block.eospublicblockchain.network.api


import kotlinx.coroutines.flow.flow
import one.block.eosiojava.interfaces.IRPCProvider
import one.block.eosiojava.models.rpcProvider.request.GetBlockRequest
import one.block.eospublicblockchain.data.parsers.RpcBlockParser
import java.math.BigInteger


class EosApiImpl(private val rpc: IRPCProvider) : EosApi,
    RpcBlockParser {

    override fun getLatestBlock() = flow {
        val number = rpc.info.headBlockNum
        emit(number)
    }

    override fun getBlockInfo(blockNumber: BigInteger) = flow {
        val block = parseBlock(rpc.getBlock(GetBlockRequest(blockNumber.toString())))
        emit(block)
    }


}