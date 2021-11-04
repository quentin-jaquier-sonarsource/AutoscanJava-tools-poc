import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;

public class ProcessData {

  public static void main(String[] args) {
    String[] names = {"aislib", "commons-csv", "commons-io", "conductor", "easybuggy", "jade",
      "junit", "maven-enforce", "rx", "slang", "spark"};

    try {
      for (String name : names) {
        System.out.println("----" + name + "----");
        compare(name, false);

        System.out.println("----" + name + " Vulnerabilities ----");
        compare(name, true);
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  private static void compare(String name, boolean vulnerabilities) throws IOException {
    Set<Issue> issuesFork = getIssueFromFile("src/main/resources/" + name + "/" + name + "-fork", vulnerabilities);
    Set<Issue> issuesDep = getIssueFromFile("src/main/resources/" + name + "/" + name + "-4", vulnerabilities);

    System.out.println("Issues FORK: " + issuesFork.size());
    System.out.println("Issues Autoscan-dependency-resolution: " + issuesDep.size());
    System.out.println("\nFORK VS Autoscan");
    versus(issuesFork, issuesDep);
  }

  private static void versus(Set<Issue> issues1, Set<Issue> issues2) {
    Set<Issue> newIssues = new HashSet<>();
    for (Issue i : issues2) {
      if (!issues1.contains(i)) {
        newIssues.add(i);
      }
    }
    System.out.println("\nNew issues:");
    printIssue(newIssues);

    Set<Issue> fnIssues = new HashSet<>();
    for (Issue i : issues1) {
      if (!issues2.contains(i)) {
        fnIssues.add(i);
      }
    }
    System.out.println("\nFN:");
    printIssue(fnIssues);
  }

  private static void printIssue(Set<Issue> newIssues) {
    System.out.println("Size: " + newIssues.size());
    Map<String, List<Issue>> groupByRules = newIssues.stream().collect(Collectors.groupingBy(i -> i.ruleKey));
    for (Map.Entry<String, List<Issue>> entry : groupByRules.entrySet()) {
      List<Issue> issues = entry.getValue();
      System.out.println("Rule: " + entry.getKey() + " (size: " + issues.size() + "):");
      issues.forEach(i -> System.out.println("  " + i.toString()));
    }
  }

  public static Set<Issue> getIssueFromFile(String file, boolean vulnerabilities) throws IOException {
    Set<Issue> allIssues = new HashSet<>();
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      for (String line; (line = br.readLine()) != null; ) {
        JSONObject jsonObject = new JSONObject(line);
        allIssues.addAll(getIssues(jsonObject, vulnerabilities));
      }
    }
    return allIssues;
  }

  private static Set<Issue> getIssues(JSONObject jsonObject, boolean vulnerabilities) {
    JSONArray issues = jsonObject.getJSONArray("issues");
    Set<Issue> expectedIssuesSet = new HashSet<>();
    for (int i = 0; i < issues.length(); i++) {
      JSONObject obj = (JSONObject) issues.get(i);
      if (vulnerabilities) {
        if (((String) obj.get("rule")).startsWith("java") && (obj.get("type")).equals("VULNERABILITY")) { //
          Issue issue = new Issue(obj);
          if (issue.line != -1 && issue.startOffSet != -1) {
            expectedIssuesSet.add(issue);
          }
        }
      } else {
        if (((String) obj.get("rule")).startsWith("java") && !(obj.get("type")).equals("VULNERABILITY")) { //
          Issue issue = new Issue(obj);
          if (issue.line != -1 && issue.startOffSet != -1) {
            expectedIssuesSet.add(issue);
          }
        }
      }

    }

    return expectedIssuesSet;
  }

}
