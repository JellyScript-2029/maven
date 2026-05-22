package com.maven;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Stack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.maven.model.Player;

public class CoreQuizzer {

    // Quizzer
    public static Player currentPlayer = null;
    public static ArrayList<Player> players = new ArrayList<>();
    public static Stack<Integer> history = new Stack<>();
    public static ArrayList<Integer> pool = new ArrayList<>();
    public static ArrayList<Integer> answeredQuestions = new ArrayList<>();
    public static int score = 0;
    public static int qCount = 1;
    public static int currentIndex = -1;
    public static final String playersFile = System.getProperty("user.home") + java.io.File.separator + "Players.json";

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
        "Ito ang pinakamaliit na isda sa buong mundo na matatagpuan sa Pilipinas.",
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
        "Anong relihiyon ang may pinakamalaking bilang ng tagasunod sa Pilipinas?",
        "Ano ang tawag sa tradisyunal na kasuotan ng mga lalaking Pilipino?"
    };

    public static String[][] choices = {
        {"Dr. Jose Rizal", "Emilio Aguinaldo", "Lapu-Lapu", "Andres Bonifacio"},
        {"Maynila", "Cebu", "Davao", "Quezon City"},
        {"Maya", "Kalapati", "Agila ng Pilipinas", "Papagayo"},
        {"Dalawa", "Tatlo", "Apat", "Lima"},
        {"Mangga", "Saging", "Papaya", "Lansones"},
        {"Rodrigo Duterte", "Ferdinand Marcos Jr.", "Leni Robredo", "Sara Duterte"},
        {"Bahay Kubo", "Bahay na Bato", "Kubo", "Nipa Hut"},
        {"Mangosteen", "Durian", "Rambutan", "Lanzones"},
        {"Rosas", "Waling-Waling", "Sampaguita", "Ilang-Ilang"},
        {"Bangus", "Pandaca Pygmaea / Tabios", "Tilapia", "Dilis"},
        {"Manuel L. Quezon", "Jose Rizal", "Andres Bonifacio", "Emilio Aguinaldo"},
        {"Dollar", "Peso", "Piso", "Centavo"},
        {"Bulkang Mayon", "Bulkang Taal", "Bulkang Pinatubo", "Bulkang Bulusan"},
        {"Sipa", "Palo Sebo", "Arnis", "Patintero"},
        {"Apat", "Isa", "Dalawa", "Tatlo"},
        {"Bundok Apo", "Bundok Pulag", "Bundok Halcon", "Bundok Kanlaon"},
        {"Jeepney", "Tricycle", "Bus", "Kalesa"},
        {"Gabriela Silang", "Melchora Aquino", "Teresa Magbanua", "Tandang Sora"},
        {"Tagaytay", "Vigan", "Baguio City", "Batangas"},
        {"Tsinelas", "Sapatilya", "Alpombra", "Bakya"},
        {"Bayan Ko", "Pilipinas Kong Mahal", "Lupang Hinirang", "Sariling Atin"},
        {"Bohol", "Cebu", "Palawan", "Batanes"},
        {"Juan Sebastian Elcano", "Lapu-Lapu", "Ferdinand Magellan", "Andres Bonifacio"},
        {"Tamaraw", "Kalabaw", "Carabao", "Pawikan"},
        {"7,107", "7,000", "8,000", "7,641"},
        {"OFW", "Migrant Worker", "Seaman", "TNT"},
        {"Lechon", "Sisig", "Crispy Pata", "Chicharon"},
        {"Manny Pacquiao", "Nonito Donaire", "Mark Magsayo", "Gerry Penalosa"},
        {"Islam", "Protestantismo", "Katoliko", "Budismo"},
        {"Polo Barong", "Camisa de Chino", "Baro't Saya", "Barong Tagalog"}
    };

    public static String[] answer = {
        "Dr. Jose Rizal", "Maynila", "Agila ng Pilipinas", "Tatlo", "Mangga",
        "Ferdinand Marcos Jr.", "Bahay Kubo", "Durian", "Sampaguita", "Pandaca Pygmaea / Tabios",
        "Manuel L. Quezon", "Piso", "Bulkang Mayon", "Arnis", "Tatlo", "Bundok Apo", "Jeepney",
        "Melchora Aquino", "Baguio City", "Bakya", "Lupang Hinirang", "Bohol", "Ferdinand Magellan", "Carabao",
        "7,641", "OFW", "Chicharon", "Manny Pacquiao", "Katoliko", "Barong Tagalog"
    };

    // --- MODULE 4: QUIZZER ---
    public static void quizzerHome() throws InterruptedException {
        System.out.println(Enterprise.BOLD_CYAN + """
┌─────────────────────────────────────────────────────────────────┐
│                                                                 │
│               Welcome to Core-Quizzer Trivia Game               │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘""" + Enterprise.RESET);
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
        try {
            choice = Enterprise.input.nextInt();
        } catch (InputMismatchException e) {
            Enterprise.input.nextLine();
        }
        Enterprise.input.nextLine();

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
                Enterprise.mainMenu();
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
        try {
            choice = Enterprise.input.nextInt();
        } catch (InputMismatchException e) {
            Enterprise.input.nextLine();
        }
        Enterprise.input.nextLine();

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
                Enterprise.mainMenu();
            default -> {
                System.out.println("Invalid choice.");
                quizGameMenu();
            }
        }
    }

    public static void playQuiz() throws InterruptedException {
        while (true) {
            boolean alreadyAnswered = answeredQuestions.contains(currentIndex);

            System.out.println("\n==================================================================");
            System.out.println(Enterprise.YELLOW + "Current Score: " + score + Enterprise.RESET);
            System.out.println(Enterprise.PURPLE + "Question #" + qCount);
            System.out.println(questions[currentIndex]);
            System.out.println("A) " + choices[currentIndex][0]);
            System.out.println("B) " + choices[currentIndex][1]);
            System.out.println("C) " + choices[currentIndex][2]);
            System.out.println("D) " + choices[currentIndex][3] + Enterprise.RESET);
            if (alreadyAnswered) {
                System.out.println(Enterprise.GREEN + "ALREADY ANSWERED");
            }
            System.out.println(Enterprise.BOLD_CYAN + "==================================================================");
            System.out.println("  [1] Next        [2] Back          [3] Answer         [0] Exit   ");
            System.out.println("==================================================================" + Enterprise.RESET);
            System.out.print("  Choice: ");
            String action = Enterprise.input.nextLine();

            switch (action) {
                case "1" -> {
                    if (qCount >= 30) {
                        if (currentPlayer != null && score > currentPlayer.getScore()) {
                            currentPlayer.setScore(score);
                        }
                        savePlayers();
                        Enterprise.mainMenu();
                        return;
                    } else {
                        qCount++;
                        if (qCount <= history.size()) {
                            currentIndex = history.get(qCount - 1);
                        } else {
                            pickRandomQuestion();
                        }
                    }
                }
                case "2" -> {
                    if (history.size() > 1) {
                        history.pop();
                        currentIndex = history.peek();
                        qCount--;
                    } else {
                        System.out.println(Enterprise.RED + "No previous questions.");
                    }
                }
                case "3" -> {
                    if (alreadyAnswered) {
                        System.out.println(Enterprise.RED + "You already answered this question!");
                        continue;
                    }

                    System.out.print("Answer: ");
                    String letter = Enterprise.input.nextLine().trim().toUpperCase();
                    String userAnswer = switch (letter) {
                        case "A" ->
                            choices[currentIndex][0];
                        case "B" ->
                            choices[currentIndex][1];
                        case "C" ->
                            choices[currentIndex][2];
                        case "D" ->
                            choices[currentIndex][3];
                        default ->
                            null;
                    };

                    if (userAnswer == null) {
                        System.out.println(Enterprise.RED + "Invalid letter.");
                        continue;
                    }

                    answeredQuestions.add(currentIndex);

                    if (userAnswer.equalsIgnoreCase(answer[currentIndex].trim())) {
                        System.out.println(Enterprise.BOLD_GREEN + "CORRECT!" + Enterprise.RESET);
                        score++;
                    } else {
                        System.out.println(Enterprise.BOLD_RED + "INCORRECT! The correct answer is: " + answer[currentIndex].toUpperCase() + Enterprise.RESET);
                    }

                    if (qCount >= 30) {
                        System.out.println(Enterprise.BOLD_YELLOW + "\n============================================");
                        System.out.println("         QUIZ COMPLETE!");
                        System.out.println("Final Score: " + score + "/30");
                        System.out.println("============================================" + Enterprise.RESET);
                        if (currentPlayer != null && score > currentPlayer.getScore()) {
                            currentPlayer.setScore(score);
                        }
                        savePlayers();
                        Enterprise.mainMenu();
                        return;
                    } else {
                        qCount++;
                        if (qCount <= history.size()) {
                            currentIndex = history.get(qCount - 1);
                        } else {
                            pickRandomQuestion();
                        }
                    }
                }
                case "0" -> {
                    System.out.println(Enterprise.YELLOW + "Total score: " + score + Enterprise.RESET);
                    if (currentPlayer != null && score > currentPlayer.getScore()) {
                        currentPlayer.setScore(score);
                    }
                    savePlayers();
                    Enterprise.mainMenu();
                    return;
                }
                default ->
                    System.out.println(Enterprise.RED + "Invalid choice.");
            }
        }
    }

    // --- HELPER METHODS ---
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
        System.out.println(Enterprise.BOLD_CYAN + """
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
╚════════════════════════════════════════════════════════╝""" + Enterprise.RESET);
        System.out.print(Enterprise.YELLOW + "Username:  " + Enterprise.RESET);
        String username = Enterprise.input.nextLine();
        System.out.print(Enterprise.YELLOW + "Password: " + Enterprise.RESET);
        String password = Enterprise.input.nextLine();

        for (Player p : players) {
            if (p.getUsername().equals(username) && p.getPassword().equals(password)) {
                currentPlayer = p;
                System.out.println(Enterprise.GREEN + "Welcome back, " + username + "\nCurrent High Score: " + p.getScore() + Enterprise.RESET);
                return true;
            }
        }
        System.out.println(Enterprise.RED + "User not found!");
        return false;
    }

    public static void signUp() {
        System.out.println(Enterprise.BOLD_CYAN + """
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
╚════════════════════════════════════════════════════════════════╝""" + Enterprise.RESET);
        System.out.print(Enterprise.YELLOW + "Create Username: " + Enterprise.RESET);
        String username = Enterprise.input.nextLine();
        System.out.print(Enterprise.YELLOW + "Create Password: " + Enterprise.RESET);
        String password = Enterprise.input.nextLine();

        currentPlayer = new Player(username, password, 0);
        players.add(currentPlayer);
        savePlayers();
        System.out.println(Enterprise.GREEN + "Account created successfully!" + Enterprise.RESET);
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
            System.out.println(Enterprise.GREEN + "Players successfully loaded");
        } catch (IOException e) {
            System.out.println(Enterprise.GREEN + "Creating new player database...");
        }
    }

    public static void savePlayers() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(playersFile)) {
            gson.toJson(players, writer);
            System.out.println(Enterprise.GREEN + "Players updated" + Enterprise.RESET);
        } catch (IOException e) {
            System.out.println(Enterprise.RED + "Could not save players: " + e.getMessage() + Enterprise.RESET);
        }
    }
}
