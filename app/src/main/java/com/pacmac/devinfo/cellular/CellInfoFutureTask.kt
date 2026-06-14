package com.pacmac.devinfo.cellular

import android.annotation.SuppressLint
import android.os.Build
import android.telephony.CellInfo
import android.telephony.TelephonyManager
import android.util.Log
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit

internal class CellInfoFutureTask : FutureTask<Void>({ null }) {

    private var cellInfo: List<CellInfo>? = null

    fun run(cellInfo: List<CellInfo>) {
        this.cellInfo = cellInfo
        super.run()
    }

    @Throws(Exception::class)
    fun getAllCellInfoBlocking(): List<CellInfo>? {
        super.get(TIMEOUT_MS, TimeUnit.MILLISECONDS)
        return cellInfo
    }

    companion object {
        private const val TIMEOUT_MS = 500L

        @SuppressLint("MissingPermission")
        fun getAllCellInfoBlocking(telephonyManager: TelephonyManager): List<CellInfo>? {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return null
            return try {
                val future = CellInfoFutureTask()
                telephonyManager.requestCellInfoUpdate(
                    { it.run() },
                    object : TelephonyManager.CellInfoCallback() {
                        override fun onCellInfo(cellInfo: List<CellInfo>) {
                            future.run(cellInfo)
                        }
                    }
                )
                future.getAllCellInfoBlocking()
            } catch (e: Exception) {
                Log.e("CellInfoFutureTask", "Failed to get cell info", e)
                null
            }
        }
    }
}
