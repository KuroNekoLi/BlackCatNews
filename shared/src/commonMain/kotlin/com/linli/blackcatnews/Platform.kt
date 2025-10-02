package com.linli.blackcatnews

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform