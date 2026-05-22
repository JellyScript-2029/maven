package com.maven;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.maven.model.Player;

public class Enterprise {

    // COLORS
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    public static final String BOLD_RED = "\u001B[1;31m";
    public static final String BOLD_GREEN = "\u001B[1;32m";
    public static final String BOLD_YELLOW = "\u001B[1;33m";
    public static final String BOLD_CYAN = "\u001B[1;36m";
    public static final String BOLD_WHITE = "\u001B[1;37m";

    public static Scanner input = new Scanner(System.in);
    public static int registration = 0;

    // Quizzer
    public static Player currentPlayer = null;
    public static ArrayList<Player> players = new ArrayList<>();
    public static Stack<Integer> history = new Stack<>();
    public static ArrayList<Integer> pool = new ArrayList<>();
    public static ArrayList<Integer> answeredQuestions = new ArrayList<>();
    public static int score = 0;
    public static int qCount = 1;
    public static int currentIndex = -1;
    public static final String playersFile = "Players.json";

    // Activity Log
    public static StringBuilder logs = new StringBuilder();

    public static String[] questions = {
            "Sino ang pambansang bayani ng Pilipinas?",
            "Ano ang kabisera ng Pilipinas?",
            "Ano ang pambansang ibon ng Pilipinas?",
            "Ilang kulay ang makikita sa watawat ng Pilipinas (hindi kasama ang puti)?",
            "Ano ang pambansang prutas ng Pilipinas?",
            "Sino ang kasalukuyang Pangulo ng Pilipinas?",
            "Ano ang pambansang bahay ng Pilipinas?",
            "Anong prutas ang tinaguriang King of Fruits at kilala sa amoy nito sa Davao?",
            "Ano ang pambansang bulaklak ng Pilipinas?",
            "Iito ang pinakamaliit na isda sa buong mundo na matatagpuan sa Pilipinas.",
            "Sino ang Ama ng Wikang Pambansa?",
            "Ano ang tawag sa pera ng Pilipinas?",
            "Anong tanyag na bulkang may perfect cone ang matatagpuan sa Albay?",
            "Ano ang pambansang laro ng Pilipinas?",
            "Ilang bituin ang mayroon sa watawat ng Pilipinas?",
            "Ano ang pinakamataas na bundok sa Pilipinas?",
            "Anong transportasyon ang tinaguriang King of the Road sa Pilipinas?",
            "Sino ang Ina ng Katipunan?",
            "Anong lungsod ang kilala bilang Summer Capital of the Philippines?",
            "Ano ang pambansang sapatos o tsinelas ng mga Pilipino noong unang panahon?",
            "Ano ang pamagat ng pambansang awit ng Pilipinas?",
            "Saan matatagpuan ang tanyag na Chocolate Hills?",
            "Sino ang namuno sa unang pag-ikot sa mundo at namatay sa labanan sa Mactan?",
            "Ano ang pambansang hayop ng Pilipinas?",
            "Ilang isla ang bumubuo sa Pilipinas?",
            "Ano ang tawag sa mga Pilipinong nagtatrabaho sa ibang bansa?",
            "Anong pagkain ang sikat na gawa sa balat ng baboy na pinirito hanggang lumutong?",
            "Sino ang pambansang kamao ng Pilipinas pagdating sa boxing?",
            "Anong relihiyon ang may pinakamalaking billing ng tagasunod sa Pilipinas?",
            "Ano ang tawag sa tradisyunal na kasuotan ng mga lalaking Pilipino?"
    };

