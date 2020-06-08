package eval.code.quality.checks;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.*;
import eval.code.quality.block.ChildBlock;
import eval.code.quality.block.IfBlock;
import eval.code.quality.block.ParentBlock;
import eval.code.quality.position.Position;
import eval.code.quality.position.Range;
import eval.code.quality.position.SinglePosition;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.utils.description.DescriptionBuilder;
import eval.code.quality.utils.description.Descriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static eval.code.quality.block.ParentBlock.getIndexNext;

public class Braces extends CompilationUnitCheck {

    private final Map<BracesProperty, List<Position>> openingProperties = new HashMap<>();
    private final Map<BracesProperty, List<Position>> closingProperties = new HashMap<>();
    private final Map<OneLinerBlock, List<Position>> oneLinerProperties = new HashMap<>();

    public Braces(ContentProvider contentProvider) {
        super(contentProvider);
    }

    @Override
    protected void checkFor(ContentProvider contentProvider) {
        CompilationUnit compilationUnit = contentProvider.getCompilationUnit();
        ParentBlock.getFor(compilationUnit, contentProvider.getString()).forEach(this::checkCurrentBlocks);
        compilationUnit.findAll(IfStmt.class).forEach(ifStmt -> {
            if (ifStmt.getParentNode().isEmpty() || !(ifStmt.getParentNode().get() instanceof IfStmt)) {
                addIfOneLiner(SinglePosition.from(ifStmt.getBegin().get()), ifStmt.getThenStmt());
                if (ifStmt.getParentNode().isEmpty() || !(ifStmt.getParentNode().get() instanceof IfStmt)) {
                    IfStmt temp = ifStmt;
                    IfStmt prev = ifStmt;
                    while (temp.hasCascadingIfStmt()) {
                        temp = temp.getElseStmt().get().asIfStmt();
                        addIfOneLiner(getIndexNext(contentProvider.getString(), "else", SinglePosition.from(prev.getThenStmt().getEnd().get())), temp.getThenStmt());
                        prev = temp;
                    }
                    final IfStmt tempIf = temp;
                    temp.getElseStmt().ifPresent(elseBranch -> addIfOneLiner(getIndexNext(contentProvider.getString(), "else", SinglePosition.from(tempIf.getThenStmt().getEnd().get())), elseBranch));
                }
            }
        });
        compilationUnit.findAll(ForStmt.class).forEach(forStmt -> addIfOneLiner(SinglePosition.from(forStmt.getBegin().get()), forStmt.getBody()));
        compilationUnit.findAll(ForEachStmt.class).forEach(forEachStmt -> addIfOneLiner(SinglePosition.from(forEachStmt.getBegin().get()), forEachStmt.getBody()));
        compilationUnit.findAll(DoStmt.class).forEach(doStmt -> addIfOneLiner(SinglePosition.from(doStmt.getBegin().get()), doStmt.getBody()));
        compilationUnit.findAll(WhileStmt.class).forEach(whileStmt -> addIfOneLiner(SinglePosition.from(whileStmt.getBegin().get()), whileStmt.getBody()));
    }

    @Override
    protected void afterChecks() {
        checkSameStyleBrace();
        inferMapProperty.checkAndReport(oneLinerProperties, "one liner block with brace", true);
    }

    private void checkSameStyleBrace() {
        inferMapProperty.checkAndReport(openingProperties, "brace position next block", true);
        inferMapProperty.checkAndReport(closingProperties, "brace position previous block", true);
    }

