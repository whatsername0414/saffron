package com.saffron.cook.feature.welcome.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.saffron.cook.core.designsystem.theme.Cream
import com.saffron.cook.core.designsystem.theme.InterFamily
import com.saffron.cook.core.designsystem.theme.PlayfairDisplayFamily
import com.saffron.cook.core.designsystem.theme.Saffron
import com.saffron.cook.core.designsystem.theme.Saffron40
import com.saffron.cook.core.designsystem.theme.SaffronTheme
import com.saffron.cook.feature.welcome.R
import org.koin.androidx.compose.koinViewModel

private val CreamTranslucent = Color(0xFFF5E8C8)
private val ScrimBase = Color(0xFF1A1208)
private val RiseEasing = CubicBezierEasing(0f, 0f, 0.2f, 1f)

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    viewModel: WelcomeViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    WelcomeContent(
        state = state,
        onNext = viewModel::next,
        onSkip = {
            viewModel.completeOnboarding()
            onGetStarted()
        },
        onGoTo = viewModel::goTo,
        onGetStarted = {
            viewModel.completeOnboarding()
            onGetStarted()
        },
    )
}

@Composable
private fun WelcomeContent(
    state: WelcomeUiState,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    onGoTo: (Int) -> Unit,
    onGetStarted: () -> Unit,
) {
    val view = LocalView.current
    if (!LocalInspectionMode.current) {
        val window = (view.context as? android.app.Activity)?.window
        if (window != null) {
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        Image(
            painter = painterResource(slideImageRes(state.currentSlide)),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to ScrimBase.copy(alpha = 0.42f),
                        0.22f to ScrimBase.copy(alpha = 0f),
                        1f to ScrimBase.copy(alpha = 0f),
                    ),
                ),
        )

        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to ScrimBase.copy(alpha = 0f),
                        0.36f to ScrimBase.copy(alpha = 0f),
                        0.66f to ScrimBase.copy(alpha = 0.58f),
                        1f to ScrimBase.copy(alpha = 0.94f),
                    ),
                ),
        )

        if (!state.isLastSlide) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(460.dp)
                    .align(Alignment.TopStart)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onNext,
                    ),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 24.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.welcome_wordmark),
                fontFamily = PlayfairDisplayFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 22.sp,
                letterSpacing = (-0.5).sp,
                color = Cream,
            )
            if (!state.isLastSlide) {
                Text(
                    text = stringResource(R.string.welcome_skip),
                    fontFamily = InterFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = CreamTranslucent.copy(alpha = 0.85f),
                    modifier = Modifier
                        .padding(6.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onSkip,
                        ),
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 30.dp),
        ) {
            AnimatedContent(
                targetState = state.currentSlide,
                transitionSpec = {
                    (fadeIn(tween(400, easing = RiseEasing)) +
                        slideInVertically(tween(400, easing = RiseEasing)) { it / 12 })
                        .togetherWith(fadeOut(tween(120)))
                },
                label = "welcome-slide-text",
            ) { slide ->
                Column {
                    Text(
                        text = stringResource(overlineRes(slide)).uppercase(),
                        fontFamily = InterFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
                        letterSpacing = 1.1.sp,
                        color = Saffron40,
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = stringResource(headlineRes(slide)),
                        fontFamily = PlayfairDisplayFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 32.sp,
                        lineHeight = 36.sp,
                        color = Cream,
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = stringResource(bodyRes(slide)),
                        fontFamily = InterFamily,
                        fontWeight = FontWeight.Light,
                        fontSize = 15.sp,
                        lineHeight = 25.sp,
                        color = CreamTranslucent.copy(alpha = 0.82f),
                        modifier = Modifier.widthIn(max = 300.dp),
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(WelcomeUiState.SLIDE_COUNT) { index ->
                    val active = index == state.currentSlide
                    val width by animateDpAsState(
                        targetValue = if (active) 22.dp else 6.dp,
                        animationSpec = tween(250, easing = FastOutSlowInEasing),
                        label = "dot-width",
                    )
                    Box(
                        modifier = Modifier
                            .width(width)
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(if (active) Saffron else CreamTranslucent.copy(alpha = 0.45f))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onGoTo(index) },
                            ),
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            Button(
                onClick = if (state.isLastSlide) onGetStarted else onNext,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Saffron),
                elevation = ButtonDefaults.buttonElevation(0.dp),
            ) {
                Text(
                    text = stringResource(if (state.isLastSlide) R.string.welcome_get_started else R.string.welcome_continue),
                    fontFamily = InterFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

private fun slideImageRes(slide: Int) = when (slide) {
    0 -> R.drawable.welcome_slide_0
    1 -> R.drawable.welcome_slide_1
    else -> R.drawable.welcome_slide_2
}

private fun overlineRes(slide: Int) = when (slide) {
    0 -> R.string.welcome_overline_0
    1 -> R.string.welcome_overline_1
    else -> R.string.welcome_overline_2
}

private fun headlineRes(slide: Int) = when (slide) {
    0 -> R.string.welcome_headline_0
    1 -> R.string.welcome_headline_1
    else -> R.string.welcome_headline_2
}

private fun bodyRes(slide: Int) = when (slide) {
    0 -> R.string.welcome_body_0
    1 -> R.string.welcome_body_1
    else -> R.string.welcome_body_2
}

@Preview(showBackground = true)
@Composable
private fun WelcomeScreenSlide0Preview() {
    SaffronTheme {
        WelcomeContent(
            state = WelcomeUiState(currentSlide = 0),
            onNext = {},
            onSkip = {},
            onGoTo = {},
            onGetStarted = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WelcomeScreenSlide2Preview() {
    SaffronTheme {
        WelcomeContent(
            state = WelcomeUiState(currentSlide = 2),
            onNext = {},
            onSkip = {},
            onGoTo = {},
            onGetStarted = {},
        )
    }
}
