package com.allangon4lves.agcast.ui

import android.app.Application
import com.google.android.gms.cast.framework.CastContext

class App : Application() {
    private lateinit var castContext: CastContext
    
    override fun onCreate() {
        castContext = CastContext.getSharedInstance(this)
        super.onCreate()
    }
}