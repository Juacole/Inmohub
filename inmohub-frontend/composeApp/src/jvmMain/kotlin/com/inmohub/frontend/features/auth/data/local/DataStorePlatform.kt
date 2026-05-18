package com.inmohub.frontend.features.auth.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import java.io.File

internal fun createDataStore(producePath: () -> String) : DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() }
    )

actual fun createDataStore(): DataStore<Preferences> {
    return createDataStore {
        val appDir = File(System.getProperty("user.home"), ".inmohub")
        if(!appDir.exists()) appDir.mkdir()
        File(appDir, "session.preferences_pb").absolutePath
    }
}