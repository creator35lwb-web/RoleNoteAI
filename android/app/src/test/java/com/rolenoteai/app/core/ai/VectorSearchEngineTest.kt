package com.rolenoteai.app.core.ai

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

/**
 * RoleNote AI - Vector Search Engine Test
 * CTO: RNA | CEO: L (Godel) | Phase 3c
 */
class VectorSearchEngineTest {

    private val epsilon = 1e-5f

    @Test
    fun testCosineSimilarity_IdenticalVectors() {
        val a = floatArrayOf(1.0f, 2.0f, 3.0f)
        val b = floatArrayOf(1.0f, 2.0f, 3.0f)
        val similarity = VectorSearchEngine.cosineSimilarity(a, b)
        assertEquals(1.0f, similarity, epsilon)
    }

    @Test
    fun testCosineSimilarity_OrthogonalVectors() {
        val a = floatArrayOf(1.0f, 0.0f, 0.0f)
        val b = floatArrayOf(0.0f, 1.0f, 0.0f)
        val similarity = VectorSearchEngine.cosineSimilarity(a, b)
        assertEquals(0.0f, similarity, epsilon)
    }

    @Test
    fun testCosineSimilarity_OppositeVectors() {
        val a = floatArrayOf(1.0f, 0.0f, -1.0f)
        val b = floatArrayOf(-1.0f, 0.0f, 1.0f)
        val similarity = VectorSearchEngine.cosineSimilarity(a, b)
        assertEquals(-1.0f, similarity, epsilon)
    }

    @Test
    fun testCosineDistance_IdenticalVectors() {
        val a = floatArrayOf(0.5f, 0.5f, 0.5f)
        val b = floatArrayOf(0.5f, 0.5f, 0.5f)
        val distance = VectorSearchEngine.cosineDistance(a, b)
        assertEquals(0.0f, distance, epsilon)
    }

    @Test
    fun testCosineDistance_OppositeVectors() {
        val a = floatArrayOf(1.0f, 0.0f, 0.0f)
        val b = floatArrayOf(-1.0f, 0.0f, 0.0f)
        val distance = VectorSearchEngine.cosineDistance(a, b)
        assertEquals(2.0f, distance, epsilon)
    }

    @Test
    fun testNormalize() {
        val vector = floatArrayOf(3.0f, 4.0f, 0.0f) // Magnitude is 5.0
        val normalized = VectorSearchEngine.normalize(vector)
        
        assertEquals(0.6f, normalized[0], epsilon)
        assertEquals(0.8f, normalized[1], epsilon)
        assertEquals(0.0f, normalized[2], epsilon)

        // The magnitude of normalized vector should be 1.0
        var sumSquares = 0.0f
        for (v in normalized) {
            sumSquares += v * v
        }
        assertEquals(1.0f, sumSquares, epsilon)
    }

    @Test
    fun testNormalize_ZeroVector() {
        val vector = floatArrayOf(0.0f, 0.0f, 0.0f)
        val normalized = VectorSearchEngine.normalize(vector)
        assertTrue(normalized.all { it == 0f })
    }

    @Test
    fun testDecayedScore() {
        val query = floatArrayOf(1.0f, 0.0f)
        val candidate = floatArrayOf(1.0f, 0.0f) // Cosine similarity is 1.0
        val lambda = 0.1f
        val ageSteps = 10

        // expected similarity = 1.0
        // expected decay = exp(-0.1 * 10) = exp(-1.0) approx 0.36787944
        val expectedScore = 0.36787944f
        val score = VectorSearchEngine.decayedScore(query, candidate, ageSteps, lambda)
        assertEquals(expectedScore, score, epsilon)
    }

    @Test
    fun testTopK() {
        val query = floatArrayOf(1.0f, 0.0f)
        
        // Candidates: (id, embedding, ageSteps)
        val candidates = listOf(
            Triple("memory_1", floatArrayOf(1.0f, 0.0f), 0),   // sim = 1.0, age = 0 -> score = 1.0
            Triple("memory_2", floatArrayOf(1.0f, 0.0f), 10),  // sim = 1.0, age = 10, lambda = 0.1 -> score = 0.36787944
            Triple("memory_3", floatArrayOf(0.0f, 1.0f), 0),   // sim = 0.0, age = 0 -> score = 0.0
            Triple("memory_4", floatArrayOf(0.8f, 0.6f), 2)    // sim = 0.8, age = 2, lambda = 0.1 -> score = 0.8 * exp(-0.2) approx 0.8 * 0.81873075 = 0.6549846
        )

        val topResults = VectorSearchEngine.topK(query, candidates, topK = 3, lambda = 0.1f)

        assertEquals(3, topResults.size)
        // Order should be: memory_1 (1.0), memory_4 (~0.655), memory_2 (~0.368)
        assertEquals("memory_1", topResults[0].first)
        assertEquals("memory_4", topResults[1].first)
        assertEquals("memory_2", topResults[2].first)

        assertEquals(1.0f, topResults[0].second, epsilon)
        assertEquals(0.6549846f, topResults[1].second, epsilon)
        assertEquals(0.36787944f, topResults[2].second, epsilon)
    }
}
