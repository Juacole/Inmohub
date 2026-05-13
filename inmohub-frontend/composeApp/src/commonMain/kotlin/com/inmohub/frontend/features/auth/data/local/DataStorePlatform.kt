package com.inmohub.frontend.features.auth.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

// Contrato multiplataforma
expect fun createDataStore() : DataStore<Preferences>

internal fun createDataStore(producePath: () -> String) : DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() }
    )