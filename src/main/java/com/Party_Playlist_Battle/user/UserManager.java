package com.Party_Playlist_Battle.user;

import com.Party_Playlist_Battle.server.DatabaseHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

public class UserManager {
    StatsManager statsManager=new StatsManager();
    List<User> users=new ArrayList<>();
    Queue<User> battleQueue=new LinkedList();
    private ReentrantLock mutex=new ReentrantLock();

    public int addUser(User user) {
        mutex.lock();
        if (users.contains(user)){
            mutex.unlock();
            return users.indexOf(user);
        }
        users.add(user);
        int temp=users.indexOf(user);
        mutex.unlock();
        return temp;
    }

    public void removeUser(int position){
        users.remove(position);
    }

    public User at(int index){
        return users.get(index);
    }

    public User at(String username){
        for (int i=0;i < users.size(); i++){
            mutex.lock();
            if(users.get(i).getUsername().compareTo(username)==0){
                User temp=users.get(i);
                mutex.unlock();
                return temp;
            }
            mutex.unlock();
        }
        return null;
    }

    public void queueUp(User user){
        mutex.lock();
        if (battleQueue.contains(user)){
            mutex.unlock();
            return;
        }
        battleQueue.add(user);
        mutex.unlock();
    }

    public String startBattle(BattleLogic battleLogic,DatabaseHandler dbHandler) throws SQLException {
        User temp1;
        User temp2;
        mutex.lock();
        temp1=battleQueue.poll();
        temp2=battleQueue.poll();
        mutex.unlock();
        if (temp2==null){
            queueUp(temp1);
        }
        int result=battleLogic.battleUsers(temp1,temp2);
        if (result==1){
            statsManager.updateUserStats(temp1.getUserID(dbHandler),temp2.getUserID(dbHandler),dbHandler);
            statsManager.insertBattleLog(temp1.getUserID(dbHandler),temp2.getUserID(dbHandler),dbHandler);
            return temp1.getUsername();
        }
        else if (result==2){
            statsManager.updateUserStats(temp2.getUserID(dbHandler),temp1.getUserID(dbHandler),dbHandler);
            statsManager.insertBattleLog(temp2.getUserID(dbHandler),temp1.getUserID(dbHandler),dbHandler);
            return temp2.getUsername();
        }
        else if (result==4) {
            return "error";
        }
        else {
            return "Draw";
        }
    }

  /*  public void startBattle(DatabaseHandler dbHandler) throws SQLException {
        mutex.lock();
        User user1=battleQueue.poll();
        User user2=battleQueue.poll();
        mutex.unlock();
        if(user1==null || user2==null){
            System.out.println("Waiting for other Users to queue up");
            mutex.lock();
            if(user2==null) {
                battleQueue.add(user1);
            }
            else {
                battleQueue.add(user2);
            }
            mutex.unlock();
            return;
        }
        else {
            System.out.println("Starting Battle between: "+user1.getUsername()+" and "+user2.getUsername());
            user1.battle(user2,dbHandler);
        }
    }*/


}
