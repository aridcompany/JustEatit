package com.ari_d.justeatit.data.entities

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Wallet::class],
    version = 1
)
abstract class WalletDatabase: RoomDatabase() {

    abstract val dao: WalletDao
}