import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class FileWriterTask {

    private static final String FILE_NAME = "output.txt";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final Random RANDOM = new Random();
    private static Calendar calendar;

    static {
        // Initialize the calendar with the start date (07-Aug-2017)
        calendar = Calendar.getInstance();
        try {
            calendar.setTime(DATE_FORMAT.parse("2017-08-07 00:00:00"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        while (true) {
            try {
                // Randomly decide the number of commits for the current day (between 15 and 50)
                int commitsToday = RANDOM.nextInt(36) + 15; // 15 to 50 commits

                for (int i = 0; i < commitsToday; i++) {
                    // Simulate random time progression within the day
                    int randomMinutes = RANDOM.nextInt(60);
                    calendar.add(Calendar.MINUTE, randomMinutes);
                    String commitDate = DATE_FORMAT.format(calendar.getTime());

                    // Skip the next 9 hours if the time is after 11:00 PM
                    if (calendar.get(Calendar.HOUR_OF_DAY) >= 23) {
                        calendar.add(Calendar.HOUR_OF_DAY, 9);
                        calendar.set(Calendar.MINUTE, 0); // Reset minutes after skipping
                        continue;
                    }

                    // Randomly skip Saturdays or Sundays
                    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                    if ((dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) && RANDOM.nextBoolean()) {
                        System.out.println("Skipping " + (dayOfWeek == Calendar.SATURDAY ? "Saturday" : "Sunday") + ", no commits today.");
                        break; // Skip the rest of the day
                    }

                    // Write to file and commit changes
                    writeToFile(commitDate);
                    commitChanges(commitDate);

                    // Sleep to avoid CPU overloading (for simulation purposes)
                    TimeUnit.MILLISECONDS.sleep(500);

                    // Check if the simulated date has reached or exceeded the current date
                    if (calendar.getTime().compareTo(new Date()) >= 0) {
                        System.out.println("Reached current date, stopping the program.");
                        return;
                    }
                }

                // Move to the next day
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void writeToFile(String commitDate) throws IOException {
        File file = new File(FILE_NAME);
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write("Start Date: 07-Aug-2017\n");
            writer.write("Change at " + commitDate + "\n");
            writer.write("----------------------\n");
            System.out.println("Changes written to file at " + commitDate);
        }
    }

    private static void commitChanges(String commitDate) throws IOException, InterruptedException {
        // Use the simulated date for setting the GIT_COMMITTER_DATE and GIT_AUTHOR_DATE
        String commitMessage = "Automated commit for the session at " + commitDate;
        String gitCommitCommand = String.format(
            "git commit -m \"%s\" --date=\"%s\"", 
            commitMessage, 
            commitDate
        );

        Runtime.getRuntime().exec("git add " + FILE_NAME).waitFor();
        Runtime.getRuntime().exec(new String[]{"bash", "-c", gitCommitCommand}).waitFor();
        System.out.println("Changes committed to Git with date: " + commitDate);
    }
}