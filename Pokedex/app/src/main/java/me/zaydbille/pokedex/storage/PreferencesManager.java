/*
 -----------------------------------------------------------------------
|                                                                       |
|   Class:          PreferencesManager                                  |
|   Description:    PreferencesManager class.                           |
|                                                                       |
|                                                                       |
|                                                                       |
|   Author:         Zayd-Waves                                          |
|   Date:           5/31/2016                                           |
|                                                                       |
|                                                                       |
|                                                                       |
 -----------------------------------------------------------------------
*/

package me.zaydbille.pokedex.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.zaydbille.pokedex.data.Ability;
import me.zaydbille.pokedex.data.Move;
import me.zaydbille.pokedex.data.Pokemon;
import me.zaydbille.pokedex.data.Team;

public class PreferencesManager {

    public static final String                  PREFS_NAME = "PrefsFile";
    private static HashSet<String>              caughtPokemon;
    private static List<Pokemon>                allPokemon;
    private static List<Move>                   allMoves;
    private static List<Ability>                allAbilities;
    private static HashMap<Integer, Integer>    machines;

    /* We'll use the next three methods to check if it's the first time opening the app. */
    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    public static void setFirstTIme(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE).edit();
        editor.putString("firstTime", "yes");
        editor.apply();
    }
    public static String getFirstTime(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        return prefs.getString("firstTime", "nil");
    }

    /* Theme related methods */
    public static void setThemeToLabTheme(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE).edit();
        editor.putString("theme", "LabTheme");
        editor.apply();
    }

    public static void setThemeToDPTheme(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE).edit();
        editor.putString("theme", "DPTheme");
        editor.apply();
    }

    public static String getTheme(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        return prefs.getString("theme", "default");
    }

    /* Managing the caught Pokemon. */
    public static void saveAllCaughtPokemon(Context context, HashSet<String> caughtSet) {
        caughtPokemon = caughtSet;
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, 0).edit();
        editor.clear();
        editor.putStringSet("caught", caughtSet);
        editor.apply();
    }

    public static Set<String> getAllCaughtPokemon(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        Set<String> set = prefs.getStringSet("caught", new HashSet<String>());
        return set;
    }

    public static void togglePokemonCaught(Context context, String pokemonOrder, int caught) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> set = prefs.getStringSet("caught", new HashSet<String>());
        if (caught == 1) {
            set.add(pokemonOrder);
        } else if (caught == 0) {
            set.remove(pokemonOrder);
        }
        editor.clear();
        editor.putStringSet("caught", set);
        editor.apply();
    }

    public static int checkIfPokemonIsCaught(Context context, String pokemonOrder) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        Set<String> set = prefs.getStringSet("caught", new HashSet<String>());
        if (set.contains(pokemonOrder)) {
            return 1;
        } else {
            return 0;
        }
    }

    /* Managing teams. */
    public static void addTeamCount(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE).edit();
        int newCount = getTeamCount(context) + 1;
        editor.putInt("teamCount", newCount);
    }

    public static void removeTeamCount(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE).edit();
        int newCount = 0;
        if (getTeamCount(context) >= 0) {
            newCount = getTeamCount(context) + 1;
        } else {
            newCount = 0;
        }
        editor.putInt("teamCount", newCount);
    }

    public static int getTeamCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        return prefs.getInt("teamCount", 0);
    }

    public static void saveTeamList(Context context, List<Team> teams) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE).edit();

        Set<String> set = new HashSet<String>();
        Gson gson = new Gson();
        for ( int i = 0; i < teams.size(); i++ ) {
            set.add(gson.toJson(teams.get(i)));
        }

        editor.putStringSet("teams", set);
        editor.apply();
    }

    public static void updateTeams(Context context, List<Team> teams) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE).edit();

        Set<String> set = new HashSet<String>();

        List<Team> oldTeams = getTeams(context);
        if (oldTeams != null) {
            /* Get the currently saved messages and merge into one list. */
            teams.addAll(oldTeams);
        }

        Gson gson = new Gson();
        for ( int i = 0; i < teams.size(); i++ ) {
            set.add(gson.toJson(teams.get(i)));
        }

        editor.putStringSet("teams", set);
        editor.apply();
    }

    public static List<Team> getTeams(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        Gson gson = new Gson();

        List<Team> teams = new ArrayList<Team>();
        Set<String> set = prefs.getStringSet("teams", null);

        if (set != null) {
            for (String s : set) {
                teams.add(gson.fromJson(s, Team.class));
            }
            return teams;
        } else {
            /* No teams found... */
            return new ArrayList<Team>();
        }
    }

    /* Managing the Pokemon data */
    public static List<Pokemon> getAllPokemon() {
        return allPokemon;
    }
    public static void setAllPokemon(List<Pokemon> pokemon) {
        allPokemon = pokemon;
    }
    public static void updateSinglePokemon(Pokemon updatedPokemon, int position) {
        allPokemon.set(position, updatedPokemon);
    }

    public static List<Move> getAllMoves() {
        return allMoves;
    }
    public static void setAllMoves(List<Move> moves) {
        allMoves = moves;
    }

    public static HashMap<Integer, Integer> getAllMachines() { return machines; }
    public static void setAllMachines(HashMap<Integer, Integer> machineList) { machines = machineList; }

    public static List<Ability> getAllAbilities() {
        return allAbilities;
    }
    public static void setAllAbilities(List<Ability> abilities) {
        allAbilities = abilities;
    }

}
