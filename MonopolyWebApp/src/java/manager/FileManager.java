/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manager;

import generated.CityType;
import generated.Monopoly;
import generated.SimpleAssetType;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import models.AssetType;
import models.MonopolyModel;
import org.xml.sax.SAXException;

/**
 *
 * @author Liraz
 */
public class FileManager {

    private static final String RESOURCES = "resources";
    private Monopoly monopolyGame;

    public FileManager(String xmlFileName, boolean uploadFile, boolean uploadFX) throws SAXException, JAXBException, FileNotFoundException, Exception {

        if (uploadFX) {
            File xmlFileFromString = turnPathToFile(xmlFileName);
            loadXmlFromUser(xmlFileFromString);
        } else {
            String xmlFile = "/" + RESOURCES + "/" + xmlFileName + ".xml";
            if (uploadFile) {
                //xmlFile = xmlFileName + ".xml";
                File xmlFileFromString = turnPathToFile(xmlFile);
                loadXmlFromUser(xmlFileFromString);
            } else {
                unmarshalle(xmlFile);
            }
        }

        boolean isGeneratedValid = ValidateGeneratedMonopoly(this.monopolyGame);
        if (!isGeneratedValid) {
            throw new Exception("Invalid Xml File");
        }
    }

    public void unmarshalle(String xmlFile) {

        InputStream xmlInputStream = FileManager.class.getResourceAsStream(xmlFile);
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(generated.Monopoly.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            this.monopolyGame = (Monopoly) jaxbUnmarshaller.unmarshal(xmlInputStream);

        } catch (JAXBException exception) {
            exception.printStackTrace();
        }
    }

    public static File turnPathToFile(String dest) {
// Directory where the files are located
        //File dir = new File("src//");
        File dir = new File(dest);
        return dir;
    }

    public void loadXmlFromUser(File LoadFile) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(generated.Monopoly.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        this.monopolyGame = (Monopoly) jaxbUnmarshaller.unmarshal(LoadFile);
    }

    public MonopolyModel initalizeGameFromXMLFile() {
        MonopolyModel.Board board = getBoardFromXML();
        MonopolyModel.Assets assets = convertToAssets();
        MonopolyModel.Surpries surpries = convertToSurpries();
        MonopolyModel.Warrant warrant = convertToWarrant();

        MonopolyModel monopolyGame = new MonopolyModel(board, assets, surpries, warrant);
        setValuesInMonopolyGame(monopolyGame);

        return monopolyGame;
    }

    private MonopolyModel.Board setBoardFromXML() {
        MonopolyModel.Board board = getBoardFromXML();
        return board;
    }

    private MonopolyModel.Board getBoardFromXML() {

        int contentSize = this.monopolyGame.getBoard().getContent().size();
        ArrayList<models.SquareBase> newContent = new ArrayList<>();

        for (int i = 0; i < contentSize; i++) {

            if (this.monopolyGame.getBoard().getContent().get(i).getDeclaredType().equals(generated.StartSquareType.class)) {
                newContent.add(new models.StartSquare());
            } else if (this.monopolyGame.getBoard().getContent().get(i).getDeclaredType().equals((generated.GotoJailSquareType.class))) {
                newContent.add(new models.GotoJailSquareType());
            } else if (this.monopolyGame.getBoard().getContent().get(i).getDeclaredType().equals(generated.JailSlashFreeSpaceSquareType.class)) {
                newContent.add(new models.JailSlashFreeSpaceSquareType());
            } else if (this.monopolyGame.getBoard().getContent().get(i).getDeclaredType().equals(generated.ParkingSquareType.class)) {
                newContent.add(new models.ParkingSquareType());
            } else if (this.monopolyGame.getBoard().getContent().get(i).getDeclaredType().equals(generated.SquareType.class)) {
                generated.SquareType squareType = new generated.SquareType();
                squareType = (generated.SquareType) this.monopolyGame.getBoard().getContent().get(i).getValue();
                switch (squareType.getType()) {
                    case "CITY":
                        newContent.add(new models.SquareType("CITY"));
                        break;
                    case "WARRANT":
                        newContent.add(new models.SquareType("WARRANT"));
                        break;
                    case "SURPRISE":
                        newContent.add(new models.SquareType("SURPRISE"));
                        break;
                    case "TRANSPORTATION":
                        newContent.add(new models.SquareType("TRANSPORTATION"));
                        break;
                    case "UTILITY":
                        newContent.add(new models.SquareType("UTILITY"));
                        break;
                }
            }
        }
        MonopolyModel.Board boardToReturn = new MonopolyModel.Board(newContent);

        return boardToReturn;
    }

