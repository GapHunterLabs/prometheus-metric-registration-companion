package dev.gaphunter.prometheusmetricregistrationcompanion.detect

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import dev.gaphunter.prometheusmetricregistrationcompanion.model.MetricRegistrationHit
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtConstructor
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

/** Kotlin counterpart of [JavaMetricRegistrationFinder]. */
object KotlinMetricRegistrationFinder {

    fun findAll(file: PsiFile): List<MetricRegistrationHit> {
        if (file !is KtFile) return emptyList()
        val hits = mutableListOf<MetricRegistrationHit>()
        file.accept(object : KtTreeVisitorVoid() {
            override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
                super.visitDotQualifiedExpression(expression)
                hitForRegisterCall(expression)?.let { hits += it }
            }
        })
        return hits
    }

    private fun hitForRegisterCall(expression: KtDotQualifiedExpression): MetricRegistrationHit? {
        val registerCall = expression.selectorExpression as? KtCallExpression ?: return null
        if (registerCall.calleeExpression?.text != "register") return null

        val metricType = MetricTypeSignals.METRIC_TYPE_NAMES.firstOrNull { type ->
            expression.receiverExpression.text.startsWith("$type.build(")
        } ?: return null

        return hitIfNotInStaticContext(expression, metricType)
    }

    private fun hitIfNotInStaticContext(element: PsiElement, metricType: String): MetricRegistrationHit? {
        if (PsiTreeUtil.getParentOfType(element, KtConstructor::class.java) != null) return null
        if (PsiTreeUtil.getParentOfType(element, KtNamedFunction::class.java) == null) return null
        return MetricRegistrationHit(metricType, leafOf(element))
    }

    private fun leafOf(element: PsiElement): PsiElement {
        var current = element
        while (current.firstChild != null) current = current.firstChild
        return current
    }
}
