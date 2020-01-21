package one.block.eospublicblockchain.ui.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.airbnb.mvrx.withState
import kotlinx.android.synthetic.main.fragment_content.*

import one.block.eospublicblockchain.R
import one.block.eospublicblockchain.ui.controllers.EosBlockDetailsController
import java.math.BigInteger

/**
 * A simple [Fragment] subclass.
 */
class EosBlockDetailsFragment : BaseFragment() {

    private lateinit var controller: EosBlockDetailsController
    private val args: EosBlockDetailsFragmentArgs by navArgs()

    override fun invalidate() = withState(blocksViewModel) {
        if(it.goBack.value == true)
            findNavController().popBackStack()

        if(::controller.isInitialized)
            controller.requestModelBuild()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_content, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reloader.isEnabled = false
        setupToolbar(getString(R.string.block_details_text),true)
        initController()
        blocksViewModel.requestBlockDetails(BigInteger(args.blockNumber))
    }

    private fun initController() {
        controller = EosBlockDetailsController(blocksViewModel, getString(R.string.cut_off_reason))
        content.setControllerAndBuildModels(controller)
    }


}
