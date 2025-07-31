package extension.ast.visitor;

import extension.ast.node.LangASTNode;
import extension.ast.node.OperatorEnum;
import extension.ast.node.declaration.LangMethodDeclaration;
import extension.ast.node.declaration.LangSingleVariableDeclaration;
import extension.ast.node.declaration.LangTypeDeclaration;
import extension.ast.node.expression.*;
import extension.ast.node.literal.*;
import extension.ast.node.metadata.LangAnnotation;
import extension.ast.node.metadata.comment.LangComment;
import extension.ast.node.pattern.LangLiteralPattern;
import extension.ast.node.pattern.LangVariablePattern;
import extension.ast.node.statement.*;
import extension.ast.node.unit.LangCompilationUnit;
import extension.ast.stringifier.LangASTFlattener;
import extension.ast.stringifier.PyASTFlattener;
import gr.uom.java.xmi.LocationInfo;
import gr.uom.java.xmi.VariableDeclarationContainer;
import gr.uom.java.xmi.decomposition.*;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.List;

public class LangVisitor implements LangASTVisitor {

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

    @Override
    public void visit(LangCompilationUnit langCompilationUnit) {

    }

    @Override
    public void visit(LangTypeDeclaration langTypeDeclaration) {
        // Visit class body members
        for (LangASTNode member : langTypeDeclaration.getMethods()) {
            member.accept(this);
        }

        // Visit decorators
        for (LangAnnotation decorator : langTypeDeclaration.getAnnotations()) {
            decorator.accept(this);
        }
    }


    @Override
    public void visit(LangMethodDeclaration methodDeclaration) {
        // Visit method parameters
        for (LangSingleVariableDeclaration param : methodDeclaration.getParameters()) {
            param.accept(this);
        }

        // Visit method body
        if (methodDeclaration.getBody() != null) {
            methodDeclaration.getBody().accept(this);
        }

        // Visit decorators/annotations
        for (LangAnnotation decorator : methodDeclaration.getAnnotations()) {
            decorator.accept(this);
        }
    }


    @Override
    public void visit(LangSingleVariableDeclaration langSingleVariableDeclaration) {
        // Create variable declaration for parameters
        VariableDeclaration varDecl = new VariableDeclaration(
                cu, sourceFolder, filePath, langSingleVariableDeclaration);

        variableDeclarations.add(varDecl);

        LeafExpression variable = new LeafExpression(cu, sourceFolder, filePath,
                langSingleVariableDeclaration,
                LocationInfo.CodeElementType.SIMPLE_NAME,
                container);
        variables.add(variable);
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
        LangASTNode leftSide = langInfixExpression.getLeft();
        LangASTNode rightSide = langInfixExpression.getRight();

        // Pattern: x + y * 2 -> we want to create "x + y"
        if (langInfixExpression.getOperator() == OperatorEnum.PLUS) {
            leftSide = langInfixExpression.getLeft();
            rightSide = langInfixExpression.getRight();

            if (rightSide instanceof LangInfixExpression rightInfix) {
                if (rightInfix.getOperator() == OperatorEnum.MULTIPLY) {
                    LangInfixExpression syntheticExpr = new LangInfixExpression(
                            leftSide,
                            OperatorEnum.PLUS,
                            rightInfix.getLeft(),
                            langInfixExpression.getPositionInfo()
                    );

                    LeafExpression syntheticLeaf = new LeafExpression(cu, sourceFolder, filePath,
                            syntheticExpr, LocationInfo.CodeElementType.INFIX_EXPRESSION, container);
                    infixExpressions.add(syntheticLeaf);
                    infixOperators.add(OperatorEnum.PLUS.name());

                    //System.out.println("Created synthetic: " + LangVisitor.stringify(syntheticExpr));
                }
            }
        }

        // Visit operands
        leftSide.accept(this);
        rightSide.accept(this);

    }


