package houk.postal.csv;

import junit.framework.TestCase;

public class AppTest
    extends TestCase
{
    public void testParseDouble() {
        assertEquals(4.0, App.parseDouble("4.0"));
        assertEquals(0.0, App.parseDouble("000.00"));
        assertEquals(104.3, App.parseDouble("104.300"));
    }

    public void testIsValidCode() {
        assertTrue(App.isValidCode("301"));
        assertTrue(App.isValidCode("200"));
        assertTrue(App.isValidCode("401"));
        assertTrue(App.isValidCode("404"));
        assertTrue(App.isValidCode("503"));
        assertFalse(App.isValidCode(""));
        assertFalse(App.isValidCode("0"));
        assertFalse(App.isValidCode("ahfja"));
        assertFalse(App.isValidCode("%20"));
    }
}
