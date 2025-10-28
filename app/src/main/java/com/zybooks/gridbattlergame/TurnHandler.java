package com.zybooks.gridbattlergame;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class TurnHandler {
    public int currentTarget = -1;
    public Characters[] target;
    public Characters[] ally = {new Characters(), new Characters(), new Characters()};
    public Characters[] opponent = {};
    public Characters[] attacker = ally;

    private void manageAttack(index){
        if (attacker == ally){
            if (currentTarget == -1 && getContent(index).contains("character")){
                Log.d("TAG", "manageMovement: Start Move");
                startMovement(index);
                if (target == opponent);
                }
            else if (target == ally) {

            }

        }
        else {
            if (target == opponent){

            }
            else if (target == ally) {
                attacker = opponent;
            }
        }
    }
}
}
}