    public static String[][] choices = {
            { "Dr. Jose Rizal", "Emilio Aguinaldo", "Lapu-Lapu", "Andres Bonifacio" },
            { "Maynila", "Cebu", "Davao", "Quezon City" },
            { "Maya", "Kalapati", "Agila ng Pilipinas", "Papagayo" },
            { "Dalawa", "Tatlo", "Apat", "Lima" },
            { "Mangga", "Saging", "Papaya", "Lansones" },
            { "Rodrigo Duterte", "Ferdinand Marcos Jr.", "Leni Robredo", "Bongbong Marcos" },
            { "Bahay Kubo", "Bahay na Bato", "Kubo", "Nipa Hut" },
            { "Mangosteen", "Durian", "Rambutan", "Lanzones" },
            { "Rosas", "Waling-Waling", "Sampaguita", "Ilang-Ilang" },
            { "Bangus", "Pandaca Pygmaea / Tabios", "Tilapia", "Dilis" },
            { "Manuel L. Quezon", "Jose Rizal", "Andres Bonifacio", "Emilio Aguinaldo" },
            { "Dollar", "Peso", "Piso", "Centavo" },
            { "Bulkang Mayon", "Bulkang Taal", "Bulkang Pinatubo", "Bulkang Bulusan" },
            { "Sipa", "Palo Sebo", "Arnis", "Patintero" },
            { "Apat", "Isa", "Dalawa", "Tatlo" },
            { "Bundok Apo", "Bundok Pulag", "Bundok Halcon", "Bundok Kanlaon" },
            { "Jeepney", "Tricycle", "Bus", "Kalesa" },
            { "Gabriela Silang", "Melchora Aquino", "Teresa Magbanua", "Tandang Sora" },
            { "Tagaytay", "Vigan", "Baguio City", "Batangas" },
            { "Tsinelas", "Sapatilya", "Alpombra", "Bakya" },
            { "Bayan Ko", "Pilipinas Kong Mahal", "Lupang Hinirang", "Sariling Atin" },
            { "Bohol", "Cebu", "Palawan", "Batanes" },
            { "Juan Sebastian Elcano", "Lapu-Lapu", "Ferdinand Magellan", "Andres Bonifacio" },
            { "Tamaraw", "Kalabaw", "Carabao", "Pawikan" },
            { "7,107", "7,000", "8,000", "7,641" },
            { "OFW", "Migrant Worker", "Seaman", "TNT" },
            { "Lechon", "Sisig", "Crispy Pata", "Chicharon" },
            { "Manny Pacquiao", "Nonito Donaire", "Mark Magsayo", "Gerry Penalosa" },
            { "Islam", "Protestantismo", "Katoliko", "Budismo" },
            { "Polo Barong", "Camisa de Chino", "Baro't Saya", "Barong Tagalog" }
    };

    public static String[] answer = {
            "Dr. Jose Rizal", "Maynila", "Agila ng Pilipinas", "Tatlo", "Mangga",
            "Ferdinand Marcos Jr.", "Bahay Kubo", "Durian", "Sampaguita", "Pandaca Pygmaea / Tabios",
            "Manuel L. Quezon", "Piso", "Bulkang Mayon", "Arnis", "Tatlo", "Bundok Apo", "Jeepney",
            "Melchora Aquino", "Baguio City", "Bakya", "Lupang Hinirang", "Bohol", "Ferdinand Magellan", "Kalabaw",
            "7,641", "OFW", "Chicharon", "Manny Pacquiao", "Katoliko", "Barong Tagalog"
    };

    public static void main(String[] args) throws InterruptedException {
        loadPlayers();
        mainMenu();
    }

    public static void loading(String core) throws InterruptedException {
        System.out.println(BOLD_GREEN + "               Loading " + core);
        for (int i = 0; i < 69; i++) {
            Thread.sleep(10);
            System.out.print(".");
        }
        System.out.println();
    }