    private MonopolyModel.Assets convertToAssets() {
        //get generated country list size
        int countryNum = this.monopolyGame.getAssets().getCountries().getCountry().size();
        Monopoly.Assets.Countries countriesGeneret = this.monopolyGame.getAssets().getCountries();
        //creat model country list from generated country list and
        //fill model country list in information from generated country list
        List<MonopolyModel.Country> countries = getCountriesFromXML(countriesGeneret);
        //creat model utilities list from generated utilities list and
        //fill model utilities list in information from generated utilities list
        MonopolyModel.Utilities utilities = getUtilitiesFromXML(monopolyGame.getAssets().getUtilities());
        //creat model transportations list from generated transportations list and
        //fill model transportations list in information from generated transportations list        
        MonopolyModel.Transportations transportations = getTransportionsFromXML(monopolyGame.getAssets().getTransportations());

        return new MonopolyModel.Assets(countries, utilities, transportations);
    }

    private MonopolyModel.Surpries convertToSurpries() {

        Monopoly.Surprise supriesGeneret = this.monopolyGame.getSurprise();
        List<generated.CardBase> cardBaseGeneretList = supriesGeneret.getSurpriseCards();
        int numOfCard = cardBaseGeneretList.size();
        List<models.CardBase> cardBaseToReturn = new ArrayList<>();
        for (int i = 0; i < numOfCard; i++) {
            models.CardBase cardBaseModel = creatCardBaseFromGeneret(cardBaseGeneretList.get(i));
            cardBaseToReturn.add(cardBaseModel);
        }
        //dosnt work
        return new MonopolyModel.Surpries(cardBaseToReturn);
    }

    private MonopolyModel.Warrant convertToWarrant() {
        Monopoly.Warrant warrantGeneret = this.monopolyGame.getWarrant();
        List<generated.CardBase> cardBaseGeneretList = warrantGeneret.getWarrantCards();
        int numOfCard = cardBaseGeneretList.size();
        List<models.CardBase> cardBaseToReturn = new ArrayList<>();
        for (int i = 0; i < numOfCard; i++) {
            models.CardBase cardBaseModel = creatCardBaseFromGeneret(cardBaseGeneretList.get(i));
            cardBaseToReturn.add(cardBaseModel);
        }
        //dosnt work
        return new MonopolyModel.Warrant(cardBaseToReturn);
    }

    private List<MonopolyModel.Country> getCountriesFromXML(Monopoly.Assets.Countries countriesGeneret) {
        int countryNum = countriesGeneret.getCountry().size();
        //list of counries to return from func 
        ArrayList<MonopolyModel.Country> countriesReturnd = new ArrayList();
        //going on the countriesGeneret list

        for (int j = 0; j < countryNum; j++) {
            //temp generated country
            Monopoly.Assets.Countries.Country currentCuntry = countriesGeneret.getCountry().get(j);
            //temp generated country name
            String countryName = currentCuntry.getName();
            //returnd model country array
            List<models.CityType> cityModelsLst = new ArrayList<>();
            //generated city list size
            int citiesNum = currentCuntry.getCity().size();
            //going on the cities list for evrey country and create modle city type for returnd country list
            for (int i = 0; i < citiesNum; i++) {
                //generated city type
                generated.CityType generetCity = (generated.CityType) currentCuntry.getCity().get(i);
                //creating city type from generated city type
                models.CityType modelCity = createModelCityTypeFromGenerated(generetCity, countryName);
                //add model city to cities list
                cityModelsLst.add(modelCity);
            }
            MonopolyModel.Country countryModel = createCountryModelFromGenerated(countryName, cityModelsLst);
            countriesReturnd.add(countryModel);
        }
        return countriesReturnd;
    }

    private models.CityType createModelCityTypeFromGenerated(CityType generetCity, String countryName) {
        return new models.CityType(generetCity.getHouseCost(), generetCity.getStayCost1(), generetCity.getStayCost2(), generetCity.getStayCost3(), generetCity.getName(), generetCity.getCost(), generetCity.getStayCost(), countryName);
    }

    private MonopolyModel.Country createCountryModelFromGenerated(String name, List<models.CityType> cityModelsLst) {
        return new MonopolyModel.Country(name, cityModelsLst);
    }

