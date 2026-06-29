package ni.edu.uam.psyconnect.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ni.edu.uam.psyconnect.data.moodjournal.MoodJournalEntry
import ni.edu.uam.psyconnect.ui.viewmodel.MoodJournalViewModel
import ni.edu.uam.psyconnect.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class MoodType(val emoji: String, val label: String, val color: Color) {
    EXCELLENT("🤩", "Increíble", Color(0xFFFACC15)),
    GOOD("😊", "Bien", Color(0xFF4ADE80)),
    NORMAL("😐", "Normal", Color(0xFF60A5FA)),
    SAD("😔", "Triste", Color(0xFFF97316)),
    VERY_BAD("😫", "Mal", Color(0xFFF87171))
}

data class ActivityItem(val emoji: String, val label: String)
val activitiesList = listOf(
    ActivityItem("💼", "Trabajo"),
    ActivityItem("🏃‍♂️", "Deporte"),
    ActivityItem("🍱", "Comida"),
    ActivityItem("🛌", "Descanso"),
    ActivityItem("🤝", "Social"),
    ActivityItem("🎨", "Hobby")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodJournalScreen(
    viewModel: MoodJournalViewModel,
    userId: Long,
    onBack: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var reflection by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf<MoodType?>(null) }
    val selectedActivities = remember { mutableStateListOf<String>() }
    
    var editingEntry by remember { mutableStateOf<MoodJournalEntry?>(null) }
    var viewingEntry by remember { mutableStateOf<MoodJournalEntry?>(null) }
    
    val entries by viewModel.entries.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var filterMood by remember { mutableStateOf<MoodType?>(null) }
    var showOnlyFavorites by remember { mutableStateOf(false) }

    val filteredEntries = remember(entries, searchQuery, filterMood, showOnlyFavorites) {
        entries.filter { entry ->
            val matchesSearch = entry.date.contains(searchQuery, ignoreCase = true) || 
                                (entry.reflection?.contains(searchQuery, ignoreCase = true) == true) ||
                                (entry.activities?.contains(searchQuery, ignoreCase = true) == true)
            val matchesMood = filterMood == null || entry.mood == filterMood?.name
            val matchesFavorite = !showOnlyFavorites || entry.isFavorite
            matchesSearch && matchesMood && matchesFavorite
        }
    }
    
    var isSheetOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    if (viewingEntry != null) {
        AlertDialog(
            onDismissRequest = { viewingEntry = null },
            confirmButton = {
                TextButton(onClick = { viewingEntry = null }) {
                    Text("Cerrar")
                }
            },
            title = {
                val mood = try { MoodType.valueOf(viewingEntry!!.mood) } catch (e: Exception) { MoodType.NORMAL }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(mood.emoji, fontSize = 28.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(viewingEntry!!.date, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (viewingEntry!!.isFavorite) {
                        Spacer(Modifier.weight(1f))
                        Icon(Icons.Default.Favorite, null, tint = Color(0xFFE91E63), modifier = Modifier.size(20.dp))
                    }
                }
            },
            text = {
                Column {
                    if (!viewingEntry!!.activities.isNullOrEmpty()) {
                        Text(
                            text = viewingEntry!!.activities!!.split(",").joinToString(" • "),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                    Text(
                        text = (viewingEntry!!.reflection ?: "").ifEmpty { "Registro rápido de ánimo" },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Mi Diario Emocional", 
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            "Regresar", 
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToHome,
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Inicio") },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToHistory,
                    icon = { Icon(Icons.Default.Favorite, null) },
                    label = { Text("Historial") },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToProfile,
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Perfil") },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        },
        floatingActionButton = {
            if (!isSheetOpen) {
                FloatingActionButton(
                    onClick = { 
                        reflection = ""
                        selectedMood = null
                        selectedActivities.clear()
                        editingEntry = null
                        isSheetOpen = true 
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(bottom = 80.dp)
                ) {
                    Icon(Icons.Default.Add, "Nueva entrada", modifier = Modifier.size(30.dp))
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        
        if (isSheetOpen) {
            ModalBottomSheet(
                onDismissRequest = { isSheetOpen = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                dragHandle = { 
                    BottomSheetDefaults.DragHandle(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    ) 
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 48.dp)
                ) {
                    Text(
                        text = if (editingEntry == null) "¿Cómo va tu día?" else "Editar momento",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold, 
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    
                    Spacer(Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MoodType.entries.forEach { mood ->
                            val isSelected = selectedMood == mood
                            val scale by animateFloatAsState(if (isSelected) 1.2f else 1f, label = "")
                            val bgCircle by animateColorAsState(
                                if (isSelected) mood.color.copy(alpha = 0.2f) else Color.Transparent, 
                                label = ""
                            )
                            
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(bgCircle)
                                    .clickable { selectedMood = mood }
                                    .padding(8.dp)
                                    .scale(scale)
                            ) {
                                Text(mood.emoji, fontSize = 34.sp)
                                Text(
                                    text = mood.label, 
                                    fontSize = 10.sp, 
                                    color = if (isSelected) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(32.dp))
                    Text(
                        "¿Qué has estado haciendo?", 
                        fontSize = 14.sp, 
                        fontWeight = FontWeight.SemiBold, 
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(12.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(activitiesList) { act ->
                            val isSelected = selectedActivities.contains(act.label)
                            FilterChip(
                                selected = isSelected,
                                onClick = { 
                                    if (isSelected) selectedActivities.remove(act.label) 
                                    else selectedActivities.add(act.label) 
                                },
                                label = { Text("${act.emoji} ${act.label}") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    
                    OutlinedTextField(
                        value = reflection,
                        onValueChange = { reflection = it },
                        placeholder = { 
                            Text(
                                "Escribe una breve reflexión...", 
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            ) 
                        },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (selectedMood == null) return@Button
                            if (editingEntry == null) {
                                val entry = MoodJournalEntry(
                                    userId = userId,
                                    mood = selectedMood?.name ?: "NORMAL",
                                    reflection = reflection,
                                    activities = selectedActivities.joinToString(","),
                                    date = SimpleDateFormat("EEEE, d MMMM", Locale("es")).format(Date()).replaceFirstChar { it.uppercase() },
                                    timestamp = System.currentTimeMillis()
                                )
                                viewModel.insertEntry(entry)
                            } else {
                                val updatedEntry = editingEntry!!.copy(
                                    mood = selectedMood?.name ?: "NORMAL",
                                    reflection = reflection,
                                    activities = selectedActivities.joinToString(",")
                                )
                                viewModel.updateEntry(updatedEntry)
                            }
                            isSheetOpen = false
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        enabled = selectedMood != null
                    ) {
                        Text(
                            if (editingEntry == null) "Guardar Momento" else "Actualizar Momento",
                            fontWeight = FontWeight.Bold, 
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "Tu Trayectoria", 
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold, 
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    
                    Spacer(Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Buscar...", fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(20.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, null, modifier = Modifier.size(20.dp))
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    )
                    
                    Spacer(Modifier.height(12.dp))
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = filterMood == null && !showOnlyFavorites,
                                onClick = { 
                                    filterMood = null
                                    showOnlyFavorites = false
                                },
                                label = { Text("Todos") },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = showOnlyFavorites,
                                onClick = { showOnlyFavorites = !showOnlyFavorites },
                                label = { 
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Favorite, 
                                            contentDescription = null, 
                                            modifier = Modifier.size(16.dp),
                                            tint = if (showOnlyFavorites) Color.White else Color(0xFFE91E63)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text("Favoritos") 
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFE91E63),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                        items(MoodType.entries) { mood ->
                            FilterChip(
                                selected = filterMood == mood,
                                onClick = { 
                                    filterMood = if (filterMood == mood) null else mood
                                    showOnlyFavorites = false
                                },
                                label = { Text("${mood.emoji} ${mood.label}") },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }
            }

            if (entries.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 100.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Face, 
                            null, 
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f), 
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Tu diario está vacío.\n¡Cuéntame cómo va tu camino!", 
                            color = MaterialTheme.colorScheme.onSurfaceVariant, 
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }
            } else if (filteredEntries.isEmpty()) {
                item {
                    Text(
                        "No se encontraron resultados",
                        modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            itemsIndexed(filteredEntries) { _, entry ->
                val mood = try { MoodType.valueOf(entry.mood) } catch (e: Exception) { MoodType.NORMAL }
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewingEntry = entry },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .background(mood.color.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(mood.emoji, fontSize = 28.sp)
                        }
                        
                        Spacer(Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                entry.date, 
                                fontSize = 11.sp, 
                                color = MaterialTheme.colorScheme.primary, 
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = (entry.reflection ?: "").ifEmpty { "Registro rápido de ánimo" },
                                fontSize = 15.sp, 
                                color = MaterialTheme.colorScheme.onSurface, 
                                fontWeight = FontWeight.Medium,
                                maxLines = 1
                            )
                            if (!entry.activities.isNullOrEmpty()) {
                                Text(
                                    text = entry.activities?.split(",")?.joinToString(" • ") ?: "",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(
                            onClick = { 
                                viewModel.updateEntry(entry.copy(isFavorite = !entry.isFavorite))
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                if (entry.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorito",
                                tint = if (entry.isFavorite) Color(0xFFE91E63) else MaterialTheme.colorScheme.primary.copy(0.4f),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        
                        IconButton(
                            onClick = { 
                                editingEntry = entry
                                reflection = entry.reflection ?: ""
                                selectedMood = try { MoodType.valueOf(entry.mood) } catch (e: Exception) { null }
                                selectedActivities.clear()
                                if (!entry.activities.isNullOrEmpty()) {
                                    selectedActivities.addAll(entry.activities?.split(",") ?: emptyList())
                                }
                                isSheetOpen = true
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit, 
                                null, 
                                tint = MaterialTheme.colorScheme.primary.copy(0.6f), 
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.deleteEntry(entry) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete, 
                                null, 
                                tint = Color.Red.copy(0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}
