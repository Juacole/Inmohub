package com.inmohub.frontend.features.property.presentation.shared

expect suspend fun pickCsvFile(): Pair<ByteArray, String>?

expect suspend fun pickImageFiles(): List<Pair<ByteArray, String>>
