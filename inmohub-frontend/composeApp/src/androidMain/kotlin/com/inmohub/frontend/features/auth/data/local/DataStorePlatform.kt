package com.inmohub.frontend.features.auth.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

lateinit var applicationContext: Context

internal fun createDataStore(producePath: () -> String) : DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() }
    )

actual fun createDataStore(): DataStore<Preferences> {
    return createDataStore {
        applicationContext.filesDir.resolve("inmohub_session.preferences_pb").absolutePath
    }
}