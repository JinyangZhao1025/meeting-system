package readWrite;

import event.EventManager;
import javafx.util.Pair;
import message.MessageManager;
import user.UserManager;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Write to file.
 */
public class Write {
    private final UserManager usermanager;
    private final EventManager eventmanager;
    private final MessageManager messagemanager;
    private final Connection conn;
    private Statement stmt;

    /**
     * Constructor of Write.
     *
     * @param userManager    an UserManager.
     * @param eventManager   an EventManager.
     * @param messageManager an MessageManager.
     */
    public Write(UserManager userManager, EventManager eventManager, MessageManager messageManager) {
        this.usermanager = userManager;
        this.eventmanager = eventManager;
        this.messagemanager = messageManager;
        Connecting cct = new Connecting();
        this.conn = cct.run();
        try{this.stmt = conn.createStatement();}
        catch (SQLException ignored){}
    }

    /**
     * Write to save files.
     */
    public void run() {
        try {
            remover();
            userWriter();
            roomWriter();
            eventWriter();
            messageWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void remover() {
        try {
            stmt.execute("DELETE FROM users");
            stmt.execute("DELETE FROM messageList");
            stmt.execute("DELETE FROM event");
            stmt.execute("DELETE FROM message");
            stmt.execute("DELETE FROM request");
            stmt.execute("DELETE FROM room");
            stmt.execute("DELETE FROM signedUp");
        } catch (SQLException e) {
            //
        }
    }

    private void userWriter() {
        String password;
        ArrayList<String> messageList;
        String type;

        Collection<String> usernames = usermanager.getAllUsernames();

        String sql = "INSERT INTO users(Username,Password,UserType) VALUES(?,?,?)";
        String sql2 = "INSERT INTO messageList(Username,CanSendMessageTo) VALUES(?,?)";

        for (String username : usernames) {
            password = usermanager.getPassword(username);
            type = usermanager.getUserType(username);
            messageList = usermanager.getContactList(username);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.setString(3, type);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            for (String username2 : messageList){
                try (PreparedStatement pstmt2 = conn.prepareStatement(sql2)){
                    pstmt2.setString(1,username);
                    pstmt2.setString(2,username2);
                    pstmt2.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void roomWriter(){
        HashMap<Integer, Integer> roomToCapacity = eventmanager.getRoomNumberMapToCapacity();
        String sql = "INSERT INTO room(RoomNumber,Capacity) VALUES(?,?)";
        for (Map.Entry<Integer, Integer> item : roomToCapacity.entrySet()) {
            Integer room = item.getKey();
            Integer capacity = item.getValue();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, room);
                pstmt.setInt(2, capacity);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void eventWriter() {
        HashMap<Integer, Integer> eventToRoom = eventmanager.getEventIDMapToRoomNumber();
        String time;
        String duration;
        String conferenceName;
        String vipStatus;
        ArrayList<String> attendees;
        ArrayList<String> speakers;
        String description;
        String sql = "INSERT INTO event(EventId,RoomNumber,MaxNumberOfSpeakers,MaxNumberOfAttendees,StartTime,Duration,Description,ConferenceName,VIPS) VALUES(?,?,?,?,?,?,?,?,?)";
        String sql2 = "INSERT INTO signedUp(EventId, UserName) VALUES (?,?)";
        int i = 0;
        for (Map.Entry<Integer, Integer> item : eventToRoom.entrySet()) {
            Integer event = item.getKey();
            Pair<Integer, Integer> capacity;
            Integer room2 = item.getValue();
            time = eventmanager.getTime(event);
            duration = eventmanager.getDuration(event);
            attendees = eventmanager.getAttendees(String.valueOf(event));
            speakers = eventmanager.getSpeakers(event);
            description = eventmanager.getDescription(event);
            capacity = eventmanager.getCapacity(event);
            conferenceName = eventmanager.getConferenceOfEvent(event);
            vipStatus = String.valueOf(eventmanager.getVipStatus(event));
            int numSpeakers = capacity.getKey();
            int numAttendees = capacity.getValue();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, i);
                pstmt.setInt(2, room2);
                pstmt.setInt(3, numSpeakers);
                pstmt.setInt(4, numAttendees);
                pstmt.setString(5, time);
                pstmt.setInt(6, Integer.parseInt(duration));
                pstmt.setString(7, description);
                pstmt.setString(8, conferenceName);
                pstmt.setString(9, vipStatus);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            for (String attendee : attendees) {
                try (PreparedStatement pstmt2 = conn.prepareStatement(sql2)) {
                    pstmt2.setInt(1, i);
                    pstmt2.setString(2, attendee);
                    pstmt2.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            for (String speaker : speakers) {
                try (PreparedStatement pstmt3 = conn.prepareStatement(sql2)) {
                    pstmt3.setInt(1, i);
                    pstmt3.setString(2, speaker);
                    pstmt3.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            i += 1;
        }
    }


    private void messageWriter() throws IOException {
        String sql = "INSERT INTO message(Sender,Receiver,Status,MessageText) VALUES(?,?,?,?)";
        ArrayList<ArrayList<String>> allMessage = messagemanager.getAllMessage();
        for (ArrayList<String> messageInfo : allMessage) {
            try (PreparedStatement pstmt2 = conn.prepareStatement(sql)) {
                pstmt2.setString(1, messageInfo.get(0));
                pstmt2.setString(2, messageInfo.get(1));
                pstmt2.setString(3, "TODO");//TODO: STATUS
                pstmt2.setString(4, messageInfo.get(2));
                pstmt2.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
