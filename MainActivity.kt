package com.sample.humannesstasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.gestures.detectTapGestures
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.rememberCoroutineScope

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

// ===== DATA MODELS =====
data class TaskHistory(
    val id: Int,
    val taskType: String,
    val title: String,
    val duration: String,
    val timestamp: String,
    val preview: String
)

// New model for saving tasks locally
data class SavedTask(
    val taskType: String,
    val text: String,
    val audioPath: String,
    val durationSec: Int,
    val timestamp: String,
    val productId: Int? = null,
    val productTitle: String? = null
)

// API Response Model
data class ProductsResponse(
    val products: List<Product>
)

data class Product(
    val id: Int,
    val title: String,
    val description: String
)

// ===== API SERVICE =====
class ApiService {
    // Simulated API call without external dependencies
    suspend fun fetchProducts(): ProductsResponse = withContext(Dispatchers.IO) {
        delay(500) // Simulate network delay
        
        // Return sample products (simulating API response)
        ProductsResponse(
            listOf(
                Product(1, "iPhone 9", "An apple mobile which is nothing like apple. Features a beautiful display, great camera, and long battery life."),
                Product(2, "iPhone X", "SIM-Free, Model A19211 6.5-inch Super Retina HD display with OLED technology."),
                Product(3, "Samsung Universe 9", "Samsung's new variant which goes beyond Galaxy to the Universe."),
                Product(4, "OPPOF19", "OPPO F19 is officially announced on April 2021."),
                Product(5, "Huawei P30", "Huawei's re-badged P30 Pro New Edition was officially unveiled yesterday in Germany."),
                Product(6, "MacBook Pro", "MacBook Pro 2021 with mini-LED display may launch between September and November."),
                Product(7, "Samsung Galaxy Book", "Samsung Galaxy Book S (2020) Laptop With Intel Lakefield Chip."),
                Product(8, "Microsoft Surface Laptop 4", "Style and speed. Stand out on HD video calls backed by Studio Mics."),
                Product(9, "Infinix INBOOK", "Infinix Inbook X1 Ci3 10th 8GB 256GB 14 Win10 Grey."),
                Product(10, "HP Pavilion 15-DK1056WM", "HP Pavilion 15-DK1056WM Gaming Laptop 10th Gen Core i5.")
            )
        )
    }
}

// ===== LOCAL STORAGE SIMULATION =====
object LocalStorage {
    private val savedTasks = mutableListOf<SavedTask>()
    
    fun saveTask(task: SavedTask) {
        savedTasks.add(task)
        println("Task saved: $task")
        println("Total tasks: ${savedTasks.size}")
    }
    
    fun getTasks(): List<SavedTask> = savedTasks.toList()
}

// ===== NAVIGATION =====
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val taskHistory = remember { mutableStateOf<List<TaskHistory>>(emptyList()) }
    
    NavHost(
        navController = navController,
        startDestination = "start"
    ) {
        composable("start") {
            StartScreen(
                onStartClicked = { navController.navigate("noiseTest") }
            )
        }
        
        composable("noiseTest") {
            NoiseTestScreen(
                onPassClicked = { navController.navigate("taskSelection") }
            )
        }
        
        composable("taskSelection") {
            TaskSelectionScreen(
                onTextReadingClicked = { navController.navigate("textReading") },
                onImageDescriptionClicked = { navController.navigate("imageDescription") },
                onPhotoCaptureClicked = { navController.navigate("photoCapture") },
                onHistoryClicked = { navController.navigate("taskHistory") }
            )
        }
        
        composable("textReading") {
            TextReadingScreen(
                onBack = { navController.navigateUp() },
                onSubmit = { task ->
                    val updatedHistory = taskHistory.value + task
                    taskHistory.value = updatedHistory
                    navController.navigate("taskSelection")
                }
            )
        }
        
        composable("imageDescription") {
            ImageDescriptionPlaceholder(
                onBack = { navController.navigateUp() }
            )
        }
        
        composable("photoCapture") {
            PhotoCapturePlaceholder(
                onBack = { navController.navigateUp() }
            )
        }
        
        composable("taskHistory") {
            TaskHistoryScreen(
                onBack = { navController.navigateUp() }
            )
        }
    }
}

// ===== STEP 1: START SCREEN =====
@Composable
fun StartScreen(onStartClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Let's start with a Sample Task for practice.",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Pehele hum ek sample task karte hain.",
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = onStartClicked,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Start Sample Task", fontSize = 18.sp)
        }
    }
}

