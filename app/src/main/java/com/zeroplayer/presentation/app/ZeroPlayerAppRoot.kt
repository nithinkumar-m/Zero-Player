package com.zeroplayer.presentation.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zeroplayer.R
import com.zeroplayer.presentation.library.LibraryScreen
import com.zeroplayer.presentation.player.PlayerScreen

object Routes {
    const val Library = "library"
    const val Player = "player"
    const val ArgUri = "uri"
}

@Composable
fun ZeroPlayerAppRoot(
    onEnterPip: () -> Unit,
) {
    val navController = rememberNavController()
    val context = LocalContext.current

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

    NavHost(
        navController = navController,
        startDestination = Routes.Library,
    ) {
        composable(Routes.Library) {
            LibraryScreen(
                onOpenPlayer = { uriString ->
                    navController.navigate("${Routes.Player}/${android.net.Uri.encode(uriString)}")
                },
            )
        }

        composable(
            route = "${Routes.Player}/{${Routes.ArgUri}}",
            arguments = listOf(navArgument(Routes.ArgUri) { type = NavType.StringType }),
        ) { backStackEntry ->
            val uriString = backStackEntry.arguments?.getString(Routes.ArgUri).orEmpty()
            PlayerScreen(
                uriString = android.net.Uri.decode(uriString),
                onBack = { navController.popBackStack() },
                onEnterPip = onEnterPip,
            )
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

