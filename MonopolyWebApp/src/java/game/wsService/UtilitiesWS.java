/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.wsService;

import java.util.ArrayList;
import java.util.List;
import models.Player;
import ws.monopoly.Event;
import ws.monopoly.PlayerDetails;
import ws.monopoly.PlayerStatus;

/**
 *
 * @author Liraz
 */
public class UtilitiesWS {
//     public static ws.monopoly.PlayerDetails getPlayerDetails(Player currPlayer)
//    {
//        PlayerDetails pDetails = new PlayerDetails();
//        
//        pDetails.setName(currPlayer.getName());
//        pDetails.setNumberOfTiles(currPlayer.getPack().size());
//        pDetails.setPlayedFirstSequence(!currPlayer.isFirstMove());
//        setPlayerType(pDetails, currPlayer.isComputer());
//        setPlayerStatuse(pDetails, currPlayer.isResign());
//        
//        
//        return pDetails;
//    }
//    
//    private static void setPlayerType(PlayerDetails pDetails, boolean isComputer)
//    {
//        if(isComputer)
//        {
//            pDetails.setType(PlayerType.COMPUTER);
//        }
//        else
//        {
//            pDetails.setType(PlayerType.HUMAN);
//        }
//    }
//    
    private static void setPlayerStatuse(PlayerDetails pDetails, boolean isResign)
    {
        if(isResign)
        {
            pDetails.setStatus(PlayerStatus.RETIRED);
        }
        else
        {
            pDetails.setStatus(PlayerStatus.ACTIVE);
        }
    }
    
//    public static void addPlayerTiles(Player currPlayer , PlayerDetails pDetails)
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
//        pDetails.getTiles().addAll(wsTiles);
//    }

    public static ws.monopoly.Event createEvent(int id, ws.monopoly.EventType type, String name)
    {
        ws.monopoly.Event res = new Event();
        
        res.setId(id);
        res.setPlayerName(name);
        res.setType(type);

        return res;
    }
    
    public static ws.monopoly.Event createBasicEvent(int id, ws.monopoly.EventType type, String name)
    {
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
}
