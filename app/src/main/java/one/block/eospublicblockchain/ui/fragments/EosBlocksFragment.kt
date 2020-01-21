package one.block.eospublicblockchain.ui.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.withState
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_content.*

import one.block.eospublicblockchain.R
import one.block.eospublicblockchain.Settings
import one.block.eospublicblockchain.ui.controllers.EosBlocksController

/**
 * A simple [Fragment] subclass.
 */
class EosBlocksFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    private var rootView: View? = null
    private lateinit var controller: EosBlocksController

    override fun invalidate() = withState(blocksViewModel) {
        reloader.isRefreshing = (it.latestBlockNumber is Loading)
        reloader.isEnabled = it.canReload
        if(::controller.isInitialized)
            controller.requestModelBuild()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = rootView ?: {
        rootView = inflater.inflate(R.layout.fragment_content, container, false)
        rootView
    }()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(getString(R.string.app_name),false)
        initController()
        reloader.setOnRefreshListener(this)
    }

    override fun onRefresh() {
        reloader.isRefreshing = true
        blocksViewModel.requestLatestBlocks(Settings.loadBlocksNumber)
    }

    private fun initController() {
        if(::controller.isInitialized)
            return
        controller = EosBlocksController(blocksViewModel, getString(R.string.unknown_error),
            Settings.loadBlocksNumber)
        controller.onError {
            val snackbar = Snackbar.make(content, it.message ?: getString(R.string.unknown_error),
                Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction(getString(R.string.retry_text)) {
                onRefresh()
            }
            snackbar.show()
        }
        controller.onBlockClick {
            blocksViewModel.resetActiveBlock()
            findNavController().navigate(EosBlocksFragmentDirections.showBlockDetails(it.toString()))
        }

        content.setControllerAndBuildModels(controller)
    }


}
