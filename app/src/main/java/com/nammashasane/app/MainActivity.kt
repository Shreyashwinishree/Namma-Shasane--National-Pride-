package com.nammashasane.app

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nammashasane.app.data.DecodeResult
import com.nammashasane.app.data.InscriptionEntity
import kotlin.math.max
import kotlin.math.min
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.activity.viewModels

class MainActivity : ComponentActivity() {
    private val viewModel: HeritageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NammaShasaneTheme {
                NammaShasaneApp(viewModel)
            }
        }
    }
}

private enum class Screen(val label: String, val icon: ImageVector) {
    Discover("Discover", Icons.Default.Map),
    Decode("Decode", Icons.Default.CameraAlt),
    Trails("Trails", Icons.Default.Directions),
    Offline("Offline", Icons.Default.CloudDone)
}

@Composable
private fun NammaShasaneApp(viewModel: HeritageViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val reports by viewModel.reports.collectAsStateWithLifecycle()
    var signedIn by rememberSaveable { mutableStateOf(false) }
    var screen by rememberSaveable { mutableStateOf(Screen.Discover) }
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    val selected = uiState.inscriptions.firstOrNull { it.id == selectedId }

    if (!signedIn) {
        AuthScreen(onContinue = { signedIn = true })
        return
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = selected?.title ?: "Namma Shasane",
                showBack = selected != null,
                onBack = { selectedId = null }
            )
        },
        bottomBar = {
            if (selected == null) {
                NavigationBar(containerColor = Sand) {
                    Screen.entries.forEach { item ->
                        NavigationBarItem(
                            selected = screen == item,
                            onClick = { screen = item },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label, maxLines = 1) }
                        )
                    }
                }
            }
        },
        containerColor = Parchment
    ) { padding ->
        Box(Modifier.padding(padding)) {
            if (selected != null) {
                StoryScreen(
                    inscription = selected,
                    onReport = { severity, note -> viewModel.queueReport(selected, severity, note) }
                )
            } else {
                when (screen) {
                    Screen.Discover -> DiscoverScreen(uiState.inscriptions, onSelect = { selectedId = it.id })
                    Screen.Decode -> DecodeScreen(onDecode = viewModel::decodeImage)
                    Screen.Trails -> TrailsScreen(uiState.inscriptions, onSelect = { selectedId = it.id })
                    Screen.Offline -> OfflineScreen(uiState.inscriptions.size, reports.size)
                }
            }
        }
    }
}

@Composable
private fun AuthScreen(onContinue: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Parchment
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.AccountCircle,
                contentDescription = null,
                tint = Terracotta,
                modifier = Modifier.size(56.dp)
            )
            Spacer(Modifier.height(18.dp))
            Text(
                "Namma Shasane",
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = DeepBrown
            )
            Text(
                "Decode Karnataka's stone inscriptions, preserve local history, and report heritage risks from the field.",
                style = MaterialTheme.typography.bodyLarge,
                color = DeepBrown.copy(alpha = 0.78f),
                modifier = Modifier.padding(top = 10.dp, bottom = 24.dp)
            )
            Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
                Text("Continue as guest")
            }
            OutlinedButton(onClick = onContinue, modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                Text("Sign in later")
            }
        }
    }
}

