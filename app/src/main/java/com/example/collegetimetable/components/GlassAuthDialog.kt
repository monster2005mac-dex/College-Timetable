package com.example.collegetimetable.components

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.FragmentActivity
import com.example.collegetimetable.AuthManager
import com.example.collegetimetable.GlassDialogShape
import com.example.collegetimetable.GlassErrorColor
import com.example.collegetimetable.GlassInputShape
import com.example.collegetimetable.GlassPanel
import com.example.collegetimetable.GlassPillShape
import com.example.collegetimetable.OnGlassErrorColor
import com.example.collegetimetable.SecurityManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch

@Composable
fun GlassAuthDialog(
    accentColor: Color,
    hazeState: HazeState,
    onSuccessLogin: () -> Unit,
    onSignOut: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentUser = AuthManager.currentUser

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Google Sign-In Launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (!idToken.isNullOrEmpty()) {
                    isLoading = true
                    coroutineScope.launch {
                        val authResult = AuthManager.signInWithGoogleCredential(idToken)
                        isLoading = false
                        authResult.fold(
                            onSuccess = {
                                onSuccessLogin()
                                onDismiss()
                            },
                            onFailure = { ex ->
                                errorMessage = ex.localizedMessage ?: "Google sign in failed"
                            }
                        )
                    }
                } else {
                    errorMessage = "Failed to retrieve Google ID Token"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Google Sign-In canceled or failed"
            }
        }
    }

    fun launchGoogleSignIn() {
        try {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("984199619270-4b4dhdqi4hm7iih60qo5dpkeor7ipp7j.apps.googleusercontent.com")
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        } catch (e: Exception) {
            errorMessage = e.localizedMessage ?: "Could not launch Google Sign-In"
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        GlassPanel(
            hazeState = hazeState,
            modifier = Modifier.fillMaxWidth(),
            shape = GlassDialogShape,
            shadowElevation = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.3f))
                            .border(1.dp, accentColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Cloud Sync & Login",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Sync timetable with Web website",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                }

                if (currentUser != null) {
                    // Logged In State
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Currently Signed In ⚡",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = accentColor
                            )
                            Text(
                                text = currentUser.email ?: "Logged In Student",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Real-time live sync is active between Android & Web.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(GlassPillShape)
                            .background(GlassErrorColor.copy(alpha = 0.25f))
                            .border(1.dp, GlassErrorColor, GlassPillShape)
                            .clickable {
                                AuthManager.signOut()
                                onSignOut()
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sign Out 🚪",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = OnGlassErrorColor
                        )
                    }
                } else {
                    // Google Sign-In Button (Identical to Web)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(GlassInputShape)
                            .background(Color.White)
                            .clickable { launchGoogleSignIn() }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Sign in with Google",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }

                    // Device Biometric Button (Identical to Web)
                    if (SecurityManager.isBiometricAvailable(context) && context is FragmentActivity) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(GlassInputShape)
                                .background(Color.White.copy(alpha = 0.12f))
                                .border(1.dp, Color.White.copy(alpha = 0.25f), GlassInputShape)
                                .clickable {
                                    SecurityManager.promptBiometricUnlock(
                                        activity = context,
                                        onSuccess = {
                                            onSuccessLogin()
                                            onDismiss()
                                        },
                                        onError = { err ->
                                            errorMessage = err
                                        }
                                    )
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Fingerprint,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Sign in with Device Biometrics 🔑",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Divider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f).height(1.dp).background(Color.White.copy(alpha = 0.2f)))
                        Text(
                            text = " OR ",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Box(modifier = Modifier.weight(1f).height(1.dp).background(Color.White.copy(alpha = 0.2f)))
                    }

                    // Email Form Inputs
                    GlassInputField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email Address",
                        icon = Icons.Default.Email
                    )

                    GlassInputField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        icon = Icons.Default.Lock
                    )

                    errorMessage?.let { err ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(GlassErrorColor.copy(alpha = 0.25f))
                                .border(1.dp, GlassErrorColor, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = err,
                                style = MaterialTheme.typography.bodySmall,
                                color = OnGlassErrorColor,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                isSignUp = !isSignUp
                                errorMessage = null
                            }
                        ) {
                            Text(
                                text = if (isSignUp) "Already registered? Sign In" else "New student? Sign Up",
                                color = accentColor,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(GlassPillShape)
                                .background(if (isLoading) Color.Gray else accentColor)
                                .clickable {
                                    if (email.trim().isEmpty() || password.trim().isEmpty()) {
                                        errorMessage = "Enter your email and password"
                                        return@clickable
                                    }
                                    isLoading = true
                                    errorMessage = null

                                    coroutineScope.launch {
                                        val result = if (isSignUp) {
                                            AuthManager.signUpWithEmail(email.trim(), password.trim())
                                        } else {
                                            AuthManager.signInWithEmail(email.trim(), password.trim())
                                        }

                                        isLoading = false
                                        result.fold(
                                            onSuccess = {
                                                onSuccessLogin()
                                                onDismiss()
                                            },
                                            onFailure = { ex ->
                                                errorMessage = ex.localizedMessage ?: "Authentication failed"
                                            }
                                        )
                                    }
                                }
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = if (isLoading) "Signing in..." else if (isSignUp) "Sign Up" else "Sign In",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
