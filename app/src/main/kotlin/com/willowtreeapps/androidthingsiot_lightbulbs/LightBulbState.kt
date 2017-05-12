package com.willowtreeapps.androidthingsiot_lightbulbs

data class LightBulbState(
        var redBulbState: Boolean = false,
        var greenBulbState: Boolean = false,
        var blueBulbState: Boolean = false
)
