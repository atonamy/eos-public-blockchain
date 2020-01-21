package one.block.eospublicblockchain

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.airbnb.epoxy.EpoxyAsyncUtil
import com.airbnb.epoxy.EpoxyController
import one.block.eospublicblockchain.service_locator.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        initServiceLocator()
        setupEpoxy()
    }

    private fun initServiceLocator() {
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }

    private fun setupEpoxy() {
        val handler = EpoxyAsyncUtil.getAsyncBackgroundHandler()
        EpoxyController.defaultDiffingHandler = handler
        EpoxyController.defaultModelBuildingHandler = handler
    }
}