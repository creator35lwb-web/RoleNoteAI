package com.rolenoteai.app.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rolenoteai.app.core.security.AuthenticationManager
import com.rolenoteai.app.domain.model.*
import com.rolenoteai.app.presentation.viewmodel.AuthViewModel
import com.rolenoteai.app.presentation.viewmodel.NoteViewModel
import com.rolenoteai.app.presentation.viewmodel.TemplateViewModel

/**
 * RoleNote AI - Screen Composables
 * CTO: RNA (Claude Code Opus 4.5)
 * Phase 3b: Core Engine - Connected to ViewModels
 */

// ==================== Auth Screen ====================

@Composable
fun AuthScreen(
    onAuthenticated: () -> Unit,
    onSetupRequired: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthenticationManager.AuthState.Unlocked -> onAuthenticated()
            is AuthenticationManager.AuthState.SetupRequired -> onSetupRequired()
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "RoleNote AI",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Enter your PIN to unlock",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = pin,
            onValueChange = {
                if (it.length <= 8 && it.all { c -> c.isDigit() }) {
                    pin = it
                    error = null
                }
            },
            label = { Text("PIN") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            isError = error != null,
            supportingText = error?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val result = viewModel.authenticateWithPin(pin)
                when (result) {
                    is AuthenticationManager.AuthResult.Success -> onAuthenticated()
                    is AuthenticationManager.AuthResult.Failed -> error = result.reason
                    is AuthenticationManager.AuthResult.LockedOut -> error = "Too many attempts. Try again later."
                    else -> {}
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = pin.length >= 4
        ) {
            Text("Unlock")
        }

        if (viewModel.isBiometricAvailable()) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { /* TODO: Biometric auth */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Fingerprint, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Use Biometrics")
            }
        }
    }
}

// ==================== Setup Screen ====================

