import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import java.util.Stream;

public class Solution {

    private static String preparePart(final String part) {
      return Arrays.stream(part.split("-")).filter().collect(Collectors.joining("")).toUpperCase();
    }
    
    public static String generate_bc(String url, String separator) {
        Pattern regex = Pattern.compile("^[^\\.\\?#]*");
        Matcher regexMatcher = regex.matcher(url);
        if (regexMatcher.find()) {
          String[] urlParts = regexMatcher.group().replaceAll("/index.*", "").split("/");
          return IntStream.range(0, urlParts.length)
            .mapToObj(i -> {
              final String part = urlParts[i];
              switch (i) {
                case 0:
                  return "<a href=\"/\">HOME</a>";
                  break;
                case urlParts.length-1:
                  return String.format("<span class=\"active\">%s</span>", preparePart(urlParts[i]));
                  break;
                default: 
                  return String.format("<a href=\"/%s/\">%s</a>", urlParts[i], preparePart(urlParts[i]));
              }
            })
            .collect(Collectors.joining(separator));
        }
        return "";
    }
}