    public void checkCurrentBlocks(ParentBlock parentBlock) {
        int parentColumn = parentBlock.getParentStart().column;
        if (parentBlock.bracesPosition != null) {
            addToMap(getOpeningType(parentBlock.getParentLineEnd(), parentColumn, parentBlock.bracesPosition.begin), null, context.getPos(parentBlock.parent));
            if (parentBlock.bracesPosition.end.column.get() != parentColumn
                    && !parentBlock.childStatements.isEmpty()
                    && !sameLineBlock(parentBlock)) {
                addError(new DescriptionBuilder()
                        .addPosition(context.getPos(parentBlock.bracesPosition.end), new Descriptor().addToDescription("Closing brace is not aligned with parent")));
            }
        }
        Range prevBlock = parentBlock.bracesPosition;
        for (ChildBlock childBlock : parentBlock.childBlocks) {
            SinglePosition child = childBlock.parent;
            Range currBlock = childBlock.bracesPosition;
            if (prevBlock == null) {
                if (child.column.get() != parentColumn) {
                    addError(new DescriptionBuilder()
                            .addPosition(context.getPos(child), new Descriptor().addToDescription("Child is not aligned with parent")));
                }
                if (currBlock != null) {
                    addToMap(getOpeningType(child.line, parentColumn, currBlock.begin), null, context.getPos(parentBlock.parent));
                    if (currBlock.end.column.get() != parentColumn
                            && !childBlock.childStatements.isEmpty()
                            && !sameLineBlock(childBlock)) {
                        addError(new DescriptionBuilder()
                                .addPosition(context.getPos(currBlock.end), new Descriptor().addToDescription("Closing brace is not aligned with parent")));
                    }
                }
            } else {
                if (currBlock == null) {
                    addToMap(null, getClosingType(parentColumn, prevBlock.end, child), context.getPos(child));
                } else {
                    addToMap(getOpeningType(child.line, parentColumn, currBlock.begin),
                            getClosingType(parentColumn, prevBlock.end, child), context.getPos(child));
                }
            }
            prevBlock = childBlock.bracesPosition;
        }
    }

    private boolean sameLineBlock(ChildBlock childBlock) {
        return childBlock.childStatements.size() == 1 && childBlock.bracesPosition.end.line == childBlock.parent.line;
    }

    private boolean sameLineBlock(ParentBlock parentBlock) {
        return parentBlock.childStatements.size() == 1 && parentBlock.bracesPosition.end.line == parentBlock.getParentLineEnd();
    }

    private BracesProperty getClosingType(int parentColumn, SinglePosition prevBracePos, SinglePosition childPos) {
        if (prevBracePos.line == childPos.line) {
            return BracesProperty.SAME_LINE;
        } else if (prevBracePos.line + 1 == childPos.line) {
            if (childPos.column.get() != parentColumn) {
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
        if (braceHasElementBefore(context.getContentProvider().getString(), bracePos) || bracePos.line == parentLine) {
            // Specific check for multiple line header (method declaration with @annotation, if on multiple line, etc)
            return BracesProperty.SAME_LINE;
        } else if (bracePos.line == parentLine + 1) {
            if (bracePos.column.get() != parentColumn) {
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
        if (braceProperty != null) {
            if (map.containsKey(braceProperty)) {
                map.get(braceProperty).add(position);
            } else {
                List<Position> list = new ArrayList<>();
                list.add(position);
                map.put(braceProperty, list);
            }
        }
    }

    private void addIfOneLiner(SinglePosition parentStart, Statement statement) {
        if (statement.isBlockStmt() && statement.asBlockStmt().getStatements().size() == 1) {
            if (statement.getEnd().isPresent() && parentStart.line == statement.getEnd().get().line) {
                add(oneLinerProperties, OneLinerBlock.BRACES_SAME_LINE, context.getPos(statement));
            } else {
                add(oneLinerProperties, OneLinerBlock.BRACES_MULTI_LINE, context.getPos(statement));
            }
        }
        if (!statement.isBlockStmt()) {
            if (statement.getEnd().isPresent() && parentStart.line == statement.getEnd().get().line) {
                add(oneLinerProperties, OneLinerBlock.NO_BRACES_SAME_LINE, context.getPos(statement));
            } else {
                add(oneLinerProperties, OneLinerBlock.NO_BRACES_MULTI_LINE, context.getPos(statement));
            }
        }
    }

    private static boolean braceHasElementBefore(String content, SinglePosition bracePos) {
        return content.split(System.lineSeparator())[bracePos.line - 1].trim().charAt(0) != '{';
    }

    private enum BracesProperty {
        SAME_LINE,
        NEXT_LINE
    }

    private enum OneLinerBlock {
        BRACES_SAME_LINE,
        BRACES_MULTI_LINE,
        NO_BRACES_SAME_LINE,
        NO_BRACES_MULTI_LINE
    }

    @Override
    public String getName() {
        return "braces";
    }
}
