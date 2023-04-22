package com.pacmac.devinfo.main

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pacmac.devinfo.R
import com.pacmac.devinfo.main.model.PermissionState
import com.pacmac.devinfo.ui.components.AdvertView
import com.pacmac.devinfo.utils.Utils
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(viewModel: MainViewModelKt = hiltViewModel()) {

    val tiles = viewModel.getDashboarItems()
    val gridState = rememberLazyGridState()

    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    checkAllPermissions(viewModel, context)

    Column() {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp, vertical = 8.dp),

            state = gridState,
            columns = GridCells.Adaptive(150.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tiles) { tile ->
                Image(
                    painter = painterResource(id = tile.imageRes),
                    contentDescription = tile.dashItem.name,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small.copy(all = CornerSize(8.dp)))
                        .clickable {
                            scope.launch {
                                tile.onclick.invoke(tile)
                            }
                        }
                )
            }
        }

        AdvertView(Modifier.fillMaxWidth(), R.string.banner_id_2)
    }
}

private fun checkAllPermissions(viewModel: MainViewModelKt, context: Context) {
    Utils.checkPermission(context, Utils.STORAGE_PERMISSION).run {
        if (this) viewModel.updatePermissionState(
            Utils.STORAGE_PERMISSION,
            PermissionState.GRANTED
        )
        viewModel._isStoragePermissionEnabled.value = this
    }
    Utils.checkPermission(context, Utils.CAMERA_PERMISSION).run {
        if (this) viewModel.updatePermissionState(
            Utils.CAMERA_PERMISSION,
            PermissionState.GRANTED
        )
        viewModel._isCameraPermissionEnabled.value = this
    }
    Utils.checkPermission(context, Utils.LOCATION_PERMISSION).run {
        if (this) viewModel.updatePermissionState(
            Utils.LOCATION_PERMISSION,
            PermissionState.GRANTED
        )
        viewModel._isLocationPermissionEnabled.value = this
    }
    Utils.checkPermission(context, Utils.PHONE_PERMISSION).run {
        if (this) viewModel.updatePermissionState(
            Utils.PHONE_PERMISSION,
            PermissionState.GRANTED
        )
        viewModel._isPhonePermissionEnabled.value = this
    }

    Utils.checkPermission(context, Utils.PHONE_NUMBER_PERMISSION).run {
        if (this) viewModel.updatePermissionState(
            Utils.PHONE_NUMBER_PERMISSION,
            PermissionState.GRANTED
        )
        viewModel._isPhoneNumberPermissionEnabled.value = this
    }
}