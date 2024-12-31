package com.todokanai.displayalarm

import android.hardware.display.DisplayManager
import android.media.MediaPlayer
import com.todokanai.displayalarm.repository.DataStoreRepository
import javax.inject.Inject

class DisplayAlarmServiceModel @Inject constructor(
    val testModel: TestModel,
    val dsRepo:DataStoreRepository,
    val timeChecker: TimeChecker,
    val mediaPlayer: MediaPlayer,
    val displayManager: DisplayManager
) {
}