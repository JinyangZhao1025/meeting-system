package readWrite;

import event.EventManager;
import javafx.util.Pair;
import message.MessageManager;
import user.UserManager;

import java.io.FileWriter;
import java.io.IOException;
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

    }

    /**
     * Write to save files.
     */
    public void run() {
        try {
            userWriter();
            roomWriter();
            eventWriter();
            messageWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void userWriter() throws IOException {
        String password;
        ArrayList<String> messageList;
        String type;
        FileWriter userWriter = new FileWriter("src/resources/user.csv", false);
        Collection<String> usernames = usermanager.getAllUsernames();
        int i = 0;
        for (String username : usernames) {
            password = usermanager.getPassword(username);
            type = usermanager.getUserType(username);
            messageList = usermanager.getContactList(username);
            threeStringOneArrayListFileWriterHelper(messageList, username, password, type, userWriter);
            i += 1;
            if (!(i == usernames.size())) {
                userWriter.append("\n");
            }
        }
        userWriter.flush();
        userWriter.close();
    }

    private void roomWriter() throws IOException{
        FileWriter roomWriter = new FileWriter("src/resources/room.csv", false);
        HashMap<Integer, Integer> roomToCapacity = eventmanager.getRoomNumberMapToCapacity();
        int i = 0;
        for (Map.Entry<Integer, Integer> item : roomToCapacity.entrySet()) {
            Integer room = item.getKey();
            Integer capacity = item.getValue();
            roomWriter.append(String.valueOf(room));
            roomWriter.append(",");
            roomWriter.append(String.valueOf(capacity));
            i += 1;
            if (!(i == roomToCapacity.entrySet().size())) {
                roomWriter.append("\n");
            }
        }
        roomWriter.flush();
        roomWriter.close();
    }

    private void eventWriter() throws IOException{
        FileWriter eventWriter = new FileWriter("src/resources/event.csv", false);
        HashMap<Integer, Integer> eventToRoom = eventmanager.getEventIDMapToRoomNumber();
        String time;
        String duration;
        ArrayList<String> attendees;
        ArrayList<String> speakers;
        String description;
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
            int numSpeakers = capacity.getKey();
            int numAttendees = capacity.getValue();
            eventWriter.append(String.valueOf(room2));
            eventWriter.append(",");
            eventWriter.append(String.valueOf(numSpeakers));
            eventWriter.append(",");
            eventWriter.append(String.valueOf(numAttendees));
            eventWriter.append(",");
            threeStringOneArrayListFileWriterHelper(attendees, time, duration, description, eventWriter);
            if (!(speakers.size() == 0)) {
                for (String speaker:speakers) {
                    eventWriter.append(",");
                    eventWriter.append(speaker);
                }
            }
            i += 1;
            if (!(i == eventToRoom.entrySet().size())) {
                eventWriter.append("\n");
            }
        }
        eventWriter.flush();
        eventWriter.close();
    }

    private void messageWriter() throws IOException {
        int i = 0;
        FileWriter messageWriter = new FileWriter("src/resources/message.csv", false);
        ArrayList<ArrayList<String>> allMessage = messagemanager.getAllMessage();
        for (ArrayList<String> messageInfo : allMessage) {
            messageWriter.append(messageInfo.get(0));
            messageWriter.append(",");
            messageWriter.append(messageInfo.get(1));
            messageWriter.append(",");
            messageWriter.append(messageInfo.get(2));
            i += 1;
            if (!(i == allMessage.size())) {
                messageWriter.append("\n");
            }
        }
        messageWriter.flush();
        messageWriter.close();
    }

    /*
     * A helper method that append 3 strings and 1 arraylist to FileWriter, with comma separations.
     *
     * @param stringArrayList An ArrayList that goes last.
     * @param string1         A String that goes first.
     * @param string2         A String that goes second.
     * @param string3         A String that goes third.
     * @param writer          The FileWriter.
     * @throws IOException should not happen.
     */
    private void threeStringOneArrayListFileWriterHelper(ArrayList<String> stringArrayList, String string1,
                                                         String string2, String string3,
                                                         FileWriter writer) throws IOException {
        writer.append(string1);
        writer.append(",");
        writer.append(string2);
        writer.append(",");
        writer.append(string3);
        for (String contactListedUser : stringArrayList) {
            writer.append(",");
            writer.append(contactListedUser);
        }
    }
}