    public static void mainMenu() throws InterruptedException {
        System.out.println(BOLD_CYAN + """
                ====================================================================
                                        W E L C O M E  TO
                                        JF  CORE  SYSTEMS
                              "The heart of your digital operations"

                                 [1] Core-Mart Inventory & POS
                                 [2] Core-Flix Rental & Booking
                                 [3] Core-Style Clothing Line
                                 [4] Core-Quizzer Trivia Game
                                 [0] Exit
                ====================================================================""" + RESET);
        System.out.print(BOLD_WHITE + "Enter choice: " + RESET);

        int choice = -1;
        if (input.hasNextInt()) {
            choice = input.nextInt();
            input.nextLine();
        } else {
            System.out.println(RED + "Please enter a valid number." + RESET);
            input.nextLine();
            mainMenu();
            return;
        }

        System.out.println(BOLD_CYAN + "====================================================================" + RESET);
        String core = "";

        switch (choice) {
            case 1 -> {
                core = "Core-Mart Inventory & POS";
                loading(core);
                mart();
            }
            case 2 -> {
                core = "Core-Flix Rental & Booking";
                loading(core);
                movies();
            }
            case 3 -> {
                core = "Core-Style Clothing Line";
                loading(core);
                ClothingBrands();
            }
            case 4 -> {
                core = "Core-Quizzer Trivia Game";
                loading(core);
                quizzerHome();
            }
            case 0 ->
                System.out.println(BOLD_CYAN + """
                        ┌─────────────────────────────────────────────────────────────────┐
                        │                                                                 │
                        │              THANK YOU FOR USING JF  CORE  SYSTEMS              │
                        │                                                                 │
                        └─────────────────────────────────────────────────────────────────┘
                                    """ + RESET);
            default -> {
                System.out.println(RED + "Invalid input" + RESET);
                mainMenu();
            }
        }
    }

    public static void mart() throws InterruptedException {
        String strProdName, strAnotherP;
        char cCustomer = 'y', cAnotherP = 'y';
        double dQty, dBill, dPrice, dTotal, dPay, dChange = 0;

        logs.append("========================= Core-Mart Session Begin =========================\n");

        do {
            dBill = 0;
            System.out.println(BOLD_CYAN + """
                    ┌─────────────────────────────────────────────────────────────────┐
                    │                                                                 │
                    │             Welcome to Core-Mart Inventory & POS                │
                    │                                                                 │
                    └─────────────────────────────────────────────────────────────────┘""" + RESET);
            do {
                System.out.print("Product name:  ");
                strProdName = input.nextLine();

                while (true) {
                    System.out.print("Price:         ");
                    if (input.hasNextDouble()) {
                        dPrice = input.nextDouble();
                        if (dPrice >= 0)
                            break;
                    }
                    System.out.println(RED + "Invalid price! Try again." + RESET);
                    input.nextLine();
                }

                while (true) {
                    System.out.print("Quantity:      ");
                    if (input.hasNextDouble()) {
                        dQty = input.nextDouble();
                        if (dQty > 0)
                            break;
                    }
                    System.out.println(RED + "Invalid quantity! Try again." + RESET);
                    input.nextLine();
                }
                input.nextLine();

                dTotal = dQty * dPrice;
                System.out.println("Total:         " + dTotal);
                dBill += dTotal;

                logs.append(String.format("Item: %s | Price: %.2f | Qty: %.2f | Total: %.2f\n", strProdName, dPrice,
                        dQty, dTotal));

                System.out.print(BOLD_GREEN + ">>>> Another product Y/N? " + RESET);
                strAnotherP = input.nextLine();
                cAnotherP = strAnotherP.isEmpty() ? 'n' : strAnotherP.charAt(0);
            } while (Character.toLowerCase(cAnotherP) == 'y');

            while (true) {
                System.out.println("Bill:            " + dBill);
                System.out.print("Payment:         ");
                if (input.hasNextDouble()) {
                    dPay = input.nextDouble();
                    input.nextLine();
                    if (dPay >= dBill) {
                        dChange = dPay - dBill;
                        System.out.println("Change:         " + dChange);
                        System.out.println(BOLD_GREEN
                                + "====================================================================" + RESET);
                        System.out.println(BOLD_WHITE + "                   Thank you for shopping! Goodbye. " + RESET);
                        System.out.println(BOLD_GREEN
                                + "====================================================================" + RESET);
                        logs.append(
                                String.format("Total Bill: %.2f | Paid: %.2f | Change: %.2f\n", dBill, dPay, dChange));
                        break;
                    } else {
                        System.out.println(BOLD_RED + "Money is not enough!" + RESET);
                    }
                } else {
                    System.out.println(RED + "Invalid amount! Enter numbers only." + RESET);
                    input.nextLine();
                }
            }

            System.out.print(BOLD_GREEN + ">>>> Another customer Y/N? " + RESET);
            String strCustomer = input.nextLine();
            cCustomer = strCustomer.isEmpty() ? 'n' : strCustomer.charAt(0);
        } while (Character.toLowerCase(cCustomer) == 'y');
        logs.append("========================= Core-Mart Session End =========================\n\n");
        try (FileWriter logWriter = new FileWriter("record.txt", true)) {
            logWriter.write(logs.toString());
            logs.setLength(0);
        } catch (IOException e) {
            System.out.println(RED + "Error writing activity logs." + RESET);
        }

        System.out.println(BOLD_GREEN + "Grocery program is terminating..." + RESET);
        mainMenu();
    }