@Composable
fun SetupScreen(
    onSetupComplete: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Welcome to RoleNote AI",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Set up a PIN to protect your notes",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = pin,
            onValueChange = {
                if (it.length <= 8 && it.all { c -> c.isDigit() }) {
                    pin = it
                    error = null
                }
            },
            label = { Text("Create PIN (4-8 digits)") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPin,
            onValueChange = {
                if (it.length <= 8 && it.all { c -> c.isDigit() }) {
                    confirmPin = it
                    error = null
                }
            },
            label = { Text("Confirm PIN") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            isError = error != null,
            supportingText = error?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                when {
                    pin != confirmPin -> error = "PINs don't match"
                    pin.length < 4 -> error = "PIN must be at least 4 digits"
                    else -> {
                        val result = viewModel.setupPin(pin)
                        when (result) {
                            is AuthenticationManager.AuthResult.Success -> onSetupComplete()
                            is AuthenticationManager.AuthResult.Failed -> error = result.reason
                            else -> {}
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = pin.length >= 4 && confirmPin.length >= 4
        ) {
            Text("Continue")
        }
    }
}

// ==================== Template Select Screen ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateSelectScreen(
    onTemplateSelected: () -> Unit,
    viewModel: TemplateViewModel = hiltViewModel()
) {
    val functionalTemplates by viewModel.functionalTemplates.collectAsState()
    val cSuiteTemplates by viewModel.cSuiteTemplates.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.lastSelectedTemplate) {
        if (uiState.lastSelectedTemplate != null) {
            onTemplateSelected()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Your Role") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "Choose a role template to personalize your note-taking experience",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            // Functional Templates Section
            item {
                Text(
                    text = "Functional Roles",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(functionalTemplates) { template ->
                TemplateCard(
                    template = template,
                    onClick = { viewModel.setActiveTemplate(template.id) },
                    isLoading = uiState.isLoading
                )
            }

            // C-Suite Templates Section
            item {
                Text(
                    text = "C-Suite Roles",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            items(cSuiteTemplates) { template ->
                TemplateCard(
                    template = template,
                    onClick = { viewModel.setActiveTemplate(template.id) },
                    isLoading = uiState.isLoading
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun TemplateCard(
    template: RoleTemplate,
    onClick: () -> Unit,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isLoading) { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color indicator
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(parseColor(template.color)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = template.name.first().toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = template.name,
                    style = MaterialTheme.typography.titleMedium
                )
                template.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ==================== Overview Screen ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(
    onNoteClick: (String) -> Unit,
    onCaptureClick: () -> Unit,
    noteViewModel: NoteViewModel = hiltViewModel(),
    templateViewModel: TemplateViewModel = hiltViewModel()
) {
    val notes by noteViewModel.allNotes.collectAsState()
    val staleTasks by noteViewModel.staleTasks.collectAsState()
    val activeTemplate by templateViewModel.activeTemplate.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Today")
                        activeTemplate?.let {
                            Text(
                                text = it.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Filter/search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCaptureClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { padding ->
        if (notes.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Today,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Your day starts here",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Tap + to capture your first note",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Stale tasks warning
                if (staleTasks.isNotEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${staleTasks.size} tasks need attention",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                // Notes list
                items(notes) { note ->
                    NoteListItem(
                        note = note,
                        onClick = { onNoteClick(note.id) },
                        onComplete = { noteViewModel.completeNote(note.id) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp)) // FAB clearance
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListItem(
    note: Note,
    onClick: () -> Unit,
    onComplete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Signifier with action
            if (note.signifier.isActionable && note.status == NoteStatus.OPEN) {
                IconButton(
                    onClick = onComplete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Text(
                        text = note.signifier.symbol,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Box(
                    modifier = Modifier.size(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = note.signifier.symbol,
                        style = MaterialTheme.typography.titleLarge,
                        color = when (note.status) {
                            NoteStatus.DONE -> MaterialTheme.colorScheme.outline
                            NoteStatus.CANCELLED -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (note.tags.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        items(note.tags.take(3)) { tag ->
                            AssistChip(
                                onClick = {},
                                label = { Text(tag, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.height(24.dp)
                            )
                        }
                    }
                }
            }

            // Migration indicator
            if (note.migrationCount > 0) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.tertiary
                ) {
                    Text("${note.migrationCount}")
                }
            }
        }
    }
}

// ==================== Notes Screen ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    onNoteClick: (String) -> Unit,
    viewModel: NoteViewModel = hiltViewModel()
) {
    val notes by viewModel.allNotes.collectAsState()
    var selectedSignifier by remember { mutableStateOf<Signifier?>(null) }

    val filteredNotes = remember(notes, selectedSignifier) {
        if (selectedSignifier == null) notes
        else notes.filter { it.signifier == selectedSignifier }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Notes") },
                actions = {
                    IconButton(onClick = { /* TODO: Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Signifier filter chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedSignifier == null,
                        onClick = { selectedSignifier = null },
                        label = { Text("All") }
                    )
                }
                items(Signifier.entries.filter { it.isActionable || it == Signifier.NOTE || it == Signifier.IDEA }) { signifier ->
                    FilterChip(
                        selected = selectedSignifier == signifier,
                        onClick = { selectedSignifier = signifier },
                        label = { Text("${signifier.symbol} ${signifier.displayName}") }
                    )
                }
            }

            if (filteredNotes.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No notes yet",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredNotes) { note ->
                        NoteListItem(
                            note = note,
                            onClick = { onNoteClick(note.id) },
                            onComplete = { viewModel.completeNote(note.id) }
                        )
                    }
                }
            }
        }
    }
}

// ==================== Capture Screen ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaptureScreen(
    onNoteSaved: () -> Unit,
    viewModel: NoteViewModel = hiltViewModel(),
    templateViewModel: TemplateViewModel = hiltViewModel()
) {
    var noteContent by remember { mutableStateOf("") }
    var selectedSignifier by remember { mutableStateOf<Signifier?>(null) }
    val uiState by viewModel.uiState.collectAsState()
    val activeTemplate by templateViewModel.activeTemplate.collectAsState()

    LaunchedEffect(uiState.lastCreatedNote) {
        if (uiState.lastCreatedNote != null) {
            onNoteSaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Note") },
                navigationIcon = {
                    IconButton(onClick = onNoteSaved) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            val content = if (selectedSignifier != null) {
                                "${selectedSignifier!!.symbol} $noteContent"
                            } else {
                                noteContent
                            }
                            viewModel.createNote(
                                rawInput = content,
                                roleTemplateId = activeTemplate?.id,
                                roleTemplateName = activeTemplate?.name
                            )
                        },
                        enabled = noteContent.isNotBlank() && !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Save")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Signifier selection
            Text(
                text = "Type",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(Signifier.entries.filter { it.isActionable || it == Signifier.NOTE || it == Signifier.IDEA }) { signifier ->
                    FilterChip(
                        selected = selectedSignifier == signifier,
                        onClick = {
                            selectedSignifier = if (selectedSignifier == signifier) null else signifier
                        },
                        label = { Text("${signifier.symbol} ${signifier.displayName}") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Note content
            OutlinedTextField(
                value = noteContent,
                onValueChange = { noteContent = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                placeholder = { Text("Start typing your note...") },
                supportingText = {
                    if (uiState.error != null) {
                        Text(
                            text = uiState.error!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            // Active template indicator
            activeTemplate?.let { template ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(parseColor(template.color))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Using ${template.name} template",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

// ==================== Chat Screen ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Assistant") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "AI Chat Coming Soon",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Phase 3c: Gemma 3 Integration",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ==================== Settings Screen ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    templateViewModel: TemplateViewModel = hiltViewModel()
) {
    val activeTemplate by templateViewModel.activeTemplate.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Active Role Section
            Text(
                text = "Active Role",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            activeTemplate?.let { template ->
                ListItem(
                    headlineContent = { Text(template.name) },
                    supportingContent = { Text(template.description ?: "") },
                    leadingContent = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(parseColor(template.color)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = template.name.first().toString(),
                                color = Color.White
                            )
                        }
                    }
                )
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Security Section
            Text(
                text = "Security",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            ListItem(
                headlineContent = { Text("Change PIN") },
                leadingContent = { Icon(Icons.Default.Lock, contentDescription = null) }
            )

            ListItem(
                headlineContent = { Text("Biometric Unlock") },
                leadingContent = { Icon(Icons.Default.Fingerprint, contentDescription = null) },
                trailingContent = { Switch(checked = false, onCheckedChange = {}) }
            )

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            Text(
                text = "About",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            ListItem(
                headlineContent = { Text("Version") },
                supportingContent = { Text("1.0.0-alpha (Phase 3b)") },
                leadingContent = { Icon(Icons.Default.Info, contentDescription = null) }
            )

            ListItem(
                headlineContent = { Text("CTO: RNA") },
                supportingContent = { Text("Claude Code Opus 4.5") },
                leadingContent = { Icon(Icons.Default.Code, contentDescription = null) }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Lock App")
            }
        }
    }
}

// ==================== Detail Screens ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    noteId: String,
    onBackClick: () -> Unit,
    viewModel: NoteViewModel = hiltViewModel()
) {
    var note by remember { mutableStateOf<Note?>(null) }
    var isEditing by remember { mutableStateOf(false) }
    var editContent by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(noteId) {
        viewModel.selectNote(noteId)
    }

    val selectedNote by viewModel.selectedNote.collectAsState()

    LaunchedEffect(selectedNote) {
        note = selectedNote
        editContent = selectedNote?.content ?: ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Note") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isEditing) {
                        TextButton(onClick = {
                            note?.let {
                                viewModel.updateNote(it.copy(content = editContent))
                            }
                            isEditing = false
                        }) {
                            Text("Save")
                        }
                    } else {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                }
            )
        }
    ) { padding ->
        note?.let { currentNote ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Signifier and status
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentNote.signifier.symbol,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    AssistChip(
                        onClick = {},
                        label = { Text(currentNote.status.value) }
                    )
                    if (currentNote.migrationCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Badge {
                            Text("Migrated ${currentNote.migrationCount}x")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isEditing) {
                    OutlinedTextField(
                        value = editContent,
                        onValueChange = { editContent = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                } else {
                    Text(
                        text = currentNote.content,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Action buttons
                if (!isEditing && currentNote.status == NoteStatus.OPEN) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.migrateNote(noteId) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Migrate >")
                        }
                        Button(
                            onClick = { viewModel.completeNote(noteId) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Complete")
                        }
                    }
                }
            }
        } ?: run {
            // Loading state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    projectId: String,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Project") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Project ID: $projectId")
            Text("Project detail view coming in Phase 3c")
        }
    }
}

// ==================== Utility Functions ====================

fun parseColor(hexColor: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hexColor))
    } catch (e: Exception) {
        Color(0xFF3B82F6) // Default blue
    }
}
