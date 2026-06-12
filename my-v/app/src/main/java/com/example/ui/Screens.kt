@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.RecordingEntity
import com.example.ui.theme.*

@Composable
fun AppNavigationWrapper(viewModel: RecordingViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
            when (screen) {
                is AppScreen.Login -> LoginScreen(onLoginSuccess = { viewModel.navigateTo(AppScreen.Home) })
                is AppScreen.Home -> HomeDashboardScreen(viewModel)
                is AppScreen.RecordSelection -> ScriptSelectionScreen(viewModel)
                is AppScreen.QuickRecording -> QuickRecordingScreen(viewModel, screen.script)
                is AppScreen.ScriptPrompter -> ScriptPrompterScreen(viewModel, screen.script)
                is AppScreen.RecordingResult -> RecordingResultScreen(viewModel, screen.recording)
                is AppScreen.AnalysisReport -> VoiceAnalysisReportScreen(viewModel, screen.recording)
                is AppScreen.SavedLibrary -> SavedLibraryScreen(viewModel)
                is AppScreen.Community -> CommunityScreen(viewModel)
                is AppScreen.Settings -> ProfileSettingsScreen(viewModel)
                is AppScreen.Notifications -> NotificationsScreen(viewModel)
            }
        }
    }
}

// SCREEN 1: LOGIN (Welcome Screen)
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Logo Icon",
                    tint = PureWhite,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "My V",
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Welcome to My V",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceText,
                modifier = Modifier.padding(top = 24.dp)
            )

            Text(
                text = "The most intelligent way to record and analyze your daily conversations.",
                fontSize = 15.sp,
                color = OnSurfaceVariantText,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, start = 20.dp, end = 20.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onLoginSuccess,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("kakao_login_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE500)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Login with Kakao", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Button(
                onClick = onLoginSuccess,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(width = 1.dp, color = OutlineVariant, shape = RoundedCornerShape(14.dp))
                    .testTag("google_login_button"),
                colors = ButtonDefaults.buttonColors(containerColor = PureWhite),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Login with Google", color = OnSurfaceText, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            TextButton(
                onClick = onLoginSuccess,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Log in using another email", color = OutlineBorder, fontWeight = FontWeight.Medium, fontSize = 13.sp)
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Terms of Service", fontSize = 11.sp, color = OutlineBorder)
                Box(modifier = Modifier.size(3.dp).background(OutlineVariant, CircleShape))
                Text("Privacy Policy", fontSize = 11.sp, color = OutlineBorder)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("POWERED BY V-STUDIO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OutlineBorder, letterSpacing = 1.sp)
        }
    }
}

// SCREEN 3: HOME DASHBOARD
@Composable
fun HomeDashboardScreen(viewModel: RecordingViewModel) {
    val recordingsList by viewModel.recordings.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(modifier = Modifier.size(38.dp).clip(CircleShape).background(ElectricBlueContainer)) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Avatar",
                            modifier = Modifier.align(Alignment.Center).size(20.dp),
                            tint = ElectricBlue
                        )
                    }
                    Text("My V", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                }

                IconButton(onClick = { viewModel.navigateTo(AppScreen.Notifications) }) {
                    Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications", tint = OnSurfaceVariantText)
                }
            }
        },
        bottomBar = { BottomNavigationShell(viewModel, activeTab = 0) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (isAnalyzing) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ElectricBlueContainer)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(color = ElectricBlue, modifier = Modifier.size(24.dp))
                            Column {
                                Text("AI analysis is running...", fontWeight = FontWeight.Bold, color = ElectricBlueOnContainer, fontSize = 14.sp)
                                Text("Generative model evaluating emotional sentiment.", color = ElectricBlueOnContainer.copy(alpha = 0.8f), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            item {
                Text("Today's Recommended Script", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OnSurfaceText)
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .clickable { viewModel.navigateTo(AppScreen.ScriptPrompter(viewModel.scriptsList[0])) },
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(20.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(PureWhite.copy(alpha = 0.2f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(imageVector = Icons.Default.Star, contentDescription = "Daily", tint = PureWhite, modifier = Modifier.size(14.dp))
                                    Text("DAILY PICK", fontSize = 11.sp, color = PureWhite, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Public Speaking: Mastering the Elevator Pitch", fontSize = 21.sp, color = PureWhite, fontWeight = FontWeight.ExtraBold)
                            Text("Learn the art of concise storytelling and professional charisma in under 60 seconds.", fontSize = 13.sp, color = PureWhite.copy(alpha = 0.8f), modifier = Modifier.padding(top = 4.dp))
                        }

                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.ScriptPrompter(viewModel.scriptsList[0])) },
                            colors = ButtonDefaults.buttonColors(containerColor = PureWhite),
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Start", tint = ElectricBlue)
                                Text("Start Script Recording", color = ElectricBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(140.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(PureWhite)
                            .border(width = 1.dp, color = SurfaceContainer, shape = RoundedCornerShape(20.dp))
                            .clickable { viewModel.navigateTo(AppScreen.QuickRecording(viewModel.scriptsList[0])) }
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
                            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(ElectricBlueContainer), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Mic", tint = ElectricBlue)
                            }
                            Column {
                                Text("Free Recording", fontWeight = FontWeight.Bold, color = OnSurfaceText, fontSize = 15.sp)
                                Text("Capture your voice", color = OnSurfaceVariantText, fontSize = 11.sp)
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(140.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(PureWhite)
                            .border(width = 1.dp, color = SurfaceContainer, shape = RoundedCornerShape(20.dp))
                            .clickable { viewModel.navigateTo(AppScreen.RecordSelection) }
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
                            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(SuccessGreenContainer), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Star, contentDescription = "AI", tint = SuccessGreen)
                            }
                            Column {
                                Text("AI Analysis", fontWeight = FontWeight.Bold, color = OnSurfaceText, fontSize = 15.sp)
                                Text("Get deep voice insights", color = OnSurfaceVariantText, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Recent Recordings", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OnSurfaceText)
                    TextButton(onClick = { viewModel.navigateTo(AppScreen.SavedLibrary) }) {
                        Text("View All", color = ElectricBlue)
                    }
                }

                if (recordingsList.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
                    ) {
                        Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No Voice Recordings yet", fontWeight = FontWeight.Bold)
                            Text("Select an action or script above to get started.", color = OutlineBorder, fontSize = 12.sp)
                        }
                    }
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        items(recordingsList.take(3)) { recording ->
                            Card(
                                modifier = Modifier
                                    .width(220.dp)
                                    .clickable { viewModel.navigateTo(AppScreen.AnalysisReport(recording)) },
                                colors = CardDefaults.cardColors(containerColor = PureWhite),
                                border = BorderStroke(1.dp, SurfaceContainer.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(ElectricBlueContainer).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                            Text(recording.emotionalTone ?: "WARM", color = ElectricBlue, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Text(formatDuration(recording.durationMs), fontSize = 11.sp, color = OnSurfaceVariantText)
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(recording.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("Voice Score: ${recording.voiceScore ?: "--"}/100", fontSize = 12.sp, color = OutlineBorder)
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Canvas(modifier = Modifier.fillMaxWidth().height(24.dp)) {
                                        val bars = 10
                                        val gapIdx = 6f
                                        val w = size.width / bars
                                        for (i in 0 until bars) {
                                            val h = (8..22).random().dp.toPx()
                                            drawRoundRect(
                                                color = ElectricBlue,
                                                topLeft = androidx.compose.ui.geometry.Offset(i * w + gapIdx, (size.height - h) / 2),
                                                size = androidx.compose.ui.geometry.Size(w - gapIdx * 2, h),
                                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text("Community", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OnSurfaceText)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(PureWhite)
                        .border(width = 1.dp, color = SurfaceContainer, shape = RoundedCornerShape(20.dp))
                        .clickable { viewModel.navigateTo(AppScreen.Community) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(WarmAccentContainer), contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = "Forum", tint = WarmAccent)
                        }
                        Column {
                            Text("Join Vocal Discourse", fontWeight = FontWeight.Bold)
                            Text("Share voice recordings & get feedback", color = OnSurfaceVariantText, fontSize = 12.sp)
                        }
                    }
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Go", tint = OutlineBorder)
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

// SCREEN 4: SCRIPT SELECTION
@Composable
fun ScriptSelectionScreen(viewModel: RecordingViewModel) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Select Script to Record", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.Home) }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = { BottomNavigationShell(viewModel, activeTab = 1) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Pick a category to start your voice synthesis session.", fontSize = 14.sp, color = OnSurfaceVariantText)
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { viewModel.navigateTo(AppScreen.QuickRecording(viewModel.scriptsList[0])) },
                    colors = CardDefaults.cardColors(containerColor = ElectricBlue),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("PRIMARY ACTION", fontSize = 10.sp, color = PureWhite.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
                            Text("Free Recording", fontSize = 21.sp, color = PureWhite, fontWeight = FontWeight.ExtraBold)
                            Text("Record your voice without a script", fontSize = 13.sp, color = PureWhite.copy(alpha = 0.8f))
                        }
                        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(PureWhite.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Record", tint = PureWhite)
                        }
                    }
                }
            }

            val scriptGroups = viewModel.scriptsList.groupBy { it.category }
            scriptGroups.forEach { (category, scripts) ->
                item {
                    Text(category.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OutlineBorder, modifier = Modifier.padding(top = 10.dp))
                }

                items(scripts) { script ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(PureWhite)
                            .border(width = 1.dp, color = SurfaceContainer, shape = RoundedCornerShape(16.dp))
                            .clickable { viewModel.navigateTo(AppScreen.ScriptPrompter(script)) }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(ElectricBlueContainer), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Script", tint = ElectricBlue, modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text(script.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(script.tone, color = OnSurfaceVariantText, fontSize = 12.sp)
                            }
                        }
                        Icon(imageVector = Icons.Default.PlayArrow, tint = OutlineBorder, contentDescription = "Go")
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmAccentContainer)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = "AI", tint = WarmAccent)
                            Text("AI PROMPT ASSISTANT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WarmAccent)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Create a custom script", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurfaceText)
                        Text("Can't find what you need? Select the prompter generator to create a unique script matching your mood.", fontSize = 13.sp, color = OnSurfaceVariantText)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.ScriptPrompter(viewModel.scriptsList[0])) },
                            colors = ButtonDefaults.buttonColors(containerColor = WarmAccent),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Generate Script", color = PureWhite, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// SCREEN 8: SCRIPT TELEPROMPTER SCREEN (대본 확인)
@Composable
fun ScriptPrompterScreen(viewModel: RecordingViewModel, script: AppScript) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("대본 확인", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.RecordSelection) }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(script.title, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = ElectricBlue, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(24.dp))

                Box(modifier = Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(16.dp)).background(SurfaceContainerLow).padding(20.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(script.text, fontSize = 18.sp, lineHeight = 28.sp, fontWeight = FontWeight.Medium, color = OnSurfaceText)

                        if (script.smartMemo != null) {
                            Card(
                                modifier = Modifier.padding(top = 16.dp),
                                colors = CardDefaults.cardColors(containerColor = PureWhite),
                                border = BorderStroke(1.dp, SuccessGreenContainer)
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(imageVector = Icons.Default.Star, contentDescription = "Idea", tint = SuccessGreen)
                                    Text(script.smartMemo, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SuccessGreen)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(100.dp))
            }

            Button(
                onClick = { viewModel.startRecordingSession(script) },
                modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp).fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Record", tint = PureWhite)
                    Text("녹음 시작하기 (Start Recording)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PureWhite)
                }
            }
        }
    }
}