    public static void movies() throws InterruptedException {
        String response;
        int dvdTotal = 0, vcdTotal = 0, tapeTotal = 0;
        int horrorTotal = 0, scifiTotal = 0, dramaTotal = 0, comedyTotal = 0, cartoonsTotal = 0;
        int rentalTotal = 0, salesTotal = 0;

        logs.append("========================= Core-Flix Session Begin =========================\n");

        do {
            System.out.println(BOLD_CYAN + """
                    ┌─────────────────────────────────────────────────────────────────┐
                    │                                                                 │
                    │              Welcome to Core-Flix Rental & Booking              │
                    │                                                                 │
                    └─────────────────────────────────────────────────────────────────┘""" + RESET);
            System.out.println(PURPLE + "\n================== R E G I S T R A T I O N  H U B =================");
            System.out.println("""
                                                T Y P E
                        Please Choose:
                                [1] DVD
                                [2] VCD
                                [3] TAPE
                    ====================================================================""" + RESET);

            int choice = -1;
            while (true) {
                System.out.print(YELLOW + "Type: " + RESET);
                if (input.hasNextInt()) {
                    choice = input.nextInt();
                    if (choice >= 1 && choice <= 3)
                        break;
                }
                System.out.println(RED + "Invalid choice! Enter 1, 2, or 3." + RESET);
                input.nextLine();
            }
            input.nextLine();

            String type = switch (choice) {
                case 1 -> {
                    dvdTotal++;
                    yield "DVD";
                }
                case 2 -> {
                    vcdTotal++;
                    yield "VCD";
                }
                case 3 -> {
                    tapeTotal++;
                    yield "Tape";
                }
                default -> "Unknown";
            };

            System.out.print(YELLOW + "Input Title: " + RESET);
            String title = input.nextLine();

            System.out.println(PURPLE
                    + "\n=====================================================================\n                         C A T E G O R Y\n    Please Choose:\n            [1] Horror\n            [2] Scifi\n            [3] Drama\n            [4] Comedy\n            [5] Cartoons ");
            System.out.println("====================================================================" + RESET);

            int choice2 = -1;
            while (true) {
                System.out.print(YELLOW + "Category: " + RESET);
                if (input.hasNextInt()) {
                    choice2 = input.nextInt();
                    if (choice2 >= 1 && choice2 <= 5)
                        break;
                }
                System.out.println(RED + "Invalid choice! Enter between 1 and 5." + RESET);
                input.nextLine();
            }

            String category = switch (choice2) {
                case 1 -> {
                    horrorTotal++;
                    yield "Horror";
                }
                case 2 -> {
                    scifiTotal++;
                    yield "Scifi";
                }
                case 3 -> {
                    dramaTotal++;
                    yield "Drama";
                }
                case 4 -> {
                    comedyTotal++;
                    yield "Comedy";
                }
                case 5 -> {
                    cartoonsTotal++;
                    yield "Cartoons";
                }
                default -> "Unknown";
            };

            double minutes = -1;
            while (true) {
                System.out.print(YELLOW + "Input Duration (mins): " + RESET);
                if (input.hasNextDouble()) {
                    minutes = input.nextDouble();
                    if (minutes > 0)
                        break;
                }
                System.out.println(RED + "Invalid duration! Enter a valid value." + RESET);
                input.nextLine();
            }
            input.nextLine();
            System.out.println(PURPLE
                    + "\n=====================================================================\n                    T R A N S A C T I O N  T Y P E\n    Please Choose:\n            [1] Rental\n            [2] Sales");
            System.out.println("====================================================================" + RESET);

            int choice3 = -1;
            while (true) {
                System.out.print(YELLOW + "Transaction Type: " + RESET);
                if (input.hasNextInt()) {
                    choice3 = input.nextInt();
                    if (choice3 == 1 || choice3 == 2)
                        break;
                }
                System.out.println(RED + "Invalid choice! Enter 1 or 2." + RESET);
                input.nextLine();
            }

            String transType = switch (choice3) {
                case 1 -> {
                    rentalTotal++;
                    yield "Rental";
                }
                case 2 -> {
                    salesTotal++;
                    yield "Sales";
                }
                default -> "Unknown";
            };

            double price = -1;
            while (true) {
                System.out.print(YELLOW + "Input Price: " + RESET);
                if (input.hasNextDouble()) {
                    price = input.nextDouble();
                    if (price >= 0)
                        break;
                }
                System.out.println(RED + "Invalid price! Enter a valid value." + RESET);
                input.nextLine();
            }
            input.nextLine();

            System.out.println(GREEN + "\n=================== O R D E R   S U M M A R Y =================");
            System.out.println("        Type:             " + type);
            System.out.println("        Title:            " + title);
            System.out.println("        Category:         " + category);
            System.out.println("        Duration:         " + minutes);
            System.out.println("        Transaction type: " + transType);
            System.out.println("==================================================================" + RESET);

            logs.append(String.format("Added Movie: %s [%s] | Genre: %s | Mode: %s | Price: %.2f\n", title, type,
                    category, transType, price));

            System.out.print("Display another Y/N? " + RESET);
            response = input.nextLine();
        } while (response.equalsIgnoreCase("Y"));

        System.out.printf(BOLD_YELLOW + """
                ┌─────────────────────────────────────────────────────────────────┐
                │                                                                 │
                │                   S Y S T E M    R E P O R T S                  │
                │                                                                 │
                │    [TRANSACTION SUMMARY]                   [GENRE BREAKDOWN]    │
                │     For rent:      %d                        Horror:      %d      │
                │     For sale:      %d                        Scifi:       %d      │
                │                                             Drama:       %d      │
                │                                             Comedy:      %d      │
                │                                             Cartoons:    %d      │
                │   [FORMAT TOTALS]                                               │
                │     VCD:        %d                                               │
                │     DVD:        %d                                               │
                │     Tape:       %d                                               │
                └─────────────────────────────────────────────────────────────────┘
                """, rentalTotal, horrorTotal, salesTotal, scifiTotal, dramaTotal, comedyTotal, cartoonsTotal, vcdTotal,
                dvdTotal, tapeTotal);
        logs.append("========================= Core-Flix Session End =========================\n\n");
        try (FileWriter logWriter = new FileWriter("record.txt", true)) {
            logWriter.write(logs.toString());
            logs.setLength(0);
        } catch (IOException e) {
            System.out.println(RED + "Error writing activity logs." + RESET);
        }

        mainMenu();
    }

