package com.pacmac.devinfo.config

import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.pacmac.devinfo.R
import com.pacmac.devinfo.export.ui.ExportActivity
import com.pacmac.devinfo.export.ExportUtils
import com.pacmac.devinfo.ui.components.InfoListView
import com.pacmac.devinfo.ui.components.SearchBar
import com.pacmac.devinfo.ui.components.TopBar
import com.pacmac.devinfo.ui.theme.DeviceInfoTheme
import com.pacmac.devinfo.ui.theme.color_title_bg
import com.pacmac.devinfo.utils.Utils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildPropertiesScreen(viewModel: BuildPropViewModelKt = hiltViewModel()) {

    var searchTerm by rememberSaveable { mutableStateOf("") }
    var enableSearch by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current

    LaunchedEffect(key1 = Unit) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            launch {
                viewModel.onExportDone.collectLatest { filePath ->
                    filePath?.let {
                        val intent = Intent(context, ExportActivity::class.java)
                        intent.putExtra(ExportUtils.EXPORT_FILE, filePath)
                        context.startActivity(intent)
                    }
                }
            }
        }
    }

    DeviceInfoTheme {
        Scaffold(topBar = {
            if (enableSearch.not()) {
                TopBar(title = stringResource(id = R.string.title_activity_build_properties),
                    exportVisible = true,
                    actionButton = {
                        IconButton(onClick = { enableSearch = true }) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = stringResource(id = R.string.action_search)
                            )
                        }
                    },
                    onExportClick = { viewModel.export(context) }
                )
            } else {
                SearchBar(
                    searchTerm = searchTerm,
                    onNewSearchTerm = {
                        searchTerm = it
                        viewModel.filterProperties(it)
                    },
                    onHideSearch = {
                        enableSearch = false
                    }
                )
            }
        }) {
            val modifier = Modifier.padding(it)
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = modifier.fillMaxSize(),
            ) {
                InfoListView(
                    modifier = Modifier.weight(1f), data = Utils.getUIObjectsFromBuildProps(
                        LocalContext.current, viewModel.filteredBuildProperties.value
                    )
                )
                PropCounter(
                    viewModel.filteredBuildProperties.value.size,
                    viewModel.getSizeOfTheList(),
                    Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun PropCounter(filtered: Int, paramCount: Int, modifier: Modifier = Modifier) {
    Surface(color = color_title_bg) {
        Text(
            text = "$filtered / $paramCount",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(vertical = 2.dp, horizontal = 8.dp)
                .fillMaxWidth()
        )
    }
}


@Preview(widthDp = 400, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewPropCounter() {
    BuildPropertiesScreen()
}