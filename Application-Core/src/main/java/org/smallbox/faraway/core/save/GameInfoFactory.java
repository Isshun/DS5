package org.smallbox.faraway.core.save;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.GameScenario;
import org.smallbox.faraway.game.planet.RegionInfo;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Random;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

@ApplicationObject
public class GameInfoFactory {
    @Inject private DataManager dataManager;

    public JSONObject toJSON(GameInfo gameInfo) {
        JSONObject json = new JSONObject();

        json.put("name", gameInfo.name);
        json.put("label", gameInfo.label);
        json.put("date", gameInfo.date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        json.put("planet", gameInfo.planet.name);
        json.put("region", gameInfo.region.name);
        json.put("width", gameInfo.worldWidth);
        json.put("height", gameInfo.worldHeight);
        json.put("floors", gameInfo.worldFloors);

        JSONArray saveArray = new JSONArray();
        for (GameSaveInfo saveInfo : gameInfo.saveFiles) {
            saveArray.put(saveInfo.toJSON());
        }
        json.put("saves", saveArray);

        return json;
    }

    public GameInfo fromJSON(JSONObject json) {
        GameInfo gameInfo = new GameInfo();

        gameInfo.name = json.getString("name");
        gameInfo.label = json.optString("label", "missing");
        gameInfo.date = LocalDateTime.parse(json.optString("date", "2011-12-03T10:15:30"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        gameInfo.planet = dataManager.getPlanet(json.getString("planet"));
        if (gameInfo.planet != null && gameInfo.planet.regions != null) {
            gameInfo.region = gameInfo.planet.regions.stream().filter(region -> region.name.equals(json.getString("region"))).findFirst().orElse(null);
        }
        gameInfo.worldWidth = json.getInt("width");
        gameInfo.worldHeight = json.getInt("height");
        gameInfo.worldFloors = json.getInt("floors");
        gameInfo.groundFloor = json.getInt("floors") - 1;

        if (json.has("saves")) {
            for (int i = 0; i < json.getJSONArray("saves").length(); i++) {
                GameSaveInfo saveInfo = GameSaveInfo.fromJSON(json.getJSONArray("saves").getJSONObject(i));
                saveInfo.game = gameInfo;
                gameInfo.saveFiles.add(saveInfo);
            }
        }

        return gameInfo;
    }

    public GameInfo create(GameScenario scenario) {
        return create(dataManager.getRegion(scenario.planet, scenario.region), scenario.width, scenario.height, scenario.level, scenario.generateMountains);
    }

    public GameInfo create(String planetName, String regionName, int worldWidth, int worldHeight, int worldFloors) {
        return create(dataManager.getRegion(planetName, regionName), worldWidth, worldHeight, worldFloors, true);
    }

    public GameInfo create(RegionInfo regionInfo, int worldWidth, int worldHeight, int worldFloors, boolean generateMountains) {
        assert worldWidth <= Constant.MAX_WORLD_WIDTH;
        assert worldHeight <= Constant.MAX_WORLD_HEIGHT;
        assert worldFloors <= Constant.MAX_WORLD_FLOORS;

        GameInfo info = new GameInfo();

        info.date = LocalDateTime.now();
        info.worldWidth = worldWidth;
        info.worldHeight = worldHeight;
        info.worldFloors = worldFloors;
        info.groundFloor = worldFloors - 1;
        info.planet = regionInfo.planet;
        info.region = regionInfo;
        info.label = buildRandomName();
        info.name = UUID.randomUUID().toString();
        info.generateMountains = generateMountains;

        return info;
    }

    private String buildRandomName() {
        return StringUtils.lowerCase(
                Random.of(Arrays.asList("shaky", "jolly", "shivering", "fast", "tranquil", "humdrum", "feeble", "unequal", "elite", "sulky", "rabid", "half", "grey", "lyrical", "prickly", "large", "synonymous", "meek", "hypnotic", "tremendous", "common", "fluttering", "used", "two", "premium", "damp", "upset", "rough", "aloof", "internal", "chilly", "obtainable", "fallacious", "panoramic", "waggish", "tight", "fretful", "yummy", "hallowed", "acceptable", "light", "omniscient", "oval", "quick", "disturbed", "spiffy", "godly", "fortunate", "polite", "nutty", "probable", "hushed", "craven", "fabulous", "smart", "dusty", "sore", "elated", "chunky", "real", "abiding", "long-term", "melted", "dizzy", "materialistic", "inquisitive", "acrid", "frail", "imperfect", "far-flung", "wasteful", "combative", "bumpy", "disgusted", "unusual", "parsimonious", "pricey", "noisy", "sore", "oafish", "moaning", "full", "uttermost", "unknown", "astonishing", "judicious", "ceaseless", "homely", "shiny", "flagrant", "calculating", "scary", "cautious", "different", "glistening", "ad hoc", "equable", "sudden", "enchanted", "certain")) +
                        " " +
                        Random.of(Arrays.asList("Adult", "Aeroplane", "Air", "Aircraft Carrier", "Airforce", "Airport", "Album", "Alphabet", "Apple", "Arm", "Army", "Baby", "Baby", "Backpack", "Balloon", "Banana", "Bank", "Barbecue", "Bathroom", "Bathtub", "Bed", "Bed", "Bee", "Bible", "Bible", "Bird", "Bomb", "Book", "Boss", "Bottle", "Bowl", "Box", "Boy", "Brain", "Bridge", "Butterfly", "Button", "Cappuccino", "Car", "Car-race", "Carpet", "Carrot", "Cave", "Chair", "Chess Board", "Chief", "Child", "Chisel", "Chocolates", "Church", "Church", "Circle", "Circus", "Circus", "Clock", "Clown", "Coffee", "Coffee-shop", "Comet", "Compact Disc", "Compass", "Computer", "Crystal", "Cup", "Cycle", "Data Base", "Desk", "Diamond", "Dress", "Drill", "Drink", "Drum", "Dung", "Ears", "Earth", "Egg", "Electricity", "Elephant", "Eraser", "Explosive", "Eyes", "Family", "Fan", "Feather", "Festival", "Film", "Finger", "Fire", "Floodlight", "Flower", "Foot", "Fork", "Freeway", "Fruit", "Fungus", "Game", "Garden", "Gas", "Gate", "Gemstone", "Girl", "Gloves", "God", "Grapes", "Guitar", "Hammer", "Hat", "Hieroglyph", "Highway", "Horoscope", "Horse", "Hose", "Ice", "Ice-cream", "Insect", "Jet fighter", "Junk", "Kaleidoscope", "Kitchen", "Knife", "Leather jacket", "Leg", "Library", "Liquid", "Magnet", "Man", "Map", "Maze", "Meat", "Meteor", "Microscope", "Milk", "Milkshake", "Mist", "Money $$$$", "Monster", "Mosquito", "Mouth", "Nail", "Navy", "Necklace", "Needle", "Onion", "PaintBrush", "Pants", "Parachute", "Passport", "Pebble", "Pendulum", "Pepper", "Perfume", "Pillow", "Plane", "Planet", "Pocket", "Post-office", "Potato", "Printer", "Prison", "Pyramid", "Radar", "Rainbow", "Record", "Restaurant", "Rifle", "Ring", "Robot", "Rock", "Rocket", "Roof", "Room", "Rope", "Saddle", "Salt", "Sandpaper", "Sandwich", "Satellite", "School", "Sex", "Ship", "Shoes", "Shop", "Shower", "Signature", "Skeleton", "Slave", "Snail", "Software", "Solid", "Space Shuttle", "Spectrum", "Sphere", "Spice", "Spiral", "Spoon", "Sports-car", "Spot Light", "Square", "Staircase", "Star", "Stomach", "Sun", "Sunglasses", "Surveyor", "Swimming Pool", "Sword", "Table", "Tapestry", "Teeth", "Telescope", "Television", "Tennis racquet", "Thermometer", "Tiger", "Toilet", "Tongue", "Torch", "Torpedo", "Train", "Treadmill", "Triangle", "Tunnel", "Typewriter", "Umbrella", "Vacuum", "Vampire", "Videotape", "Vulture", "Water", "Weapon", "Web", "Wheelchair", "Window", "Woman", "Worm", "X-ray"))
        );
    }
}
