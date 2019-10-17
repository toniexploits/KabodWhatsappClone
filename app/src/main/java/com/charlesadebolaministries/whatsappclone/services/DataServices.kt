package com.charlesadebolaministries.whatsappclone.services

import com.charlesadebolaministries.whatsappclone.util.AttachOption

object DataServices {
    val attachOption = listOf(
        AttachOption("gallery.png", "Gallery"),
        AttachOption("video.png", "Video"),
        AttachOption("audio.png", "Audio"),
        AttachOption("document.png", "Document")
    )
}