package dev.gaphunter.prometheusmetricregistrationcompanion.gutter

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import dev.gaphunter.prometheusmetricregistrationcompanion.detect.JavaMetricRegistrationFinder
import dev.gaphunter.prometheusmetricregistrationcompanion.detect.KotlinMetricRegistrationFinder
import dev.gaphunter.prometheusmetricregistrationcompanion.model.MetricRegistrationHit
import dev.gaphunter.prometheusmetricregistrationcompanion.review.ReviewPrompt

class MetricRegisteredPerCallLineMarkerProvider : LineMarkerProviderDescriptor(), DumbAware {

    override fun getName(): String = "Prometheus metric built inside a method"

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? = null

    override fun collectSlowLineMarkers(elements: MutableList<out PsiElement>, result: MutableCollection<in LineMarkerInfo<*>>) {
        val file = elements.firstOrNull()?.containingFile ?: return
        val hits = when (file.language.id) {
            "JAVA" -> JavaMetricRegistrationFinder.findAll(file)
            "kotlin" -> KotlinMetricRegistrationFinder.findAll(file)
            else -> emptyList()
        }
        if (hits.isEmpty()) return

        val hitsByElement = hits.associateBy { it.callElement }
        for (element in elements) {
            val hit = hitsByElement[element] ?: continue
            result.add(buildMarker(hit))

            val path = file.virtualFile?.path ?: continue
            val lineNumber = file.viewProvider.document?.getLineNumber(element.textRange.startOffset) ?: -1
            ReviewPrompt.recordHit(file.project, "$path:$lineNumber")
        }
    }

    private fun buildMarker(hit: MetricRegistrationHit): LineMarkerInfo<PsiElement> {
        val tooltip = "This ${hit.metricType} is built and registered here inside a method -- Prometheus's own " +
            "docs say the best way to register a metric is via a static final class variable; calling " +
            "register() more than once for the same name throws IllegalArgumentException at runtime"
        return LineMarkerInfo(
            hit.callElement,
            hit.callElement.textRange,
            MetricRegistrationIcons.RISK,
            { _: PsiElement -> tooltip },
            null,
            GutterIconRenderer.Alignment.RIGHT,
            { tooltip },
        )
    }
}
