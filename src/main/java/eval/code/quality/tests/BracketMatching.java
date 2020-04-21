package eval.code.quality.tests;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;
import eval.code.quality.position.Position;
import eval.code.quality.position.Range;
import eval.code.quality.position.SinglePosition;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.utils.ReportPosition;
import eval.code.quality.utils.Tuple;

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
        compilationUnit.findAll(BlockStmt.class).forEach(b -> b.getParentNode().ifPresent(parentNode -> {
            if(!(parentNode instanceof BlockStmt)) {
                if(!(parentNode instanceof IfStmt) && !(parentNode instanceof TryStmt) && !(parentNode instanceof CatchClause) && !(parentNode instanceof DoStmt)) {
                    if(parentNode instanceof MethodDeclaration) {
                        System.out.println("--" + (((MethodDeclaration) parentNode).getAnnotations())); // TODO javaparser get position without annotation
                    }
                    checkCurrentBlocks(parentNode, new BracketPos(b));
                }
            }
        }));
       compilationUnit.findAll(TryStmt.class).forEach(tryStmt -> {
           List<Tuple<SinglePosition, BracketPos>> bracketPosMap = new ArrayList<>();
           for(CatchClause catchClause: tryStmt.getCatchClauses()) {
               bracketPosMap.add(new Tuple<>(SinglePosition.from(catchClause.getBegin().get()), new BracketPos(catchClause.getBody())));
           }
           checkCurrentBlocks(tryStmt, new BracketPos(tryStmt.getTryBlock()), bracketPosMap);
       });
        compilationUnit.findAll(IfStmt.class).forEach(ifStmt -> {
            if(ifStmt.getParentNode().isEmpty() || !(ifStmt.getParentNode().get() instanceof IfStmt)) {
                List<Tuple<SinglePosition, BracketPos>> bracketPosMap = new ArrayList<>();
                IfStmt temp = ifStmt;
                IfStmt prev = ifStmt;
                while(temp.hasCascadingIfStmt()) {
                    temp = temp.getElseStmt().get().asIfStmt();
                    if(temp.hasThenBlock()) {
                        bracketPosMap.add(new Tuple<>(getIndexNext(contentProvider.getString(), "else", prev.getBegin().get().line),
                                new BracketPos(temp.getThenStmt().asBlockStmt())));
                    } else {
                        bracketPosMap.add(new Tuple<>(getIndexNext(contentProvider.getString(), "else", prev.getBegin().get().line), null));
                    }
                    prev = temp;
                    addIfOneLiner(temp.getThenStmt());
                }
                if(temp.hasElseBranch()) {
                    if(temp.hasElseBlock()) {
                        bracketPosMap.add(new Tuple<>(getIndexNext(contentProvider.getString(), "else", temp.getBegin().get().line),
                                new BracketPos(temp.getElseStmt().get().asBlockStmt())));
                    } else {
                        bracketPosMap.add(new Tuple<>(getIndexNext(contentProvider.getString(), "else", temp.getBegin().get().line), null));
                    }
                    addIfOneLiner(temp.getElseStmt().get());
                }
                if(ifStmt.hasThenBlock()) {
                    checkCurrentBlocks(ifStmt, new BracketPos(ifStmt.getThenStmt().asBlockStmt()), bracketPosMap);
                } else {
                    checkCurrentBlocks(ifStmt, null, bracketPosMap);
                }
                addIfOneLiner(ifStmt.getThenStmt());
            }
        });
        compilationUnit.findAll(ForStmt.class).forEach(forStmt -> addIfOneLiner(forStmt.getBody()));
        compilationUnit.findAll(ForEachStmt.class).forEach(forEachStmt -> addIfOneLiner(forEachStmt.getBody()));
        compilationUnit.findAll(DoStmt.class).forEach(doStmt -> {
            if(doStmt.getBody().isBlockStmt()) {
                List<Tuple<SinglePosition, BracketPos>> bracketPosMap = new ArrayList<>();
                bracketPosMap.add(new Tuple<>(getIndexNext(contentProvider.getString(), "while", doStmt.getBegin().get().line), null));
                checkCurrentBlocks(doStmt, new BracketPos(doStmt.getBody().asBlockStmt()), bracketPosMap);
            }
            addIfOneLiner(doStmt.getBody());
        });
        compilationUnit.findAll(WhileStmt.class).forEach(whileStmt -> addIfOneLiner(whileStmt.getBody()));
    }

    @Override
    protected void afterTests() {
        checkSameStyleBracket();
        reportWith(isOneLinerBlock, (isBlock) -> "one liner " + (isBlock ? "with" : "without") + " block", "one liner type");
    }

    private void checkSameStyleBracket() {
        reportWith(openingProperties, bracketProperty -> printWithType("opening bracket", "parent", bracketProperty), "opening bracket and parent");
        reportWith(dualProperties, (tuple) ->
                "child bracket property: (" +
                        printWithType("opening bracket", "parent", tuple._1) + ", " +
                        printWithType("child", "closing bracket", tuple._2) + ")", "child between opening and closing bracket");
        reportWith(closingProperties, bracketProperty -> printWithType("child", "closing bracket", bracketProperty), "closing bracket and child");
    }

    private String printWithType(String type1, String type2, BracketProperty bracketProperty) {
        return bracketProperty == BracketProperty.NEXT_LINE ?
                type1 + " on the line after " + type2 : type1 + " on the same line than " + type2;
    }

    public void checkCurrentBlocks(Node parent, BracketPos bracketsPosition) {
        checkCurrentBlocks(parent, bracketsPosition, new ArrayList<>());
    }

    public void checkCurrentBlocks(Node parent, BracketPos bracketsPosition, List<Tuple<SinglePosition, BracketPos>> bracketPosMap) {
        int parentLine = parent.getBegin().get().line;
        int parentColumn = parent.getBegin().get().column;
        if(bracketsPosition != null) {
            addToMap(getOpeningType(parentLine, parentColumn, bracketsPosition.range.begin), null, context.getPos(parent));
            if(bracketsPosition.range.end.column.get() != parentColumn) { // TODO check non empty too
                addError(ReportPosition.at(bracketsPosition.namedClosingPosition, "Closing bracket is not aligned with parent"));
            }
        }
        BracketPos prevBlock = bracketsPosition;
        for(Tuple<SinglePosition, BracketPos> tuple: bracketPosMap) {
            SinglePosition child = tuple._1;
            BracketPos currBlock = tuple._2;
            if(prevBlock == null) {
                if(child.column.get() != parentColumn) {
                    addError(ReportPosition.at(context.getPos(child), "Child is not aligned with parent"));
                }
                if(currBlock != null) {
                    addToMap(getOpeningType(child.line, parentColumn, currBlock.range.begin), null, context.getPos(parent));
                    if(currBlock.range.end.column.get() !=  parentColumn) {// TODO check non empty too
                        addError(ReportPosition.at(currBlock.namedClosingPosition, "Closing bracket is not aligned with parent"));
                    }
                }
            } else {
                if(currBlock == null) {
                    addToMap(null, getClosingType(parentColumn, prevBlock.range.end, child), context.getPos(child));
                } else {
                    addToMap(getOpeningType(child.line, parentColumn, currBlock.range.begin),
                            getClosingType(parentColumn, prevBlock.range.end, child), context.getPos(child));
                }
            }
            prevBlock = tuple._2;
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

    private SinglePosition getIndexNext(String content, String match, int fromLine) {
        Scanner scanner = new Scanner(content);
        int currentLine = 1;
        for(int i = 0; i < fromLine && scanner.hasNextLine(); ++i) {
            scanner.nextLine();
            currentLine++;
        }
        while(scanner.hasNextLine()) {
            int result = scanner.nextLine().indexOf(match);
            if(result != -1) {
                scanner.close();
                return new SinglePosition(currentLine, result + 1);
            }
            currentLine++;
        }
        scanner.close();
        return null;
    }

    private enum BracketProperty {
        SAME_LINE, NEXT_LINE
    }

    @Override
    protected String getName() {
        return "bracket matching";
    }

    private class BracketPos {
        public final Position namedOpeningPosition;
        public final Position namedClosingPosition;
        public final Range range;

        public BracketPos(BlockStmt blockStmt) {
            this.range = Range.from(blockStmt.getRange().get());
            this.namedOpeningPosition = context.getPos(range.begin);
            this.namedClosingPosition = context.getPos(range.end);
        }
    }
}