    public static void ClothingBrands() throws InterruptedException {
        String strBrand, strproduct, strColor, strSize;
        double dPrice;
        char strResponse = 'y';
        logs.append("========================= Core-Style Session Begin =========================\n");
        System.out.println(BOLD_CYAN + """
                ┌─────────────────────────────────────────────────────────────────┐
                │                                                                 │
                │               Welcome to Core-Style Clothing Line               │
                │                                                                 │
                └─────────────────────────────────────────────────────────────────┘""" + RESET);

        do {
            System.out
                    .println(PURPLE + "\n============== R E G I S T R A T I O N    A R E A  ================" + RESET);
            System.out.print("Brand Name:      ");
            strBrand = input.nextLine();
            System.out.print("Product:         ");
            strproduct = input.nextLine();
            System.out.print("Color:           ");
            strColor = input.nextLine();
            System.out.print("Size:            ");
            strSize = input.nextLine();

            while (true) {
                System.out.print("Price:           $");
                if (input.hasNextDouble()) {
                    dPrice = input.nextDouble();
                    if (dPrice >= 0)
                        break;
                }
                System.out.println(RED + "Invalid price! Enter numbers only." + RESET);
                input.nextLine();
            }
            input.nextLine();

            registration++;
            logs.append(String.format("Brand: %s | Product: %s | Color: %s | Size: %s | Price: $%.2f\n", strBrand,
                    strproduct, strColor, strSize, dPrice));
            strResponse = anotherRegistration();
        } while (Character.toLowerCase(strResponse) == 'y');

        System.out.println(GREEN + numberOfRegistrations() + " items registered" + RESET);
        logs.append("========================= Core-Style Session End =========================\n\n");
        try (FileWriter logWriter = new FileWriter("record.txt", true)) {
            logWriter.write(logs.toString());
            logs.setLength(0);
        } catch (IOException e) {
            System.out.println(RED + "Error writing activity logs." + RESET);
        }

        mainMenu();
    }

