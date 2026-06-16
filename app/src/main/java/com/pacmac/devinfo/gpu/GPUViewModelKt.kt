package com.pacmac.devinfo.gpu

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.opengl.EGL14
import android.opengl.GLES20
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import com.pacmac.devinfo.ListType
import com.pacmac.devinfo.R
import com.pacmac.devinfo.ThreeState
import com.pacmac.devinfo.UIObject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject

private const val TAG = "GPUViewModel"

@HiltViewModel
class GPUViewModelKt @Inject constructor(
    @ApplicationContext private val context: Context,
    private val packageManager: PackageManager
) : ViewModel() {

    val EXPORT_FILE_NAME = "gpu_info"

    private val _gpuInfo = MutableStateFlow<List<UIObject>>(emptyList())
    val gpuInfo: StateFlow<List<UIObject>> = _gpuInfo.asStateFlow()

    init {
        loadGPUInfo()
    }

    fun getGpuInfoForExport(context: Context): List<UIObject> = buildList {
        add(UIObject(context.getString(R.string.title_activity_gpu_info), "", ListType.TITLE))
        add(UIObject(context.getString(R.string.param), context.getString(R.string.value), ListType.TITLE))
        addAll(gpuInfo.value)
    }

    private fun loadGPUInfo() {
        val list = mutableListOf<UIObject>()

        // OpenGL ES version from ActivityManager
        try {
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val configInfo = activityManager.deviceConfigurationInfo
            list.add(
                UIObject(
                    context.getString(R.string.gpu_gl_version),
                    configInfo.glEsVersion ?: "N/A"
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get OpenGL ES version from ActivityManager", e)
            list.add(UIObject(context.getString(R.string.gpu_gl_version), "Not Available"))
        }

        // Vulkan version from system features
        list.add(getVulkanVersion())

        // EGL-based GPU queries
        loadEGLInfo(list)

        // GPU Headroom (API 36+)
        list.add(getGpuHeadroom())

        // AGSL Shader support (API 33+ RuntimeShader)
        list.add(getAgslSupport())

        _gpuInfo.value = list
    }

    private fun getVulkanVersion(): UIObject {
        val features = packageManager.systemAvailableFeatures
        val vulkanFeature = features?.firstOrNull { it.name == "android.hardware.vulkan.version" }
        return if (vulkanFeature != null) {
            val version = vulkanFeature.version
            val major = version shr 22
            val minor = (version shr 12) and 0x3FF
            val patch = version and 0xFFF
            UIObject(
                context.getString(R.string.gpu_vulkan_version),
                String.format(Locale.US, "%d.%d.%d", major, minor, patch)
            )
        } else {
            UIObject(context.getString(R.string.gpu_vulkan_version), "Not Supported")
        }
    }

    private fun loadEGLInfo(list: MutableList<UIObject>) {
        var eglDisplay = EGL14.EGL_NO_DISPLAY
        var eglContext = EGL14.EGL_NO_CONTEXT
        var eglSurface = EGL14.EGL_NO_SURFACE

        try {
            // 1. Get display
            eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
            if (eglDisplay == EGL14.EGL_NO_DISPLAY) {
                addNotAvailableGLInfo(list)
                return
            }

            // 2. Initialize EGL
            val majorVersion = IntArray(1)
            val minorVersion = IntArray(1)
            if (!EGL14.eglInitialize(eglDisplay, majorVersion, 0, minorVersion, 0)) {
                addNotAvailableGLInfo(list)
                return
            }

            // 3. Choose config
            val configAttribs = intArrayOf(
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
                EGL14.EGL_NONE
            )
            val configs = arrayOfNulls<android.opengl.EGLConfig>(1)
            val numConfigs = IntArray(1)
            if (!EGL14.eglChooseConfig(
                    eglDisplay, configAttribs, 0,
                    configs, 0, 1, numConfigs, 0
                ) || numConfigs[0] == 0
            ) {
                addNotAvailableGLInfo(list)
                EGL14.eglTerminate(eglDisplay)
                return
            }

            // 4. Create context
            val contextAttribs = intArrayOf(
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE
            )
            eglContext = EGL14.eglCreateContext(
                eglDisplay, configs[0]!!, EGL14.EGL_NO_CONTEXT,
                contextAttribs, 0
            )
            if (eglContext == EGL14.EGL_NO_CONTEXT) {
                addNotAvailableGLInfo(list)
                EGL14.eglTerminate(eglDisplay)
                return
            }

            // 5. Create 1x1 PBuffer surface
            val surfaceAttribs = intArrayOf(
                EGL14.EGL_WIDTH, 1,
                EGL14.EGL_HEIGHT, 1,
                EGL14.EGL_NONE
            )
            eglSurface = EGL14.eglCreatePbufferSurface(
                eglDisplay, configs[0]!!, surfaceAttribs, 0
            )
            if (eglSurface == EGL14.EGL_NO_SURFACE) {
                addNotAvailableGLInfo(list)
                EGL14.eglDestroyContext(eglDisplay, eglContext)
                EGL14.eglTerminate(eglDisplay)
                return
            }

            // 6. Make current
            if (!EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
                addNotAvailableGLInfo(list)
                EGL14.eglDestroySurface(eglDisplay, eglSurface)
                EGL14.eglDestroyContext(eglDisplay, eglContext)
                EGL14.eglTerminate(eglDisplay)
                return
            }

            // 7. Query GL strings
            val renderer = GLES20.glGetString(GLES20.GL_RENDERER) ?: "N/A"
            val vendor = GLES20.glGetString(GLES20.GL_VENDOR) ?: "N/A"
            val glVersion = GLES20.glGetString(GLES20.GL_VERSION) ?: "N/A"
            val extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS) ?: "N/A"

            list.add(UIObject(context.getString(R.string.gpu_renderer), renderer))
            list.add(UIObject(context.getString(R.string.gpu_vendor), vendor))

            // 8. Max texture size
            val maxTextureSizeBuf = IntArray(1)
            GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTextureSizeBuf, 0)
            list.add(
                UIObject(
                    context.getString(R.string.gpu_max_texture_size),
                    "${maxTextureSizeBuf[0]} x ${maxTextureSizeBuf[0]}",
                    "px"
                )
            )

            // 9. Shader precision
            list.add(
                UIObject(
                    context.getString(R.string.gpu_shader_precision),
                    "",
                    ListType.TITLE
                )
            )
            addShaderPrecision(list, GLES20.GL_VERTEX_SHADER, GLES20.GL_HIGH_FLOAT,
                context.getString(R.string.gpu_vertex_high))
            addShaderPrecision(list, GLES20.GL_VERTEX_SHADER, GLES20.GL_MEDIUM_FLOAT,
                context.getString(R.string.gpu_vertex_medium))
            addShaderPrecision(list, GLES20.GL_VERTEX_SHADER, GLES20.GL_LOW_FLOAT,
                context.getString(R.string.gpu_vertex_low))
            addShaderPrecision(list, GLES20.GL_FRAGMENT_SHADER, GLES20.GL_HIGH_FLOAT,
                context.getString(R.string.gpu_fragment_high))
            addShaderPrecision(list, GLES20.GL_FRAGMENT_SHADER, GLES20.GL_MEDIUM_FLOAT,
                context.getString(R.string.gpu_fragment_medium))
            addShaderPrecision(list, GLES20.GL_FRAGMENT_SHADER, GLES20.GL_LOW_FLOAT,
                context.getString(R.string.gpu_fragment_low))

            // 10. Compressed texture formats
            val numFormatsBuf = IntArray(1)
            GLES20.glGetIntegerv(GLES20.GL_NUM_COMPRESSED_TEXTURE_FORMATS, numFormatsBuf, 0)
            val numFormats = numFormatsBuf[0]
            if (numFormats > 0) {
                val formatsBuf = IntArray(numFormats)
                GLES20.glGetIntegerv(GLES20.GL_COMPRESSED_TEXTURE_FORMATS, formatsBuf, 0)
                val formatsStr = formatsBuf.joinToString(", ") {
                    String.format(Locale.US, "0x%04X", it)
                }
                list.add(
                    UIObject(
                        context.getString(R.string.gpu_compressed_formats),
                        formatsStr
                    )
                )
            } else {
                list.add(
                    UIObject(
                        context.getString(R.string.gpu_compressed_formats),
                        "None"
                    )
                )
            }

            // GL extensions (last, since it can be long)
            list.add(UIObject(context.getString(R.string.gpu_extensions), extensions))

        } catch (e: Exception) {
            Log.e(TAG, "Failed to query GPU info via EGL", e)
            addNotAvailableGLInfo(list)
        } finally {
            // Cleanup EGL resources
            try {
                if (eglDisplay != EGL14.EGL_NO_DISPLAY) {
                    EGL14.eglMakeCurrent(
                        eglDisplay,
                        EGL14.EGL_NO_SURFACE,
                        EGL14.EGL_NO_SURFACE,
                        EGL14.EGL_NO_CONTEXT
                    )
                    if (eglSurface != EGL14.EGL_NO_SURFACE) {
                        EGL14.eglDestroySurface(eglDisplay, eglSurface)
                    }
                    if (eglContext != EGL14.EGL_NO_CONTEXT) {
                        EGL14.eglDestroyContext(eglDisplay, eglContext)
                    }
                    EGL14.eglTerminate(eglDisplay)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to cleanup EGL", e)
            }
        }
    }

    private fun addShaderPrecision(
        list: MutableList<UIObject>,
        shaderType: Int,
        precisionType: Int,
        label: String
    ) {
        try {
            val range = IntArray(2)
            val precision = IntArray(1)
            GLES20.glGetShaderPrecisionFormat(shaderType, precisionType, range, 0, precision, 0)
            list.add(
                UIObject(
                    label,
                    String.format(Locale.US, "range: [%d, %d], precision: %d",
                        range[0], range[1], precision[0])
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get shader precision for $label", e)
            list.add(UIObject(label, "Not Available"))
        }
    }

    @android.annotation.SuppressLint("WrongConstant")
    private fun getGpuHeadroom(): UIObject {
        if (Build.VERSION.SDK_INT >= 36) {
            return try {
                val perfHintManager = context.getSystemService(Context.PERFORMANCE_HINT_SERVICE)
                        as? android.os.PerformanceHintManager
                if (perfHintManager != null) {
                    val method = perfHintManager.javaClass.getMethod("getGpuHeadroom")
                    val headroom = method.invoke(perfHintManager) as Float
                    if (headroom.isNaN() || headroom < 0) {
                        UIObject(context.getString(R.string.gpu_headroom), "N/A")
                    } else {
                        UIObject(
                            context.getString(R.string.gpu_headroom),
                            String.format(Locale.US, "%.1f%%", headroom * 100)
                        )
                    }
                } else {
                    UIObject(context.getString(R.string.gpu_headroom), "N/A")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get GPU headroom", e)
                UIObject(context.getString(R.string.gpu_headroom), "N/A")
            }
        }
        return UIObject(context.getString(R.string.gpu_headroom), "N/A")
    }

    private fun getAgslSupport(): UIObject {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return try {
                Class.forName("android.graphics.RuntimeShader")
                UIObject(
                    context.getString(R.string.gpu_agsl_shaders),
                    ThreeState.YES,
                    ListType.ICON
                )
            } catch (e: ClassNotFoundException) {
                UIObject(
                    context.getString(R.string.gpu_agsl_shaders),
                    ThreeState.NO,
                    ListType.ICON
                )
            }
        }
        return UIObject(context.getString(R.string.gpu_agsl_shaders), "N/A")
    }

    private fun addNotAvailableGLInfo(list: MutableList<UIObject>) {
        list.add(UIObject(context.getString(R.string.gpu_renderer), "Not Available"))
        list.add(UIObject(context.getString(R.string.gpu_vendor), "Not Available"))
        list.add(UIObject(context.getString(R.string.gpu_max_texture_size), "Not Available"))
        list.add(UIObject(context.getString(R.string.gpu_extensions), "Not Available"))
        list.add(UIObject(context.getString(R.string.gpu_compressed_formats), "Not Available"))
    }
}
