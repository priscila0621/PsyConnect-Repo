package ni.edu.uam.psyconnect.ui.screens

import android.graphics.Typeface
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import ni.edu.uam.psyconnect.data.moodjournal.MoodJournalEntry
import ni.edu.uam.psyconnect.ui.viewmodel.MoodHistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodHistoryScreen(
    viewModel: MoodHistoryViewModel,
    onBack: () -> Unit
) {
    val entries by viewModel.moodEntries.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Historial de Ánimo", 
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    "Tu Progreso Emocional",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
            }

            // Gráfica de Ánimo
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    MoodChart(entries)
                }
            }

            item {
                Text(
                    "Registros Recientes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (entries.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(top = 32.dp), contentAlignment = Alignment.Center) {
                        Text("No hay registros de ánimo aún.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            items(entries) { entry ->
                MoodHistoryItem(entry)
            }
            
            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun MoodChart(entries: List<MoodJournalEntry>) {
    val primaryColor = MaterialTheme.colorScheme.primary
    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                setTouchEnabled(true)
                setPinchZoom(false)
                setDrawGridBackground(false)
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    textColor = android.graphics.Color.GRAY
                    granularity = 1f
                }
                axisLeft.apply {
                    setDrawGridLines(true)
                    textColor = android.graphics.Color.GRAY
                    axisMinimum = 0f
                    axisMaximum = 6f
                    labelCount = 6
                }
                axisRight.isEnabled = false
                legend.isEnabled = false
                animateX(1000)
            }
        },
        update = { chart ->
            if (entries.isEmpty()) return@AndroidView

            // Mapeo de ánimo a valores numéricos (invertido para que lo mejor esté arriba)
            val chartEntries = entries.reversed().mapIndexed { index, moodEntry ->
                val value = when (moodEntry.mood) {
                    "EXCELLENT" -> 5f
                    "GOOD" -> 4f
                    "NORMAL" -> 3f
                    "SAD" -> 2f
                    "VERY_BAD" -> 1f
                    else -> 3f // Default a normal
                }
                Entry(index.toFloat(), value)
            }

            val dataSet = LineDataSet(chartEntries, "Estado emocional").apply {
                color = primaryColor.toArgb()
                setCircleColor(primaryColor.toArgb())
                lineWidth = 3f
                circleRadius = 5f
                setDrawCircleHole(false)
                valueTextSize = 0f
                setDrawFilled(true)
                fillColor = primaryColor.toArgb()
                fillAlpha = 50
                mode = LineDataSet.Mode.CUBIC_BEZIER
            }

            chart.data = LineData(dataSet)
            chart.invalidate()
        }
    )
}

@Composable
fun MoodHistoryItem(entry: MoodJournalEntry) {
    val moodType = try { MoodType.valueOf(entry.mood) } catch (e: Exception) { MoodType.NORMAL }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .background(moodType.color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(moodType.emoji, fontSize = 24.sp)
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.date,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = (entry.reflection ?: "").ifEmpty { "Sin reflexión guardada" },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 2
                )
            }
        }
    }
}
