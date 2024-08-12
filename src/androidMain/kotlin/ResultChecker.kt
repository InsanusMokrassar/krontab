package dev.inmo.krontab

import android.annotation.SuppressLint
import androidx.work.ListenableWorker

@SuppressLint("RestrictedApi")
internal inline fun ListenableWorker.Result.checkResults(
    onFailure: () -> Unit,
    onSuccess: () -> Unit
) {
    when (this) {
        is ListenableWorker.Result.Failure -> onFailure()
        is ListenableWorker.Result.Success -> onSuccess()
    }
}