// SCREEN 10: RECORDING / FREE RECORDING STATE
@Composable
fun QuickRecordingScreen(viewModel: RecordingViewModel, script: AppScript) {
    val durationSeconds by viewModel.recordingDurationSeconds.collectAsState()
    val amplitudes by viewModel.amplitudeList.collectAsState()

    val mins = (durationSeconds / 60).toString().padStart(2, '0')
    val secs = (durationSeconds % 60).toString().padStart(2, '0')

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My V -  녹음 중", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.cancelRecordingSession() }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(modifier = Modifier.clip(RoundedCornerShape(30.dp)).background(ElectricBlueContainer).padding(horizontal = 16.dp, vertical = 6.dp)) {
                Text("Script: ${script.title}", color = ElectricBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Box(modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(16.dp)).background(SurfaceContainerLow), contentAlignment = Alignment.Center) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (amplitudes.isEmpty()) {
                        for (i in 0..16) {
                            Box(modifier = Modifier.width(4.dp).height(8.dp).background(OutlineVariant, CircleShape))
                        }
                    } else {
                        amplitudes.takeLast(18).forEach { amp ->
                            Box(modifier = Modifier.width(4.dp).height(amp.dp).background(ElectricBlue, CircleShape))
                        }
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$mins:$secs", fontSize = 64.sp, fontWeight = FontWeight.ExtraBold, color = OnSurfaceText, letterSpacing = (-2).sp)
                Text("RECORDING", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OutlineBorder, letterSpacing = 1.sp)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { viewModel.cancelRecordingSession() }) {
                        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(SurfaceContainerLow), contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = OutlineBorder)
                        }
                        Text("Cancel", fontSize = 11.sp, color = OutlineBorder, modifier = Modifier.padding(top = 4.dp))
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { viewModel.finishRecordingSession(script.title) }) {
                        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(ElectricBlueContainer), contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Done", tint = ElectricBlue)
                        }
                        Text("Done", fontSize = 11.sp, color = ElectricBlue, modifier = Modifier.padding(top = 4.dp), fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = { viewModel.finishRecordingSession(script.title) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Finish & Analyze", fontWeight = FontWeight.Bold, color = PureWhite)
                }
            }
        }
    }
}