    private MonopolyModel.Utilities getUtilitiesFromXML(Monopoly.Assets.Utilities utilitiesGeneret) {
        //modle utilities list to return
        ArrayList<models.SimpleAssetType> utilitisModelList = new ArrayList<>();
        //generated utilities list size
        int numOfUtilitis = utilitiesGeneret.getUtility().size();
        //going on generated list and fiil model utilities list to return
        for (int i = 0; i < numOfUtilitis; i++) {
            models.SimpleAssetType simpelAssetModel = creatSimpeleAssetTypeFromGeneret(utilitiesGeneret.getUtility().get(i));
            utilitisModelList.add(simpelAssetModel);
        }
        return new MonopolyModel.Utilities(utilitiesGeneret.getStayCost(), utilitisModelList);
    }

    private MonopolyModel.Transportations getTransportionsFromXML(Monopoly.Assets.Transportations transportationsGeneret) {
        ArrayList<models.SimpleAssetType> transportionModelList = new ArrayList<>();
        int numOfTransportion = transportationsGeneret.getTransportation().size();

        for (int i = 0; i < numOfTransportion; i++) {
            models.SimpleAssetType simpelAssetModel = creatSimpeleAssetTypeFromGeneret(transportationsGeneret.getTransportation().get(i));
            transportionModelList.add(simpelAssetModel);
        }
        return new MonopolyModel.Transportations(transportationsGeneret.getStayCost(), transportionModelList);
    }

    private models.SimpleAssetType creatSimpeleAssetTypeFromGeneret(SimpleAssetType simpeleAssetGeneret) {
        return new models.SimpleAssetType(simpeleAssetGeneret.getName(), simpeleAssetGeneret.getCost(), simpeleAssetGeneret.getStayCost());
    }

    private models.CardBase creatCardBaseFromGeneret(generated.CardBase cardBaseGenert) {
        models.CardBase cardBaseModel = null;
        if (cardBaseGenert.getClass().equals(generated.GotoCard.class)) {
            generated.GotoCard goToCardGeneret = (generated.GotoCard) cardBaseGenert;
            cardBaseModel = new models.GotoCard(goToCardGeneret.getText(), goToCardGeneret.getNum(), goToCardGeneret.getTo().toString());
        } else if (cardBaseGenert.getClass().equals(generated.MonetaryCard.class)) {
            generated.MonetaryCard monoteryCardGeneret = (generated.MonetaryCard) cardBaseGenert;
            cardBaseModel = new models.MonetaryCard(monoteryCardGeneret.getSum(), monoteryCardGeneret.getWho().toString(), monoteryCardGeneret.getText(), monoteryCardGeneret.getNum());
        } else {
            cardBaseModel = new models.GetOutOfJailCard(cardBaseGenert.getText(), cardBaseGenert.getNum());
        }
        return cardBaseModel;//new models.CardBase(cardBaseGenert.getNum(), cardBaseGenert.getText()) {

    }

