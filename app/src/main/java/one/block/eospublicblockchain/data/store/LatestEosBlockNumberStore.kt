package one.block.eospublicblockchain.data.store

import com.dropbox.android.external.store4.Store
import java.math.BigInteger

class LatestEosBlockNumberStore(val value: Store<Unit, BigInteger>)