    public static void quizzerHome() throws InterruptedException {
        System.out.println(BOLD_CYAN + """
                ┌─────────────────────────────────────────────────────────────────┐
                │                                                                 │
                │               Welcome to Core-Quizzer Trivia Game               │
                │                                                                 │
                └─────────────────────────────────────────────────────────────────┘""" + RESET);
        System.out.println("""
                ==================================================================
                                           MAIN MENU
                                    [1] Log In
                                    [2] Sign Up
                                    [0] Back to Main Menu
                ==================================================================
                                """);
        System.out.print("Choice: ");

        int choice = -1;
        if (input.hasNextInt()) {
            choice = input.nextInt();
            input.nextLine();
        } else {
            System.out.println(RED + "Please enter a valid choice number." + RESET);
            input.nextLine();
            quizzerHome();
            return;
        }

        switch (choice) {
            case 1 -> {
                if (logIn()) {
                    quizGameMenu();
                } else {
                    quizzerHome();
                }
            }
            case 2 -> {
                signUp();
                quizGameMenu();
            }
            case 0 ->
                mainMenu();
            default -> {
                System.out.println("Invalid choice.");
                quizzerHome();
            }
        }
    }

    public static void quizGameMenu() throws InterruptedException {
        System.out.println("""
                ==================================================================
                               [1] PLAY               [2] EXIT TO MAIN
                ==================================================================""");
        System.out.print("Choice: ");

        int choice = -1;
        if (input.hasNextInt()) {
            choice = input.nextInt();
            input.nextLine();
        } else {
            System.out.println(RED + "Please enter numbers only." + RESET);
            input.nextLine();
            quizGameMenu();
            return;
        }

        switch (choice) {
            case 1 -> {
                score = 0;
                qCount = 1;
                history.clear();
                pool.clear();
                answeredQuestions.clear();
                for (int i = 0; i < questions.length; i++) {
                    pool.add(i);
                }
                pickRandomQuestion();
                playQuiz();
            }
            case 2 ->
                mainMenu();
            default -> {
                System.out.println("Invalid choice.");
                quizGameMenu();
            }
        }
    }

