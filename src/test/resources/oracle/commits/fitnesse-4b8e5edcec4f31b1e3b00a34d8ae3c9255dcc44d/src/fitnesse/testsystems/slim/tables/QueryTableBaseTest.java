package fitnesse.testsystems.slim.tables;

import fitnesse.slim.SlimClient;
import fitnesse.slim.converters.VoidConverter;
import fitnesse.slim.instructions.CallInstruction;
import fitnesse.slim.instructions.Instruction;
import fitnesse.slim.instructions.InstructionExecutor;
import fitnesse.slim.instructions.MakeInstruction;
import fitnesse.testsystems.slim.*;
import fitnesse.wiki.InMemoryPage;
import fitnesse.wiki.WikiPage;
import fitnesse.wiki.WikiPageUtil;
import fitnesse.wikitext.Utils;
import org.junit.Before;
import org.junit.Test;
import util.ListUtility;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static util.ListUtility.list;

public abstract class QueryTableBaseTest {
  private WikiPage root;
  private List<Assertion> assertions;
  private String queryTableHeader;
  public QueryTable qt;
  private MockSlimTestContext testContext;
  protected String headRow;

  @Before
  public void setUp() throws Exception {
    root = InMemoryPage.makeRoot("root");
    assertions = new ArrayList<Assertion>();
    queryTableHeader =
      "|" + tableType() + ":fixture|argument|\n" +
        "|n|2n|\n";
    headRow = "[pass(" + tableType() + ":fixture), argument], ";
  }

  protected abstract String tableType();

  protected abstract Class<? extends QueryTable> queryTableClass();

  private QueryTable makeQueryTableAndBuildInstructions(String pageContents) throws Exception {
    qt = makeQueryTable(pageContents);
    assertions.addAll(qt.getAssertions());
    return qt;
  }

  private QueryTable makeQueryTable(String tableText) throws Exception {
    WikiPageUtil.setPageContents(root, tableText);
    TableScanner ts = new HtmlTableScanner(root.getData().getHtml());
    Table t = ts.getTable(0);
    testContext = new MockSlimTestContext();
    return constructQueryTable(t);
  }

