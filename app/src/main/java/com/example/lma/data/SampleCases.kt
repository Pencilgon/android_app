package com.example.lma.data

import com.example.lma.model.MediaCase
import com.example.lma.model.MediaCategory
import com.example.lma.model.RecoveryStatus

object SampleCases {

    val cases = listOf(
        MediaCase(
            id = 1,
            title = "The Unreleased Cartoon Pilot",
            description = "An early pilot episode reportedly created before the series was commissioned. Only production notes and a few still images are known to survive.",
            category = MediaCategory.TV_SHOW,
            lastKnownYear = 1998,
            recoveryStatus = RecoveryStatus.LOST,
            sourceUrl = null
        ),
        MediaCase(
            id = 2,
            title = "Project Starfall",
            description = "An abandoned adventure game shown at a small industry event. A short demonstration was recorded, but the original game build has not surfaced.",
            category = MediaCategory.VIDEO_GAME,
            lastKnownYear = 2004,
            recoveryStatus = RecoveryStatus.PARTIALLY_FOUND,
            sourceUrl = "https://example.com/starfall"
        ),
        MediaCase(
            id = 3,
            title = "Midnight Radio Broadcast",
            description = "A late-night radio programme remembered by listeners. The station's archive is incomplete, and no full recording has been located.",
            category = MediaCategory.MUSIC,
            lastKnownYear = 1987,
            recoveryStatus = RecoveryStatus.LOST,
            sourceUrl = null
        ),
        MediaCase(
            id = 4,
            title = "The Forgotten Web Portal",
            description = "A community website that disappeared after its hosting service shut down. Some screenshots and fragments have been preserved.",
            category = MediaCategory.WEBSITE,
            lastKnownYear = 2009,
            recoveryStatus = RecoveryStatus.PARTIALLY_FOUND,
            sourceUrl = "https://example.com/web-portal"
        ),
        MediaCase(
            id = 5,
            title = "Festival Cut",
            description = "An alternate cut of an independent film reportedly screened at a festival. The widely available version differs from audience descriptions.",
            category = MediaCategory.FILM,
            lastKnownYear = 2012,
            recoveryStatus = RecoveryStatus.FOUND,
            sourceUrl = null
        ),
        MediaCase(
            id = 6,
            title = "Prototype Handheld Game",
            description = "A prototype game described in an old magazine. No playable copy or verified footage has been found.",
            category = MediaCategory.VIDEO_GAME,
            lastKnownYear = 1995,
            recoveryStatus = RecoveryStatus.LOST,
            sourceUrl = null
        )
    )
}