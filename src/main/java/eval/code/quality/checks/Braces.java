package eval.code.quality.checks;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.*;
import eval.code.quality.block.ChildBlock;
import eval.code.quality.block.ParentBlock;
import eval.code.quality.position.Position;
import eval.code.quality.position.Range;
import eval.code.quality.position.SinglePosition;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.utils.description.DescriptionBuilder;
import eval.code.quality.utils.description.Descriptor;

import java.util.*;

public class Braces extends CompilationUnitCheck {

    private Map<BracketProperty, List<Position>> openingProperties = new HashMap<>();
    private Map<BracketProperty, List<Position>> closingProperties = new HashMap<>();
    private Map<Boolean, List<Position>> isOneLinerBlock = new HashMap<>();

    public Braces(ContentProvider contentProvider) {
        super(contentProvider);
        isOneLinerBlock.put(true, new ArrayList<>());
        isOneLinerBlock.put(false, new ArrayList<>());
    }

    @Override
    protected void checkFor(ContentProvider contentProvider) {
        CompilationUnit compilationUnit = contentProvider.getCompilationUnit();
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
    protected void afterChecks() {
        checkSameStyleBracket();
        inferMapProperty.checkAndReport(isOneLinerBlock, "one liner block with bracket", true);
    }

    private void checkSameStyleBracket() {
        inferMapProperty.checkAndReport(openingProperties, "bracket position next block", true);
        inferMapProperty.checkAndReport(closingProperties, "bracket position previous block", true);
    }

    public void checkCurrentBlocks(ParentBlock parentBlock) {
        int parentColumn = parentBlock.getParentStart().column;
        if(parentBlock.bracketPosition != null) {
            addToMap(getOpeningType(parentBlock.getParentLineEnd(), parentColumn, parentBlock.bracketPosition.begin), null, context.getPos(parentBlock.parent));
            if(parentBlock.bracketPosition.end.column.get() != parentColumn && !parentBlock.childStatements.isEmpty()) {
                addError(new DescriptionBuilder()
                        .addPosition(context.getPos(parentBlock.bracketPosition.end), new Descriptor().addToDescription("Closing bracket is not aligned with parent")));
            }
        }
        Range prevBlock = parentBlock.bracketPosition;
        for(ChildBlock childBlock: parentBlock.childBlocks) {
            SinglePosition child = childBlock.parent;
            Range currBlock = childBlock.bracketPosition;
            if(prevBlock == null) {
                if(child.column.get() != parentColumn) {
                    addError(new DescriptionBuilder()
                            .addPosition(context.getPos(child), new Descriptor().addToDescription("Child is not aligned with parent")));
                }
                if(currBlock != null) {
                    addToMap(getOpeningType(child.line, parentColumn, currBlock.begin), null, context.getPos(parentBlock.parent));
                    if(currBlock.end.column.get() !=  parentColumn && !childBlock.childStatements.isEmpty()) {
                        addError(new DescriptionBuilder()
                                .addPosition(context.getPos(currBlock.end), new Descriptor().addToDescription("Closing bracket is not aligned with parent")));
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
                addError(new DescriptionBuilder()
                        .addPosition(context.getPos(childPos), new Descriptor().addToDescription("Child is not aligned with parent")));
            }
            return BracketProperty.NEXT_LINE;
        } else {
            addError(new DescriptionBuilder()
                    .addPosition(context.getPos(childPos), new Descriptor().addToDescription("Child was more than one line after closing bracket of previous element")));
            return null;
        }
    }

    private BracketProperty getOpeningType(int parentLine, int parentColumn, SinglePosition bracketPos) {
        if(bracketHasElementBefore(context.getContentProvider().getString(), bracketPos) || bracketPos.line == parentLine) {
            // Specific check for multiple line header (method declaration with @annotation, if on multiple line, etc)
            return BracketProperty.SAME_LINE;
        } else if(bracketPos.line == parentLine + 1) {
            if(bracketPos.column.get() != parentColumn) {
                addError(new DescriptionBuilder()
                        .addPosition(context.getPos(bracketPos), new Descriptor().addToDescription("Opening bracket is not aligned with parent")));
            }
            return BracketProperty.NEXT_LINE;
        } else {
            addError(new DescriptionBuilder()
                    .addPosition(context.getPos(bracketPos), new Descriptor().addToDescription("Opening bracket was more than one line after parent")));
            return null;
        }
    }

    private void addToMap(BracketProperty openingProperty, BracketProperty closingProperty, Position position) {
            add(openingProperties, openingProperty, position);
            add(closingProperties, closingProperty, position);
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

    private static boolean bracketHasElementBefore(String content, SinglePosition bracketPos) {
        return content.split(System.lineSeparator())[bracketPos.line-1].trim().charAt(0) != '{';
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