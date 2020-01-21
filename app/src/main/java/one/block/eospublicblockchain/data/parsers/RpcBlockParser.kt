package one.block.eospublicblockchain.data.parsers

import com.google.gson.GsonBuilder
import one.block.eosiojava.models.rpcProvider.response.GetBlockResponse
import one.block.eospublicblockchain.data.model.EosAction
import one.block.eospublicblockchain.data.model.EosBlockInfo
import one.block.eospublicblockchain.data.model.EosTransaction
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

interface RpcBlockParser {

    val Map<*, *>.asTransaction
        get() = if(this["trx"] is Map<*, *> &&
            (this["trx"] as Map<*, *>)["transaction"] is Map<*, *> )
            Pair((this["trx"] as Map<*, *>)["id"],
                ((this["trx"] as Map<*, *>)["transaction"] as Map<*, *>))
        else Pair(null, emptyMap<Any, Any>())

    val Map<*, *>.asActions: List<Map<*, *>>
        get() = this["actions"].asListWithMap

    val Map<*, *>.asData
        get() = if(this["data"] is Map<*, *>) (this["data"] as Map<*, *>)
            else emptyMap<Any, Any>()

    val Map<*, *>.asAuth: List<Map<*, *>>
        get() = this["authorization"].asListWithMap


    fun Map<*, *>?.getString(key: Any): String {
        val value = this?.get(key)
        return if(value is String) value else ""
    }

    private val Any?.asListWithMap: List<Map<*, *>>
        get() {
            val result = if (this is List<*>) this
            else emptyList<List<*>>()
            return result.filterIsInstance<Map<*, *>>()
        }

    fun parseBlock(blockData: GetBlockResponse): EosBlockInfo {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
        var actionsSum  = 0
        return EosBlockInfo(
            number = blockData.blockNum,
            hash = blockData.id,
            created = LocalDateTime.parse(blockData.timestamp, formatter),
            producer = blockData.producer,
            transactionsCount = blockData.transactions.size,
            transactions = mapTransactions(blockData.transactions) {
                actionsSum += it
            },
            actionsCount = actionsSum,
            signature = blockData.producerSignature,
            rawData = GsonBuilder().setPrettyPrinting().create().toJson(blockData)
        )
    }

    private inline fun mapTransactions(transactions: List<Map<*, *>>, onSum: (Int) -> Unit) =
        transactions.map {
            val tx = it.asTransaction
            val actions = tx.second.asActions
            val id = when (tx.first) {
                is String -> tx.first as String
                else -> it.getString("trx")
            }

            onSum(actions.size)

            EosTransaction(
                id,
                actions.map { action ->
                    val auth = action.asAuth.firstOrNull()
                    val data = action.asData
                    if (action["name"] == "transfer")
                        EosAction.Transfer(
                            action.getString("hex_data"),
                            auth.getString("actor"),
                            data.getString("memo"),
                            data.getString("from"),
                            data.getString("to"),
                            data.getString("quantity")
                        )
                    else
                        EosAction.Execution(
                            action.getString("hex_data"),
                            auth.getString("actor"),
                            data.getString("memo"),
                            action.getString("name")
                        )
                }
            )
        }
}