    private void setValuesInMonopolyGame(MonopolyModel monopolyGame) {

        int boardsSize = 36;
        int countryIndex = 0;
        int cityIndex = 0;
        int utilitiesIndex = 0;
        int transportationIndex = 0;
        AssetType currentAssetInfo;

        for (int i = 0; i < boardsSize; i++) {
            if (monopolyGame.getBoard().getSqureBaseBySqureNum(i).getClass().equals(models.SquareType.class)) {
                //get current squar type from board
                models.SquareType currentSquarType = (models.SquareType) monopolyGame.getBoard().getSqureBaseBySqureNum(i);
                //get current country ist from board
                List<MonopolyModel.Country> countryList = monopolyGame.getAssets().getCountries();

                switch (currentSquarType.getType().toString()) {
                    case "CITY":
                        //get current counrty from country list
                        MonopolyModel.Country currentCountry = countryList.get(countryIndex);
                        //get current city list from current country
                        List<models.CityType> currentCitiesFromCurrentCountry = currentCountry.getCitys();
                        //get current city from current city list
                        models.CityType currentCityInfo = currentCitiesFromCurrentCountry.get(cityIndex);
                        //creat current asset to put inside game current squar
                        currentAssetInfo = new models.CityType(currentCityInfo.getHouseCost(), currentCityInfo.getStayCost1(),
                                currentCityInfo.getStayCost2(), currentCityInfo.getStayCost3(), currentCityInfo.getName(),
                                currentCityInfo.getCost(), currentCityInfo.getStayCost(), currentCityInfo.getCuntryName());
                        //put information inside monopoly game squar type
                        currentSquarType.setAsset(currentAssetInfo);
                        cityIndex++;
                        if (cityIndex >= currentCitiesFromCurrentCountry.size()) {
                            cityIndex = 0;
                            countryIndex++;
                            if (countryIndex >= countryList.size()) {
                                countryIndex = 0;
                            }
                        }
                        break;
                    case "TRANSPORTATION":
                        //set simple asset type insted if asset type unside squar type
                        //get current Transportations from game 
                        MonopolyModel.Transportations currentTransportations = monopolyGame.getAssets().getTransportations();
                        //get transportationsInfo list
                        List<models.SimpleAssetType> transportationsInfo = currentTransportations.getTransport();
                        //get current tranportation asset
                        models.SimpleAssetType currentAsset = transportationsInfo.get(transportationIndex);
                        //String name, long cost, long staycost)
                        currentAssetInfo = new models.SimpleAssetType(currentAsset.getName(), currentAsset.getCost(), currentAsset.getStaycost());
                        //put current info inside game
                        currentSquarType.setAsset(currentAssetInfo);
                        transportationIndex++;
                        if (transportationIndex >= transportationsInfo.size()) {
                            transportationIndex = 0;
                        }
                        break;
                    case "UTILITY":
                        //set simple asset type insted if asset type unside squar type
                        //get current Transportations from game 
                        MonopolyModel.Utilities currentUtilities = monopolyGame.getAssets().getUtilities();
                        //get transportationsInfo list
                        List<models.SimpleAssetType> UtilitiesInfo = currentUtilities.getUtility();
                        //get current tranportation asset
                        models.SimpleAssetType currentUtility = UtilitiesInfo.get(utilitiesIndex);
                        //String name, long cost, long staycost)
                        currentAssetInfo = new models.SimpleAssetType(currentUtility.getName(), currentUtility.getCost(), currentUtility.getStaycost());
                        //put current info inside game
                        currentSquarType.setAsset(currentAssetInfo);
                        utilitiesIndex++;
                        if (utilitiesIndex >= UtilitiesInfo.size()) {
                            utilitiesIndex = 0;
                        }
                        break;
                }
            }

        }
    }

    private boolean ValidateGeneratedMonopoly(Monopoly monopolyGame) {

        boolean assetsFlag = false;
        boolean boardFlag = false;

        if (monopolyGame.getBoard().getContent().size() == 36) {
            boardFlag = true;
        }
        assetsFlag = checkBoardAsset(monopolyGame.getBoard());
        return boardFlag && assetsFlag;
    }

    private boolean checkBoardAsset(Monopoly.Board board) {

        boolean boardAsset = false;
        //must have squar type : Start Squar, Jail/Free Pass / Surprise / Warrent / Parking Squar /
        boolean haveStartSquar = false;
        boolean haveJail_FreePassSquar = false;
        boolean haveGoToJailSquar = false;
        boolean haveParkingSquar = false;
        boolean haveWarrentSquar = false;
        boolean haveSurpriseSquar = false;
        //going on generated boad to find must have squar
        for (int i = 0; i < board.getContent().size(); i++) {
            if (this.monopolyGame.getBoard().getContent().get(i).getDeclaredType().equals(generated.StartSquareType.class)) {
                haveStartSquar = true;
            } else if (this.monopolyGame.getBoard().getContent().get(i).getDeclaredType().equals((generated.GotoJailSquareType.class))) {
                haveGoToJailSquar = true;
            } else if (this.monopolyGame.getBoard().getContent().get(i).getDeclaredType().equals(generated.JailSlashFreeSpaceSquareType.class)) {
                haveJail_FreePassSquar = true;
            } else if (this.monopolyGame.getBoard().getContent().get(i).getDeclaredType().equals(generated.ParkingSquareType.class)) {
                haveParkingSquar = true;
            } else if (this.monopolyGame.getBoard().getContent().get(i).getDeclaredType().equals(generated.SquareType.class)) {
                generated.SquareType squareType = new generated.SquareType();
                squareType = (generated.SquareType) this.monopolyGame.getBoard().getContent().get(i).getValue();
                switch (squareType.getType()) {
                    case "WARRANT":
                        haveWarrentSquar = true;
                        break;
                    case "SURPRISE":
                        haveSurpriseSquar = true;
                        break;
                }
            }
        }

        boardAsset = haveGoToJailSquar && haveJail_FreePassSquar && haveParkingSquar && haveStartSquar && haveSurpriseSquar && haveWarrentSquar;
        return boardAsset;
    }
}
