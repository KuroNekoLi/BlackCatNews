package com.linli.blackcatnews.data.local.database

import androidx.room.Room
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import androidx.room.RoomDatabase

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

/**
 * 建立 iOS 平台的 RoomDatabase.Builder
 *
 * @return [RoomDatabase.Builder] 用於後續跨平台共用設定
 */
fun getDatabaseBuilder(): RoomDatabase.Builder<NewsDatabase> {
    val databasePath = documentsDirectoryPath() + "/" + NewsDatabase.DATABASE_NAME
    return Room.databaseBuilder<NewsDatabase>(name = databasePath)
}