  private QueryTable constructQueryTable(Table t) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
    Class<? extends QueryTable> queryTableClass = queryTableClass();
    Constructor<? extends QueryTable> constructor = queryTableClass.getConstructor(Table.class, String.class, SlimTestContext.class);
    return constructor.newInstance(t, "id", testContext);
  }

  @SuppressWarnings("unchecked")
  protected void assertQueryResults(String queryRows, List<Object> queryResults, String table) throws Exception {
    makeQueryTableAndBuildInstructions(queryTableHeader + queryRows);
    Map<String, Object> pseudoResults = SlimClient.resultToMap(list(
      list("queryTable_id_0", "OK"),
      list("queryTable_id_1", "blah"),
      list("queryTable_id_2", queryResults)
    ));
    Assertion.evaluateExpectations(assertions, pseudoResults);
    org.junit.Assert.assertEquals(table, qt.getTable().toString());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void instructionsForQueryTable() throws Exception {
    makeQueryTableAndBuildInstructions(queryTableHeader);
    List<Instruction> expectedInstructions = list(
            new MakeInstruction("queryTable_id_0", "queryTable_id", "fixture", new Object[]{"argument"}),
            new CallInstruction("queryTable_id_1", "queryTable_id", "table", new Object[]{list(list("n", "2n"))}),
            new CallInstruction("queryTable_id_2", "queryTable_id", "query")
    );
    org.junit.Assert.assertEquals(expectedInstructions, instructions());
  }

  private List<Instruction> instructions() {
    return Assertion.getInstructions(assertions);
  }

  @Test
  public void nullResultsForNullTable() throws Exception {
    assertQueryResults("", list(),
      "[" +
        headRow +
        "[n, 2n]" +
        "]"
    );
  }

  @Test
  public void oneRowThatMatches() throws Exception {
    assertQueryResults("|2|4|\n",
            ListUtility.<Object>list(
                    list(list("n", "2"), list("2n", "4"))
            ),
      "[" +
        headRow +
        "[n, 2n], " +
        "[pass(2), pass(4)]" +
        "]"
    );
  }

  @Test
  public void oneRowFirstCellMatchesSecondCellBlank() throws Exception {
    assertQueryResults("|2||\n",
            ListUtility.<Object>list(
                    list(list("n", "2"), list("2n", "4"))
            ),
      "[" +
        headRow +
        "[n, 2n], " +
        "[pass(2), ignore(4)]" +
        "]"
    );
  }

  @Test
  public void oneRowThatFails() throws Exception {
    assertQueryResults("|2|4|\n",
            ListUtility.<Object>list(
                    list(list("n", "3"), list("2n", "5"))
            ),
      "[" +
        headRow +
        "[n, 2n], " +
        "[fail(e=2;missing), 4], " +
        "[fail(a=3;surplus), 5]" +
        "]"
    );
  }

  @Test
  public void oneRowWithPartialMatch() throws Exception {
    assertQueryResults("|2|4|\n",
            ListUtility.<Object>list(
                    list(list("n", "2"), list("2n", "5"))
            ),
      "[" +
        headRow +
        "[n, 2n], " +
        "[pass(2), fail(a=5;e=4)]" +
        "]"
    );
  }

  @Test
  public void twoMatchingRows() throws Exception {
    assertQueryResults(
      "|2|4|\n" +
        "|3|6|\n",
            ListUtility.<Object>list(
                    list(list("n", "2"), list("2n", "4")),
                    list(list("n", "3"), list("2n", "6"))
            ),
      "[" +
        headRow +
        "[n, 2n], " +
        "[pass(2), pass(4)], " +
        "[pass(3), pass(6)]" +
        "]"
    );
  }

  @Test
  public void twoRowsFirstMatchesSecondDoesnt() throws Exception {
    assertQueryResults(
      "|3|6|\n" +
        "|99|99|\n",
            ListUtility.<Object>list(
                    list(list("n", "2"), list("2n", "4")),
                    list(list("n", "3"), list("2n", "6"))
            ),
      "[" +
        headRow +
        "[n, 2n], " +
        "[pass(3), pass(6)], " +
        "[fail(e=99;missing), 99], " +
        "[fail(a=2;surplus), 4]" +
        "]"
    );
  }

  @Test
  public void twoRowsSecondMatchesFirstDoesnt() throws Exception {
    assertQueryResults(
      "|99|99|\n" +
        "|2|4|\n",
            ListUtility.<Object>list(
                    list(list("n", "2"), list("2n", "4")),
                    list(list("n", "3"), list("2n", "6"))
            ),
      "[" +
        headRow +
        "[n, 2n], " +
        "[fail(e=99;missing), 99], " +
        "[pass(2), pass(4)], " +
        "[fail(a=3;surplus), 6]" +
        "]"
    );
  }

  @Test
  public void fieldInMatchingRowDoesntExist() throws Exception {
    assertQueryResults(
      "|3|4|\n",
            ListUtility.<Object>list(
                    list(list("n", "3"))
            ),
      "[" +
        headRow +
        "[n, 2n], " +
        "[pass(3), fail(a=field 2n not present;e=4)]" +
        "]"
    );
  }

  @Test
  public void fieldInSurplusRowDoesntExist() throws Exception {
    assertQueryResults(
      "",
            ListUtility.<Object>list(
                    list(list("n", "3"))
            ),
      "[" +
        headRow +
        "[n, 2n], " +
        "[fail(a=3;surplus), fail(field 2n not present)]" +
        "]"
    );
  }

  @Test
  public void variablesAreReplacedInMatch() throws Exception {
    makeQueryTableAndBuildInstructions(queryTableHeader + "|2|$V|\n");
    qt.setSymbol("V", "4");
    Map<String, Object> pseudoResults = SlimClient.resultToMap(
      list(
        list("queryTable_id_0", "OK"),
        list("queryTable_id_1", VoidConverter.VOID_TAG),
        list("queryTable_id_2",
          list(
            list(list("n", "2"), list("2n", "4"))
          )
        )
      )
    );
    Assertion.evaluateExpectations(assertions, pseudoResults);
    org.junit.Assert.assertEquals(
      "[" +
        headRow +
        "[n, 2n], " +
        "[pass(2), pass($V->[4])]" +
        "]",
      Utils.unescapeWiki(qt.getTable().toString())
    );
  }

  @Test
  public void variablesAreReplacedInExpected() throws Exception {
    makeQueryTableAndBuildInstructions(queryTableHeader + "|2|$V|\n");
    qt.setSymbol("V", "5");
    Map<String, Object> pseudoResults = SlimClient.resultToMap(
      list(
        list("queryTable_id_0", "OK"),
        list("queryTable_id_1", VoidConverter.VOID_TAG),
        list("queryTable_id_2",
          list(
            list(list("n", "2"), list("2n", "4"))
          )
        )
      )
    );
    Assertion.evaluateExpectations(assertions, pseudoResults);
    org.junit.Assert.assertEquals(
      "[" +
        headRow +
        "[n, 2n], " +
        "[pass(2), fail(a=4;e=$V->[5])]" +
        "]",
      Utils.unescapeWiki(qt.getTable().toString())
    );
  }

  @Test
  public void variablesAreReplacedInMissing() throws Exception {
    makeQueryTableAndBuildInstructions(queryTableHeader + "|3|$V|\n");
    qt.setSymbol("V", "5");
    Map<String, Object> pseudoResults = SlimClient.resultToMap(
      list(
        list("queryTable_id_0", "OK"),
        list("queryTable_id_1", VoidConverter.VOID_TAG),
        list("queryTable_id_2",
          list(
          )
        )
      )
    );
    Assertion.evaluateExpectations(assertions, pseudoResults);
    org.junit.Assert.assertEquals(
      "[" +
        headRow +
        "[n, 2n], " +
        "[fail(e=3;missing), $V->[5]]" +
        "]", qt.getTable().toString()
    );
  }

  @Test
  public void oneRowThatMatchesExpression() throws Exception {
    assertQueryResults("|<5|4|\n",
            ListUtility.<Object>list(
                    list(list("n", "2"), list("2n", "4"))
            ),
      "[" +
        headRow +
        "[n, 2n], " +
        "[pass(2<5), pass(4)]" +
        "]"
    );
  }

}
