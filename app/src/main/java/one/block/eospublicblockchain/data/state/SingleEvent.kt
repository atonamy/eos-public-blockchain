package one.block.eospublicblockchain.data.state

data class SingleEvent<T>(private var _value: T?) {
    val value: T?
        get() {
            val v = _value
            _value = null
            return v
        }
}