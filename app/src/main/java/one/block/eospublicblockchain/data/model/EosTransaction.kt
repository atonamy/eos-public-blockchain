package one.block.eospublicblockchain.data.model

data class EosTransaction (
    val id: String,
    val actions: List<EosAction>
)