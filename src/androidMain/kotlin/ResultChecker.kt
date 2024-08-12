package dev.inmo.krontab

import android.annotation.SuppressLint
import androidx.work.ListenableWorker

@SuppressLint("RestrictedApi")
internal fun ListenableWorker.Result.isSuccess() = this is ListenableWorker.Result.Success

@SuppressLint("RestrictedApi")
internal fun ListenableWorker.Result.isFailure() = this is ListenableWorker.Result.Failure
