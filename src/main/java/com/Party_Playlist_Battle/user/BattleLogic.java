package com.Party_Playlist_Battle.user;

public class BattleLogic {
    public int battleUsers(User user1,User user2){
        char temp1,temp2;
        int conclusion;
        int roundCounter1=0;
        int roundCounter2=0;
        for (int i=0;i<5;i++){
            temp1=user1.actions.pop();
            temp2=user2.actions.pop();
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
            return 1;
        }
        else if(temp1=='R' && temp2=='P'){
            return 2;
        }
        else if(temp1=='R' && temp2=='R'){
            return 3;
        }
        else if(temp1=='P' && temp2=='P'){
            return 3;
        }
        else if(temp1=='P' && temp2=='R'){
            return 1;
        }
        else if(temp1=='P' && temp2=='S'){
            return 2;
        }
        else if(temp1=='S' && temp2=='S'){
            return 3;
        }
        else if(temp1=='S' && temp2=='R'){
            return 2;
        }
        else if(temp1=='S' && temp2=='P'){
            return 1;
        }
        return 4;
    }
}