// ===== STEP 2: NOISE TEST SCREEN =====
@Composable
fun NoiseTestScreen(onPassClicked: () -> Unit) {
    var dbLevel by remember { mutableIntStateOf(0) }
    var isTesting by remember { mutableStateOf(false) }
    var testComplete by remember { mutableStateOf(false) }
    
    LaunchedEffect(isTesting) {
        if (isTesting) {
            dbLevel = 0
            testComplete = false
            
            repeat(10) {
                delay(200)
                dbLevel = (20..60).random()
            }
            
            delay(500)
            testComplete = true
            isTesting = false
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🔊 Noise Test",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(vertical = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            )
            
            val progress = dbLevel / 60f
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .background(
                        if (dbLevel < 40) Color.Green else Color.Red,
                        RoundedCornerShape(20.dp)
                    )
            )
            
            Text(
                text = "$dbLevel dB",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        
        Text(
            text = when {
                dbLevel == 0 -> "Press Start to test noise level"
                dbLevel < 40 && testComplete -> "✅ Good to proceed"
                dbLevel >= 40 && testComplete -> "⚠ Please move to a quieter place"
                isTesting -> "Testing... $dbLevel dB"
                else -> "Ready to test"
            },
            fontSize = 18.sp,
            color = when {
                dbLevel < 40 && testComplete -> Color.Green
                dbLevel >= 40 && testComplete -> Color.Red
                else -> Color.Gray
            },
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        Button(
            onClick = { isTesting = true },
            enabled = !isTesting,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (isTesting) "Testing..." else "Start Test",
                fontSize = 18.sp
            )
        }
        
        Button(
            onClick = onPassClicked,
            enabled = testComplete && dbLevel < 40,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Continue to Tasks", fontSize = 18.sp)
        }
    }
}

// ===== STEP 3: TASK SELECTION SCREEN =====
@Composable
fun TaskSelectionScreen(
    onTextReadingClicked: () -> Unit,
    onImageDescriptionClicked: () -> Unit,
    onPhotoCaptureClicked: () -> Unit,
    onHistoryClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Choose a Task Type",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 30.dp)
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
            onClick = onTextReadingClicked
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📖",
                    fontSize = 36.sp,
                    modifier = Modifier.padding(end = 20.dp)
                )
                Column {
                    Text(
                        text = "Text Reading Task",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Read a passage from products API",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
            onClick = onImageDescriptionClicked
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🖼",
                    fontSize = 36.sp,
                    modifier = Modifier.padding(end = 20.dp)
                )
                Column {
                    Text(
                        text = "Image Description Task",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Describe an image via audio",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
            onClick = onPhotoCaptureClicked
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📸",
                    fontSize = 36.sp,
                    modifier = Modifier.padding(end = 20.dp)
                )
                Column {
                    Text(
                        text = "Photo Capture Task",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Capture and describe a photo",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        OutlinedButton(
            onClick = onHistoryClicked,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("📋 View Task History", fontSize = 16.sp)
        }
    }
}

// ===== STEP 4: TEXT READING TASK (UPDATED WITH API) =====
@Composable
fun TextReadingScreen(onBack: () -> Unit, onSubmit: (TaskHistory) -> Unit) {
    var isRecording by remember { mutableStateOf(false) }
    var recordingTime by remember { mutableIntStateOf(0) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isButtonPressed by remember { mutableStateOf(false) }
    var sampleText by remember { mutableStateOf("Loading product description...") }
    var productTitle by remember { mutableStateOf("Product Description") }
    var productId by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    
    val coroutineScope = rememberCoroutineScope()
    
    // Load product from API
    LaunchedEffect(Unit) {
        isLoading = true
        coroutineScope.launch {
            try {
                val apiService = ApiService()
                val response = apiService.fetchProducts()
                
                if (response.products.isNotEmpty()) {
                    val randomProduct = response.products.random()
                    productId = randomProduct.id
                    productTitle = randomProduct.title
                    sampleText = randomProduct.description
                }
            } finally {
                isLoading = false
            }
        }
    }
    
    // Timer simulation
    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (isRecording && recordingTime < 30) {
                delay(1000)
                recordingTime++
            }
        } else {
            // Check recording length AFTER recording stops
            if (recordingTime > 0) {
                showError = when {
                    recordingTime < 10 -> {
                        errorMessage = "Recording too short (min 10 s)."
                        true
                    }
                    recordingTime > 20 -> {
                        errorMessage = "Recording too long (max 20 s)."
                        true
                    }
                    else -> false
                }
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "📖 Text Reading",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(48.dp))
        }
        
        // Using regular Divider instead of HorizontalDivider
        Divider(modifier = Modifier.padding(vertical = 16.dp))
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Text to Read (from API)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Read this product description:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Product: $productTitle",
                        fontSize = 14.sp,
                        color = Color.Blue,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = sampleText,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = "Instruction: Read the description aloud in your native language.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
            
            // Recording Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Mic Button with Press and Hold
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                if (isRecording) Color.Red else MaterialTheme.colorScheme.primary,
                                CircleShape
                            )
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        isButtonPressed = true
                                        isRecording = true
                                        try {
                                            tryAwaitRelease()
                                            isButtonPressed = false
                                            isRecording = false
                                        } catch (_: Exception) {
                                            isButtonPressed = false
                                            isRecording = false
                                        }
                                    }
                                )
                            }
                            .align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isRecording) "⏹" else "🎤",
                            fontSize = 30.sp,
                            color = Color.White
                        )
                    }
                    
                    // Timer Display
                    Text(
                        text = "${recordingTime}s",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = when (recordingTime) {
                            0 -> Color.Gray
                            in 10..20 -> Color.Green
                            else -> Color.Red
                        }
                    )
                    
                    // Instruction
                    Text(
                        text = when {
                            isButtonPressed && isRecording -> "Recording... Release to stop"
                            isRecording -> "Recording..."
                            else -> "Press & Hold to record"
                        },
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Color.Gray
                    )
                    
                    // Error Message
                    if (showError) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 8.dp)
                        )
                    }
                    
                    // Playback Bar (after valid recording)
                    if (!isRecording && recordingTime in 10..20) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                .padding(top = 16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .background(Color.Green, RoundedCornerShape(4.dp))
                            )
                        }
                        Text(
                            text = "Playback",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    
                    // Checkboxes (after valid recording)
                    if (!isRecording && recordingTime in 10..20) {
                        var check1 by remember { mutableStateOf(false) }
                        var check2 by remember { mutableStateOf(false) }
                        var check3 by remember { mutableStateOf(false) }
                        
                        Column(modifier = Modifier.padding(top = 16.dp)) {
                            Text(
                                text = "Quality Check:",
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = check1, onCheckedChange = { check1 = it })
                                Text("No background noise", modifier = Modifier.padding(start = 8.dp))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = check2, onCheckedChange = { check2 = it })
                                Text("No mistakes while reading", modifier = Modifier.padding(start = 8.dp))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = check3, onCheckedChange = { check3 = it })
                                Text("Beech me koi galti nahi hai", modifier = Modifier.padding(start = 8.dp))
                            }
                            
                            // Buttons
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                OutlinedButton(onClick = { 
                                    isRecording = false
                                    recordingTime = 0
                                    showError = false
                                    isButtonPressed = false
                                }) {
                                    Text("Record Again")
                                }
                                
                                Button(
                                    onClick = {
                                        // Generate timestamp
                                        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                                        val timestamp = sdf.format(Date())
                                        
                                        // Generate audio path (simulated)
                                        val audioPath = "/local/path/audio_${System.currentTimeMillis()}.mp3"
                                        
                                        // Save task locally
                                        val savedTask = SavedTask(
                                            taskType = "text_reading",
                                            text = sampleText,
                                            audioPath = audioPath,
                                            durationSec = recordingTime,
                                            timestamp = timestamp,
                                            productId = productId,
                                            productTitle = productTitle
                                        )
                                        
                                        LocalStorage.saveTask(savedTask)
                                        
                                        // Also submit to navigation
                                        onSubmit(
                                            TaskHistory(
                                                id = productId,
                                                taskType = "text_reading",
                                                title = "Text Reading: $productTitle",
                                                duration = "${recordingTime}s",
                                                timestamp = timestamp,
                                                preview = sampleText.take(50) + "..."
                                            )
                                        )
                                    },
                                    enabled = check1 && check2 && check3
                                ) {
                                    Text("Submit")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ===== TASK HISTORY SCREEN =====
@Composable
fun TaskHistoryScreen(onBack: () -> Unit) {
    val savedTasks = remember { mutableStateOf(LocalStorage.getTasks()) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "📋 Task History",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        // Using regular Divider instead of HorizontalDivider
        Divider(modifier = Modifier.padding(vertical = 16.dp))
        
        if (savedTasks.value.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No tasks saved yet",
                    fontSize = 18.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Complete a text reading task first",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        } else {
            LazyColumn {
                items(savedTasks.value) { task ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Task: ${task.taskType}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "${task.durationSec}s",
                                    color = Color.Blue,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            if (task.productTitle != null) {
                                Text(
                                    text = "Product: ${task.productTitle}",
                                    fontSize = 14.sp,
                                    color = Color.DarkGray,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            
                            Text(
                                text = task.text.take(100) + if (task.text.length > 100) "..." else "",
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            ) {
                                Text(
                                    text = task.timestamp,
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "Audio: ${task.audioPath}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ===== PLACEHOLDER SCREENS =====
@Composable
fun ImageDescriptionPlaceholder(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🖼 Image Description Task",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Coming in Step 5",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Button(onClick = onBack) {
            Text("Back to Tasks")
        }
    }
}

@Composable
fun PhotoCapturePlaceholder(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📸 Photo Capture Task",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Coming in Step 6",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Button(onClick = onBack) {
            Text("Back to Tasks")
        }
    }
}
