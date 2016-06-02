/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.wsService;

import java.util.ArrayList;
import java.util.List;
import models.HumanPlayer;
import models.Player;
import ws.monopoly.Event;
import ws.monopoly.PlayerDetails;
import ws.monopoly.PlayerStatus;
import ws.monopoly.PlayerType;

/**
 *
 * @author Liraz
 */
public class UtilitiesWS {
//     public static ws.monopoly.PlayerDetails getPlayerDetails(Player currPlayer)
//    {
//        PlayerDetails playerDetails = new PlayerDetails();
//        
//        playerDetails.setName(currPlayer.getName());
//        playerDetails.setNumberOfTiles(currPlayer.getPack().size());
//        playerDetails.setPlayedFirstSequence(!currPlayer.isFirstMove());
//        setPlayerType(playerDetails, currPlayer.isComputer());
//        setPlayerStatuse(playerDetails, currPlayer.isResign());
//        
//        
//        return playerDetails;
//    }
//    
//    private static void setPlayerType(PlayerDetails playerDetails, boolean isComputer)
//    {
//        if(isComputer)
//        {
//            playerDetails.setType(PlayerType.COMPUTER);
//        }
//        else
//        {
//            playerDetails.setType(PlayerType.HUMAN);
//        }
//    }
//    

    private static void setPlayerStatuse(PlayerDetails playerDetails, boolean isResign) {
        if (isResign) {
            playerDetails.setStatus(PlayerStatus.RETIRED);
        } else {
            playerDetails.setStatus(PlayerStatus.ACTIVE);
        }
    }

//    public static void addPlayerTiles(Player currPlayer , PlayerDetails playerDetails)
//    {
//        List<ws.rummikub.Tile> wsTiles = new ArrayList<>();
//        ws.rummikub.Tile wslTile;
//        
//        for (game.model.Tile tile : currPlayer.getPackByList())
//        {
//          wslTile = new ws.rummikub.Tile();
//          wslTile.setColor(ws.rummikub.Color.fromValue(covertColorToXmlName(tile.getTileColor())));
//          if(tile.isAJoker())
//          {
//              tile.setValuSign(0);
//          }
//          
//          wslTile.setValue(tile.getValuSign());
//          wsTiles.add(wslTile);
//        }
//        
//        playerDetails.getTiles().addAll(wsTiles);
//    }
    public static ws.monopoly.Event createEvent(int id, ws.monopoly.EventType type, String name, int timeoutCount) {
        ws.monopoly.Event res = new Event();
        res.setId(id);
        res.setPlayerName(name);
        res.setType(type);
        res.setTimeout(timeoutCount);
        return res;
    }
    
    public static ws.monopoly.Event createBasicEvent(int id, ws.monopoly.EventType type, String name) {
        ws.monopoly.Event res = new Event();
        
        res.setId(id);
        res.setPlayerName(name);
        res.setType(type);
        return res;
    }

//      public static ws.monopoly.Event createEventSequens(int id, ws.monopoly.EventType type, List<ws.rummikub.Tile> tiles, String name)
//      {
//        ws.monopoly.Event res = createBasicEvent(id,type,name);
//        
//        res.getTiles().addAll(tiles);
//        //TODO not finished
//        return res;
//      }
//      public static ws.monopoly.Event createEventAddTile(int id, ws.rummikub.EventType type, String name , int sequenceIndex, int sequencePosition, ws.rummikub.Tile tile)
//      {
//        ws.monopoly.Event res = createBasicEvent(id,type,name);
//        res.setSourceSequenceIndex(sequenceIndex);
//        res.setSourceSequencePosition(sequencePosition);
//        res.setTargetSequenceIndex(sequenceIndex);
//        res.setTargetSequencePosition(sequencePosition);
//        res.getTiles().add(tile);
//        //TODO
//        return res;
//      }
//      public static ws.monopoly.Event createEventTile (int id, ws.rummikub.EventType type, String name , int sequenceIndex, int sequencePosition, int newSequenceIndex , int newSequencePosition, ws.rummikub.Tile tile)
//      {
//          ws.monopoly.Event res = createBasicEvent(id,type,name);
//          res.setSourceSequenceIndex(sequenceIndex);
//          res.setSourceSequencePosition(sequencePosition);
//          res.setTargetSequenceIndex(newSequenceIndex);
//          res.setTargetSequencePosition(newSequencePosition);
//          res.getTiles().add(tile);
//          return res;
//      }
    public static PlayerDetails getPlayerDetails(Player currPlayer) {
        PlayerDetails playerDetails = new PlayerDetails();
        
        playerDetails.setName(currPlayer.getName());
        playerDetails.setMoney((int) currPlayer.getAmount());
        setPlayerStatuse(playerDetails, currPlayer.isResign());
        if (currPlayer.getClass().equals(HumanPlayer.class)) {
            playerDetails.setType(PlayerType.HUMAN);
        } else {
            playerDetails.setType(PlayerType.COMPUTER);
        }
        
        return playerDetails;
    }
}
