package com.volozhinsky.lifetasktracker.ui.utils

object UtilsLogResults {

    fun Int.caseResult(caseArray: Array<String>): String{
        return when(this) {
            0 -> ""
            in(11..20) -> "$this ${caseArray[2]}" //дней часов минут
            else -> {
                when(this % 10){
                    1 -> "$this ${caseArray[0]} " // день час минута
                    in(2..4) -> "$this ${caseArray[1]}" //дня часа минуты
                    else -> "$this ${caseArray[2]}" //дней часов минут
                }
            }
        }
    }
}