// SCREEN 7: RECORDING RESULT
@Composable
fun RecordingResultScreen(viewModel: RecordingViewModel, recording: RecordingEntity) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Recording Result", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.Home) }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Ready for AI Sentiment Engine", fontSize = 10.sp, color = WarmAccent, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(recording.title, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    Text("Saved locally at files/recordings", fontSize = 12.sp, color = OutlineBorder)

                    Spacer(modifier = Modifier.height(20.dp))
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("0:00", fontSize = 11.sp, color = OutlineBorder)
                        LinearProgressIndicator(
                            progress = { 0.4f },
                            modifier = Modifier.weight(1f).padding(horizontal = 12.dp).height(4.dp),
                            color = ElectricBlue,
                            trackColor = SurfaceContainer
                        )
                        Text(formatDuration(recording.durationMs), fontSize = 11.sp, color = OutlineBorder)
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(ElectricBlue)
                                        .clickable { viewModel.navigateTo(AppScreen.AnalysisReport(recording)) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, tint = PureWhite, contentDescription = "Play", modifier = Modifier.size(32.dp))
                        }
                    }
                }
            }

            Button(
                onClick = { viewModel.navigateTo(AppScreen.AnalysisReport(recording)) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LightElectricBlue)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = "Analyze")
                    Text("AI Analysis", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Actions", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = OnSurfaceVariantText)
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PureWhite)
                        .border(width = 1.dp, color = SurfaceContainer, shape = RoundedCornerShape(12.dp))
                        .clickable { viewModel.navigateTo(AppScreen.SavedLibrary) }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Save to Library", fontWeight = FontWeight.Bold)
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Arrow", tint = OutlineBorder)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ErrorRedContainer.copy(alpha = 0.3f))
                        .border(width = 1.dp, color = ErrorRedContainer, shape = RoundedCornerShape(12.dp))
                        .clickable { viewModel.deleteRecording(recording) }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Delete Recording", fontWeight = FontWeight.Bold, color = ErrorRed)
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = ErrorRed)
                }
            }
        }
    }
}

