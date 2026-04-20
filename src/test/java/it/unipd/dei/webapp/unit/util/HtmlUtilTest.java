package it.unipd.dei.webapp.unit.util;

import it.unipd.dei.webapp.util.HtmlUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HtmlUtilTest {

    @Test
    void escapeReturnsEmptyStringForNull() {
        assertEquals("", HtmlUtil.escape(null));
    }

    @Test
    void escapeLeavesSafeTextUnchanged() {
        assertEquals("FinTrack EUR", HtmlUtil.escape("FinTrack EUR"));
    }

    @Test
    void escapeEncodesHtmlSpecialCharacters() {
        String raw = "<script>alert(\"x\")</script> O'Brien & sons";
        String escaped = HtmlUtil.escape(raw);

        assertEquals("&lt;script&gt;alert(&quot;x&quot;)&lt;/script&gt; O&#x27;Brien &amp; sons", escaped);
    }
}
