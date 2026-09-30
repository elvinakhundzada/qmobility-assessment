package ae.qmobility.assessment

import ae.qmobility.kmp.di.CommonConfiguration
import ae.qmobility.kmp.di.initKoin
import android.app.Application
import org.koin.android.ext.koin.androidContext

class QMobilityApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(config = CommonConfiguration(enableNetworkLogs = BuildConfig.DEBUG)) {
            androidContext(this@QMobilityApp)
        }
    }
}