// SCREEN 5: AI VOICE ANALYSIS REPORT
@Composable
fun VoiceAnalysisReportScreen(viewModel: RecordingViewModel, recording: RecordingEntity) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("AI Voice Analysis", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.Home) }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = { BottomNavigationShell(viewModel, activeTab = 2) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Card(
                        modifier = Modifier.weight(1f).height(180.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        border = BorderStroke(1.dp, SurfaceContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp).fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("QUALITY SCORE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OutlineBorder)
                            Box(modifier = Modifier.size(90.dp), contentAlignment = Alignment.Center) {
                                val score = recording.voiceScore ?: 82
                                CircularProgressIndicator(
                                    progress = { score / 100f },
                                    modifier = Modifier.fillMaxSize(),
                                    color = ElectricBlue,
                                    trackColor = SurfaceContainerLow,
                                    strokeWidth = 8.dp,
                                    strokeCap = StrokeCap.Round
                                )
                                Text("$score", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = ElectricBlue)
                            }
                            Text("Top 12% in Community", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f).height(180.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        border = BorderStroke(1.dp, SurfaceContainer)
                    ) {
                        Column(modifier = Modifier.padding(12.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                            Text("ANALYSIS BADGES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OutlineBorder)
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(modifier = Modifier.clip(RoundedCornerShape(30.dp)).background(ElectricBlueContainer).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                    Text("Professional", fontSize = 10.sp, color = ElectricBlue, fontWeight = FontWeight.Bold)
                                }
                            }

                            Column {
                                Text("Pitch Est:", fontSize = 11.sp, color = OutlineBorder)
                                Text("${recording.pitchHz?.toInt() ?: 164} Hz", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = BorderStroke(1.dp, SurfaceContainer)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("Analysis Summary", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = OnSurfaceText)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = recording.sentiment ?: "Your voice shows high resonance and exceptional clarity. The emotional tone is predominantly confident with a welcoming warmth.",
                            lineHeight = 22.sp,
                            fontSize = 14.sp,
                            color = OnSurfaceVariantText
                        )
                    }
                }
            }

            item {
                Text("Detailed Metrics", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = OnSurfaceText)
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = PureWhite), border = BorderStroke(1.dp, SurfaceContainer)) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Vocal Fundamental Pitch", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("${recording.pitchHz?.toInt() ?: 164} Hz", color = ElectricBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = PureWhite), border = BorderStroke(1.dp, SurfaceContainer)) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Clarity & Diction", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("${recording.clarityPercent ?: 94} %", color = SuccessGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = PureWhite), border = BorderStroke(1.dp, SurfaceContainer)) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Predominant Emotional Tone", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(recording.emotionalTone ?: "Warm", color = WarmAccent, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (!recording.transcript.isNullOrEmpty()) {
                item {
                    Text("AI Transcript Output", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = OnSurfaceText)
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)) {
                        Text(recording.transcript, modifier = Modifier.padding(16.dp), fontSize = 13.sp, lineHeight = 20.sp, color = OnSurfaceText)
                    }
                }
            }

            item {
                Button(
                    onClick = { viewModel.deleteRecording(recording) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRedContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete recording from Library database", color = ErrorRed, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

// SCREEN 11: LIBRARY / PAST SAVED RECORDINGS
@Composable
fun SavedLibraryScreen(viewModel: RecordingViewModel) {
    val recordingsList by viewModel.recordings.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Saved Recordings Library", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.Home) }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = { BottomNavigationShell(viewModel, activeTab = 2) }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().testTag("library_search_input"),
                placeholder = { Text("Search recordings...") },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricBlue, unfocusedBorderColor = SurfaceContainer),
                leadingIcon = { Icon(Icons.Default.PlayArrow, contentDescription = "Search") }
            )

            val filtered = recordingsList.filter {
                it.title.contains(searchQuery, ignoreCase = true) || it.scriptName.contains(searchQuery, ignoreCase = true)
            }

            if (filtered.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = "Empty", tint = OutlineBorder, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No recordings found matching search query", fontWeight = FontWeight.Bold, color = OutlineBorder)
                        Text("Completed analyses will sync here.", fontSize = 12.sp, color = OutlineBorder)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth().weight(1f)) {
                    items(filtered) { recording ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(PureWhite)
                                .border(width = 1.dp, color = SurfaceContainer, shape = RoundedCornerShape(14.dp))
                                .clickable { viewModel.navigateTo(AppScreen.AnalysisReport(recording)) }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(recording.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(recording.scriptName, color = OutlineBorder, fontSize = 11.sp)
                                    Box(modifier = Modifier.size(3.dp).background(OutlineVariant, CircleShape))
                                    Text(recording.emotionalTone ?: "Warm", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SuccessGreen)
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(formatDuration(recording.durationMs), fontSize = 12.sp, color = OnSurfaceVariantText)
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Go", tint = OutlineBorder)
                            }
                        }
                    }
                }
            }
        }
    }
}

