import java.io.*;
import java.util.*;

public class HotelReservationApp {
    static class Room {
        int num; String cat; double price; boolean avail = true;
        Room(int n, String c, double p) { num = n; cat = c; price = p; }
        public String toString() { return "Room #" + num + " [" + cat + "] - $" + price + "/night | " + (avail ? "Available" : "Booked"); }
    }
    static List<Room> rooms = new ArrayList<>(List.of(new Room(101,"Standard",80), new Room(102,"Standard",80), new Room(201,"Deluxe",150), new Room(301,"Suite",300)));
    static List<String> bookings = new ArrayList<>();
    static final String FILE = "hotel_data.txt";

    public static void main(String[] args) {
        try {
            File f = new File(FILE);
            if (f.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(f)); String l;
                while ((l = br.readLine()) != null) {
                    String[] p = l.split(",");
                    if (p[0].equals("ROOM")) rooms.stream().filter(r->r.num==Integer.parseInt(p[1])).findFirst().ifPresent(r->r.avail=Boolean.parseBoolean(p[2]));
                    if (p[0].equals("BOOK")) bookings.add(l.substring(5));
                }
                br.close();
            }
        } catch (Exception e) {}

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== HOTEL RESERVATION SYSTEM ===\n1. Search Rooms\n2. Book Room\n3. Cancel Booking\n4. View Bookings\n5. Exit");
            System.out.print("Choose option: "); String opt = sc.nextLine();
            if (opt.equals("1")) rooms.forEach(System.out::println);
            else if (opt.equals("2")) {
                System.out.print("Enter Room Number: "); int rNum = Integer.parseInt(sc.nextLine());
                Room r = rooms.stream().filter(rm -> rm.num == rNum && rm.avail).findFirst().orElse(null);
                if (r == null) { System.out.println("Room unavailable."); continue; }
                System.out.print("Enter Guest Name: "); String name = sc.nextLine();
                System.out.print("Enter Nights: "); int nights = Integer.parseInt(sc.nextLine());
                System.out.println("Cost: $" + (r.price * nights) + "\nEnter 4-digit PIN to pay: "); sc.nextLine();
                r.avail = false;
                String bId = "BKID" + (1000 + new Random().nextInt(9000));
                String bDetails = bId + " | Guest: " + name + " | Room: " + rNum + " | Paid: $" + (r.price * nights);
                bookings.add(bDetails); System.out.println("Success! " + bDetails);
            } else if (opt.equals("3")) {
                System.out.print("Enter Booking ID: "); String id = sc.nextLine().trim();
                String t = bookings.stream().filter(b -> b.startsWith(id)).findFirst().orElse(null);
                if (t == null) { System.out.println("Not found."); continue; }
                int rNum = Integer.parseInt(t.split("Room: ")[1].split(" \\|")[0].trim());
                rooms.stream().filter(rm -> rm.num == rNum).findFirst().ifPresent(rm -> rm.avail = true);
                bookings.remove(t); System.out.println("Cancelled successfully.");
            } else if (opt.equals("4")) {
                if (bookings.isEmpty()) System.out.println("No records."); else bookings.forEach(System.out::println);
            } else if (opt.equals("5")) {
                try {
                    PrintWriter out = new PrintWriter(new FileWriter(FILE));
                    for (Room r : rooms) out.println("ROOM," + r.num + "," + r.avail);
                    for (String b : bookings) out.println("BOOK," + b);
                    out.close();
                } catch (Exception e) {}
                System.out.println("Goodbye!"); break;
            }
        }
        sc.close();
    }
}


