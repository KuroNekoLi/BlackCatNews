package com.linli.dictionary.data.local.database

import androidx.room.RoomDatabase

/**
 * 平台特定的資料庫建構器函數
 *
 * @return RoomDatabase.Builder<DictionaryDatabase> 資料庫建構器
 */
expect fun getDatabaseBuilder(): RoomDatabase.Builder<DictionaryDatabase>