package com.qxdzbc.p6.ui.format.pack

import com.qxdzbc.common.compose.StateUtils.ms
import com.qxdzbc.p6.ui.format.MockedAttr
import com.qxdzbc.p6.ui.format.marked.MarkedAttributes
import io.kotest.matchers.shouldBe
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ImmutableAttributePackTest {

    @Test
    fun constructor() {
        val count = 10

        val mset = List(count) { MockedAttr(it) }.map {
            ms(MarkedAttributes.valid(it).upCounter())
        }.toMutableSet()

        val pack = ImmutableAttributePack(mset)

        pack.allAttrs.size shouldBe mset.size
        pack.allMarkedAttrs.size shouldBe mset.size
        pack.size shouldBe mset.size
    }

    @Test
    fun remove() {
        val count = 10
        val mset = (0 until count).map { MockedAttr(it) }.map {
            ms(MarkedAttributes.valid(it))
        }.toMutableSet()
        val pack = ImmutableAttributePack(mset)
        val p2 = pack.remove(MarkedAttributes.valid(MockedAttr(3)))
        assertEquals(count - 1, p2.allAttrs.size)
        for (e in p2.allAttrs) {
            assertNotEquals(3, (e as MockedAttr).i)
        }
    }

    @Test
    fun add() {
        val count = 10
        val mset = (0 until count).map { MockedAttr(it) }.map {
            ms(MarkedAttributes.valid(it))
        }.toMutableSet()
        val pack = ImmutableAttributePack(mset)
        val p2 = pack.add(MarkedAttributes.valid(MockedAttr(99)))
        var c= 0
        for(e in p2.allAttrs){
            if((e as MockedAttr).i == 99){
                c+=1
            }
        }
        assertEquals(1,c)
    }

    @Test
    fun cleanInvalidAttribute() {
        val count = 10
        val mset = (0 until count).map { MockedAttr(it) }.map {
            ms(MarkedAttributes.valid(it).upCounter())
        }.toMutableSet()
        val invalidSet = (30 until 35).map { MockedAttr(it)}.map {
            ms(MarkedAttributes.invalid(it))
        }.toMutableSet()
        val pack = ImmutableAttributePack((mset+invalidSet).toMutableSet())
        val p2 = pack.removeInvalidAttribute()
        p2.size shouldBe mset.size
        p2.allMarkedAttrs shouldBe mset.map{it.value}
    }
}
