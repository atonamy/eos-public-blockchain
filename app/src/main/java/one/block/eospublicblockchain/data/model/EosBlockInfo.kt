package one.block.eospublicblockchain.data.model

import org.threeten.bp.LocalDateTime
import java.math.BigInteger

data class EosBlockInfo (
    val number: BigInteger,
    val hash: String,
    val actionsCount: Int,
    val transactionsCount: Int,
    val created: LocalDateTime,
    val producer: String,
    val signature: String,
    val transactions: List<EosTransaction>,
    val rawData: String
)