package com.allangon4lves.agcast.app

import android.app.Application
import com.google.android.gms.cast.framework.CastContext

class App : Application() {
    private lateinit var castContext: CastContext
    
    override fun onCreate() {
        super.onCreate()
        castContext = CastContext.getSharedInstance(this)
    }
}