    public static void playQuiz() throws InterruptedException {
        boolean alreadyAnswered = answeredQuestions.contains(currentIndex);

        System.out.println("\n==================================================================");
        System.out.println(YELLOW + "Current Score: " + score + RESET);
        System.out.println(PURPLE + "Question #" + qCount);
        System.out.println(questions[currentIndex]);
        System.out.println("A) " + choices[currentIndex][0]);
        System.out.println("B) " + choices[currentIndex][1]);
        System.out.println("C) " + choices[currentIndex][2]);
        System.out.println("D) " + choices[currentIndex][3] + RESET);
        if (alreadyAnswered) {
            System.out.println(GREEN + "ALREADY ANSWERED");
        }
        System.out.println(BOLD_CYAN + "==================================================================");
        System.out.println("  [1] Next        [2] Back          [3] Answer         [0] Exit   ");
        System.out.println("==================================================================" + RESET);
        System.out.print("  Choice: ");
        String action = input.nextLine().trim();

        switch (action) {
            case "1" -> {
                if (qCount >= 30) {
                    if (currentPlayer != null) {
                        currentPlayer.setScore(score);
                    }
                    savePlayers();
                    mainMenu();
                } else {
                    if (qCount < history.size()) {
                        currentIndex = history.get(qCount);
                    } else {
                        pickRandomQuestion();
                    }
                    qCount++;
                    playQuiz();
                }
            }
            case "2" -> {
                if (history.size() > 1) {
                    history.pop();
                    currentIndex = history.peek();
                    qCount--;
                    playQuiz();
                } else {
                    System.out.println(RED + "No previous questions.");
                    playQuiz();
                }
            }
            case "3" -> {
                if (alreadyAnswered) {
                    System.out.println(RED + "You already answered this question!");
                    playQuiz();
                    return;
                }

                System.out.print("Answer: ");
                String letter = input.nextLine().trim().toUpperCase();
                String userAnswer = switch (letter) {
                    case "A" -> choices[currentIndex][0];
                    case "B" -> choices[currentIndex][1];
                    case "C" -> choices[currentIndex][2];
                    case "D" -> choices[currentIndex][3];
                    default -> null;
                };

                if (userAnswer == null) {
                    System.out.println(RED + "Invalid letter. Choose A, B, C, or D." + RESET);
                    playQuiz();
                    return;
                }

                answeredQuestions.add(currentIndex);

                if (userAnswer.equalsIgnoreCase(answer[currentIndex].trim())) {
                    System.out.println(BOLD_GREEN + "CORRECT!" + RESET);
                    score++;
                } else {
                    System.out.println(BOLD_RED + "INCORRECT! The correct answer is: "
                            + answer[currentIndex].toUpperCase() + RESET);
                }

                if (qCount >= 30) {
                    System.out.println(BOLD_YELLOW + "\n============================================");
                    System.out.println("         QUIZ COMPLETE!");
                    System.out.println("Final Score: " + score + "/30");
                    System.out.println("============================================" + RESET);
                    if (currentPlayer != null && score > currentPlayer.getScore()) {
                        currentPlayer.setScore(score);
                    }

                    logs.append(String.format("Quizzer: %s | Completed Full Attempt | Final Score: %d/30\n",
                            (currentPlayer != null ? currentPlayer.getUsername() : "Guest"), score));
                    try (FileWriter logWriter = new FileWriter("record.txt", true)) {
                        logWriter.write(logs.toString());
                        logs.setLength(0);
                    } catch (IOException e) {
                        System.out.println(RED + "Error writing activity logs." + RESET);
                    }

                    savePlayers();
                    mainMenu();
                } else {
                    if (qCount == history.size()) {
                        pickRandomQuestion();
                    } else {
                        currentIndex = history.get(qCount);
                    }
                    qCount++;
                    playQuiz();
                }
            }
            case "0" -> {
                System.out.println(YELLOW + "Total score: " + score + RESET);
                if (currentPlayer != null && score > currentPlayer.getScore()) {
                    currentPlayer.setScore(score);
                }

                logs.append(String.format(
                        "Quizzer: %s | Score Achieved: %d | Exited at Question: #%d\n",
                        (currentPlayer != null ? currentPlayer.getUsername() : "Guest"), score, qCount));
                try (FileWriter logWriter = new FileWriter("record.txt", true)) {
                    logWriter.write(logs.toString());
                    logs.setLength(0);
                } catch (IOException e) {
                    System.out.println(RED + "Error writing activity logs." + RESET);
                }

                savePlayers();
                mainMenu();
            }
            default -> {
                System.out.println(RED + "Invalid choice.");
                playQuiz();
            }
        }
    }

    public static void pickRandomQuestion() {
        Random random = new Random();

        if (pool.isEmpty()) {
            return;
        }

        int poolPosition = random.nextInt(pool.size());
        currentIndex = pool.get(poolPosition);
        pool.remove(poolPosition);

        history.push(currentIndex);
    }