@Composable
private fun AppTopBar(title: String, showBack: Boolean, onBack: () -> Unit) {
    Surface(color = Sand, tonalElevation = 3.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBack) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            } else {
                Icon(Icons.Default.Home, contentDescription = null, tint = Terracotta, modifier = Modifier.padding(12.dp))
            }
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = DeepBrown,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DiscoverScreen(inscriptions: List<InscriptionEntity>, onSelect: (InscriptionEntity) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var dynasty by rememberSaveable { mutableStateOf("All") }
    val dynastyOptions = listOf("All") + inscriptions.map { it.dynasty }.distinct().sorted()
    val filtered = inscriptions.filter {
        (dynasty == "All" || it.dynasty == dynasty) &&
            listOf(it.title, it.district, it.dynasty, it.script).any { value ->
                value.contains(query, ignoreCase = true)
            }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                label = { Text("Search inscription, district, dynasty") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                dynastyOptions.take(3).forEach { option ->
                    FilterChip(
                        selected = dynasty == option,
                        onClick = { dynasty = option },
                        label = { Text(option, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    )
                }
            }
        }
        item {
            HeritageMap(filtered, onSelect)
        }
        items(filtered, key = { it.id }) { inscription ->
            InscriptionCard(inscription, onClick = { onSelect(inscription) })
        }
    }
}

@Composable
private fun HeritageMap(inscriptions: List<InscriptionEntity>, onSelect: (InscriptionEntity) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.7f)
            .background(
                Brush.linearGradient(listOf(Color(0xFFBFD4B2), Color(0xFFD8C18F), Color(0xFFA8C0D9))),
                RoundedCornerShape(8.dp)
            )
            .border(1.dp, DeepBrown.copy(alpha = 0.18f), RoundedCornerShape(8.dp))
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val path = Path().apply {
                moveTo(size.width * 0.20f, size.height * 0.18f)
                cubicTo(size.width * 0.45f, size.height * 0.05f, size.width * 0.72f, size.height * 0.22f, size.width * 0.82f, size.height * 0.46f)
                cubicTo(size.width * 0.91f, size.height * 0.70f, size.width * 0.56f, size.height * 0.93f, size.width * 0.31f, size.height * 0.77f)
                cubicTo(size.width * 0.12f, size.height * 0.64f, size.width * 0.06f, size.height * 0.32f, size.width * 0.20f, size.height * 0.18f)
            }
            drawPath(path, color = Color(0xFFEADAA9).copy(alpha = 0.65f))
        }
        Text(
            "Karnataka heritage map",
            modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = DeepBrown
        )
        inscriptions.forEach { item ->
            val x = mapRange(item.longitude, 74.0, 78.5)
            val y = 1f - mapRange(item.latitude, 11.5, 16.0)
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = (x * 280).dp, top = (y * 130 + 34).dp)
                    .size(if (item.isEndangered) 22.dp else 18.dp)
                    .background(if (item.isEndangered) AlertRed else Terracotta, CircleShape)
                    .border(2.dp, Color.White, CircleShape)
                    .clickable { onSelect(item) }
            )
        }
    }
}

private fun mapRange(value: Double, minValue: Double, maxValue: Double): Float {
    val normalized = (value - minValue) / (maxValue - minValue)
    return min(1.0, max(0.0, normalized)).toFloat()
}

@Composable
private fun InscriptionCard(inscription: InscriptionEntity, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Sand)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, contentDescription = null, tint = Terracotta)
                Text(
                    inscription.title,
                    modifier = Modifier.padding(start = 8.dp).weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepBrown
                )
            }
            Text("${inscription.district} • ${inscription.dynasty} • ${inscription.era}", color = DeepBrown.copy(alpha = 0.72f))
            Text(inscription.summary, modifier = Modifier.padding(top = 8.dp), color = DeepBrown)
            if (inscription.isEndangered) {
                AssistChip(
                    onClick = onClick,
                    label = { Text("Preservation alert") },
                    leadingIcon = { Icon(Icons.Default.ReportProblem, contentDescription = null) },
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StoryScreen(inscription: InscriptionEntity, onReport: (String, String) -> Unit) {
    var note by rememberSaveable { mutableStateOf("") }
    var severity by rememberSaveable { mutableStateOf(if (inscription.isEndangered) "High" else "Medium") }
    var submitted by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Stone)) {
                Column(Modifier.padding(18.dp)) {
                    Text(inscription.era, color = Sand, style = MaterialTheme.typography.labelLarge)
                    Text(
                        inscription.title,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                    Text(inscription.summary, color = Sand, modifier = Modifier.padding(top = 12.dp))
                }
            }
        }
        item { DetailBlock("Modern reading", inscription.translation) }
        item { DetailBlock("Script and type", "${inscription.script} • ${inscription.inscriptionType}") }
        item { DetailBlock("Condition", inscription.condition) }
        item {
            Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Sand)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Report damage", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Low", "Medium", "High").forEach {
                            FilterChip(selected = severity == it, onClick = { severity = it }, label = { Text(it) })
                        }
                    }
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Damage notes") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            onReport(severity, note)
                            submitted = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                        Text("Queue preservation alert", modifier = Modifier.padding(start = 8.dp))
                    }
                    if (submitted) Text("Saved offline and ready to sync.", color = SuccessGreen)
                }
            }
        }
    }
}

