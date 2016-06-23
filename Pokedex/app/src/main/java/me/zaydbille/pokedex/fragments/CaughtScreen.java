package me.zaydbille.pokedex.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import me.zaydbille.pokedex.R;
import me.zaydbille.pokedex.adapters.CaughtListAdapter;
import me.zaydbille.pokedex.data.Pokemon;
import me.zaydbille.pokedex.storage.PreferencesManager;
import me.zaydbille.pokedex.utils.PokedexUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CaughtScreen.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CaughtScreen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CaughtScreen extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ListView listView;
    private ProgressBar progressBar;
    private TextView progressText;
    static Context mContext;
    static List<Pokemon> pokemonList;
    private CaughtListAdapter adapter;
    private int caughtRowTextColor;
    private int rowColour;

    private OnFragmentInteractionListener mListener;

    public CaughtScreen() {
        // Required empty public constructor
    }

    public static CaughtScreen newInstance(Context con, List<Pokemon> data, int caughtText, int rowCol) {
        CaughtScreen fragment = new CaughtScreen();

        mContext = con;
        pokemonList = data;
        fragment.caughtRowTextColor = caughtText;
        fragment.rowColour = rowCol;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_caughtscreen, container, false);
        progressBar = (ProgressBar)v.findViewById(R.id.progress_bar);
        progressBar.setMax(pokemonList.size());
        progressText = (TextView)v.findViewById(R.id.progress_text);
        updatePokedexProgress();
        EditText searchBox = (EditText)v.findViewById(R.id.searchBox);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
        listView = (ListView) v.findViewById(R.id.caughtList);
        adapter = new CaughtListAdapter(mContext, PokedexUtils.sortByNumber(pokemonList), caughtRowTextColor, rowColour);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Pokemon p = (Pokemon)parent.getAdapter().getItem(position);
                onPokemonPressed(p);

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view, int pos, long id) {
                Pokemon p = adapter.getItem(pos);

                int caught = PreferencesManager.checkIfPokemonIsCaught(mContext, p.getOrderString());
                if (caught == 1) {
                    view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.textSecondary));
                    view.findViewById(R.id.checkmark).setVisibility(View.INVISIBLE);
                    PreferencesManager.togglePokemonCaught(mContext, p.getOrderString(), 0);
                    updatePokedexProgress();
                } else {
                    view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.caught));
                    view.findViewById(R.id.checkmark).setVisibility(View.VISIBLE);
                    PreferencesManager.togglePokemonCaught(mContext, p.getOrderString(), 1);
                    updatePokedexProgress();
                }

                return true;
            }
        });

        return v;
    }

    public void onPokemonPressed(Pokemon pokemon) {
        if (mListener != null) {
            mListener.onFragmentInteraction(pokemon.getId(), "");
        }
    }

    private void updatePokedexProgress() {
        double caughtPokemonCount = PreferencesManager.getAllCaughtPokemon(mContext).size();
        int count = PreferencesManager.getAllCaughtPokemon(mContext).size();
        double allPokemonCount = pokemonList.size();
        int allCount = pokemonList.size();
        float caughtPercentage = (float)((caughtPokemonCount * 100) / allPokemonCount);
        NumberFormat f = NumberFormat.getInstance(Locale.ENGLISH);
        f.setMaximumFractionDigits(2);
        f.setMinimumFractionDigits(2);
        String newText = + count
                         + " / "
                         + allCount
                         + " ( "
                         + f.format(caughtPercentage)
                         + "% "
                         + ")";

        progressText.setText(newText);
        progressBar.setProgress(count);


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(int pokemonId, String className);
    }
}