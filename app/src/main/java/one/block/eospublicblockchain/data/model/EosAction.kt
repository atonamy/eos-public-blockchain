package one.block.eospublicblockchain.data.model



sealed class EosAction(
    open val hex: String,
    open val owner: String,
    open val memo: String
) {
    data class Transfer (
        override val hex: String,
        override val owner: String,
        override val memo: String,
        val from: String,
        val to: String,
        val amount: String
    ) : EosAction(hex, owner, memo)

    data class Execution(
        override val hex: String,
        override val owner: String,
        override val memo: String,
        val type: String
    ) : EosAction(hex, owner, memo)
}