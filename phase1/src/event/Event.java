package event;

import user.User;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores properties of events, with default 1 hour as length of the event. getAttendees and toString
 * @author Dechen Han, Shaohong Chen.
 * @version 1.0.3
 *
 */
//TODO: REMEMBER TO CHANGE THE AUTHOR AND REMOVE THE TODOS.
public class Event {

    private int hour;
    private int min;
    private int length = 1;
    private int id;
    private static int eventNumber = 0;
    private List<User> user_list;
    private User speaker;

    /**
     * Initiates the Meeting, with its time and a default length of 1 hour.
     * @param time: The time the meeting begins.
     */
    public Event(String time){
        //TODO: Constructor, The input of time is an input made by users in string.
        //The input time needs to be in the form of 05:00, which means with length 5.
        convertTime(time);
        this.id = eventNumber; //To be used in other useCases and entities.
        eventNumber += 1;
    }

    public ArrayList<String> getAttendees(){
        //TODO: We need an arraylist of all attendees of this event, in usernames.
        ArrayList<String> name_list = new ArrayList<>();
        for (User u : this.user_list){
            name_list.add(u.getName());
        }
        return name_list;
    }

    public User getSpeaker(){ return speaker; }
        //TODO: return speaker of the event, in usernames.


    public String toString(){
        //TODO: return the string of the event
        return "Event{" + "Time:" + hour + ":" + min +
                ", Attendees:" + this.getAttendees() + "}";
    }

    private void convertTime(String str){
        this.hour = Integer.parseInt(str.substring(0, 2));
        this.min = Integer.parseInt(str.substring(3, 5));
    }

    /**
     * Get the id of the event.
     * @return the id of event in int.
     */
    public int getId(){
        return this.id;
    }

    /**
     * Get the hour of the event time, in 24 hours, in int.
     * @return the hour part of the time, in int.
     */
    public String getTime(){
        return "The meeting will begin on:"+this.hour+":"+this.min+".";
    }

    /**
     * Get the length of the Event.
     * @return length of the Event lasts this hour(s), in int.
     */
    public int getMeetingLength(){
        return length;
    }

    /**
     * @param n: The wanted length of the Event.
     */
    public void setMeetingLength(int n){
        this.length = n;
    }

    /**
     * Check if the time is in proper working time so that attendees can attend.
     * @return a boolean of whether the time slot is in working time.
     */
    public boolean inOfficeHour(){
        if(this.hour>=9){
            if(this.hour==16 && this.min == 0){
                return true;
            }else{
                return this.hour < 16;
            }
        }
        return false;
    }

    /**
     * Check if the event contradicts the other event in time.
     * @param t: The other event.
     * @return A boolean showing if the two events contradicts. true for contradict.
     */
    public boolean contradicts(Event t){
        ArrayList<Integer> endTime = new ArrayList<>();
        endTime.add(this.hour+length);
        endTime.add(this.min);
        ArrayList<Integer> t_endTime = new ArrayList<>();
        t_endTime.add(t.getHour()+t.getLength());
        t_endTime.add(t.getMin());
        if(this.hour == t_endTime.get(0)) {
            return !(this.min >= t_endTime.get(1));
        }else if(endTime.get(0) == t.getHour()){
            return !(endTime.get(1) < t.getMin());
        }else return t_endTime.get(0) >= this.hour && t.getHour() <= endTime.get(0);
    }

    private int getHour(){
        return this.hour;
    }

    private int getMin(){
        return this.min;
    }

    private int getLength(){
        return this.length;
    }

}
