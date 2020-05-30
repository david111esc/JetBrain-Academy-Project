package flashcards;
import java.io.*;
import java.util.*;

class LoggerEngine{
    private ArrayList<String> logRecords = new ArrayList<>();
    public void writeLog(String record){
        logRecords.add(record);
    }
    public ArrayList<String> getLogRecords(){
        return logRecords;
    }
}
class Flashcards {
    private final PrintStream printStream;
    private final Scanner scanner;
    private final LoggerEngine logger;
    Map<String, String> termsToDefinitions = new HashMap<>();
    Map<String,Integer> errorRecords = new TreeMap<>();
    int maxErrorTimes = 0;
    int numberOfHardestCard = 0;

 
    public Flashcards(InputStream inputStream, PrintStream printStream, LoggerEngine logger) {
        this.printStream = printStream;
        this.scanner = new Scanner(inputStream);
        this.logger = logger;
    }
 
    private String getTerm(String definition) {
        String term = "";
        for(Map.Entry<String, String> card : termsToDefinitions.entrySet()) {
            if (card.getValue().equals(definition)) {
                term = card.getKey();
                break;
            }
        }
        return term;
    }
 
    public void add() {
        printStream.println("The card:");
        logger.writeLog("The card:");
        String term = scanner.nextLine();
        logger.writeLog(term);
        if (termsToDefinitions.containsKey(term)) {
            printStream.printf("The card \"%s\" already exists.%n", term);
            logger.writeLog("The card \"" + term + "\" already exists.");
            printStream.println();
            return;
        }
        printStream.println("The definition of the card:");
        logger.writeLog("The definition of the card:");
        String definition = scanner.nextLine();
        logger.writeLog(definition);
        if (termsToDefinitions.containsValue(definition)) {
            printStream.printf("The definition \"%s\" already exists.%n", definition);
            logger.writeLog("The definition \"" + definition + "\" already exists.");
            printStream.println();
            return;
        }
        termsToDefinitions.put(term, definition);
        errorRecords.put(term,0);
        printStream.printf("The pair (\"%s\":\"%s\") has been added.%n", term, definition);
        logger.writeLog(String.format("The pair (\"%s\":\"%s\") has been added.", term, definition));
        printStream.println();
    }
    public void remove() {
        printStream.println("The card:");
        logger.writeLog("The card:");
        String card = scanner.nextLine();
        logger.writeLog(card);
        if (termsToDefinitions.containsKey(card)) {
            termsToDefinitions.remove(card);
            if(errorRecords.containsKey(card)){
                errorRecords.remove(card);
            }
            printStream.println("The card has been removed.");
            logger.writeLog("The card has been removed.");
        } else {
            printStream.printf("Can't remove \"%s\": there is no such card.%n", card);
            logger.writeLog(String.format("Can't remove \"%s\": there is no such card.", card));
        }
    }
    private String randomTerm() {
        int size = termsToDefinitions.size();
        int randomIndex = new Random().nextInt(size);
        int index = 0;
        String randomTerm = "";
        for(String term : termsToDefinitions.keySet()) {
            if (index == randomIndex) {
                randomTerm = term;
                break;
            }
            index++;
        }
        return randomTerm;
    }
    private void addErrorCount(String term){
        int errorTimes;
        if(errorRecords.containsKey(term)){
            errorTimes = errorRecords.get(term) + 1;
        }else{
            errorTimes = 1;
        }
        errorRecords.put(term, errorTimes);
    }
    public void ask() {
        printStream.println("How many times to ask?");
        logger.writeLog("How many times to ask?");
        int times = Integer.parseInt(scanner.nextLine());
        logger.writeLog(String.valueOf(times));
        for (int i = 0; i < times; i++) {
            String term = randomTerm();
            String correctAnswer = termsToDefinitions.get(term);
            printStream.printf("Print the definition of \"%s\".%n", term);
            logger.writeLog(String.format("Print the definition of \"%s\".", term));
            String userAnswer = scanner.nextLine();
            logger.writeLog(userAnswer);
            if (Objects.equals(correctAnswer, userAnswer)) {
                printStream.println("Correct answer.");
                logger.writeLog("Correct answer.");
            } else if (termsToDefinitions.containsValue(userAnswer)) {
                printStream.printf("Wrong answer. The correct one is \"%s\", you've just written the definition of \"%s\".%n", correctAnswer, getTerm(userAnswer));
                logger.writeLog(String.format("Wrong answer. The correct one is \"%s\", you've just written the definition of \"%s\".", correctAnswer, getTerm(userAnswer)));
                addErrorCount(term);
            } else {
                printStream.printf("Wrong answer. The correct one is \"%s\".%n", correctAnswer);
                logger.writeLog(String.format("Wrong answer. The correct one is \"%s\".", correctAnswer));
                addErrorCount(term);
            }
        }
    }
    public void load() {
        printStream.println("File name:");
        logger.writeLog("File name:");
        String fileName = scanner.nextLine();
        logger.writeLog(fileName);
        File file = new File(fileName);
        int counter = 0;
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String key = scanner.nextLine();
                String value = scanner.nextLine();
                int errorTimes = Integer.parseInt(scanner.nextLine());
                termsToDefinitions.put(key, value);
                errorRecords.put(key,errorTimes);
                counter++;
            }
            printStream.printf("%d cards have been loaded.%n", counter);
            logger.writeLog(String.format("%d cards have been loaded.", counter));
        } catch (FileNotFoundException e) {
            printStream.println("File not found.");
            logger.writeLog("File not found.");
        }
    }
    public void load(String fileName) {
        File file = new File(fileName);
        int counter = 0;
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String key = scanner.nextLine();
                String value = scanner.nextLine();
                int errorTimes = Integer.parseInt(scanner.nextLine());
                termsToDefinitions.put(key, value);
                errorRecords.put(key,errorTimes);
                counter++;
            }
            printStream.printf("%d cards have been loaded.%n", counter);
            logger.writeLog(String.format("%d cards have been loaded.", counter));
        } catch (FileNotFoundException e) {
            printStream.println("File not found.");
            logger.writeLog("File not found.");
        }
    }
    public void save() {
        printStream.println("File name:");
        logger.writeLog("File name:");
        String fileName = scanner.nextLine();
        logger.writeLog(fileName);
        File file = new File(fileName);
        int counter = 0;
        try (PrintWriter printWriter = new PrintWriter(file)) {
            for (Map.Entry<String, String> card : termsToDefinitions.entrySet()) {
                printWriter.println(card.getKey());
                printWriter.println(card.getValue());
                printWriter.println(errorRecords.get(card.getKey()));
                counter++;
            }
            printStream.printf("%d cards have been saved.%n", counter);
            logger.writeLog(String.format("%d cards have been saved.", counter));
        } catch (IOException e) {
            System.out.printf("An exception occurs %s", e.getMessage());
            logger.writeLog(String.format("An exception occurs %s", e.getMessage()));
        }
    }
    public void save(String fileName) {
        File file = new File(fileName);
        int counter = 0;
        try (PrintWriter printWriter = new PrintWriter(file)) {
            for (Map.Entry<String, String> card : termsToDefinitions.entrySet()) {
                printWriter.println(card.getKey());
                printWriter.println(card.getValue());
                printWriter.println(errorRecords.get(card.getKey()));
                counter++;
            }
            printStream.printf("%d cards have been saved.%n", counter);
            logger.writeLog(String.format("%d cards have been saved.", counter));
        } catch (IOException e) {
            System.out.printf("An exception occurs %s", e.getMessage());
            logger.writeLog(String.format("An exception occurs %s", e.getMessage()));
        }
    }
    public void exit() {
        printStream.println("Bye bye!");
        logger.writeLog("Bye bye!");
    }
    public void log(){
        printStream.println("File name:");
        logger.writeLog("File name:");
        String fileName = scanner.nextLine();
        logger.writeLog(fileName);
        File file = new File(fileName);
        try (PrintWriter printWriter = new PrintWriter(file)) {
            for (String record : logger.getLogRecords()) {
                printWriter.println(record);
            }
            printStream.println("The log has been saved.");
            logger.writeLog("The log has been saved.");
        } catch (IOException e) {
            System.out.printf("An exception occurs %s", e.getMessage());
            logger.writeLog(String.format("An exception occurs %s", e.getMessage()));
        }
    }
    public String findHardestCards(){
        StringBuilder hardestCards = new StringBuilder();
        int count = 0;
        maxErrorTimes = 0;
        numberOfHardestCard = 0;
        for(Map.Entry<String,Integer> errorRecord: errorRecords.entrySet()){
            if(errorRecord.getValue() > maxErrorTimes){
                maxErrorTimes = errorRecord.getValue();
                count = 0;
                hardestCards= new StringBuilder();
            }
            if(errorRecord.getValue() == maxErrorTimes){
                count ++;
                hardestCards.append("\"");
                hardestCards.append(errorRecord.getKey());
                hardestCards.append("\", ");
            }
        }
        numberOfHardestCard = count;
        if(count > 0){
            return hardestCards.toString().substring(0,hardestCards.length()-2);
        } else {
            return "";
        }

    }

    public void hardest(){
        String hardestErrorCards = findHardestCards();
        if(maxErrorTimes == 0){
            printStream.println("There are no cards with errors.");
            logger.writeLog("There are no cards with errors.");
        }else{
            if(numberOfHardestCard == 1){
                printStream.printf("The hardest card is %s. You have %d errors answering it.",hardestErrorCards,maxErrorTimes);
                logger.writeLog(String.format("The hardest card is %s. You have %d errors answering it.",hardestErrorCards,maxErrorTimes));
            }else{
                printStream.printf("The hardest card are %s. You have %d errors answering it.",hardestErrorCards,maxErrorTimes);
                logger.writeLog(String.format("The hardest card are %s. You have %d errors answering it.",hardestErrorCards,maxErrorTimes));
            }
            printStream.println();
            printStream.println();
        }
    }

    public void resetStats(){
        errorRecords.clear();
        maxErrorTimes = 0;
        numberOfHardestCard =0;
        printStream.println("Card statistics has been reset.");
        logger.writeLog("Card statistics has been reset.");
        printStream.println();
    }
}
 
