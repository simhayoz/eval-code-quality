package eval.code.quality.tests;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;
import eval.code.quality.position.Position;
import eval.code.quality.position.Range;
import eval.code.quality.position.SinglePosition;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.utils.*;

import java.util.*;

public class BracketMatching extends CompilationUnitTest {

    private Map<BracketProperty, List<Position>> openingProperties = new HashMap<>();
    private Map<Tuple<BracketProperty, BracketProperty>, List<Position>> dualProperties = new HashMap<>();
    private Map<BracketProperty, List<Position>> closingProperties = new HashMap<>();
    private Map<Boolean, List<Position>> isOneLinerBlock = new HashMap<>();

    public BracketMatching(ContentProvider contentProvider) {
        super(contentProvider);
        isOneLinerBlock.put(true, new ArrayList<>());
        isOneLinerBlock.put(false, new ArrayList<>());
    }

    @Override
    protected void testFor(ContentProvider contentProvider) {
        CompilationUnit compilationUnit = contentProvider.getCompilationUnit();
        // TODO Javaparser get position without annotation
        ParentBlock.getFor(compilationUnit, contentProvider.getString()).forEach(this::checkCurrentBlocks);
        compilationUnit.findAll(IfStmt.class).forEach(ifStmt -> {
            addIfOneLiner(ifStmt.getThenStmt());
            ifStmt.getElseStmt().ifPresent(elseStmt -> {
                if(!elseStmt.isIfStmt()) {
                    addIfOneLiner(elseStmt);
                }
            });
        });
        compilationUnit.findAll(ForStmt.class).forEach(forStmt -> addIfOneLiner(forStmt.getBody()));
        compilationUnit.findAll(ForEachStmt.class).forEach(forEachStmt -> addIfOneLiner(forEachStmt.getBody()));
        compilationUnit.findAll(DoStmt.class).forEach(doStmt -> addIfOneLiner(doStmt.getBody()));
        compilationUnit.findAll(WhileStmt.class).forEach(whileStmt -> addIfOneLiner(whileStmt.getBody()));
    }

    @Override
    protected void afterTests() {
        checkSameStyleBracket();
        checkAndReport(isOneLinerBlock, "one liner block with bracket", true);
    }

    private void checkSameStyleBracket() {
        checkAndReport(openingProperties, "bracket position next block", true);
        checkAndReport(dualProperties, "bracket position (next block, previous block)", true);
        checkAndReport(closingProperties, "bracket position previous block", true);
    }

    public void checkCurrentBlocks(ParentBlock parentBlock) {
        if(parentBlock.parent instanceof MethodDeclaration) {
            System.out.println(parentBlock);
        }
        int parentLine = parentBlock.parent.getBegin().get().line;
        int parentColumn = parentBlock.parent.getBegin().get().column;
        if(parentBlock.bracketPosition != null) {
            addToMap(getOpeningType(parentLine, parentColumn, parentBlock.bracketPosition.begin), null, context.getPos(parentBlock.parent));
            if(parentBlock.bracketPosition.end.column.get() != parentColumn && !parentBlock.childStatements.isEmpty()) {
                addError(ReportPosition.at(context.getPos(parentBlock.bracketPosition.end), "Closing bracket is not aligned with parent"));
            }
        }
        Range prevBlock = parentBlock.bracketPosition;
        for(ChildBlock childBlock: parentBlock.childBlocks) {
            SinglePosition child = childBlock.parent;
            Range currBlock = childBlock.bracketPosition;
            if(prevBlock == null) {
                if(child.column.get() != parentColumn) {
                    addError(ReportPosition.at(context.getPos(child), "Child is not aligned with parent"));
                }
                if(currBlock != null) {
                    addToMap(getOpeningType(child.line, parentColumn, currBlock.begin), null, context.getPos(parentBlock.parent));
                    if(currBlock.end.column.get() !=  parentColumn && !childBlock.childStatements.isEmpty()) {
                        addError(ReportPosition.at(context.getPos(currBlock.end), "Closing bracket is not aligned with parent"));
                    }
                }
            } else {
                if(currBlock == null) {
                    addToMap(null, getClosingType(parentColumn, prevBlock.end, child), context.getPos(child));
                } else {
                    addToMap(getOpeningType(child.line, parentColumn, currBlock.begin),
                            getClosingType(parentColumn, prevBlock.end, child), context.getPos(child));
                }
            }
            prevBlock = childBlock.bracketPosition;
        }
    }

    private BracketProperty getClosingType(int parentColumn, SinglePosition prevBracketPos, SinglePosition childPos) {
        if(prevBracketPos.line == childPos.line) {
            return BracketProperty.SAME_LINE;
        } else if(prevBracketPos.line + 1 == childPos.line) {
            if(childPos.column.get() != parentColumn) {
                addError(ReportPosition.at(context.getPos(childPos), "Child not aligned with parent"));
            }
            return BracketProperty.NEXT_LINE;
        } else {
            addError(ReportPosition.at(context.getPos(childPos), "Child was more than one line after closing bracket of previous element"));
            return null;
        }
    }

    private BracketProperty getOpeningType(int parentLine, int parentColumn, SinglePosition bracketPos) {
        if(bracketPos.line == parentLine) {
            return BracketProperty.SAME_LINE;
        } else if(bracketPos.line == parentLine + 1) {
            if(bracketPos.column.get() != parentColumn) {
                addError(ReportPosition.at(context.getPos(bracketPos), "Opening bracket not aligned with parent"));
            }
            return BracketProperty.NEXT_LINE;
        } else {
            addError(ReportPosition.at(context.getPos(bracketPos), "Opening bracket was more than one line after parent"));
            return null;
        }
    }

    private void addToMap(BracketProperty openingProperty, BracketProperty closingProperty, Position position) {
        if(openingProperty != null && closingProperty != null) {
            add(dualProperties, new Tuple<>(openingProperty, closingProperty), position);
        } else {
            add(openingProperties, openingProperty, position);
            add(closingProperties, closingProperty, position);
        }
    }

    private <T> void add(Map<T, List<Position>> map, T bracketProperty, Position position) {
        if(bracketProperty != null) {
            if(map.containsKey(bracketProperty)) {
                map.get(bracketProperty).add(position);
            } else {
                List<Position> list = new ArrayList<>();
                list.add(position);
                map.put(bracketProperty, list);
            }
        }
    }

    private void addIfOneLiner(Statement statement) {
        if(statement.isBlockStmt() && statement.asBlockStmt().getStatements().size() == 1) {
            isOneLinerBlock.get(true).add(context.getPos(statement));
        }
        if(!statement.isBlockStmt()) {
            isOneLinerBlock.get(false).add(context.getPos(statement));
        }
    }

    private enum BracketProperty {
        SAME_LINE,
        NEXT_LINE
    }

    @Override
    protected String getName() {
        return "bracket matching";
    }
}
