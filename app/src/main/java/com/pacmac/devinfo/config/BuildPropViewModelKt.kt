package com.pacmac.devinfo.config

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pacmac.devinfo.export.ExportTask
import com.pacmac.devinfo.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuildPropViewModelKt @Inject constructor() : ViewModel() {

    val EXPORT_FILE_NAME = "build_properties"

    private var _buildProperties: List<Pair<String, String?>> = emptyList()

    private val _filteredBuildProperties = mutableStateOf(_buildProperties)
    val filteredBuildProperties: State<List<Pair<String, String?>>> = _filteredBuildProperties

    init {
        viewModelScope.launch {
            loadBuildProperties()
        }
    }

    private suspend fun loadBuildProperties() {
        _buildProperties = Utils.getBuildPropsList()
        _filteredBuildProperties.value = Utils.getBuildPropsList()
    }


    fun filterProperties(searchTerm: String) {
        _filteredBuildProperties.value = _buildProperties.filter { it.first.contains(searchTerm) }
    }

    fun getSizeOfTheList() = _buildProperties.size


    private var isExporting = false
    private val _onExportDone = MutableSharedFlow<String?>()
    val onExportDone = _onExportDone.asSharedFlow()

    // REFACTOR EXPORT logic
    fun export(context: Context) {
        if (!isExporting) {
            isExporting = true
            ExportTask(context, EXPORT_FILE_NAME) {
                viewModelScope.launch {
                    isExporting = false
                    _onExportDone.emit(it)
                }
            }.execute(this)
        }
    }
}