// SCREEN 2: NOTIFICATIONS
@Composable
fun NotificationsScreen(viewModel: RecordingViewModel) {
    val list by viewModel.notifications.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.Home) }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.markAllNotificationsRead() }) {
                        Text("Mark all read", color = ElectricBlue)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val categorised = list.groupBy { it.category }
            categorised.forEach { (category, items) ->
                item {
                    Text(category.uppercase(), fontWeight = FontWeight.Bold, color = OutlineBorder, fontSize = 12.sp, modifier = Modifier.padding(top = 14.dp, bottom = 4.dp))
                }

                items(items) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { viewModel.navigateTo(AppScreen.Home) },
                        colors = CardDefaults.cardColors(containerColor = if (item.isRead) PureWhite else ElectricBlueContainer.copy(alpha = 0.3f)),
                        border = BorderStroke(1.dp, SurfaceContainer)
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Icon(imageVector = Icons.Default.Star, contentDescription = "Alert", tint = if (item.starred) WarmAccent else OutlineBorder)
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(item.timeAgo, fontSize = 11.sp, color = OutlineBorder)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(item.message, fontSize = 12.sp, color = OnSurfaceVariantText)
                                }
                            }

                            if (!item.isRead) {
                                Box(modifier = Modifier.size(8.dp).background(ElectricBlue, CircleShape))
                            }
                        }
                    }
                }
            }
        }
    }
}

