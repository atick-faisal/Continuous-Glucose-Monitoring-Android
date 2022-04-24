package dev.atick.compose.ui.home.components

import ai.atick.material.MaterialColor
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GlucoseCard(glucose: Float) {
    return Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = if (isSystemInDarkTheme()) 0.dp else 2.dp,
        shape = RoundedCornerShape(16.dp),
        backgroundColor = when (glucose) {
            in 0.0F..70.0F -> MaterialColor.Red400
            in 70.0F..126.0F -> MaterialColor.Blue400
            in 126.0F..180.0F -> MaterialColor.Teal400
            in 126.0F..180.0F -> MaterialColor.Indigo400
            in 180.0F..248.0F -> MaterialColor.Teal400
            in 248.0F..999.0F -> MaterialColor.Yellow400
            else -> MaterialColor.Red400
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Glucose",
                fontSize = 24.sp,
                color = MaterialTheme.colors.surface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$glucose mg/dL",
                fontSize = 32.sp,
                color = MaterialTheme.colors.surface
            )
        }
    }
}