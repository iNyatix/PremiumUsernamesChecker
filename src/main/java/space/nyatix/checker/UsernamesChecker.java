package space.nyatix.checker;

import lombok.SneakyThrows;
import space.nyatix.checker.premium.PremiumRequest;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Nyatix
 * @since 26.08.2022 - 20:08
 **/
public class UsernamesChecker {

    private final PremiumRequest premiumRequest = new PremiumRequest(Executors.newFixedThreadPool(15));

    @SneakyThrows
    public void run() {
        var checked = new AtomicInteger();
        var max = 0;

        var checkFile = new File("check.txt");

        if (checkFile.createNewFile()) {
            System.out.println("Created check.txt file, add usernames into a file!");
            System.exit(0);
        }

        String line;

        var playersFile = new File("players.txt");

        if (playersFile.createNewFile()) {
            System.out.println("Created players.txt file!");
        }

        try (var reader = new BufferedReader(new FileReader(checkFile))) {
            while (reader.readLine() != null) {
                max += 1;
            }
        }

        try (var reader = new BufferedReader(new FileReader(checkFile))) {
            while ((line = reader.readLine()) != null) {
                var username = line;
                int finalMax = max;
                premiumRequest.sendRequest(username, hasPaid -> {
                    var progress = (checked.getAndIncrement() * 100) / finalMax;
                    if (hasPaid) {
                        try (var bufferedWriter = new BufferedWriter(new FileWriter(playersFile, true))) {
                            System.out.print("\rChecked " + progress + "% usernames!");
                            bufferedWriter.append(username).append("\n");
                            bufferedWriter.flush();
                        } catch (IOException ignored) {}
                    }

                    System.out.print("\rChecked " + (progress * 100) / finalMax + "% usernames!");
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}