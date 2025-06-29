package com.yogeshpaliyal.chainreaction

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform