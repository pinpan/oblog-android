package com.applego.oblog.tppwatch.util

enum class TimePeriod(val desc: String) {
    SinceTheBigBang("Since The Big Bang"),
    ThisYear("This year"),
    PrevYear("Previous year"),
    LastYear("Last year"),
    LastSixMOnths("Last six months"),
    LastQuarter("Last quarter"),
    LastMonth("Last month"),
    LastWeek("Last week");

    companion object {
        fun getByOrdinalValue(order: Int) : TimePeriod {
            enumValues<TimePeriod>().forEach {
                if (it.ordinal == order) {
                    return it
                }
            }
            return SinceTheBigBang
        }

    }
}
