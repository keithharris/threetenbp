/*
 * Copyright (c) 2008-2011, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package javax.time.calendar;

import static javax.time.calendar.ISODateTimeRule.DAY_OF_MONTH;
import static javax.time.calendar.ISODateTimeRule.MONTH_OF_QUARTER;
import static javax.time.calendar.ISODateTimeRule.MONTH_OF_YEAR;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import javax.time.Instant;
import javax.time.TimeSource;
import javax.time.calendar.format.CalendricalParseException;
import javax.time.calendar.format.DateTimeFormatters;
import javax.time.calendar.format.MockSimpleCalendrical;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test MonthDay.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestMonthDay {

    private MonthDay TEST_07_15;

    @BeforeMethod
    public void setUp() {
        TEST_07_15 = MonthDay.of(7, 15);
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        Object obj = TEST_07_15;
        assertTrue(obj instanceof Calendrical);
        assertTrue(obj instanceof Serializable);
        assertTrue(obj instanceof Comparable<?>);
        assertTrue(obj instanceof DateAdjuster);
        assertTrue(obj instanceof CalendricalMatcher);
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(TEST_07_15);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), TEST_07_15);
    }

    public void test_immutable() {
        Class<MonthDay> cls = MonthDay.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            assertTrue(Modifier.isPrivate(field.getModifiers()));
            assertTrue(Modifier.isFinal(field.getModifiers()));
        }
    }

    //-----------------------------------------------------------------------
    void check(MonthDay test, int m, int d) {
        assertEquals(test.getMonthOfYear().getValue(), m);
        assertEquals(test.getDayOfMonth(), d);
    }

    //-----------------------------------------------------------------------
    // now()
    //-----------------------------------------------------------------------
    public void now() {
        MonthDay expected = MonthDay.now(Clock.systemDefaultZone());
        MonthDay test = MonthDay.now();
        for (int i = 0; i < 100; i++) {
            if (expected.equals(test)) {
                return;
            }
            expected = MonthDay.now(Clock.systemDefaultZone());
            test = MonthDay.now();
        }
        assertEquals(test, expected);
    }

    //-----------------------------------------------------------------------
    // now(Clock)
    //-----------------------------------------------------------------------
    public void now_Clock() {
        Instant instant = Instant.of(OffsetDateTime.of(2010, 12, 31, 0, 0, ZoneOffset.UTC));
        Clock clock = Clock.clock(TimeSource.fixed(instant), ZoneId.UTC);
        MonthDay test = MonthDay.now(clock);
        assertEquals(test.getMonthOfYear(), MonthOfYear.DECEMBER);
        assertEquals(test.getDayOfMonth(), 31);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void now_Clock_nullClock() {
        MonthDay.now(null);
    }

    //-----------------------------------------------------------------------
    public void factory_intMonth() {
        assertEquals(TEST_07_15, MonthDay.of(MonthOfYear.JULY, 15));
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_intMonth_dayTooLow() {
        try {
            MonthDay.of(MonthOfYear.JANUARY, 0);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), DAY_OF_MONTH);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_intMonth_dayTooHigh() {
        try {
            MonthDay.of(MonthOfYear.JANUARY, 32);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), DAY_OF_MONTH);
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_intMonth_nullMonth() {
        MonthDay.of(null, 15);
    }

    //-----------------------------------------------------------------------
    public void factory_ints() {
        check(TEST_07_15, 7, 15);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_ints_dayTooLow() {
        try {
            MonthDay.of(1, 0);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), DAY_OF_MONTH);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_ints_dayTooHigh() {
        try {
            MonthDay.of(1, 32);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), DAY_OF_MONTH);
            throw ex;
        }
    }


    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_ints_monthTooLow() {
        try {
            MonthDay.of(0, 1);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), MONTH_OF_YEAR);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_ints_monthTooHigh() {
        try {
            MonthDay.of(13, 1);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), MONTH_OF_YEAR);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    public void factory_Calendrical() {
        Calendrical cal = new MockSimpleCalendrical(MONTH_OF_YEAR, DateTimeField.of(MONTH_OF_YEAR, 7), DAY_OF_MONTH, DateTimeField.of(DAY_OF_MONTH, 15));
        assertEquals(MonthDay.of(cal), TEST_07_15);
    }

    public void factory_Calendrical_otherFieldsIgnored() {
        Calendrical cal = LocalDate.of(2007, 7, 15);
        assertEquals(MonthDay.of(cal), TEST_07_15);
    }

    @Test(expectedExceptions=UnsupportedRuleException.class)
    public void factory_Calendrical_unsupportedField() {
        Calendrical cal = LocalTime.of(12, 30);
        MonthDay.of(cal);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_Calendrical_null() {
        MonthDay.of((Calendrical) null);
    }

    //-----------------------------------------------------------------------
    // parse()
    //-----------------------------------------------------------------------
    @DataProvider(name="goodParseData")
    Object[][] provider_goodParseData() {
        return new Object[][] {
                {"--01-01", MonthDay.of(1, 1)},
                {"--01-31", MonthDay.of(1, 31)},
                {"--02-01", MonthDay.of(2, 1)},
                {"--02-29", MonthDay.of(2, 29)},
                {"--03-01", MonthDay.of(3, 1)},
                {"--03-31", MonthDay.of(3, 31)},
                {"--04-01", MonthDay.of(4, 1)},
                {"--04-30", MonthDay.of(4, 30)},
                {"--05-01", MonthDay.of(5, 1)},
                {"--05-31", MonthDay.of(5, 31)},
                {"--06-01", MonthDay.of(6, 1)},
                {"--06-30", MonthDay.of(6, 30)},
                {"--07-01", MonthDay.of(7, 1)},
                {"--07-31", MonthDay.of(7, 31)},
                {"--08-01", MonthDay.of(8, 1)},
                {"--08-31", MonthDay.of(8, 31)},
                {"--09-01", MonthDay.of(9, 1)},
                {"--09-30", MonthDay.of(9, 30)},
                {"--10-01", MonthDay.of(10, 1)},
                {"--10-31", MonthDay.of(10, 31)},
                {"--11-01", MonthDay.of(11, 1)},
                {"--11-30", MonthDay.of(11, 30)},
                {"--12-01", MonthDay.of(12, 1)},
                {"--12-31", MonthDay.of(12, 31)},
        };
    }

    @Test(dataProvider="goodParseData")
    public void factory_parse_success(String text, MonthDay expected) {
        MonthDay monthDay = MonthDay.parse(text);
        assertEquals(monthDay, expected);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="badParseData")
    Object[][] provider_badParseData() {
        return new Object[][] {
                {"", 0},
                {"-00", 0},
                {"--FEB-23", 2},
                {"--01-0", 5},
                {"--01-3A", 5},
        };
    }

    @Test(dataProvider="badParseData", expectedExceptions=CalendricalParseException.class)
    public void factory_parse_fail(String text, int pos) {
        try {
            MonthDay.parse(text);
            fail(String.format("Parse should have failed for %s at position %d", text, pos));
        }
        catch (CalendricalParseException ex) {
            assertEquals(ex.getParsedString(), text);
            assertEquals(ex.getErrorIndex(), pos);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_parse_illegalValue_Day() {
        MonthDay.parse("--06-32");
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void factory_parse_invalidValue_Day() {
        MonthDay.parse("--06-31");
    }

    // TODO: Fix code
//    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
//    public void factory_parse_illegalValue_Month() {
//        MonthDay.parse("--13-25");
//    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_parse_nullText() {
        MonthDay.parse(null);
    }

    //-----------------------------------------------------------------------
    // parse(DateTimeFormatter)
    //-----------------------------------------------------------------------
    public void factory_parse_formatter() {
        MonthDay t = MonthDay.parse("12 03", DateTimeFormatters.pattern("MM dd"));
        assertEquals(t, MonthDay.of(12, 3));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_parse_formatter_nullText() {
        MonthDay.parse((String) null, DateTimeFormatters.basicIsoDate());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_parse_formatter_nullFormatter() {
        MonthDay.parse("12 03", null);
    }

    //-----------------------------------------------------------------------
    // getChronology()
    //-----------------------------------------------------------------------
    public void test_getChronology() {
        assertEquals(ISOChronology.INSTANCE, TEST_07_15.getChronology());
    }

    //-----------------------------------------------------------------------
    // get(CalendricalRule)
    //-----------------------------------------------------------------------
    public void test_get_CalendricalRule() {
        MonthDay test = MonthDay.of(6, 12);
        assertEquals(test.get(Chronology.rule()), ISOChronology.INSTANCE);
        assertEquals(test.get(MONTH_OF_YEAR), MONTH_OF_YEAR.field(6));
        assertEquals(test.get(MONTH_OF_QUARTER), MONTH_OF_QUARTER.field(3));
        assertEquals(test.get(DAY_OF_MONTH), DAY_OF_MONTH.field(12));
    }

    @Test(expectedExceptions=NullPointerException.class )
    public void test_get_CalendricalRule_null() {
        MonthDay test = MonthDay.of(6, 12);
        test.get((CalendricalRule<?>) null);
    }

    public void test_get_unsupported() {
        MonthDay test = MonthDay.of(6, 12);
        assertEquals(test.get(MockRuleNoValue.INSTANCE), null);
    }

    //-----------------------------------------------------------------------
    // get*()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleDates")
    Object[][] provider_sampleDates() {
        return new Object[][] {
            {1, 1},
            {1, 31},
            {2, 1},
            {2, 28},
            {2, 29},
            {7, 4},
            {7, 5},
        };
    }

    @Test(dataProvider="sampleDates")
    public void test_get(int m, int d) {
        MonthDay a = MonthDay.of(m, d);
        assertEquals(a.getMonthOfYear(), MonthOfYear.of(m));
        assertEquals(a.getDayOfMonth(), d);
    }

    //-----------------------------------------------------------------------
    // with(MonthOfYear)
    //-----------------------------------------------------------------------
    public void test_with_MonthOfYear() {
        assertEquals(MonthDay.of(6, 30).with(MonthOfYear.JANUARY), MonthDay.of(1, 30));
    }

    public void test_with_MonthOfYear_adjustToValid() {
        assertEquals(MonthDay.of(7, 31).with(MonthOfYear.JUNE), MonthDay.of(6, 30));
    }

    public void test_with_MonthOfYear_adjustToValidFeb() {
        assertEquals(MonthDay.of(7, 31).with(MonthOfYear.FEBRUARY), MonthDay.of(2, 29));
    }

    public void test_with_MonthOfYear_noChange() {
        MonthDay test = MonthDay.of(6, 30);
        assertSame(test.with(MonthOfYear.JUNE), test);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_with_MonthOfYear_null() {
        MonthDay.of(6, 30).with((MonthOfYear) null);
    }

    //-----------------------------------------------------------------------
    // withMonthOfYear()
    //-----------------------------------------------------------------------
    public void test_withMonthOfYear() {
        assertEquals(MonthDay.of(6, 30).withMonthOfYear(1), MonthDay.of(1, 30));
    }

    public void test_withMonthOfYear_adjustToValid() {
        assertEquals(MonthDay.of(7, 31).withMonthOfYear(6), MonthDay.of(6, 30));
    }

    public void test_withMonthOfYear_adjustToValidFeb() {
        assertEquals(MonthDay.of(7, 31).withMonthOfYear(2), MonthDay.of(2, 29));
    }

    public void test_withMonthOfYear_int_noChange() {
        MonthDay test = MonthDay.of(6, 30);
        assertSame(test.withMonthOfYear(6), test);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withMonthOfYear_tooLow() {
        try {
            MonthDay.of(6, 30).withMonthOfYear(0);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), MONTH_OF_YEAR);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withMonthOfYear_tooHigh() {
        try {
            MonthDay.of(6, 30).withMonthOfYear(13);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), MONTH_OF_YEAR);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // withDayOfMonth()
    //-----------------------------------------------------------------------
    public void test_withDayOfMonth() {
        assertEquals(MonthDay.of(6, 30).withDayOfMonth(1), MonthDay.of(6, 1));
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_withDayOfMonth_invalid() {
        try {
            MonthDay.of(6, 30).withDayOfMonth(31);
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getRule(), DAY_OF_MONTH);
            throw ex;
        }
    }

    public void test_withDayOfMonth_adjustToValidFeb() {
        assertEquals(MonthDay.of(2, 1).withDayOfMonth(29), MonthDay.of(2, 29));
    }

    public void test_withDayOfMonth_noChange() {
        MonthDay test = MonthDay.of(6, 30);
        assertSame(test.withDayOfMonth(30), test);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withDayOfMonth_tooLow() {
        try {
            MonthDay.of(6, 30).withDayOfMonth(0);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), DAY_OF_MONTH);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withDayOfMonth_tooHigh() {
        try {
            MonthDay.of(6, 30).withDayOfMonth(32);
        } catch (IllegalCalendarFieldValueException ex) {
            assertEquals(ex.getRule(), DAY_OF_MONTH);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // rollMonthOfYear()
    //-----------------------------------------------------------------------
    public void test_rollMonthOfYear() {
        MonthDay base = MonthDay.of(7, 31);
        assertSame(base.rollMonthOfYear(0), base);
        assertEquals(base.rollMonthOfYear(1), MonthDay.of(8, 31));
        assertEquals(base.rollMonthOfYear(2), MonthDay.of(9, 30));
        assertEquals(base.rollMonthOfYear(3), MonthDay.of(10, 31));
        assertEquals(base.rollMonthOfYear(4), MonthDay.of(11, 30));
        assertEquals(base.rollMonthOfYear(5), MonthDay.of(12, 31));
        assertEquals(base.rollMonthOfYear(6), MonthDay.of(1, 31));
        assertEquals(base.rollMonthOfYear(7), MonthDay.of(2, 29));
        assertEquals(base.rollMonthOfYear(8), MonthDay.of(3, 31));
        assertEquals(base.rollMonthOfYear(9), MonthDay.of(4, 30));
        assertEquals(base.rollMonthOfYear(10), MonthDay.of(5, 31));
        assertEquals(base.rollMonthOfYear(11), MonthDay.of(6, 30));
        assertEquals(base.rollMonthOfYear(12), MonthDay.of(7, 31));
        
        assertEquals(base.rollMonthOfYear(-1), MonthDay.of(6, 30));
        assertEquals(base.rollMonthOfYear(-2), MonthDay.of(5, 31));
        assertEquals(base.rollMonthOfYear(-3), MonthDay.of(4, 30));
        assertEquals(base.rollMonthOfYear(-4), MonthDay.of(3, 31));
        assertEquals(base.rollMonthOfYear(-5), MonthDay.of(2, 29));
        assertEquals(base.rollMonthOfYear(-6), MonthDay.of(1, 31));
        assertEquals(base.rollMonthOfYear(-7), MonthDay.of(12, 31));
        assertEquals(base.rollMonthOfYear(-8), MonthDay.of(11, 30));
        assertEquals(base.rollMonthOfYear(-9), MonthDay.of(10, 31));
        assertEquals(base.rollMonthOfYear(-10), MonthDay.of(9, 30));
        assertEquals(base.rollMonthOfYear(-11), MonthDay.of(8, 31));
        assertEquals(base.rollMonthOfYear(-12), MonthDay.of(7, 31));
    }

    //-----------------------------------------------------------------------
    // rollDayOfMonth()
    //-----------------------------------------------------------------------
    public void test_rollDayOfMonth_july() {
        MonthDay base = MonthDay.of(7, 31);
        assertEquals(base.rollDayOfMonth(-2), MonthDay.of(7, 29));
        assertEquals(base.rollDayOfMonth(-1), MonthDay.of(7, 30));
        assertSame(base.rollDayOfMonth(0), base);
        assertEquals(base.rollDayOfMonth(1), MonthDay.of(7, 1));
        assertEquals(base.rollDayOfMonth(2), MonthDay.of(7, 2));
    }

    public void test_rollDayOfMonth_feb() {
        MonthDay base = MonthDay.of(2, 29);
        assertEquals(base.rollDayOfMonth(-2), MonthDay.of(2, 27));
        assertEquals(base.rollDayOfMonth(-1), MonthDay.of(2, 28));
        assertSame(base.rollDayOfMonth(0), base);
        assertEquals(base.rollDayOfMonth(1), MonthDay.of(2, 1));
        assertEquals(base.rollDayOfMonth(2), MonthDay.of(2, 2));
    }

    //-----------------------------------------------------------------------
    // adjustDate()
    //-----------------------------------------------------------------------
    public void test_adjustDate() {
        MonthDay test = MonthDay.of(6, 30);
        LocalDate date = LocalDate.of(2007, 1, 1);
        assertEquals(test.adjustDate(date), LocalDate.of(2007, 6, 30));
    }

    public void test_adjustDate_same() {
        MonthDay test = MonthDay.of(6, 30);
        LocalDate date = LocalDate.of(2007, 6, 30);
        assertSame(test.adjustDate(date), date);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_adjustDate_invalid() {
        MonthDay test = MonthDay.of(2, 29);
        LocalDate date = LocalDate.of(2007, 6, 30);
        try {
            test.adjustDate(date);
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getRule(), DAY_OF_MONTH);
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustDate_null() {
        TEST_07_15.adjustDate((LocalDate) null);
    }

    //-----------------------------------------------------------------------
    public void test_adjustDate_DateResolver() {
        MonthDay test = MonthDay.of(6, 30);
        LocalDate date = LocalDate.of(2007, 1, 1);
        assertEquals(test.adjustDate(date, DateResolvers.nextValid()), LocalDate.of(2007, 6, 30));
    }

    public void test_adjustDate_DateResolver_same() {
        MonthDay test = MonthDay.of(6, 30);
        LocalDate date = LocalDate.of(2007, 6, 30);
        assertSame(test.adjustDate(date, DateResolvers.nextValid()), date);
    }

    public void test_adjustDate_DateResolver_resolve() {
        MonthDay test = MonthDay.of(2, 29);
        LocalDate date = LocalDate.of(2007, 1, 1);
        assertEquals(test.adjustDate(date, DateResolvers.nextValid()), LocalDate.of(2007, 3, 1));
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_adjustDate_DateResolver_invalid() {
        MonthDay test = MonthDay.of(2, 29);
        LocalDate date = LocalDate.of(2007, 6, 30);
        try {
            test.adjustDate(date, DateResolvers.strict());
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getRule(), DAY_OF_MONTH);
            throw ex;
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustDate_DateResolver_nullDate() {
        TEST_07_15.adjustDate((LocalDate) null, DateResolvers.nextValid());
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustDate_DateResolver_nullResolver() {
        MonthDay test = MonthDay.of(2, 29);
        LocalDate date = LocalDate.of(2008, 2, 29);  // same date, but resolver should still NPE
        test.adjustDate(date, (DateResolver) null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_adjustDate_DateResolver_badResolver() {
        MonthDay test = MonthDay.of(2, 29);
        LocalDate date = LocalDate.of(2007, 6, 30);
        test.adjustDate(date, new MockDateResolverReturnsNull());
    }

    //-----------------------------------------------------------------------
    // matchesDate()
    //-----------------------------------------------------------------------
    public void test_matchesDate() {
        assertEquals(MonthDay.of(1, 1).matchesCalendrical(LocalDate.of(2007, 1, 1)), true);
        assertEquals(MonthDay.of(1, 1).matchesCalendrical(LocalDate.of(2008, 1, 1)), true);
        
        assertEquals(MonthDay.of(2, 1).matchesCalendrical(LocalDate.of(2007, 1, 1)), false);
        assertEquals(MonthDay.of(1, 2).matchesCalendrical(LocalDate.of(2008, 1, 1)), false);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_matchesDate_null() {
        TEST_07_15.matchesCalendrical(null);
    }

    //-----------------------------------------------------------------------
    // isValidYear(int)
    //-----------------------------------------------------------------------
    public void test_isValidYear_june() {
        MonthDay test = MonthDay.of(6, 30);
        assertEquals(test.isValidYear(2007), true);
    }

    public void test_isValidYear_febNonLeap() {
        MonthDay test = MonthDay.of(2, 29);
        assertEquals(test.isValidYear(2007), false);
    }

    public void test_isValidYear_febLeap() {
        MonthDay test = MonthDay.of(2, 29);
        assertEquals(test.isValidYear(2008), true);
    }

    //-----------------------------------------------------------------------
    // atYear(int)
    //-----------------------------------------------------------------------
    public void test_atYear_int() {
        MonthDay test = MonthDay.of(6, 30);
        assertEquals(test.atYear(2008), LocalDate.of(2008, 6, 30));
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_atYear_int_invalidYear() {
        MonthDay test = MonthDay.of(6, 30);
        test.atYear(Integer.MIN_VALUE);
    }

    @Test(expectedExceptions=InvalidCalendarFieldException.class)
    public void test_atYear_int_notLeapYear() {
        MonthDay test = MonthDay.of(2, 29);
        try {
            test.atYear(2005);
        } catch (InvalidCalendarFieldException ex) {
            assertEquals(ex.getRule(), DAY_OF_MONTH);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // toFields()
    //-----------------------------------------------------------------------
    public void test_toFields() {
        assertEquals(MonthDay.of(6, 30).toFields(), DateTimeFields.of(MONTH_OF_YEAR, 6, DAY_OF_MONTH, 30));
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_comparisons() {
        doTest_comparisons_MonthDay(
            MonthDay.of(1, 1),
            MonthDay.of(1, 31),
            MonthDay.of(2, 1),
            MonthDay.of(2, 29),
            MonthDay.of(3, 1),
            MonthDay.of(12, 31)
        );
    }

    void doTest_comparisons_MonthDay(MonthDay... localDates) {
        for (int i = 0; i < localDates.length; i++) {
            MonthDay a = localDates[i];
            for (int j = 0; j < localDates.length; j++) {
                MonthDay b = localDates[j];
                if (i < j) {
                    assertTrue(a.compareTo(b) < 0, a + " <=> " + b);
                    assertEquals(a.isBefore(b), true, a + " <=> " + b);
                    assertEquals(a.isAfter(b), false, a + " <=> " + b);
                    assertEquals(a.equals(b), false, a + " <=> " + b);
                } else if (i > j) {
                    assertTrue(a.compareTo(b) > 0, a + " <=> " + b);
                    assertEquals(a.isBefore(b), false, a + " <=> " + b);
                    assertEquals(a.isAfter(b), true, a + " <=> " + b);
                    assertEquals(a.equals(b), false, a + " <=> " + b);
                } else {
                    assertEquals(a.compareTo(b), 0, a + " <=> " + b);
                    assertEquals(a.isBefore(b), false, a + " <=> " + b);
                    assertEquals(a.isAfter(b), false, a + " <=> " + b);
                    assertEquals(a.equals(b), true, a + " <=> " + b);
                }
            }
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_compareTo_ObjectNull() {
        TEST_07_15.compareTo(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isBefore_ObjectNull() {
        TEST_07_15.isBefore(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isAfter_ObjectNull() {
        TEST_07_15.isAfter(null);
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    public void test_equals() {
        MonthDay a = MonthDay.of(1, 1);
        MonthDay b = MonthDay.of(1, 1);
        MonthDay c = MonthDay.of(2, 1);
        MonthDay d = MonthDay.of(1, 2);
        
        assertEquals(a.equals(a), true);
        assertEquals(a.equals(b), true);
        assertEquals(a.equals(c), false);
        assertEquals(a.equals(d), false);
        
        assertEquals(b.equals(a), true);
        assertEquals(b.equals(b), true);
        assertEquals(b.equals(c), false);
        assertEquals(b.equals(d), false);
        
        assertEquals(c.equals(a), false);
        assertEquals(c.equals(b), false);
        assertEquals(c.equals(c), true);
        assertEquals(c.equals(d), false);
        
        assertEquals(d.equals(a), false);
        assertEquals(d.equals(b), false);
        assertEquals(d.equals(c), false);
        assertEquals(d.equals(d), true);
    }

    public void test_equals_itself_true() {
        assertEquals(TEST_07_15.equals(TEST_07_15), true);
    }

    public void test_equals_string_false() {
        assertEquals(TEST_07_15.equals("2007-07-15"), false);
    }

    public void test_equals_null_false() {
        assertEquals(TEST_07_15.equals(null), false);
    }

    //-----------------------------------------------------------------------
    // hashCode()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_hashCode(int m, int d) {
        MonthDay a = MonthDay.of(m, d);
        assertEquals(a.hashCode(), a.hashCode());
        MonthDay b = MonthDay.of(m, d);
        assertEquals(a.hashCode(), b.hashCode());
    }

    public void test_hashCode_unique() {
        int leapYear = 2008;
        Set<Integer> uniques = new HashSet<Integer>(366);
        for (int i = 1; i <= 12; i++) {
            for (int j = 1; j <= 31; j++) {
                if (YearMonth.of(leapYear, i).isValidDay(j)) {
                    assertTrue(uniques.add(MonthDay.of(i, j).hashCode()));
                }
            }
        }
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleToString")
    Object[][] provider_sampleToString() {
        return new Object[][] {
            {7, 5, "--07-05"},
            {12, 31, "--12-31"},
            {1, 2, "--01-02"},
        };
    }

    @Test(dataProvider="sampleToString")
    public void test_toString(int m, int d, String expected) {
        MonthDay test = MonthDay.of(m, d);
        String str = test.toString();
        assertEquals(str, expected);
    }

    //-----------------------------------------------------------------------
    // toString(DateTimeFormatter)
    //-----------------------------------------------------------------------
    public void test_toString_formatter() {
        String t = MonthDay.of(12, 3).toString(DateTimeFormatters.pattern("MM dd"));
        assertEquals(t, "12 03");
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_toString_formatter_null() {
        MonthDay.of(12, 3).toString(null);
    }

}
