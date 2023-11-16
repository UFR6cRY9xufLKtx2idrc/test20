package com.hippo.ehviewer.ui.tools

import androidx.compose.foundation.gestures.draggable2D
import androidx.compose.foundation.gestures.rememberDraggable2DState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.Layout
import com.hippo.ehviewer.Settings
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private const val FULL_CIRCLE: Float = (PI * 2).toFloat()
private const val QUARTER_CIRCLE = PI / 2

private var currentTheta: Float? = null
private var currentOfs: Offset? = null

@Composable
fun CircularLayout(
    modifier: Modifier = Modifier,
    placeFirstItemInCenter: Boolean = false,
    content: @Composable () -> Unit,
) {
    var delta by Settings::favDialogTheta.observed
    val state = rememberDraggable2DState {
        val prevOfs = currentOfs
        val prevTheta = currentTheta
        if (prevOfs != null && prevTheta != null) {
            val current = (prevOfs + it).apply { currentOfs = this }
            val theta = atan2(current.y, current.x)
            if (!theta.isNaN()) {
                currentTheta = theta
                delta += theta - prevTheta
            }
        }
    }
    Layout(
        modifier = modifier.draggable2D(
            state = state,
            onDragStarted = {
                // Get relative offset to center
                val ofs = it - Offset(500f, 500f)
                currentOfs = ofs
                currentTheta = atan2(ofs.y, ofs.x)
            },
            onDragStopped = {
                currentOfs = null
                currentTheta = null
            },
        ),
        content = content,
    ) { measurables, constraints ->
        val radiusPx = constraints.maxWidth * 0.4
        val itemConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = measurables.map { measurable -> measurable.measure(itemConstraints) }
        val outPlaceables = if (placeFirstItemInCenter) placeables.drop(1) else placeables
        val theta = FULL_CIRCLE / outPlaceables.count()

        layout(
            width = constraints.minWidth,
            height = constraints.minHeight,
        ) {
            if (placeFirstItemInCenter) {
                placeables[0].let {
                    val centerOffsetX = constraints.maxWidth / 2 - it.width / 2
                    val centerOffsetY = constraints.maxHeight / 2 - it.height / 2
                    it.place(
                        x = centerOffsetX,
                        y = centerOffsetY,
                    )
                }
            }
            outPlaceables.forEachIndexed { i, it ->
                val centerOffsetX = constraints.maxWidth / 2 - it.width / 2
                val centerOffsetY = constraints.maxHeight / 2 - it.height / 2
                val offsetX = radiusPx * cos(theta * i - QUARTER_CIRCLE + delta) + centerOffsetX
                val offsetY = radiusPx * sin(theta * i - QUARTER_CIRCLE + delta) + centerOffsetY
                it.place(
                    x = offsetX.roundToInt(),
                    y = offsetY.roundToInt(),
                )
            }
        }
    }
}
