package com.allangon4lves.agcast

import android.content.Context
import com.google.android.gms.cast.framework.CastOptions
import com.google.android.gms.cast.framework.OptionsProvider
import com.google.android.gms.cast.framework.SessionProvider
import com.google.android.gms.cast.framework.media.CastMediaOptions

class CastOptionsProvider : OptionsProvider {

    override fun getCastOptions(context: Context): CastOptions {

        val mediaOptions = CastMediaOptions.Builder().build()

        return CastOptions.Builder()
            .setReceiverApplicationId("CC1AD845") // padrão (funciona)
            .setCastMediaOptions(mediaOptions)
            .build()
    }

    override fun getAdditionalSessionProviders(context: Context): MutableList<SessionProvider>? {
        return null
    }
}