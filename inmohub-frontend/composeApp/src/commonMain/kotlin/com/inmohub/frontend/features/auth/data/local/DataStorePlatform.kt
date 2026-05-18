package com.inmohub.frontend.features.auth.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

expect fun createDataStore() : DataStore<Preferences>