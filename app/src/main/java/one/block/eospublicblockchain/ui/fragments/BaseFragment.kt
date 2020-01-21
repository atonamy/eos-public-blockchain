package one.block.eospublicblockchain.ui.fragments

import androidx.appcompat.app.AppCompatActivity
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.activityViewModel
import one.block.eospublicblockchain.view_model.EosBlocksViewModel

abstract class BaseFragment : BaseMvRxFragment() {
    protected val blocksViewModel: EosBlocksViewModel by activityViewModel()

    protected fun setupToolbar(title: String, showBackButton: Boolean = false) {
        val activity = requireActivity()
        if(activity is AppCompatActivity) {
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(showBackButton)
            activity.supportActionBar?.title = title
        }
    }
}