class CLI {
    private final PrintStream printStream;
    private final Flashcards flashcards;
    private final Scanner scanner;
    private final LoggerEngine logger;
 
    public CLI(InputStream inputStream, PrintStream printStream, Flashcards flashcards,LoggerEngine logger) {
        this.printStream = printStream;
        this.flashcards = flashcards;
        this.scanner = new Scanner(inputStream);
        this.logger = logger;
    }
 
    private void printMenu () {
        printStream.println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
        logger.writeLog("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
    }
    private String getCommand () {
        return scanner.nextLine();
    }
    private void handleCommand (Flashcards flashcards, String command) {
        switch (command) {
            case "add":
                flashcards.add();
                break;
            case "remove":
                flashcards.remove();
                break;
            case "export":
                flashcards.save();
                break;
            case "import":
                flashcards.load();
                break;
            case "ask":
                flashcards.ask();
                break;
            case "exit":
                flashcards.exit();
                break;
            case"log":
                flashcards.log();
                break;
            case "hardest card":
                flashcards.hardest();
                break;
            case "reset stats":
                flashcards.resetStats();
                break;
        }
    }
    public void start() {
        String command;
        do {
            printMenu();
            command = getCommand();
            logger.writeLog(command);
            handleCommand(flashcards, command);
        } while (!"exit".equals(command));
    }
}

class ArgsParser{
    public static boolean needImport = false;
    public static String importFileName = "";
    public static boolean needExport = false;
    public static String exportFileName = "";

    public static void parser(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-import")) {
                needImport = true;
                i = i + 1;
                importFileName = args[i];
            } else if (args[i].equals("-export")) {
                needExport = true;
                i = i + 1;
                exportFileName = args[i];
            }
        }
    }

}

public class Main {
    public static void main(String[] args) {
        ArgsParser.parser(args);
        LoggerEngine logger = new LoggerEngine();
        Flashcards flashcards = new Flashcards(System.in, System.out,logger);
        if (ArgsParser.needImport) {
            flashcards.load(ArgsParser.importFileName);
        }
        CLI cli = new CLI(System.in, System.out, flashcards,logger);
        cli.start();
        if (ArgsParser.needExport) {
            flashcards.save(ArgsParser.exportFileName);
        }
    }
}