    public static boolean logIn() {
        System.out.println(BOLD_CYAN + """
                ╔════════════════════════════════════════════════════════╗
                ║                                                        ║
                ║      ██╗      ██████╗  ██████╗     ██╗███╗   ██╗       ║
                ║      ██║     ██╔═══██╗██╔════╝     ██║████╗  ██║       ║
                ║      ██║     ██║   ██║██║  ███╗    ██║██╔██╗ ██║       ║
                ║      ██║     ██║   ██║██║   ██║    ██║██║╚██╗██║       ║
                ║      ███████╗╚██████╔╝╚██████╔╝    ██║██║ ╚████║       ║
                ║      ╚══════╝ ╚═════╝  ╚═════╝     ╚═╝╚═╝  ╚═══╝       ║
                ║                                                        ║
                ║              AUTHORIZED PERSONNEL ONLY                 ║
                ║                                                        ║
                ╚════════════════════════════════════════════════════════╝""" + RESET);
        System.out.print(YELLOW + "Username:  " + RESET);
        String username = input.nextLine();
        System.out.print(YELLOW + "Password: " + RESET);
        String password = input.nextLine();

        for (Player p : players) {
            if (p.getUsername().equals(username) && p.getPassword().equals(password)) {
                currentPlayer = p;
                System.out
                        .println(GREEN + "Welcome back, " + username + "\nCurrent High Score: " + p.getScore() + RESET);
                return true;
            }
        }
        System.out.println(RED + "User not found!" + RESET);
        return false;
    }

    public static void signUp() {
        System.out.println(BOLD_CYAN + """
                ╔════════════════════════════════════════════════════════════════╗
                ║                                                                ║
                ║      ███████╗██╗ ██████╗ ███╗   ██╗    ██╗   ██╗██████╗        ║
                ║      ██╔════╝██║██╔════╝ ████╗  ██║    ██║   ██║██╔══██╗       ║
                ║      ███████╗██║██║  ███╗██╔██╗ ██║    ██║   ██║██████╔╝       ║
                ║      ╚════██║██║██║   ██║██║╚██╗██║    ██║   ██║██╔═══╝        ║
                ║      ███████║██║╚██████╔╝██║ ╚████║    ╚██████╔╝██║            ║
                ║      ╚══════╝╚═╝ ╚═════╝ ╚═╝  ╚═══╝     ╚═════╝ ╚═╝            ║
                ║                                                                ║
                ║                  CREATE YOUR ACCOUNT BELOW                     ║
                ║                                                                ║
                ╚════════════════════════════════════════════════════════════════╝""" + RESET);
        System.out.print(YELLOW + "Create Username: " + RESET);
        String username = input.nextLine();
        System.out.print(YELLOW + "Create Password: " + RESET);
        String password = input.nextLine();

        currentPlayer = new Player(username, password, 0);
        players.add(currentPlayer);
        savePlayers();
        System.out.println(GREEN + "Account created successfully!" + RESET);
    }

    public static void loadPlayers() {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(playersFile)) {
            Type listType = new TypeToken<ArrayList<Player>>() {
            }.getType();
            ArrayList<Player> loaded = gson.fromJson(reader, listType);
            if (loaded != null) {
                players = loaded;
            }
            System.out.println(GREEN + "Players successfully loaded" + RESET);
        } catch (IOException e) {
            System.out.println(GREEN + "Creating new player database..." + RESET);
        }
    }

    public static void savePlayers() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(playersFile)) {
            gson.toJson(players, writer);
            System.out.println(GREEN + "Players updated" + RESET);
        } catch (IOException e) {
            System.out.println(RED + "Could not save players: " + e.getMessage() + RESET);
        }
    }

    public static char anotherRegistration() {
        System.out.println(GREEN + "====================================================================");
        System.out.print("Another registration? (Y/N): " + RESET);
        String another = input.nextLine();
        return (another.length() > 0) ? another.charAt(0) : 'n';
    }

    public static int numberOfRegistrations() {
        return registration;
    }
}