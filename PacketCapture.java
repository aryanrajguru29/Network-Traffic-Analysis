import java.io.*;
import java.util.Scanner;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
public class PacketCapture {
    public static void main(String[] args) throws CsvValidationException {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the network interface (e.g., eth0, wlan0): ");
            String networkInterface = scanner.nextLine();

            // Full path to the tshark executable
            String tsharkPath = "C:\\Program Files\\Wireshark\\tshark.exe"; // Path to tshark executable

            // Command to start TShark packet capture with user-input network interface, stop after 20 packets, and export specific fields to a CSV file with custom column names via the '-o' option
            String[] command = {tsharkPath, "-i", networkInterface, "-c", "20", "-T", "fields", "-e", "frame.number", "-e", "frame.time_relative", "-e", "ip.src", "-e", "ip.dst", "-e", "tcp.srcport", "-e", "tcp.dstport", "-e", "_ws.col.Protocol", "-e", "icmp.type", "-e", "frame.len", "-e", "tcp.flags", "-E", "header=y", "-E", "separator=, ", "-E", "occurrence=f"};
            //String[] command = {tsharkPath, "-i", networkInterface, "-o", "gui.format:\"No.\",\"%m\",\"Time\",\"%t\",\"Source\",\"%s\",\"Destination\",\"%d\",\"Protocol\",\"%p\",\"Length\",\"%L\",\"Info\",\"%i\""};


            // Create a ProcessBuilder
            ProcessBuilder processBuilder = new ProcessBuilder(command);

            // Redirect the output to a file
            File outputFile = new File("captured_packets1.csv");
            processBuilder.redirectOutput(outputFile);

            // Merge error stream with normal stream
            processBuilder.redirectErrorStream(true);

            // Start the TShark packet capture process
            Process process = processBuilder.start();

            // Wait for the capture process to finish
            process.waitFor();

            try (CSVReader reader = new CSVReader(new FileReader("captured_packets1.csv"));
            CSVWriter writer = new CSVWriter(new FileWriter("output.csv"))) {

            reader.skip(2); 
            String[] newColumnNames = {"No.","Time","Source IP","Destination IP","Source Port","Destination Port","Protocol","ICMP type","Length","Flags"};
            writer.writeNext(newColumnNames); // Write the new column names

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {

                writer.writeNext(nextLine);
            }
            int exitValue = process.exitValue();
            System.out.println("TShark process exited with value: " + exitValue);
            scanner.close();
        } 
    }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    
}
}
