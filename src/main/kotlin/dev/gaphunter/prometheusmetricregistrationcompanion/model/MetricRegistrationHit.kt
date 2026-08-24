package dev.gaphunter.prometheusmetricregistrationcompanion.model

import com.intellij.psi.PsiElement

/** One `Counter.build()....register()`-shaped call site built inside a non-constructor, non-static-initializer method. */
data class MetricRegistrationHit(val metricType: String, val callElement: PsiElement)
