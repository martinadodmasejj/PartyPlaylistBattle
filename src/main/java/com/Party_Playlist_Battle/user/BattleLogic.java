package com.Party_Playlist_Battle.user;

import java.util.Iterator;

public class BattleLogic {
    public int battleUsers(User user1,User user2){
        if (user1==null || user2==null){
            System.out.println("only 1 user in battle queue ");
            return 4;
        }
        System.out.println("User "+user1.getUsername()+" aka user1 actions:");
        user1.printActions();
        System.out.println("User "+user2.getUsername()+" aka user2 actions:");
        user2.printActions();
        char temp1,temp2;
        int conclusion;
        int roundCounter1=0;
        int roundCounter2=0;
        for (int i=0;i<5;i++){
            temp1=user1.actions.get(i);
            temp2=user2.actions.get(i);
            conclusion=rpc_battle(temp1,temp2);
            if (conclusion==1){
                 roundCounter1++;
            }
            else if (conclusion == 2){
                roundCounter2++;
            }
            else if (conclusion == 4){
                System.out.println("Invalid Action");
                break;
            }
        }
        if (roundCounter1 > roundCounter2){
            return 1;
        }
        else if (roundCounter1 < roundCounter2){
            return 2;
        }
        else {
            return 3;
        }
    }

    private int rpc_battle(char temp1,char temp2){
        //1 user 1 won , 2 user 2 won , 3 draw , 4 error
        if (temp1=='R' && temp2=='S'){
            System.out.println("user1 : rock VS user2 : scissors");
            System.out.println("user1 wins round");
            return 1;
        }
        else if(temp1=='R' && temp2=='P'){
            System.out.println("user1 : rock VS user2 : paper");
            System.out.println("user2 wins round");
            return 2;
        }
        else if(temp1=='R' && temp2=='R'){
            System.out.println("user1 : rock VS user2 : rock");
            System.out.println("draw");
            return 3;
        }
        else if(temp1=='P' && temp2=='P'){
            System.out.println("user1 : paper VS user2 : paper");
            System.out.println("draw");
            return 3;
        }
        else if(temp1=='P' && temp2=='R'){
            System.out.println("user1 : paper VS user2 : rock");
            System.out.println("user1 wins round");
            return 1;
        }
        else if(temp1=='P' && temp2=='S'){
            System.out.println("user1 : paper VS user2 : scissors");
            System.out.println("user2 wins round");
            return 2;
        }
        else if(temp1=='S' && temp2=='S'){
            System.out.println("user1 : scissors VS user2 : scissors");
            System.out.println("draw");
            return 3;
        }
        else if(temp1=='S' && temp2=='R'){
            System.out.println("user1 : scissors VS user2 : rock");
            System.out.println("user2 wins round");
            return 2;
        }
        else if(temp1=='S' && temp2=='P'){
            System.out.println("user1 : scissors VS user2 : paper");
            System.out.println("user1 wins round");
            return 1;
        }
        System.out.println("Undefined Actions!");
        return 4;
    }
}
