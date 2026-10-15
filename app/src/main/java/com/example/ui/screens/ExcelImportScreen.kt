package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.ExcelHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExcelImportScreen(
    importMessage: String?,
    onImportSchools: (String) -> Unit,
    onImportTeachers: (String) -> Unit,
    onClearMessage: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var selectedImportType by remember { mutableStateOf(0) } // 0: Teachers, 1: Schools
    var inputText by remember { mutableStateOf(ExcelHelper.getSampleTeachersCsv()) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            val content = ExcelHelper.readContentFromUri(context, uri)
            if (content != null) {
                inputText = content
                selectedFileName = uri.lastPathSegment ?: "ملف Excel تم اختياره"
            }
        }
    }

    LaunchedEffect(selectedImportType) {
        if (selectedFileName == null) {
            inputText = if (selectedImportType == 0) {
                ExcelHelper.getSampleTeachersCsv()
            } else {
                ExcelHelper.getSampleSchoolsCsv()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "استيراد بيانات Excel و CSV",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EduBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Success / Status Banner
            if (importMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EduGreen.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EduGreen)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = importMessage,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = EduGreen,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onClearMessage) {
                            Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = EduGreen)
                        }
                    }
                }
            }

            // Tab Selector
            TabRow(
                selectedTabIndex = selectedImportType,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = EduBlue,
                modifier = Modifier.testTag("import_tab_row")
            ) {
                Tab(
                    selected = selectedImportType == 0,
                    onClick = {
                        selectedImportType = 0
                        selectedFileName = null
                    },
                    text = { Text("استيراد بيانات المعلمين", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedImportType == 1,
                    onClick = {
                        selectedImportType = 1
                        selectedFileName = null
                    },
                    text = { Text("استيراد بيانات المدارس", fontWeight = FontWeight.Bold) }
                )
            }

            // File Picker Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (selectedImportType == 0) "أعمدة ملف المعلمين المطلوبة:" else "أعمدة ملف المدارس المطلوبة:",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (selectedImportType == 0) {
                            "الاسم ، المدرسة ، المرحلة ، الوظيفة ، رقم الهاتف ، الرقم القومي ، المادة ، تاريخ التعيين"
                        } else {
                            "اسم المدرسة ، المرحلة ، عدد الفصول ، عدد العاملين ، المدير ، الوكيل ، العنوان"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Buttons Row: Pick File & Load Sample
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                filePickerLauncher.launch(
                                    arrayOf(
                                        "text/*",
                                        "text/csv",
                                        "text/comma-separated-values",
                                        "application/vnd.ms-excel",
                                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                                        "*/*"
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EduBlue),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("pick_excel_file_button")
                        ) {
                            Icon(Icons.Default.FileOpen, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("اختيار ملف Excel / CSV")
                        }

                        OutlinedButton(
                            onClick = {
                                selectedFileName = null
                                inputText = if (selectedImportType == 0) {
                                    ExcelHelper.getSampleTeachersCsv()
                                } else {
                                    ExcelHelper.getSampleSchoolsCsv()
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = EduGoldDark)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("نموذج تجريبي", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    if (selectedFileName != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AttachFile, contentDescription = null, tint = EduGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "تم تحميل: $selectedFileName",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = EduGreen
                            )
                        }
                    }
                }
            }

            // Text Preview / Edit Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "معاينة وتحرير محتوى الملف:",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .testTag("excel_preview_text"),
                        shape = RoundedCornerShape(10.dp),
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Execute Import Button
            Button(
                onClick = {
                    if (selectedImportType == 0) {
                        onImportTeachers(inputText)
                    } else {
                        onImportSchools(inputText)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EduGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("execute_import_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (selectedImportType == 0) "بدء استيراد المعلمين وتحديث السجل" else "بدء استيراد المدارس وتحديث السجل",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
