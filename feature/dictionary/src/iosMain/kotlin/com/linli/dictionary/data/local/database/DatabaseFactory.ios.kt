package com.linli.dictionary.data.local.database

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

/**
 * iOS 平台上的 getDatabaseBuilder 定義，定位至 iOS 文件系統
 */
@OptIn(ExperimentalForeignApi::class)
actual fun getDatabaseBuilder(): RoomDatabase.Builder<DictionaryDatabase> {
    val databasePath = documentsDirectoryPath() + "/" + DictionaryDatabase.DATABASE_NAME
    return Room.databaseBuilder<DictionaryDatabase>(name = databasePath)
        .fallbackToDestructiveMigration(dropAllTables = true)
}

/**
 * 取得 iOS 文件儲存路徑
 *
 * @return iOS 文件路徑字串
 */
@OptIn(ExperimentalForeignApi::class)
private fun documentsDirectoryPath(): String {
    val url = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null
    )
    return requireNotNull(url?.path)
}