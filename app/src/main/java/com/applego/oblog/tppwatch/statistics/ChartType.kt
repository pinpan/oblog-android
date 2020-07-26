package com.applego.oblog.tppwatch.statistics;

enum class ChartType (val desc: String) {
    PerCountry("Tpps per country"),
    PerInstitutionType("Tpps per institution type"),
    PerCountryChange("Tpps per country change"),
    PerInstitutionTypeChange("tpps per institution type change");
}
