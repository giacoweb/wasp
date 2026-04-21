package it.unipd.dei.webapp.util;

public final class HtmlUtil {

    private HtmlUtil() {
        // Utility class
    }

    public static String escape(final Object value) {
        if (value == null) {
            return "";
        }

        String input = String.valueOf(value);
        StringBuilder escaped = new StringBuilder(input.length() + 16);

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            switch (ch) {
                case '&':
                    escaped.append("&amp;");
                    break;
                case '<':
                    escaped.append("&lt;");
                    break;
                case '>':
                    escaped.append("&gt;");
                    break;
                case '"':
                    escaped.append("&quot;");
                    break;
                case '\'':
                    escaped.append("&#x27;");
                    break;
                default:
                    escaped.append(ch);
                    break;
            }
        }

        return escaped.toString();
    }
}
