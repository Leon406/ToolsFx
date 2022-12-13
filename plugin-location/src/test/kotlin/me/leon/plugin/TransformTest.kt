package me.leon.plugin

import kotlin.test.Test
import me.leon.toolsfx.plugin.CoordinatorTransform

/**
 * @author Leon
 * @since 2022-10-21 17:22
 */
class TransformTest {
    @Test
    fun lbh() {
        println(CoordinatorTransform.lbh2xyz(108.964_176, -34.218_229, 427.0).contentToString())
    }

    @Test
    fun xyz() {
        println(
            CoordinatorTransform.xyz2lbh(
                    -2_069_660.736_791_513_6,
                    6_022_962.061_253_682,
                    4_302_093.211_439_487,
                    "cgcs2000"
                )
                .contentToString()
        )
    }
}
