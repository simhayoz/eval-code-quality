package eval.code.quality.tests;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.*;
import eval.code.quality.position.MultiplePosition;
import eval.code.quality.position.Position;
import eval.code.quality.position.Range;
import eval.code.quality.position.SinglePosition;
import eval.code.quality.provider.ContentProvider;
import eval.code.quality.utils.MultiplePossibility;
import eval.code.quality.utils.ReportPosition;
import eval.code.quality.utils.Tuple;

import java.util.*;
import java.util.stream.Collectors;

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
                }
                if(temp.hasElseBranch()) {
                    if(temp.hasElseBlock()) {
                        bracketPosMap.add(new Tuple<>(getIndexNext(contentProvider.getString(), "else", temp.getBegin().get().line),
                                new BracketPos(temp.getElseStmt().get().asBlockStmt())));
                    } else {
                        bracketPosMap.add(new Tuple<>(getIndexNext(contentProvider.getString(), "else", temp.getBegin().get().line), null));
                    }
                }
                if(ifStmt.hasThenBlock()) {
                    checkCurrentBlocks(ifStmt, new BracketPos(ifStmt.getThenStmt().asBlockStmt()), bracketPosMap);
                } else {
                    checkCurrentBlocks(ifStmt, null, bracketPosMap);
                }

            }
            addIfOneLiner(ifStmt.getThenStmt());
            ifStmt.getElseStmt().ifPresent(this::addIfOneLiner);
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
        checkSameStyleOneLiner();
    }

    private void checkSameStyleOneLiner() {
        if(isOneLinerBlock.get(true).size() > 0 && isOneLinerBlock.get(false).size() > 0) {
            if(isOneLinerBlock.get(true).size() > isOneLinerBlock.get(false).size()) {
                MultiplePosition multiplePosition = new MultiplePosition();
                isOneLinerBlock.get(false).forEach(multiplePosition::add);
                addError(ReportPosition.at(multiplePosition, "one liner with block (bracket)", "one liner without block (bracket)"));
            } else if(isOneLinerBlock.get(true).size() < isOneLinerBlock.get(false).size()) {
                MultiplePosition multiplePosition = new MultiplePosition();
                isOneLinerBlock.get(true).forEach(multiplePosition::add);
                addError(ReportPosition.at(multiplePosition, "one liner without block (bracket)", "one liner with block (bracket)"));
            } else {
                MultiplePosition multiplePositionBlock = new MultiplePosition();
                isOneLinerBlock.get(true).forEach(multiplePositionBlock::add);
                MultiplePosition multiplePositionNoBlock = new MultiplePosition();
                isOneLinerBlock.get(false).forEach(multiplePositionNoBlock::add);
                Map<Position, String> map = new HashMap<>();
                map.put(multiplePositionBlock, "opening bracket on next line");
                map.put(multiplePositionNoBlock, "opening bracket on same line");
                addError(MultiplePossibility.at(map, "expected the same style for opening bracket"));
            }
        }
    }

    private void checkSameStyleBracket() {
//        openingProperties.forEach((k, v) -> System.out.println(k + System.lineSeparator() + v.stream().map(e -> e.toString().indent(2)).collect(Collectors.joining(System.lineSeparator()))));
//        System.out.println("-");
//        dualProperties.forEach((k, v) -> System.out.println(k + System.lineSeparator() + v.stream().map(e -> e.toString().indent(2)).collect(Collectors.joining(System.lineSeparator()))));
//        System.out.println("-");
//        closingProperties.forEach((k, v) -> System.out.println(k + System.lineSeparator() + v.stream().map(e -> e.toString().indent(2)).collect(Collectors.joining(System.lineSeparator()))));
        checkAndReportStyleError(openingProperties, "opening");
        // TODO check and report error for dualProperties
        checkAndReportStyleError(closingProperties, "closing");
    }

    private void checkAndReportStyleError(Map<BracketProperty, List<Position>> propertiesMap, String type) { // TODO better error message
        if(!propertiesMap.get(BracketProperty.SAME_LINE).isEmpty() && !propertiesMap.get(BracketProperty.NEXT_LINE).isEmpty()) {
            if(propertiesMap.get(BracketProperty.SAME_LINE).size() > propertiesMap.get(BracketProperty.NEXT_LINE).size()) {
                MultiplePosition multiplePosition = new MultiplePosition(propertiesMap.get(BracketProperty.NEXT_LINE));
                addError(ReportPosition.at(multiplePosition, type + " bracket on the same line than parent", "on the line after"));
            } else if(propertiesMap.get(BracketProperty.SAME_LINE).size() < propertiesMap.get(BracketProperty.NEXT_LINE).size()) {
                MultiplePosition multiplePosition = new MultiplePosition(propertiesMap.get(BracketProperty.SAME_LINE));
                addError(ReportPosition.at(multiplePosition, type + " bracket on the line after parent", "on the same line"));
            } else {
                MultiplePosition multiplePositionNextLine = new MultiplePosition(propertiesMap.get(BracketProperty.NEXT_LINE));
                MultiplePosition multiplePositionSameLine = new MultiplePosition(propertiesMap.get(BracketProperty.SAME_LINE));
                Map<Position, String> map = new HashMap<>();
                map.put(multiplePositionNextLine, type + " bracket on next line");
                map.put(multiplePositionSameLine, type + " bracket on same line");
                addError(MultiplePossibility.at(map, "expected the same style for " + type + " bracket"));
            }
        }
    }

    public void checkCurrentBlocks(Node parent, BracketPos bracketsPosition) {
        checkCurrentBlocks(parent, bracketsPosition, new ArrayList<>());
    }

    public void checkCurrentBlocks(Node parent, BracketPos bracketsPosition, List<Tuple<SinglePosition, BracketPos>> bracketPosMap) {
//        System.out.println("Will check the following: " + parent +  "->" + bracketsPosition);
//        int i = 0;
//        System.out.println("Children: ");
//        for(Tuple<Node, BracketPos> t: bracketPosMap) {
//            System.out.println(t.toString() + i++);
//        }
//        System.out.println("-----------------------------------------------------");

        int parentLine = parent.getBegin().get().line;
        int parentColumn = parent.getBegin().get().column;

        if(bracketsPosition != null) {
            int openingBracketLine = bracketsPosition.range.begin.line;
            int openingBracketColumn = bracketsPosition.range.begin.column.get();

            // Check same line or not if not aligned opening 1st child
            addToMap(getOpeningType(parentLine, parentColumn, bracketsPosition.range.begin), null, context.getPos(parent));

            // Check closing bracket aligned with parent
            if(bracketsPosition.range.end.column.get() != parentColumn) {
                addError(ReportPosition.at(bracketsPosition.namedClosingPosition, "Closing bracket is not aligned with parent"));
            }
        }

        BracketPos prevBlock = bracketsPosition;

        // Iterate over children
        for(Tuple<SinglePosition, BracketPos> tuple: bracketPosMap) {
            SinglePosition child = tuple._1;
            BracketPos currBlock = tuple._2;
            if(prevBlock == null) {
                if(child.column.get() != parentColumn) {
                    addError(ReportPosition.at(context.getPos(child), "Child is not aligned with parent"));
                }
                if(currBlock != null) {
                    addToMap(getOpeningType(child.line, parentColumn, currBlock.range.begin), null, context.getPos(parent));
                    if(currBlock.range.end.column.get() !=  parentColumn) {
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
            return BracketProperty.NONE;
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
            return BracketProperty.NONE;
        }
    }

    private void addToMap(BracketProperty openingProperty, BracketProperty closingProperty, Position position) {
        BracketProperty rightOpening = openingProperty == BracketProperty.NONE ? null : openingProperty;
        BracketProperty rightClosing = closingProperty == BracketProperty.NONE ? null : closingProperty;
        if(rightOpening != null && rightClosing != null) {
            add(dualProperties, new Tuple<>(rightOpening, rightClosing), position);
        } else {
            add(openingProperties, rightOpening, position);
            add(closingProperties, rightClosing, position);
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
        SAME_LINE, NEXT_LINE, NONE
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

        @Override
        public String toString() {
            return "BracketPos{" +
                    "namedOpeningPosition=" + namedOpeningPosition +
                    ", namedClosingPosition=" + namedClosingPosition +
                    ", range=" + range +
                    '}';
        }
    }
}
