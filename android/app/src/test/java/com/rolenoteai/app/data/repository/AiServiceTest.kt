package com.rolenoteai.app.data.repository

import android.content.Context
import android.content.res.AssetManager
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.io.FileNotFoundException

/**
 * RoleNote AI - AI Service Test (Mock Fallback Mode)
 * CTO: RNA | CEO: L (Godel) | Phase 3c
 */
class AiServiceTest {

    private lateinit var mockContext: Context
    private lateinit var mockAssets: AssetManager
    private lateinit var aiService: AiService

    @Before
    fun setUp() {
        mockContext = mock(Context::class.java)
        mockAssets = mock(AssetManager::class.java)
        whenever(mockContext.assets).thenReturn(mockAssets)
        
        // assets.open() throws FileNotFoundException -> triggers mock fallback
        whenever(mockAssets.open(any())).thenThrow(FileNotFoundException::class.java)
        
        aiService = AiService(mockContext)
    }

    @Test
    fun testInitialization_fallbackToMockMode() = runTest {
        aiService.initialize()
        assertTrue(aiService.isMockMode)
    }

    @Test
    fun testGenerateEmbedding_mockMode() = runTest {
        aiService.initialize()
        
        val text = "Test query for embedding"
        val result = aiService.generateEmbedding(text)
        
        assertTrue(result.isSuccess)
        val embedding = result.getOrThrow()
        
        // Assert dimension size is 384
        assertEquals(384, embedding.size)
        
        // Assert L2 normalization (sum of squares approx 1.0)
        var sumSquares = 0.0f
        for (v in embedding) {
            sumSquares += v * v
        }
        assertEquals(1.0f, sumSquares, 1e-4f)
    }

    @Test
    fun testGenerateEmbedding_emptyText_returnsFailure() = runTest {
        aiService.initialize()
        val result = aiService.generateEmbedding("")
        assertTrue(result.isFailure)
    }

    @Test
    fun testGenerateResponse_mockMode() = runTest {
        aiService.initialize()
        val prompt = "Translate this text"
        val persona = "Assistant"
        
        val result = aiService.generateResponse(prompt, persona)
        
        assertTrue(result.isSuccess)
        val response = result.getOrThrow()
        assertTrue(response.contains("[Mock AI] Response to: "))
    }

    @Test
    fun testCompilePrompt_matchesMACP() {
        val context = "System is running on device."
        val role = "helpful assistant"
        val task = "describe local state"
        
        val prompt = aiService.compilePrompt(context, role, task)
        
        val expected = """
            [CONTEXT]
            System is running on device.

            [ROLE]
            helpful assistant

            [TASK]
            describe local state
        """.trimIndent()
        
        assertEquals(expected, prompt)
    }

    @Test
    fun testCheckPersonaDrift_mockMode() = runTest {
        aiService.initialize()
        val response = "This is a response."
        val persona = "Persona description."
        
        val result = aiService.checkPersonaDrift(response, persona)
        
        assertTrue(result.isSuccess)
        val drift = result.getOrThrow()
        
        // Cosine distance range is [0, 2]
        assertTrue(drift in 0f..2f)
    }
}