// SCREEN 6: COMMUNITY
@Composable
fun CommunityScreen(viewModel: RecordingViewModel) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Community Forum", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.Home) }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = { BottomNavigationShell(viewModel, activeTab = 3) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ElectricBlue),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("Weekly Spotlight", fontSize = 10.sp, color = PureWhite.copy(alpha = 0.8f), fontWeight = FontWeight.Bold)
                        Text("\"The sound of our shared mornings.\"", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                    }
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Popular", fontWeight = FontWeight.Bold, color = ElectricBlue)
                    Text("Latest", color = OutlineBorder)
                }
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = OutlineVariant)
            }

            items(viewModel.communityPosts) { post ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PureWhite)
                        .border(width = 1.dp, color = SurfaceContainer, shape = RoundedCornerShape(12.dp))
                        .clickable { viewModel.navigateTo(AppScreen.Home) }
                        .padding(14.dp)
                ) {
                    Text(post.first, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(post.second, fontSize = 11.sp, color = OutlineBorder)
                }
            }
        }
    }
}

// SCREEN 9: SETTINGS / PROFILE SCREEN
@Composable
fun ProfileSettingsScreen(viewModel: RecordingViewModel) {
    val themeLight by viewModel.themeDefaultLight.collectAsState()
    val language by viewModel.userLanguage.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile & Settings", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.Home) }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = { BottomNavigationShell(viewModel, activeTab = 4) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = BorderStroke(1.dp, SurfaceContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(ElectricBlueContainer), contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Avatar", tint = ElectricBlue, modifier = Modifier.size(32.dp))
                        }
                        Column {
                            Text("Felix Chen", fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
                            Text("@felix_myv_pro", color = OutlineBorder, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(modifier = Modifier.clip(RoundedCornerShape(30.dp)).background(ElectricBlueContainer).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                Text("Pro Member", fontSize = 10.sp, color = ElectricBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                Text("Account", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OutlineBorder)
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = PureWhite)) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Nickname", fontWeight = FontWeight.Bold)
                                Text("Felix Chen", fontSize = 12.sp, color = OutlineBorder)
                            }
                            Icon(Icons.Default.PlayArrow, contentDescription = "Go", tint = OutlineBorder)
                        }
                        HorizontalDivider(color = SurfaceContainer)
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Phone Number", fontWeight = FontWeight.Bold)
                                Text("+1 (555) 012-3456", fontSize = 12.sp, color = OutlineBorder)
                            }
                            Icon(Icons.Default.PlayArrow, contentDescription = "Go", tint = OutlineBorder)
                        }
                    }
                }
            }

            item {
                Text("App Settings", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OutlineBorder)
            }

            item {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = PureWhite)) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { viewModel.toggleTheme() }.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Theme Mode", fontWeight = FontWeight.Bold)
                                Text(if (themeLight) "System Default (Light)" else "Cosmic Slate (Dark)", fontSize = 12.sp, color = OutlineBorder)
                            }
                            Switch(checked = !themeLight, onCheckedChange = { viewModel.toggleTheme() })
                        }
                        HorizontalDivider(color = SurfaceContainer)
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { viewModel.setLanguage(if (language == "English (US)") "Korean" else "English (US)") }.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Language", fontWeight = FontWeight.Bold)
                                Text(language, fontSize = 12.sp, color = OutlineBorder)
                            }
                            Icon(Icons.Default.PlayArrow, contentDescription = "Go", tint = OutlineBorder)
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = { viewModel.navigateTo(AppScreen.Login) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerLow),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Logout", color = OnSurfaceText, fontWeight = FontWeight.Bold)
                }
            }

            item {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("My V Version 2.4.0 (Build 892)", fontSize = 11.sp, color = OutlineBorder)
                        Text("Made with precision & care", fontSize = 11.sp, color = OutlineBorder)
                    }
                }
            }
        }
    }
}

