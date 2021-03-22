/*
 * Copyright 2014-2020 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.routing

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import kotlin.math.*

/**
 * Represents a result of routing resolution.
 *
 * @property route specifies a routing node for successful resolution, or nearest one for failed.
 */
public sealed class RoutingResolveResult(public val route: Route) {
    /**
     * Provides all captured values for this result.
     */
    public abstract val parameters: Parameters

    /**
     * Represents a successful result
     */
    public class Success internal constructor(
        route: Route,
        override val parameters: Parameters,
        internal val quality: Double
    ) :
        RoutingResolveResult(route) {

        @Deprecated("Binary compatibility")
        public constructor(route: Route, parameters: Parameters) : this(route, parameters, 0.0)

        override fun toString(): String = "SUCCESS${if (parameters.isEmpty()) "" else "; $parameters"} @ $route"
    }

    /**
     * Represents a failed result
     * @param reason provides information on reason of a failure
     */
    public class Failure(route: Route, public val reason: String) : RoutingResolveResult(route) {
        override val parameters: Nothing
            get() = throw UnsupportedOperationException("Parameters are available only when routing resolve succeeds")

        override fun toString(): String = "FAILURE \"$reason\" @ $route"
    }
}

/**
 * Represents a context in which routing resolution is being performed
 * @param routing root node for resolution to start at
 * @param call instance of [ApplicationCall] to use during resolution
 */
public class RoutingResolveContext(
    public val routing: Route,
    public val call: ApplicationCall,
    private val tracers: List<(RoutingResolveTrace) -> Unit>
) {

    /**
     * List of path segments parsed out of a [call]
     */
    public val segments: List<String>

    /**
     * Flag showing if path ends with slash
     */
    public val hasTrailingSlash: Boolean = call.request.path().endsWith('/')

    private val trace: RoutingResolveTrace?

    init {
        try {
            segments = parse(call.request.path())
            trace = if (tracers.isEmpty()) null else RoutingResolveTrace(call, segments)
        } catch (cause: URLDecodeException) {
            throw BadRequestException("Url decode failed for ${call.request.uri}", cause)
        }
    }

    private fun parse(path: String): List<String> {
        if (path.isEmpty() || path == "/") return listOf()
        val length = path.length
        var beginSegment = 0
        var nextSegment = 0
        val segmentCount = path.count { it == '/' }
        val segments = ArrayList<String>(segmentCount)
        while (nextSegment < length) {
            nextSegment = path.indexOf('/', beginSegment)
            if (nextSegment == -1) {
                nextSegment = length
            }
            if (nextSegment == beginSegment) {
                // empty path segment, skip it
                beginSegment = nextSegment + 1
                continue
            }
            val segment = path.decodeURLPart(beginSegment, nextSegment)
            segments.add(segment)
            beginSegment = nextSegment + 1
        }
        if (path.endsWith("/")) {
            segments.add("")
        }
        return segments
    }

    /**
     * Executes resolution procedure in this context and returns [RoutingResolveResult]
     */
    public fun resolve(): RoutingResolveResult {
        val root = routing
        val rootEvaluation = root.selector.evaluate(this, 0)
        if (!rootEvaluation.succeeded) {
            val result = RoutingResolveResult.Failure(root, "rootPath didn't match")
            trace?.skip(root, 0, result)
            return result
        }
        val successResults = mutableListOf<List<RoutingResolveResult.Success>>()

        val rootResolveResult = RoutingResolveResult.Success(root, rootEvaluation.parameters, rootEvaluation.quality)
        val rootResolveResults = listOf(rootResolveResult)
        if (rootEvaluation.segmentIncrement == segments.size && root.handlers.isNotEmpty()) {
            successResults.add(rootResolveResults)
        }

        trace?.begin(root, 0)
        resolveStep(
            root,
            successResults,
            rootResolveResults,
            rootEvaluation.segmentIncrement
        )
        trace?.finish(root, 0, rootResolveResult)

        trace?.registerSuccessResults(successResults)
        val resolveResult = findBestRoute(root, successResults)
        trace?.registerFinalResult(resolveResult)

        trace?.apply { tracers.forEach { it(this) } }
        return resolveResult
    }

    private fun resolveStep(
        entry: Route,
        successResults: MutableList<List<RoutingResolveResult.Success>>,
        trait: List<RoutingResolveResult.Success>,
        segmentIndex: Int
    ): Boolean {
        var matchedChildren = false
        if (entry.children.isEmpty() && segmentIndex != segments.size) {
            trace?.skip(entry, segmentIndex, RoutingResolveResult.Failure(entry, "Not all segments matched"))
            return false
        }
        if (entry.handlers.isNotEmpty() && segmentIndex == segments.size) {
            successResults.add(trait)
            matchedChildren = true
        }

        var bestChildResult: RouteSelectorEvaluation? = null

        // iterate using indices to avoid creating iterator
        for (childIndex in 0..entry.children.lastIndex) {
            val child = entry.children[childIndex]
            val selectorResult = child.selector.evaluate(this, segmentIndex)
            if (!selectorResult.succeeded) {
                trace?.skip(child, segmentIndex, RoutingResolveResult.Failure(child, "Selector didn't match"))
                continue // selector didn't match, skip entire subtree
            }
            if (selectorResult.quality < bestChildResult?.quality ?: -Double.MAX_VALUE) {
                trace?.skip(child, segmentIndex, RoutingResolveResult.Failure(child, "Better match was already found"))
                continue
            }

            val result = RoutingResolveResult.Success(child, selectorResult.parameters, selectorResult.quality)
            val newIndex = segmentIndex + selectorResult.segmentIncrement
            trace?.begin(child, newIndex)
            val success = resolveStep(child, successResults, trait + result, newIndex)
            trace?.finish(child, newIndex, result)
            if (success && bestChildResult == null) {
                bestChildResult = selectorResult
            }
            matchedChildren = matchedChildren || success
        }
        return matchedChildren
    }

    private fun findBestRoute(
        root: Route,
        successResults: List<List<RoutingResolveResult.Success>>
    ): RoutingResolveResult {
        if (successResults.isEmpty()) {
            return RoutingResolveResult.Failure(root, "No matched subtrees found")
        }
        val bestPath = successResults
            .maxWithOrNull { result1, result2 ->
                var i1 = 0
                var i2 = 0
                while (i1 < result1.size && i2 < result2.size) {
                    val quality1 = result1[i1].quality
                    val quality2 = result2[i2].quality
                    if (quality1 == RouteSelectorEvaluation.qualityTransparent) {
                        i1++
                        continue
                    }
                    if (quality2 == RouteSelectorEvaluation.qualityTransparent) {
                        i2++
                        continue
                    }
                    if (quality1 != quality2) {
                        return@maxWithOrNull compareValues(quality1, quality2)
                    }
                    i1++
                    i2++
                }
                compareValues(result1.size, result2.size)
            }!!

        val parameters = bestPath
            .fold(ParametersBuilder()) { acc, result -> acc.apply { appendAll(result.parameters) } }
            .build()
        return RoutingResolveResult.Success(
            bestPath.last().route,
            parameters,
            bestPath.minOf { result ->
                result.quality.takeUnless { it == RouteSelectorEvaluation.qualityTransparent }
                    ?: RouteSelectorEvaluation.qualityConstant
            }
        )
    }
}
