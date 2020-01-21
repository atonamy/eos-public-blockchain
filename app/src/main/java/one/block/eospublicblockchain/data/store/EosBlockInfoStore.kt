package one.block.eospublicblockchain.data.store

import com.dropbox.android.external.store4.Store
import one.block.eospublicblockchain.data.model.EosBlockInfo
import java.math.BigInteger

class EosBlockInfoStore(val value: Store<BigInteger, EosBlockInfo>)