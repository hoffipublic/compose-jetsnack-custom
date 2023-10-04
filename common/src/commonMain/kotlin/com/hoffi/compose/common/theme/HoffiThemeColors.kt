package com.accorddesk.frontend.ui.theme

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

object HoffiThemeSolarized {
    val Base03  = Color(0xFF002b36) // dark theme: background (dark)
    val Base02  = Color(0xFF073642) // dark theme: background (variant)
    val Base01  = Color(0xFF586e75) // content tone: darkest
    val Base00  = Color(0xFF657b83) // .
    val Base0   = Color(0xFF839496) // .
    val Base1   = Color(0xFF93a1a1) // content tone: lightest
    val Base2   = Color(0xFFeee8d5) // light theme: background (dark)
    val Base3   = Color(0xFFfdf6e3) // light theme: background (variant)
    // accent tones
    val Yellow  = Color(0xFFb58900)
    val Orange  = Color(0xFFcb4b16)
    val Red     = Color(0xFFdc322f)
    val Magenta = Color(0xFFd33682)
    val Violet  = Color(0xFF6c71c4)
    val Blue    = Color(0xFF268bd2)
    val Cyan    = Color(0xFF2aa198)
    val Green   = Color(0xFF859900)
}

fun HoffiThemeDarkColorPalette() = darkColors(
    primary = HoffiThemeSolarized.Base1,
    primaryVariant = HoffiThemeSolarized.Base3,
    secondary = Color.Green,
    background = HoffiThemeSolarized.Base03,
    surface = HoffiThemeSolarized.Base02,
    onPrimary = HoffiThemeSolarized.Base1,
    onSecondary = HoffiThemeSolarized.Base03,
    onBackground = HoffiThemeSolarized.Base2,
    onSurface = HoffiThemeSolarized.Base2,
    secondaryVariant = Color.Blue ,
    error = HoffiThemeSolarized.Red,
    onError = HoffiThemeSolarized.Base03
)

fun HoffiThemeLightColorPalette() = lightColors(
    primary = HoffiThemeSolarized.Base01,
    primaryVariant = HoffiThemeSolarized.Base03,
    secondary = Color.Green,
    background = HoffiThemeSolarized.Base3,
    surface = HoffiThemeSolarized.Base2,
    onPrimary = HoffiThemeSolarized.Base01,
    onSecondary = HoffiThemeSolarized.Base3,
    onBackground = HoffiThemeSolarized.Base02,
    onSurface = HoffiThemeSolarized.Base02,
    secondaryVariant = Color.Blue,
    error = HoffiThemeSolarized.Red,
    onError = HoffiThemeSolarized.Base03
)

// making Color a bit lighter by giving it a bit opacity val xcolorLight = xcolor.copy(alpha = 0.1f)
object ADColor {
    // Gray
    val CONCLUDEDBG = Color.LightGray
    // Red
    val REDORANGE = Color(255,165,0)
    val ERRORBG = REDORANGE
    val REDLIGHT = Color(250,128,114)
    val DRAFTBG = REDLIGHT
    val REDDARK = Color(139,0,0)
    // Green
    val GREENLIGHT = Color(50,205,50)
    val ACTIVEBG = GREENLIGHT
    val GREENKHAKI = Color(240,230,140)
    val NEGOBG = GREENKHAKI
    val GREENOLIVE = Color(128,128,0)
    val READYBG = GREENOLIVE
    // Blue
    val BLUELIGHT = Color(135,206,250)
    val SIGNEDBG = BLUELIGHT
    // Yellow
    val YELLOWPALE = Color(255, 255, 158)
    val TEMPLATEBG = YELLOWPALE
    val YELLOWDARK = Color(255, 234, 97)
}
