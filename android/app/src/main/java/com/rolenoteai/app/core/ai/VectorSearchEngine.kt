package com.rolenoteai.app.core.ai

// CTO: RNA | CEO: L (Godel) | Phase 3c

import kotlin.math.exp
import kotlin.math.sqrt

/**
 * RoleNote AI - Vector Search Engine
 * CTO: RNA | CEO: L (Godel) | Phase 3c
 *
 * Pure-Kotlin vector operations for semantic memory retrieval:
 * - Cosine similarity
 * - Time-decay scoring (MemPalace-Android)
 * - Top-K retrieval
 *
 * Optimized for <10k items. No native/NDK dependencies.
 */
object VectorSearchEngine {

    /**
     * Compute cosine similarity between two vectors.
     * Returns value in [-1, 1]. Identical vectors = 1.0.
     */
    fun cosineSimilarity(a: FloatArray, b: FloatArray): Float {
        require(a.size == b.size) { "Vector dimensions must match: ${a.size} vs ${b.size}" }
        if (a.isEmpty()) return 0f

        var dot = 0f
        var normA = 0f
        var normB = 0f
        for (i in a.indices) {
            dot += a[i] * b[i]
            normA += a[i] * a[i]
            normB += b[i] * b[i]
        }

        val denominator = sqrt(normA) * sqrt(normB)
        return if (denominator == 0f) 0f else dot / denominator
    }

    /**
     * Compute time-decayed similarity score.
     * score = cosineSimilarity(query, candidate) * exp(-lambda * ageSteps)
     *
     * @param query The query embedding vector
     * @param candidate The candidate memory embedding vector
     * @param ageSteps How old the candidate is (in discrete steps, e.g. hours or turns)
     * @param lambda Decay rate. Higher = faster decay. Default 0.05.
     */
    fun decayedScore(
        query: FloatArray,
        candidate: FloatArray,
        ageSteps: Int,
        lambda: Float = 0.05f
    ): Float {
        val similarity = cosineSimilarity(query, candidate)
        val decay = exp(-lambda * ageSteps.toFloat())
        return similarity * decay
    }

    /**
     * Retrieve top-K candidates ranked by decayed similarity score.
     *
     * @param query The query embedding
     * @param candidates List of (id, embedding, ageSteps) triples
     * @param topK Number of results to return
     * @param lambda Decay rate
     * @return List of (id, score) pairs sorted by descending score
     */
    fun topK(
        query: FloatArray,
        candidates: List<Triple<String, FloatArray, Int>>,
        topK: Int = 5,
        lambda: Float = 0.05f
    ): List<Pair<String, Float>> {
        return candidates
            .map { (id, embedding, age) ->
                id to decayedScore(query, embedding, age, lambda)
            }
            .sortedByDescending { it.second }
            .take(topK)
    }

    /**
     * Compute cosine distance (1 - similarity). Used for persona drift detection.
     * Returns value in [0, 2]. 0 = identical, >0.35 = significant drift.
     */
    fun cosineDistance(a: FloatArray, b: FloatArray): Float {
        return 1f - cosineSimilarity(a, b)
    }

    /**
     * Normalize a vector to unit length (L2 normalization).
     */
    fun normalize(vector: FloatArray): FloatArray {
        val norm = sqrt(vector.sumOf { (it * it).toDouble() }).toFloat()
        return if (norm == 0f) vector else FloatArray(vector.size) { vector[it] / norm }
    }
}
