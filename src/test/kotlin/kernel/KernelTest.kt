package kernel

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import utils.gaussianKernel

class KernelTest {

    @Test
    fun testKernel() {
        val kernel = gaussianKernel(3)
        println(kernel)

        Assertions.assertEquals(1.0, kernel.sum())
    }


    @Test
    fun testKernelSum() {
        (1..10).forEach { radius ->
            val kernel = gaussianKernel(radius)
            Assertions.assertTrue(kernel.sum() in 0.9999..1.0001)
        }
    }
}