import java.util.Objects;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.stream.IntStream;
import java.util.function.Function;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Mixing {

    public static Map<Integer, Integer[]> getLettersMap(String s1, String s2) {
      final Map<Integer, Integer[]> map = new HashMap<>();
      s1.chars()
        .filter(i -> Character.isLetter(i) && Character.isLowerCase(i))
        .forEach(i -> map.merge(i, new Integer[]{1, 0}, (a, b) -> new Integer[]{a[0]+b[0],a[1]+b[1]}));
      s2.chars()
        .filter(i -> Character.isLetter(i) && Character.isLowerCase(i))
        .forEach(i -> map.merge(i, new Integer[]{0, 1}, (a, b) -> new Integer[]{a[0]+b[0],a[1]+b[1]}));
      return map;
    }
    
    public static String mix(String s1, String s2) {
      return getLettersMap(s1, s2).entrySet().stream()
        .filter(entry -> Math.max(entry.getValue()[0], entry.getValue()[1]) > 1)
        .map(entry -> {
          int[] value = Arrays.stream(entry.getValue()).mapToInt(Integer::intValue).toArray();
          int max = value[0];
          String[] parts = new String[2];
          if (value[0] == value[1]) {
            parts[0] = "=";
          } else if (value[0] > value[1]) {
            parts[0] = "1";
          } else {
            parts[0] = "2";
            max = value[1];
          }
          parts[1] = IntStream.range(0, max).mapToObj(i -> String.valueOf((char) entry.getKey().intValue())).collect(Collectors.joining());
          return String.join(":", parts);
        })
        .sorted((part1, part2) -> {
          int cmp =  Objects.compare(
            part2.length(),
            part1.length(),
            Comparator.<Integer>naturalOrder()
          );
          if (cmp != 0) return cmp;
          String[] parts1 = part1.split(":"), parts2 = part2.split(":");
          cmp = Objects.compare(
            parts1[0],
            parts2[0],
            (a, b) -> {
              switch (a) {
                case "1": {
                  switch (b) {
                    case "1": return 0;
                    case "2":
                    case "=": 
                      return -1;
                  }
                }
                case "2": {
                  switch (b) {
                    case "1": return 1;
                    case "2": return 0;
                    case "=": return -1;
                  }
                }
                case "=":  {
                  switch (b) {
                    case "1":
                    case "2": 
                      return 1;
                    case "=": return 0;
                  }
                }
              }
              return 0;
            }
          );
          if (cmp != 0) return cmp;
          return Objects.compare(
            parts1[1],
            parts2[1],
            String.CASE_INSENSITIVE_ORDER
          );
        })
        .collect(Collectors.joining("/"));
    }
}
