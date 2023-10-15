package com.accorddesk.frontend.ui.theme

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color
import com.hoffi.compose.showcase.appState

fun themeBasedAccentColor() = if (appState.isDarkTheme) Color.Black else Color.White
fun themeBasedAccentColorOpposite() = if (appState.isDarkTheme) Color.White else Color.Black
fun hiddenFuncBackground() = if (appState.isDarkTheme) Color.DarkGray else Color.LightGray

object HoffiThemeSolarized {
    /** @startuml
    title
    |<#002b36> Base03 |
    |<#073642> Base02 |
    |<#586e75> Base01 |
    |<#657b83> Base00 |
    |<#839496> Base0 |
    |<#93a1a1> Base1 |
    |<#eee8d5> Base2 |
    |<#fdf6e3> Base3 |
    end title
    @enduml */
    val Base03  = Color(0xFF002b36) // dark theme: background (dark)
    val Base02  = Color(0xFF073642) // dark theme: background (variant)
    val Base01  = Color(0xFF586e75) // content tone: darkest
    val Base00  = Color(0xFF657b83) // .
    val Base0   = Color(0xFF839496) // .
    val Base1   = Color(0xFF93a1a1) // content tone: lightest
    val Base2   = Color(0xFFeee8d5) // light theme: background (dark)
    val Base3   = Color(0xFFfdf6e3) // light theme: background (variant)
    // accent tones
    /** @startuml
    title
    |<#b58900> Yellow |
    |<#cb4b16> Orange |
    |<#dc322f> Red |
    |<#d33682> Magenta |
    |<#6c71c4> Violet |
    |<#268bd2> Blue |
    |<#2aa198> Cyan |
    |<#859900> Green |
    end title
    @enduml */
    val Yellow  = Color(0xFFb58900)
    val Orange  = Color(0xFFcb4b16)
    val Red     = Color(0xFFdc322f)
    val Magenta = Color(0xFFd33682)
    val Violet  = Color(0xFF6c71c4)
    val Blue    = Color(0xFF268bd2)
    val Cyan    = Color(0xFF2aa198)
    val Green   = Color(0xFF859900)
}

/** @startuml
title
|<#93a1a1> primary |
|<#fdf6e3> primaryVariant |
|<#00FF00> secondary |
|<#002b36> background |
|<#073642> surface |
|<#93a1a1> onPrimary |
|<#002b36> onSecondary |
|<#eee8d5> onBackground |
|<#eee8d5> onSurface |
|<#0000FF> secondaryVariant |
|<#dc322f> error |
|<#002b36> onError |
end title
@enduml */
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

/** @startuml
title
|<#586e75> primary |
|<#002b36> primaryVariant |
|<#00FF00> secondary |
|<#fdf6e3> background |
|<#eee8d5> surface |
|<#586e75> onPrimary |
|<#fdf6e3> onSecondary |
|<#073642> onBackground |
|<#073642> onSurface |
|<#0000FF> secondaryVariant |
|<#dc322f> error |
|<#002b36> onError |
end title
@enduml */
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

/** making Color a bit lighter by giving it a bit opacity val xcolorLight = xcolor.copy(alpha = 0.1f)
    @startuml
    title
    |// Gray |
    |// Red |
    |<#FFA500> REDORANGE |
    |<#FA8072> REDLIGHT |
    |<#8B0000> REDDARK |
    |// Green |
    |<#32CD32> GREENLIGHT |
    |<#F0E68C> GREENKHAKI |
    |<#808000> GREENOLIVE |
    |// Blue |
    |<#87CEFA> BLUELIGHT |
    |// Yellow |
    |<#FFFF9E> YELLOWPALE |
    |<#FFEA61> YELLOWDARK |
    end title
    @enduml */
object ADColor {
    // Gray
    val CONCLUDEDBG = Color.LightGray
    // Red
    val REDORANGE = Color(0xFFFFA500)
    val ERRORBG = REDORANGE
    val REDLIGHT = Color(0xFFFA8072)
    val DRAFTBG = REDLIGHT
    val REDDARK = Color(0xFF8B0000)
    // Green
    val GREENLIGHT = Color(0xFF32CD32)
    val ACTIVEBG = GREENLIGHT
    val GREENKHAKI = Color(0xFFF0E68C)
    val NEGOBG = GREENKHAKI
    val GREENOLIVE = Color(0xFF808000)
    val READYBG = GREENOLIVE
    // Blue
    val BLUELIGHT = Color(0xFF87CEFA)
    val SIGNEDBG = BLUELIGHT
    // Yellow
    val YELLOWPALE = Color(0xFFFFFF9E)
    val TEMPLATEBG = YELLOWPALE
    val YELLOWDARK = Color(0xFFFFEA61)
}
