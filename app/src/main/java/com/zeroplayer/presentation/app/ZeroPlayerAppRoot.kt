package com.zeroplayer.presentation.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zeroplayer.R
import com.zeroplayer.player.PlayerActivity
import com.zeroplayer.presentation.folder.FolderVideosScreen
import com.zeroplayer.presentation.folders.FoldersScreen
import com.zeroplayer.presentation.settings.SettingsScreen
import androidx.compose.material3.ExperimentalMaterial3Api

object Routes {
    const val Folders = "folders"
    const val FolderVideos = "folderVideos"
    const val Settings = "settings"
    const val ArgBucketId = "bucketId"
    const val ArgFolderName = "folderName"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZeroPlayerAppRoot(
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route.orEmpty()

    val requiredPermission = remember {
        if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_VIDEO
        else Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val hasPermission = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, requiredPermission) == PackageManager.PERMISSION_GRANTED,
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasPermission.value = granted
    }

    LaunchedEffect(Unit) {
        if (!hasPermission.value) {
            permissionLauncher.launch(requiredPermission)
        }
    }

    if (!hasPermission.value) {
        PermissionGate(
            onGrant = { permissionLauncher.launch(requiredPermission) },
        )
        return
    }

    Scaffold(
        topBar = {
            run {
                val canGoBack = navController.previousBackStackEntry != null &&
                    (route.startsWith(Routes.FolderVideos) || route == Routes.Settings)

                val title = when {
                    route == Routes.Settings -> stringResource(id = R.string.settings_title)
                    route.startsWith(Routes.FolderVideos) -> "Videos"
                    else -> "Folders"
                }

                CenterAlignedTopAppBar(
                    title = { Text(text = title) },
                    navigationIcon = {
                        if (canGoBack) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        }
                    },
                    actions = {
                        if (route == Routes.Folders) {
                            IconButton(onClick = { navController.navigate(Routes.Settings) }) {
                                Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                            }
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.Folders,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Routes.Folders) {
                FoldersScreen(
                    onOpenFolder = { bucketId, folderName ->
                        navController.navigate(
                            "${Routes.FolderVideos}/$bucketId/${android.net.Uri.encode(folderName)}",
                        )
                    },
                )
            }

            composable(Routes.Settings) {
                SettingsScreen()
            }

            composable(
                route = "${Routes.FolderVideos}/{${Routes.ArgBucketId}}/{${Routes.ArgFolderName}}",
                arguments = listOf(
                    navArgument(Routes.ArgBucketId) { type = NavType.StringType },
                    navArgument(Routes.ArgFolderName) { type = NavType.StringType },
                ),
            ) {
                FolderVideosScreen(
                    onBack = { navController.popBackStack() },
                    onOpenPlayer = { uriString ->
                        context.startActivity(PlayerActivity.intent(context, uriString))
                    },
                )
            }
        }
    }
}

@Composable
private fun PermissionGate(
    onGrant: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(id = R.string.permission_needed_title),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = stringResource(id = R.string.permission_needed_body),
            style = MaterialTheme.typography.bodyMedium,
        )
        Button(onClick = onGrant) {
            Text(text = stringResource(id = R.string.grant_permission))
        }
    }
}