@Composable
private fun DetailBlock(title: String, body: String) {
    Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Sand)) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DeepBrown)
            Text(body, modifier = Modifier.padding(top = 6.dp), color = DeepBrown.copy(alpha = 0.82f))
        }
    }
}

@Composable
private fun DecodeScreen(onDecode: (String?) -> DecodeResult) {
    var pickedImage by rememberSaveable { mutableStateOf<Uri?>(null) }
    var result by remember { mutableStateOf<DecodeResult?>(null) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        pickedImage = uri
        result = onDecode(uri?.lastPathSegment)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Sand)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("AI inscription decoder", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = DeepBrown)
                Text("Pick a stone inscription photo. The demo engine returns a structured reading while keeping the Gemini and ML Kit integration point clean.", color = DeepBrown.copy(alpha = 0.75f))
                Button(onClick = { picker.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Text("Choose photo", modifier = Modifier.padding(start = 8.dp))
                }
                OutlinedButton(
                    onClick = { result = onDecode(null) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Translate, contentDescription = null)
                    Text("Run demo decode", modifier = Modifier.padding(start = 8.dp))
                }
                pickedImage?.let { Text("Selected: ${it.lastPathSegment}", color = DeepBrown.copy(alpha = 0.72f)) }
            }
        }
        result?.let {
            DetailBlock("Detected script", "${it.detectedScript} (${it.confidence})")
            DetailBlock("Modern Kannada", it.modernKannada)
            DetailBlock("Historical context", it.historicalContext)
        }
    }
}

@Composable
private fun TrailsScreen(inscriptions: List<InscriptionEntity>, onSelect: (InscriptionEntity) -> Unit) {
    val endangered = inscriptions.filter { it.isEndangered }
    val westernGanga = inscriptions.filter { it.dynasty.contains("Ganga", ignoreCase = true) }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { TrailCard("Endangered inscription patrol", "Prioritize places needing field documentation.", endangered, onSelect) }
        item { TrailCard("Western Ganga memory route", "Connect early Kannada records around Bengaluru and Hassan.", westernGanga, onSelect) }
        item { TrailCard("Five-site MVP trail", "A compact route using every seeded inscription.", inscriptions, onSelect) }
    }
}

@Composable
private fun TrailCard(
    title: String,
    subtitle: String,
    inscriptions: List<InscriptionEntity>,
    onSelect: (InscriptionEntity) -> Unit
) {
    Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Sand)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DeepBrown)
            Text(subtitle, color = DeepBrown.copy(alpha = 0.74f))
            inscriptions.forEachIndexed { index, inscription ->
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onSelect(inscription) }.padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier.size(28.dp).background(Terracotta, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${index + 1}", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Column(Modifier.padding(start = 10.dp)) {
                        Text(inscription.title, color = DeepBrown, fontWeight = FontWeight.SemiBold)
                        Text(inscription.district, color = DeepBrown.copy(alpha = 0.68f))
                    }
                }
            }
        }
    }
}

@Composable
private fun OfflineScreen(inscriptionCount: Int, reportCount: Int) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DetailBlock("Offline database", "$inscriptionCount inscriptions are available from Room storage after first launch.")
        DetailBlock("Queued alerts", "$reportCount preservation reports are stored locally for future sync.")
        DetailBlock("Production sync path", "Connect Firebase Authentication, Firestore, Storage, and Cloud Messaging behind the repository without changing the UI screens.")
    }
}

@Composable
private fun NammaShasaneTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = androidx.compose.material3.lightColorScheme(
            primary = Terracotta,
            secondary = DeepBrown,
            background = Parchment,
            surface = Sand,
            error = AlertRed
        ),
        typography = MaterialTheme.typography,
        content = content
    )
}

private val Parchment = Color(0xFFF7EBCD)
private val Sand = Color(0xFFE9D3A2)
private val Terracotta = Color(0xFF9C4D2E)
private val DeepBrown = Color(0xFF39251A)
private val Stone = Color(0xFF5F5146)
private val AlertRed = Color(0xFFB33A2E)
private val SuccessGreen = Color(0xFF2E7D51)
