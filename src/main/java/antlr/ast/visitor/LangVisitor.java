package antlr.ast.visitor;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.declaration.LangMethodDeclaration;
import antlr.ast.node.declaration.LangSingleVariableDeclaration;
import antlr.ast.node.declaration.LangTypeDeclaration;
import antlr.ast.node.expression.*;
import antlr.ast.node.literal.*;
import antlr.ast.node.metadata.LangAnnotation;
import antlr.ast.node.metadata.comment.LangComment;
import antlr.ast.node.pattern.LangLiteralPattern;
import antlr.ast.node.pattern.LangVariablePattern;
import antlr.ast.node.statement.*;
import antlr.ast.node.unit.LangCompilationUnit;
import antlr.ast.stringifier.LangASTFlattener;
import antlr.ast.stringifier.PyASTFlattener;
import gr.uom.java.xmi.LocationInfo;
import gr.uom.java.xmi.VariableDeclarationContainer;
import gr.uom.java.xmi.decomposition.*;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LangVisitor implements LangASTVisitor {

    public static final Pattern METHOD_INVOCATION_PATTERN = Pattern.compile("!(\\w|\\.)*@\\w*");
    public static final Pattern METHOD_SIGNATURE_PATTERN = Pattern.compile("(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) *\\([^\\)]*\\) *(\\{?|[^;])");
    private LangCompilationUnit cu;
    private String sourceFolder;
    private String filePath;
    private VariableDeclarationContainer container;
    private List<LeafExpression> variables = new ArrayList<>();
    private List<String> types = new ArrayList<>();
    private List<AbstractCall> methodInvocations = new ArrayList<>();
    private List<VariableDeclaration> variableDeclarations = new ArrayList<VariableDeclaration>();
    private List<AnonymousClassDeclarationObject> anonymousClassDeclarations = new ArrayList<AnonymousClassDeclarationObject>();
    private List<LeafExpression> textBlocks = new ArrayList<>();
    private List<LeafExpression> stringLiterals = new ArrayList<>();
    private List<LeafExpression> charLiterals = new ArrayList<>();
    private List<LeafExpression> numberLiterals = new ArrayList<>();
    private List<LeafExpression> nullLiterals = new ArrayList<>();
    private List<LeafExpression> booleanLiterals = new ArrayList<>();
    private List<LeafExpression> typeLiterals = new ArrayList<>();
    private List<AbstractCall> creations = new ArrayList<>();
    private List<LeafExpression> infixExpressions = new ArrayList<>();
    private List<LeafExpression> assignments = new ArrayList<>();
    private List<String> infixOperators = new ArrayList<>();
    private List<LeafExpression> arrayAccesses = new ArrayList<>();
    private List<LeafExpression> prefixExpressions = new ArrayList<>();
    private List<LeafExpression> postfixExpressions = new ArrayList<>();
    private List<LeafExpression> thisExpressions = new ArrayList<>();
    private List<LeafExpression> arguments = new ArrayList<>();
    private List<LeafExpression> parenthesizedExpressions = new ArrayList<>();
    private List<LeafExpression> castExpressions = new ArrayList<>();
    private List<TernaryOperatorExpression> ternaryOperatorExpressions = new ArrayList<TernaryOperatorExpression>();
    private List<LambdaExpressionObject> lambdas = new ArrayList<LambdaExpressionObject>();
    private DefaultMutableTreeNode root = new DefaultMutableTreeNode();
    private DefaultMutableTreeNode current = root;

    public LangVisitor(LangCompilationUnit cu, String sourceFolder, String filePath, VariableDeclarationContainer container) {
        this.cu = cu;
        this.sourceFolder = sourceFolder;
        this.filePath = filePath;
        this.container = container;
    }

    private void processArgument(LangASTNode argument) {
        // Create a LeafExpression for this argument and add to arguments list
        LeafExpression argExpression = new LeafExpression(cu, sourceFolder, filePath, argument,
                LocationInfo.CodeElementType.EXPRESSION, container);
        arguments.add(argExpression);

        // Continue visiting the argument to collect its sub-expressions
        argument.accept(this);
    }


    @Override
    public void visit(LangCompilationUnit langCompilationUnit) {

    }

    @Override
    public void visit(LangTypeDeclaration langTypeDeclaration) {

    }

    @Override
    public void visit(LangMethodDeclaration methodDeclaration) {

    }

    @Override
    public void visit(LangSingleVariableDeclaration langSingleVariableDeclaration) {

    }

    @Override
    public void visit(LangBlock langBlock) {
        // For blocks, visit all child statements
        for (LangASTNode statement : langBlock.getStatements()) {
            statement.accept(this);
        }
    }

    @Override
    public void visit(LangReturnStatement langReturnStatement) {
        if (langReturnStatement.getExpression() != null) {
            langReturnStatement.getExpression().accept(this);
        }
    }

    @Override
    public void visit(LangInfixExpression langInfixExpression) {
        // Add infix expressions and operators
        LeafExpression infix = new LeafExpression(cu, sourceFolder, filePath,
                langInfixExpression, LocationInfo.CodeElementType.INFIX_EXPRESSION, container);
        infixExpressions.add(infix);
        infixOperators.add(langInfixExpression.getOperator().name());

        // Visit operands
        langInfixExpression.getLeft().accept(this);
        langInfixExpression.getRight().accept(this);
    }


    @Override
    public void visit(LangMethodInvocation langMethodInvocation) {
        OperationInvocation invocation = new OperationInvocation(cu, sourceFolder, filePath, langMethodInvocation, container);
        methodInvocations.add(invocation);

        if(current.getUserObject() != null) {
            AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
            anonymous.getMethodInvocations().add(invocation);
        }

        // 🔧 NEW: Check if this method invocation is on 'self' (Python's 'this')
        LangASTNode expression = langMethodInvocation.getExpression();
        if (expression instanceof LangSimpleName) {
            LangSimpleName simpleName = (LangSimpleName) expression;
            if ("self".equals(simpleName.getIdentifier()) || "cls".equals(simpleName.getIdentifier())) {
                // This is a 'this' expression in Python (self.method())
                LeafExpression thisExpr = new LeafExpression(cu, sourceFolder, filePath,
                        expression, LocationInfo.CodeElementType.THIS_EXPRESSION, container);
                thisExpressions.add(thisExpr);
            }
        }

        // Visit child nodes for further processing
        if (langMethodInvocation.getExpression() != null && !(langMethodInvocation.getExpression() instanceof LangFieldAccess)) {
            langMethodInvocation.getExpression().accept(this);
        }

        // Visit child nodes for further processing (but don't re-process arguments)
        List<LangASTNode> arguments = langMethodInvocation.getArguments();
        for(LangASTNode argument : arguments) {
            argument.accept(this);
        }
    }


    @Override
    public void visit(LangSimpleName langSimpleName) {
        // Add variable references to the variables list
        LeafExpression variable = new LeafExpression(cu, sourceFolder, filePath,
                langSimpleName, LocationInfo.CodeElementType.SIMPLE_NAME, container);
        variables.add(variable);
    }

    @Override
    public void visit(LangIfStatement langIfStatement) {

    }

    @Override
    public void visit(LangWhileStatement langWhileStatement) {

    }

    @Override
    public void visit(LangForStatement langForStatement) {

    }

    @Override
    public void visit(LangExpressionStatement langExpressionStatement) {
        LangASTNode expression = langExpressionStatement.getExpression();
        if (expression != null) {
            expression.accept(this);  // This will traverse into the assignment and find method invocations
        }
    }


    @Override
    public void visit(LangAssignment langAssignment) {
        // Add assignments
        LeafExpression assignment = new LeafExpression(cu, sourceFolder, filePath,
                langAssignment, LocationInfo.CodeElementType.ASSIGNMENT, container);
        assignments.add(assignment);

        // 🔧 CRITICAL: Visit left and right sides to find nested expressions
        if (langAssignment.getLeftSide() != null) {
            langAssignment.getLeftSide().accept(this);
        }
        if (langAssignment.getRightSide() != null) {
            langAssignment.getRightSide().accept(this);  // This will find the method invocation!
        }

        if (langAssignment.getLeftSide() instanceof LangSimpleName) {
            String varName = ((LangSimpleName) langAssignment.getLeftSide()).getIdentifier();
            VariableDeclaration varDecl = new VariableDeclaration(cu, sourceFolder, filePath,
                    langAssignment, container, varName);
            variableDeclarations.add(varDecl);
        }

    }

    @Override
    public void visit(LangBooleanLiteral langBooleanLiteral) {
        // Add boolean literals
        LeafExpression booleanLit = new LeafExpression(cu, sourceFolder, filePath,
                langBooleanLiteral, LocationInfo.CodeElementType.BOOLEAN_LITERAL, container);
        booleanLiterals.add(booleanLit);
    }


    @Override
    public void visit(LangIntegerLiteral langIntegerLiteral) {
        // Add number literals
        LeafExpression numberLit = new LeafExpression(cu, sourceFolder, filePath,
                langIntegerLiteral, LocationInfo.CodeElementType.NUMBER_LITERAL, container);
        numberLiterals.add(numberLit);
    }


    @Override
    public void visit(LangStringLiteral langStringLiteral) {
        // Add string literals
        LeafExpression stringLit = new LeafExpression(cu, sourceFolder, filePath,
                langStringLiteral, LocationInfo.CodeElementType.STRING_LITERAL, container);
        stringLiterals.add(stringLit);
    }


    @Override
    public void visit(LangListLiteral langListLiteral) {

    }

    @Override
    public void visit(LangFieldAccess langFieldAccess) {
        // Handle field access like self.add, self.value, etc.

        // Check if this is a 'self' reference (Python's equivalent of 'this')
        LangASTNode expression = langFieldAccess.getExpression();
        if (expression instanceof LangSimpleName) {
            LangSimpleName simpleName = (LangSimpleName) expression;
            if ("self".equals(simpleName.getIdentifier()) || "cls".equals(simpleName.getIdentifier())) {
                // This is a 'this' expression in Python (self.something)
                LeafExpression thisExpr = new LeafExpression(cu, sourceFolder, filePath,
                        langFieldAccess, LocationInfo.CodeElementType.THIS_EXPRESSION, container);
                thisExpressions.add(thisExpr);
            }
        }

        // Continue visiting child nodes
        if (langFieldAccess.getExpression() != null) {
            langFieldAccess.getExpression().accept(this);
        }
//        if (langFieldAccess.getName() != null) {
//            langFieldAccess.getName().accept(this);
//        }
    }


    @Override
    public void visit(LangDictionaryLiteral langDictionaryLiteral) {

    }

    @Override
    public void visit(LangTupleLiteral langTupleLiteral) {

    }

    @Override
    public void visit(LangImportStatement langImportStatement) {

    }

    @Override
    public void visit(LangPrefixExpression langPrefixExpression) {
        // Add prefix expressions
        LeafExpression prefixExpr = new LeafExpression(cu, sourceFolder, filePath,
                langPrefixExpression, LocationInfo.CodeElementType.PREFIX_EXPRESSION, container);
        prefixExpressions.add(prefixExpr);

        // Visit the operand
        if (langPrefixExpression.getOperand() != null) {
            langPrefixExpression.getOperand().accept(this);
        }
    }


    @Override
    public void visit(LangPostfixExpression langPostFixExpression) {
        // Add postfix expressions
        LeafExpression postfixExpr = new LeafExpression(cu, sourceFolder, filePath,
                langPostFixExpression, LocationInfo.CodeElementType.POSTFIX_EXPRESSION, container);
        postfixExpressions.add(postfixExpr);

        // Visit the operand
        if (langPostFixExpression.getOperand() != null) {
            langPostFixExpression.getOperand().accept(this);
        }
    }


    @Override
    public void visit(LangNullLiteral langNullLiteral) {
        // Add null literals
        LeafExpression nullLit = new LeafExpression(cu, sourceFolder, filePath,
                langNullLiteral, LocationInfo.CodeElementType.NULL_LITERAL, container);
        nullLiterals.add(nullLit);
    }


    @Override
    public void visit(LangTryStatement langTryStatement) {

    }

    @Override
    public void visit(LangCatchClause langCatchClause) {

    }

    @Override
    public void visit(LangBreakStatement langBreakStatement) {

    }

    @Override
    public void visit(LangContinueStatement langContinueStatement) {

    }

    @Override
    public void visit(LangDelStatement langDelStatement) {

    }

    @Override
    public void visit(LangGlobalStatement langGlobalStatement) {

    }

    @Override
    public void visit(LangPassStatement langPassStatement) {

    }

    @Override
    public void visit(LangYieldStatement langYieldStatement) {

    }

    @Override
    public void visit(LangAnnotation langAnnotation) {

    }

    @Override
    public void visit(LangAssertStatement langAssertStatement) {

    }

    @Override
    public void visit(LangThrowStatement langThrowStatement) {

    }

    @Override
    public void visit(LangWithContextItem langWithContextItem) {

    }

    @Override
    public void visit(LangWithStatement langWithStatement) {

    }

    @Override
    public void visit(LangNonLocalStatement langNonLocalStatement) {

    }

    @Override
    public void visit(LangAsyncStatement langAsyncStatement) {

    }

    @Override
    public void visit(LangAwaitExpression langAwaitExpression) {

    }

    @Override
    public void visit(LangLambdaExpression langLambdaExpression) {

    }

    @Override
    public void visit(LangSwitchStatement langSwitchStatement) {

    }

    @Override
    public void visit(LangCaseStatement langCaseStatement) {

    }

    @Override
    public void visit(LangVariablePattern langVariablePattern) {

    }

    @Override
    public void visit(LangLiteralPattern langLiteralPattern) {

    }

    @Override
    public void visit(LangComment langComment) {

    }

    public LangCompilationUnit getCu() {
        return cu;
    }

    public String getSourceFolder() {
        return sourceFolder;
    }

    public String getFilePath() {
        return filePath;
    }

    public VariableDeclarationContainer getContainer() {
        return container;
    }

    public List<LeafExpression> getVariables() {
        return variables;
    }

    public List<String> getTypes() {
        return types;
    }

    public List<AbstractCall> getMethodInvocations() {
        return methodInvocations;
    }

    public List<VariableDeclaration> getVariableDeclarations() {
        return variableDeclarations;
    }

    public List<AnonymousClassDeclarationObject> getAnonymousClassDeclarations() {
        return anonymousClassDeclarations;
    }

    public List<LeafExpression> getTextBlocks() {
        return textBlocks;
    }

    public List<LeafExpression> getStringLiterals() {
        return stringLiterals;
    }

    public List<LeafExpression> getCharLiterals() {
        return charLiterals;
    }

    public List<LeafExpression> getNumberLiterals() {
        return numberLiterals;
    }

    public List<LeafExpression> getNullLiterals() {
        return nullLiterals;
    }

    public List<LeafExpression> getBooleanLiterals() {
        return booleanLiterals;
    }

    public List<LeafExpression> getTypeLiterals() {
        return typeLiterals;
    }

    public List<AbstractCall> getCreations() {
        return creations;
    }

    public List<LeafExpression> getInfixExpressions() {
        return infixExpressions;
    }

    public List<LeafExpression> getAssignments() {
        return assignments;
    }

    public List<String> getInfixOperators() {
        return infixOperators;
    }

    public List<LeafExpression> getArrayAccesses() {
        return arrayAccesses;
    }

    public List<LeafExpression> getPrefixExpressions() {
        return prefixExpressions;
    }

    public List<LeafExpression> getPostfixExpressions() {
        return postfixExpressions;
    }

    public List<LeafExpression> getThisExpressions() {
        return thisExpressions;
    }

    public List<LeafExpression> getArguments() {
        return arguments;
    }

    public List<LeafExpression> getParenthesizedExpressions() {
        return parenthesizedExpressions;
    }

    public List<LeafExpression> getCastExpressions() {
        return castExpressions;
    }

    public List<TernaryOperatorExpression> getTernaryOperatorExpressions() {
        return ternaryOperatorExpressions;
    }

    public List<LambdaExpressionObject> getLambdas() {
        return lambdas;
    }

    public DefaultMutableTreeNode getRoot() {
        return root;
    }

    public DefaultMutableTreeNode getCurrent() {
        return current;
    }

    public static String stringify(LangASTNode node) {
        LangASTFlattener printer = new PyASTFlattener(node);
        node.accept(printer);
        return printer.getResult();
    }
}
