package dev.gaphunter.prometheusmetricregistrationcompanion.detect

import com.intellij.psi.JavaRecursiveElementWalkingVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.util.PsiTreeUtil
import dev.gaphunter.prometheusmetricregistrationcompanion.model.MetricRegistrationHit

/**
 * Finds `Counter.build()....register()` (and `Histogram`/`Gauge`/
 * `Summary`, Prometheus's Java client's four metric types) written
 * inside a non-constructor method body -- the client's own official
 * documentation states "the best way to register a metric is via a
 * static final class variable" and registration happens against the
 * process-global default registry: calling `.register()` more than
 * once for the same metric name throws a real
 * `IllegalArgumentException` ("Collector already registered that
 * provides name...") at runtime, not just a style nit.
 *
 * **v0.1 scope, stated honestly:** matches by simple text, not real
 * type resolution -- an unrelated `Counter`/`Histogram`/`Gauge`/
 * `Summary` class from a different library sharing the same
 * `build()...register()` shape is a possible (rare) false positive.
 * Only the "build from scratch inside a regular method" shape is
 * flagged; a metric stored as a static final field (the documented
 * correct pattern) is never flagged.
 */
object JavaMetricRegistrationFinder {

    fun findAll(file: PsiFile): List<MetricRegistrationHit> {
        val hits = mutableListOf<MetricRegistrationHit>()
        file.accept(object : JavaRecursiveElementWalkingVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                hitForRegisterCall(expression)?.let { hits += it }
            }
        })
        return hits
    }

    private fun hitForRegisterCall(registerCall: PsiMethodCallExpression): MetricRegistrationHit? {
        if (registerCall.methodExpression.referenceName != "register") return null
        val qualifier = registerCall.methodExpression.qualifierExpression ?: return null

        val metricType = MetricTypeSignals.METRIC_TYPE_NAMES.firstOrNull { type ->
            qualifier.text.startsWith("$type.build(")
        } ?: return null

        return hitIfNotInStaticContext(registerCall, metricType)
    }

    private fun hitIfNotInStaticContext(element: PsiElement, metricType: String): MetricRegistrationHit? {
        val containingMethod = PsiTreeUtil.getParentOfType(element, PsiMethod::class.java) ?: return null
        if (containingMethod.isConstructor) return null
        return MetricRegistrationHit(metricType, leafOf(element))
    }

    private fun leafOf(element: PsiElement): PsiElement {
        var current = element
        while (current.firstChild != null) current = current.firstChild
        return current
    }
}
