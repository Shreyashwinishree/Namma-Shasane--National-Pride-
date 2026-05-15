package com.nammashasane.app

import android.app.Application
import com.nammashasane.app.data.HeritageDatabase
import com.nammashasane.app.data.HeritageRepository

class NammaShasaneApplication : Application() {
    val database: HeritageDatabase by lazy { HeritageDatabase.create(this) }
    val repository: HeritageRepository by lazy { HeritageRepository(database.inscriptionDao()) }
}
