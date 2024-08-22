import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class FileWriterTask {

    private static final String FILE_NAME = "output.txt";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
                // Simulate time by incrementing 10 seconds
                String commitDate = DATE_FORMAT.format(calendar.getTime());
                writeToFile(commitDate);
                commitChanges(commitDate);

                // Increment time by 10 seconds
                calendar.add(Calendar.SECOND, 10);

                // Check if the simulated date has reached or exceeded the current date
                if (calendar.getTime().compareTo(new Date()) >= 0) {
                    System.out.println("Reached current date, stopping the program.");
                    break;
                }

                // Sleep for a short time to avoid overloading the CPU (for simulation purposes)
                TimeUnit.MILLISECONDS.sleep(500);

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