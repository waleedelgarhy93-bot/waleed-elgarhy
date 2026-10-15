package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.util.ExcelHelper
import com.example.util.WhatsAppHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("أمن سمالوط", appName)
  }

  @Test
  fun `test whatsapp egyptian number formatting`() {
    val formatted1 = WhatsAppHelper.formatEgyptianNumber("01004567891")
    assertEquals("201004567891", formatted1)

    val formatted2 = WhatsAppHelper.formatEgyptianNumber("+201123456789")
    assertEquals("201123456789", formatted2)
  }

  @Test
  fun `test excel parser with sample schools`() {
    val sample = ExcelHelper.getSampleSchoolsCsv()
    val result = ExcelHelper.parseSchoolsFromText(sample)
    assertTrue(result.items.isNotEmpty())
    assertEquals("مدرسة منقطين الثانوية المشتركة", result.items[0].name)
  }

  @Test
  fun `test excel parser with sample teachers`() {
    val sample = ExcelHelper.getSampleTeachersCsv()
    val result = ExcelHelper.parseTeachersFromText(sample)
    assertTrue(result.items.isNotEmpty())
    assertEquals("أ/ مصطفى كمال محمود", result.items[0].name)
  }
}
