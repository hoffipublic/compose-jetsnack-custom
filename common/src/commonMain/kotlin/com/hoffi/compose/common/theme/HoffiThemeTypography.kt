package com.accorddesk.frontend.ui.theme

import androidx.compose.material.Typography

fun HoffiThemeDarkTypography() : Typography {
    val orig = Typography()
    return orig
        .copy(
            subtitle1 = orig.subtitle1.copy(color = HoffiThemeDarkColorPalette().onBackground),
            subtitle2 = orig.subtitle2.copy(color = HoffiThemeDarkColorPalette().onBackground),
            body1 = orig.body1.copy(color = HoffiThemeDarkColorPalette().onSurface),
            body2 = orig.body2.copy(color = HoffiThemeDarkColorPalette().onSurface)
        )
}

fun HoffiThemeLightTypography() : Typography {
    val orig = Typography()
    return orig
        .copy(
            subtitle1 = orig.subtitle1.copy(color = HoffiThemeLightColorPalette().onBackground),
            subtitle2 = orig.subtitle2.copy(color = HoffiThemeLightColorPalette().onBackground),
            body1 = orig.body1.copy(color = HoffiThemeLightColorPalette().onSurface),
            body2 = orig.body2.copy(color = HoffiThemeLightColorPalette().onSurface)
        )
}

//object ADTheme {
//    fun h1() = if(ADState.ADTheme.isDarkTheme()) DarkTypography().h1 else LightTypography().h1
//    fun h2() = if(ADState.ADTheme.isDarkTheme()) DarkTypography().h2 else LightTypography().h2
//    fun h3() = if(ADState.ADTheme.isDarkTheme()) DarkTypography().h3 else LightTypography().h3
//    fun h4() = if(ADState.ADTheme.isDarkTheme()) DarkTypography().h4 else LightTypography().h4
//    fun h5() = if(ADState.ADTheme.isDarkTheme()) DarkTypography().h5 else LightTypography().h5
//    fun h6() = if(ADState.ADTheme.isDarkTheme()) DarkTypography().h6 else LightTypography().h6
//    fun subtitle1() = if(ADState.ADTheme.isDarkTheme()) DarkTypography().subtitle1 else LightTypography().subtitle1
//    fun subtitle2() = if(ADState.ADTheme.isDarkTheme()) DarkTypography().subtitle2 else LightTypography().subtitle2
//    fun body1() = if(ADState.ADTheme.isDarkTheme()) DarkTypography().body1 else LightTypography().body1
//    fun body2() = if(ADState.ADTheme.isDarkTheme()) DarkTypography().body2 else LightTypography().body2
//    fun button() = if(ADState.ADTheme.isDarkTheme()) DarkTypography().button else LightTypography().button
//    fun caption() = if(ADState.ADTheme.isDarkTheme()) DarkTypography().caption else LightTypography().caption
//    fun overline() = if(ADState.ADTheme.isDarkTheme()) DarkTypography().overline else LightTypography().overline
//}

