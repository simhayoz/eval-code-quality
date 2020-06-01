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

    private Map<BracesProperty, List<Position>> openingProperties = new HashMap<>();
    private Map<BracesProperty, List<Position>> closingProperties = new HashMap<>();
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
        checkSameStyleBrace();
        inferMapProperty.checkAndReport(isOneLinerBlock, "one liner block with brace", true);
    }

    private void checkSameStyleBrace() {
        inferMapProperty.checkAndReport(openingProperties, "brace position next block", true);
        inferMapProperty.checkAndReport(closingProperties, "brace position previous block", true);
    }

    public void checkCurrentBlocks(ParentBlock parentBlock) {
        int parentColumn = parentBlock.getParentStart().column;
        if(parentBlock.bracesPosition != null) {
            addToMap(getOpeningType(parentBlock.getParentLineEnd(), parentColumn, parentBlock.bracesPosition.begin), null, context.getPos(parentBlock.parent));
            if(parentBlock.bracesPosition.end.column.get() != parentColumn && !parentBlock.childStatements.isEmpty()) {
                addError(new DescriptionBuilder()
                        .addPosition(context.getPos(parentBlock.bracesPosition.end), new Descriptor().addToDescription("Closing brace is not aligned with parent")));
            }
        }
        Range prevBlock = parentBlock.bracesPosition;
        for(ChildBlock childBlock: parentBlock.childBlocks) {
            SinglePosition child = childBlock.parent;
            Range currBlock = childBlock.bracesPosition;
            if(prevBlock == null) {
                if(child.column.get() != parentColumn) {
                    addError(new DescriptionBuilder()
                            .addPosition(context.getPos(child), new Descriptor().addToDescription("Child is not aligned with parent")));
                }
                if(currBlock != null) {
                    addToMap(getOpeningType(child.line, parentColumn, currBlock.begin), null, context.getPos(parentBlock.parent));
                    if(currBlock.end.column.get() !=  parentColumn && !childBlock.childStatements.isEmpty()) {
                        addError(new DescriptionBuilder()
                                .addPosition(context.getPos(currBlock.end), new Descriptor().addToDescription("Closing brace is not aligned with parent")));
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
            prevBlock = childBlock.bracesPosition;
        }
    }

    private BracesProperty getClosingType(int parentColumn, SinglePosition prevBracePos, SinglePosition childPos) {
        if(prevBracePos.line == childPos.line) {
            return BracesProperty.SAME_LINE;
        } else if(prevBracePos.line + 1 == childPos.line) {
            if(childPos.column.get() != parentColumn) {
                addError(new DescriptionBuilder()
                        .addPosition(context.getPos(childPos), new Descriptor().addToDescription("Child is not aligned with parent")));
            }
            return BracesProperty.NEXT_LINE;
        } else {
            addError(new DescriptionBuilder()
                    .addPosition(context.getPos(childPos), new Descriptor().addToDescription("Child was more than one line after closing brace of previous element")));
            return null;
        }
    }

    private BracesProperty getOpeningType(int parentLine, int parentColumn, SinglePosition bracePos) {
        if(braceHasElementBefore(context.getContentProvider().getString(), bracePos) || bracePos.line == parentLine) {
            // Specific check for multiple line header (method declaration with @annotation, if on multiple line, etc)
            return BracesProperty.SAME_LINE;
        } else if(bracePos.line == parentLine + 1) {
            if(bracePos.column.get() != parentColumn) {
                addError(new DescriptionBuilder()
                        .addPosition(context.getPos(bracePos), new Descriptor().addToDescription("Opening brace is not aligned with parent")));
            }
            return BracesProperty.NEXT_LINE;
        } else {
            addError(new DescriptionBuilder()
                    .addPosition(context.getPos(bracePos), new Descriptor().addToDescription("Opening brace was more than one line after parent")));
            return null;
        }
    }

    private void addToMap(BracesProperty openingProperty, BracesProperty closingProperty, Position position) {
            add(openingProperties, openingProperty, position);
            add(closingProperties, closingProperty, position);
    }

    private <T> void add(Map<T, List<Position>> map, T braceProperty, Position position) {
        if(braceProperty != null) {
            if(map.containsKey(braceProperty)) {
                map.get(braceProperty).add(position);
            } else {
                List<Position> list = new ArrayList<>();
                list.add(position);
                map.put(braceProperty, list);
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

    private static boolean braceHasElementBefore(String content, SinglePosition bracePos) {
        return content.split(System.lineSeparator())[bracePos.line-1].trim().charAt(0) != '{';
    }

    private enum BracesProperty {
        SAME_LINE,
        NEXT_LINE
    }

    @Override
    public String getName() {
        return "braces";
    }
}