// Common Bottom Navigation bar
@Composable
fun BottomNavigationShell(viewModel: RecordingViewModel, activeTab: Int) {
    NavigationBar(
        containerColor = PureWhite,
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        NavigationBarItem(
            selected = activeTab == 0,
            onClick = { viewModel.navigateTo(AppScreen.Home) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = ElectricBlue, unselectedIconColor = OutlineBorder, selectedTextColor = ElectricBlue)
        )
        NavigationBarItem(
            selected = activeTab == 1,
            onClick = { viewModel.navigateTo(AppScreen.RecordSelection) },
            icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Record") },
            label = { Text("Record") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = ElectricBlue, unselectedIconColor = OutlineBorder, selectedTextColor = ElectricBlue)
        )
        NavigationBarItem(
            selected = activeTab == 2,
            onClick = { viewModel.navigateTo(AppScreen.SavedLibrary) },
            icon = { Icon(Icons.Default.Star, contentDescription = "Analysis") },
            label = { Text("Analysis") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = ElectricBlue, unselectedIconColor = OutlineBorder, selectedTextColor = ElectricBlue)
        )
        NavigationBarItem(
            selected = activeTab == 3,
            onClick = { viewModel.navigateTo(AppScreen.Community) },
            icon = { Icon(Icons.Default.Star, contentDescription = "Community") },
            label = { Text("Community") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = ElectricBlue, unselectedIconColor = OutlineBorder, selectedTextColor = ElectricBlue)
        )
        NavigationBarItem(
            selected = activeTab == 4,
            onClick = { viewModel.navigateTo(AppScreen.Settings) },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = ElectricBlue, unselectedIconColor = OutlineBorder, selectedTextColor = ElectricBlue)
        )
    }
}

// Helpers
fun formatDuration(ms: Long): String {
    val totalSecs = ms / 1000
    val mm = totalSecs / 60
    val ss = totalSecs % 60
    return String.format("%02d:%02d", mm, ss)
}
