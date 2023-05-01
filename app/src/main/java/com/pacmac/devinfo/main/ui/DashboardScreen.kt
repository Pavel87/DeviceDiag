package com.pacmac.devinfo.main

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pacmac.devinfo.R
import com.pacmac.devinfo.main.model.DashItem
import com.pacmac.devinfo.main.model.DashModel
import com.pacmac.devinfo.main.model.PermissionState
import com.pacmac.devinfo.storage.StorageInfoKt
import com.pacmac.devinfo.ui.components.AdvertView
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme
import com.pacmac.devinfo.ui.theme.tileEnd
import com.pacmac.devinfo.ui.theme.tileStart
import com.pacmac.devinfo.utils.Utils
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    viewModel: MainViewModelKt = hiltViewModel(),
    windowWidthSizeClass: WindowWidthSizeClass
) {

    val tiles = viewModel.getDashboarItems()
    val gridState = rememberLazyGridState()

    val context = LocalContext.current

    checkAllPermissions(viewModel, context)

    val (gridColumns, tileSpacing) = when (windowWidthSizeClass) {
        WindowWidthSizeClass.Expanded -> GridCells.Fixed(4) to 12.dp
        WindowWidthSizeClass.Medium -> GridCells.Fixed(3) to 12.dp
        WindowWidthSizeClass.Compact -> GridCells.Adaptive(150.dp) to 8.dp
        else -> GridCells.Adaptive(150.dp) to 8.dp
    }

    Column() {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp, vertical = 8.dp),


            state = gridState,
            columns = gridColumns,
            verticalArrangement = Arrangement.spacedBy(tileSpacing),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tiles) { tile ->

                TileView(tile = tile)

//                Box(modifier = Modifier
//                    .background(MaterialTheme.colorScheme.primary)
//                    .clip(MaterialTheme.shapes.small.copy(all = CornerSize(8.dp)))
//                    .clickable {
//                        scope.launch {
//                            tile.onclick.invoke(tile)
//                        }
//                    }
//                    .height(120.dp),
//                    contentAlignment = Alignment.Center) {
//
//                    Image(
//                        painter = painterResource(id = tile.imageRes),
//                        contentDescription = tile.dashItem.name,
//                        contentScale = ContentScale.FillWidth,
//                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
//                        modifier = Modifier.width(150.dp)
//                    )
//                }
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

@Composable
fun TileView(tile: DashModel) {
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.tileStart(),
                        MaterialTheme.colorScheme.tileEnd()
                    )
                ),
                shape = MaterialTheme.shapes.small.copy(all = CornerSize(8.dp))
            )
            .height(120.dp)
            .clickable {
                scope.launch {
                    tile.onclick.invoke(tile)
                }
            },
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = tile.imageRes),
            contentDescription = tile.dashItem.name,
            contentScale = ContentScale.FillWidth,
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
            modifier = Modifier.width(150.dp)
        )

        Text(
            text = tile.title,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewTileView() {
    DeviceInfoTheme {

        val tile = DashModel(
            DashItem.RAM_SCREEN, R.drawable.ram_img_2, "RAM", StorageInfoKt::class.java
        ) { }

        TileView(tile = tile)
    }

}