import org.json.JSONObject;

public class Issue {
  String component;
  int line;
  String ruleKey;
  int startOffSet;

  Issue(JSONObject obj) {
    this.ruleKey = ((String) obj.get("rule"));
    String fullComponent = ((String) obj.get("component"));
    // Remove project key from component
    this.component = fullComponent.substring(fullComponent.indexOf(":"));
    if (obj.has("line")) {
      this.line = ((int) obj.get("line"));
    } else {
      line = -1;
    }
    if (obj.has("textRange")) {
      this.startOffSet = (int) (((JSONObject) obj.get("textRange")).get("startOffset"));
    } else {
      startOffSet = -1;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Issue issue = (Issue) o;

    if (line != issue.line) return false;
    if (startOffSet != issue.startOffSet) return false;
    if (!component.equals(issue.component)) return false;
    return ruleKey.equals(issue.ruleKey);
  }

  @Override
  public int hashCode() {
    int result = component.hashCode();
    result = 31 * result + line;
    result = 31 * result + ruleKey.hashCode();
    result = 31 * result + startOffSet * 12;
    return result;
  }

  @Override
  public String toString() {
    return "Issue{" +
      "component='" + component + '\'' +
      ", line=" + line +
      ", ruleKey='" + ruleKey + '\'' +
      ", startOffSet=" + startOffSet +
      '}';
  }
}