    @Override
    public void visit(LangMethodInvocation langMethodInvocation) {
        OperationInvocation invocation = new OperationInvocation(cu, sourceFolder, filePath, langMethodInvocation, container);
        methodInvocations.add(invocation);

        if(current.getUserObject() != null) {
            AnonymousClassDeclarationObject anonymous = (AnonymousClassDeclarationObject)current.getUserObject();
            anonymous.getMethodInvocations().add(invocation);
        }

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
        // Visit condition
        if (langIfStatement.getCondition() != null) {
            langIfStatement.getCondition().accept(this);
        }

        // Visit then block
        if (langIfStatement.getElseBody() != null) {
            langIfStatement.getElseBody().accept(this);
        }

    }


    @Override
    public void visit(LangWhileStatement langWhileStatement) {
        // Visit condition
        if (langWhileStatement.getCondition() != null) {
            langWhileStatement.getCondition().accept(this);
        }

        // Visit body
        if (langWhileStatement.getBody() != null) {
            langWhileStatement.getBody().accept(this);
        }
    }


    @Override
    public void visit(LangForStatement langForStatement) {
        // Handle for loop variable declarations: for var in collection:
        if (langForStatement.getInitializers() != null) {
            // Process each loop variable (e.g., 'num' in 'for num in numbers:')
            for (LangSingleVariableDeclaration initializer : langForStatement.getInitializers()) {
                // Let the single variable declaration visitor handle it
                initializer.accept(this);
            }
        }

        // Visit the iterable expression (condition field contains the iterable in Python for loops)
        if (langForStatement.getCondition() != null) {
            langForStatement.getCondition().accept(this);
        }

        // Visit update expressions (typically empty in Python for-each loops)
        if (langForStatement.getUpdates() != null) {
            for (LangASTNode update : langForStatement.getUpdates()) {
                update.accept(this);
            }
        }

        // Visit the body
        if (langForStatement.getBody() != null) {
            langForStatement.getBody().accept(this);
        }

        // Visit the else body (Python-specific: for-else construct)
        if (langForStatement.getElseBody() != null) {
            langForStatement.getElseBody().accept(this);
        }
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

            //System.out.println("Added local variable declaration: " + varDecl);
            variableDeclarations.add(varDecl);

        } // TODO
        else if (langAssignment.getLeftSide() instanceof LangFieldAccess fieldAccess) {
            // Handle field access like self.add, self.value etc
            if (fieldAccess.getExpression() != null) {
                fieldAccess.getExpression().accept(this);
            }

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
    public void visit(LangNumberLiteral langNumberLiteral) {
        // Add number literals
        LeafExpression numberLit = new LeafExpression(cu, sourceFolder, filePath,
                langNumberLiteral, LocationInfo.CodeElementType.NUMBER_LITERAL, container);
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
        // Process list elements: [1, 2, 3, variable]
        for (LangASTNode element : langListLiteral.getElements()) {
            element.accept(this);
        }
    }


    @Override
    public void visit(LangFieldAccess langFieldAccess) {
        // Handle field access like self.add, self.value, etc.

        // Check if this is a 'self' reference (Python's equivalent of 'this')
        LangASTNode expression = langFieldAccess.getExpression();
        if (expression instanceof LangSimpleName simpleName) {
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
        // CRITICAL: Also visit the field name to capture attribute references
        if (langFieldAccess.getName() != null) {
            langFieldAccess.getName().accept(this);
        }

//        if (langFieldAccess.getName() != null) {
//            langFieldAccess.getName().accept(this);
//        }
    }


    @Override
    public void visit(LangDictionaryLiteral langDictionaryLiteral) {
        // Process dictionary key-value pairs: {"key": value}
        for (LangDictionaryLiteral.Entry entry : langDictionaryLiteral.getEntries()) {
            // Visit the key
            if (entry.getKey() != null) {
                entry.getKey().accept(this);
            }
            // Visit the value
            if (entry.getValue() != null) {
                entry.getValue().accept(this);
            }
        }
    }


    @Override
    public void visit(LangTupleLiteral langTupleLiteral) {
        // Process tuple elements: (1, 2, variable)
        for (LangASTNode element : langTupleLiteral.getElements()) {
            element.accept(this);
        }
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
        // Visit try block
        if (langTryStatement.getBody() != null) {
            langTryStatement.getBody().accept(this);
        }

        // Visit catch clauses
        for (LangCatchClause catchClause : langTryStatement.getCatchClauses()) {
            catchClause.accept(this);
        }

        // Visit finally block
        if (langTryStatement.getFinallyBlock() != null) {
            langTryStatement.getFinallyBlock().accept(this);
        }
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
        // Handle global variable declarations
        for (LangSimpleName varName : langGlobalStatement.getVariableNames()) {
            LeafExpression globalVar = new LeafExpression(cu, sourceFolder, filePath,
                    varName, LocationInfo.CodeElementType.SIMPLE_NAME, container);
            variables.add(globalVar);
        }
    }


    @Override
    public void visit(LangPassStatement langPassStatement) {

    }

    @Override
    public void visit(LangYieldStatement langYieldStatement) {
        // Visit yielded expression
        if (langYieldStatement.getExpression() != null) {
            langYieldStatement.getExpression().accept(this);
        }
    }


    @Override
    public void visit(LangAnnotation langAnnotation) {

    }

    @Override
    public void visit(LangAssertStatement langAssertStatement) {
        // Visit the test expression (the condition being asserted)
        if (langAssertStatement.getExpression() != null) {
            langAssertStatement.getExpression().accept(this);
        }

        // Visit the optional message expression
        if (langAssertStatement.getMessage() != null) {
            langAssertStatement.getMessage().accept(this);
        }

    }

    @Override
    public void visit(LangThrowStatement langThrowStatement) {
        // Visit the thrown expression
        if (langThrowStatement.getExpressions() != null) {
            langThrowStatement.getExpressions().forEach(expression -> expression.accept(this));
        }
    }


    @Override
    public void visit(LangWithContextItem langWithContextItem) {
        // Visit the context expression
        if (langWithContextItem.getContextExpression() != null) {
            langWithContextItem.getContextExpression().accept(this);
        }

        // Visit the optional variables (could be name, tuple, etc.)
        if (langWithContextItem.getAlias() != null) {
            langWithContextItem.getAlias().accept(this);
        }
    }


    @Override
    public void visit(LangWithStatement langWithStatement) {
        // Visit context items
        for (LangASTNode item : langWithStatement.getContextItems()) {
            item.accept(this);
        }

        // Visit body
        if (langWithStatement.getBody() != null) {
            langWithStatement.getBody().accept(this);
        }
    }


    @Override
    public void visit(LangNonLocalStatement langNonLocalStatement) {

    }

    @Override
    public void visit(LangAsyncStatement langAsyncStatement) {
        // Visit the wrapped statement (async def, async with, async for)
        if (langAsyncStatement.getBody() != null) {
            langAsyncStatement.getBody().accept(this);
        }
    }


    @Override
    public void visit(LangAwaitExpression langAwaitExpression) {

    }

    @Override
    public void visit(LangLambdaExpression langLambdaExpression) {
        //TODO
//        // Create lambda expression object
//        LambdaExpressionObject lambdaObj = new LambdaExpressionObject(
//                cu, sourceFolder, filePath, langLambdaExpression, container);
//        lambdas.add(lambdaObj);
//
//        // Visit lambda parameters
//        for (LangASTNode param : langLambdaExpression.getParameters()) {
//            param.accept(this);
//        }
//
//        // Visit lambda body
//        if (langLambdaExpression.getBody() != null) {
//            langLambdaExpression.getBody().accept(this);
//        }
    }

    @Override
    public void visit(LangSwitchStatement langSwitchStatement) {
        // Visit the match expression
        if (langSwitchStatement.getExpression() != null) {
            langSwitchStatement.getExpression().accept(this);
        }

        // Visit all case statements
        for (LangCaseStatement caseStmt : langSwitchStatement.getCases()) {
            caseStmt.accept(this);
        }
    }

    @Override
    public void visit(LangCaseStatement langCaseStatement) {
        // Visit the case pattern (can contain variables and expressions)
        if (langCaseStatement.getPattern() != null) {
            langCaseStatement.getPattern().accept(this);
        }

        // Visit the case body
        if (langCaseStatement.getBody() != null) {
            langCaseStatement.getBody().accept(this);
        }
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
