package com.linli.blackcatnews.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import blackcatnews.composeapp.generated.resources.Res
import blackcatnews.composeapp.generated.resources.bg_login
import blackcatnews.composeapp.generated.resources.icon_apple
import blackcatnews.composeapp.generated.resources.icon_google_logo
import com.linli.authentication.platform
import com.linli.authentication.presentation.SignInEffect
import com.linli.authentication.presentation.SignInViewModel
import com.linli.authentication.presentation.onAnonymousClick
import com.linli.authentication.presentation.onAppleClick
import com.linli.authentication.presentation.onEmailSignIn
import com.linli.authentication.presentation.onEmailSignUp
import com.linli.authentication.presentation.onGoogleClick
import com.linli.blackcatnews.navigation.rememberSignInUIClients
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit
) {
    val signInClients = rememberSignInUIClients()
    val viewModel: SignInViewModel = koinViewModel {
        parametersOf(signInClients)
    }

    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SignInEffect.NavigateHome -> onNavigateToHome()
                is SignInEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        SignInContentScreen(
            modifier = Modifier.fillMaxSize(),
            onEmailSignIn = { email, password ->
                viewModel.onEmailSignIn(email, password)
            },
            onGoogleSignIn = {
                viewModel.onGoogleClick()
            },
            onAnonymousSignIn = {
                viewModel.onAnonymousClick()
            },
            onAppleSignIn = {
                viewModel.onAppleClick()
            },
            onForgotPassword = {
                // TODO: Implement forgot password functionality
            },
            onRegister = { email, password ->
                viewModel.onEmailSignUp(email, password)
            },
            isLoading = state.isLoading
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun SignInContentScreen(
    modifier: Modifier = Modifier,
    onEmailSignIn: (email: String, password: String) -> Unit,
    onGoogleSignIn: () -> Unit,
    onAnonymousSignIn: () -> Unit,
    onAppleSignIn: () -> Unit,
    onForgotPassword: () -> Unit,
    onRegister: (email: String, password: String) -> Unit,
    isLoading: Boolean = false
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }

    val passwordFocusRequester = remember { FocusRequester() }

    val isIOS = platform() == "iOS"

    fun validate(): Boolean {
        val emailValid = email.isValidEmail()
        emailError = if (!emailValid) "請輸入有效的電子郵件地址" else null
        val pwdValid = password.isNotBlank()
        passwordError = if (!pwdValid) "請輸入密碼" else null
        return emailValid && pwdValid
    }

    fun submit() {
        if (validate()) {
            onEmailSignIn(email, password)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 180.dp)
                .weight(1f, fill = true)
        ) {
            Image(
                painter = painterResource(Res.drawable.bg_login),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0x99000000),
                                Color(0x33000000),
                                Color.Transparent
                            )
                        )
                    )
            )
            Text(
                text = "黑貓新聞",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 20.dp)
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            tonalElevation = 2.dp,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        if (emailError != null) emailError = null
                    },
                    label = { Text("電子郵件") },
                    placeholder = { Text("name@example.com") },
                    isError = emailError != null,
                    supportingText = { if (emailError != null) Text(emailError!!) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { passwordFocusRequester.requestFocus() }
                    ),
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        if (passwordError != null) passwordError = null
                    },
                    label = { Text("密碼") },
                    placeholder = { Text("請輸入密碼") },
                    isError = passwordError != null,
                    supportingText = { if (passwordError != null) Text(passwordError!!) },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val icon =
                            if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                icon,
                                contentDescription = if (passwordVisible) "隱藏密碼" else "顯示密碼"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { submit() }),
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(passwordFocusRequester)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = onForgotPassword,
                        contentPadding = PaddingValues(0.dp),
                        enabled = !isLoading
                    ) {
                        Text("忘記密碼？")
                    }
                    TextButton(
                        onClick = {
                            if (validate()) {
                                onRegister(email, password)
                            }
                        },
                        contentPadding = PaddingValues(0.dp),
                        enabled = !isLoading
                    ) {
                        Text("註冊")
                    }
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { submit() },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("以帳號密碼登入")
                    }
                }

                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalDivider(Modifier.weight(1f))
                    Text(
                        "或",
                        modifier = Modifier.padding(horizontal = 10.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                    HorizontalDivider(Modifier.weight(1f))
                }

                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isIOS) {
                        OutlinedButton(
                            onClick = onAppleSignIn,
                            enabled = !isLoading,
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.icon_apple),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Apple")
                        }
                    }
                    OutlinedButton(
                        onClick = onGoogleSignIn,
                        enabled = !isLoading,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.icon_google_logo),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Google")
                    }
                    OutlinedButton(
                        onClick = onAnonymousSignIn,
                        enabled = !isLoading,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    ) {
                        Text("匿名登入")
                    }
                }

                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

fun String.isValidEmail(): Boolean {
    return this.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"))
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    SignInContentScreen(
        onEmailSignIn = { _, _ -> },
        onGoogleSignIn = {},
        onAnonymousSignIn = {},
        onAppleSignIn = {},
        onForgotPassword = {},
        onRegister = { _, _ -> },
    )
}