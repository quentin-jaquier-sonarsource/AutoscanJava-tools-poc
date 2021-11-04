import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class NoisyRules {

  public static void main(String[] args) throws IOException, InterruptedException {
    // "rx" is excluded because tests files were deleted in order to reduce the size for autoscan
    String[] names = {"aislib", "commons-csv", "commons-io", "conductor", "easybuggy", "jade",
      "junit", "maven-enforce", "slang", "spark"};

    Map<String, Integer> countFP = new HashMap<>();
    Map<String, Integer> countFN = new HashMap<>();

    for (String name : names) {
      Set<Issue> issuesFork = ProcessData.getIssueFromFile("src/main/resources/" + name + "/" + name + "-fork", false);
      Set<Issue> issuesDep = ProcessData.getIssueFromFile("src/main/resources/" + name + "/" + name + "-4", false);

      addCount(issuesFork, issuesDep, countFP, countFN);
    }

    System.out.println("FP");
    prettyPrint(countFP);
    System.out.println("FN");
    prettyPrint(countFN);
  }

  static void prettyPrint(Map<String, Integer> map) {
    List<Map.Entry<String, Integer>> collect = map.entrySet().stream().sorted((o1, o2) ->
      o2.getValue().compareTo(o1.getValue()))
      .collect(Collectors.toList());

    for (Map.Entry<String, Integer> entry : collect) {
      System.out.println(entry.getKey() + " = " + entry.getValue());
    }
  }

  private static void addCount(Set<Issue> issuesFork, Set<Issue> issuesBatch, Map<String, Integer> countFP, Map<String, Integer> countFN) {
    for (Issue i : issuesBatch) {
      if (!issuesFork.contains(i)) {
        int count = countFP.getOrDefault(i.ruleKey, 0);
        countFP.put(i.ruleKey, count + 1);
      }
    }
    for (Issue i : issuesFork) {
      if (!issuesBatch.contains(i)) {
        int count = countFN.getOrDefault(i.ruleKey, 0);
        countFN.put(i.ruleKey, count + 1);
      }
    }
  }


}
