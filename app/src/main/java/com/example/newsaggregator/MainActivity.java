package com.example.newsaggregator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final List<Sources> sourcesList = new ArrayList<>();
    private final List<Articles> articlesList = new ArrayList<>();
    private final List<String> categories = new ArrayList<>();
    private final List<String> languagesOld = new ArrayList<>();
    private final List<String> countriesOld = new ArrayList<>();
    private final List<String> languagesNew = new ArrayList<>();
    private final List<String> countriesNew = new ArrayList<>();
    private final List<String> sourcesForViewer = new ArrayList<>();

    private DrawerLayout myDrawerLayout;
    private ListView myListView;
    private ActionBarDrawerToggle myDrawerToggle;
    private String[] items;
    private ViewPager pager;
    private MyPageAdapter pagerAdapter;

    private List<Fragment> fragments;
    //private Menu opt_menu;
    private SubMenu topicsMenu;
    private SubMenu languagesMenu;
    private SubMenu countriesMenu;

    private String categoryFilter = "";
    private String languageFilter = "";
    private String countryFilter = "";

    private String languageFilterOriginal = "";
    private String countryFilterOriginal = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDrawerLayout = findViewById(R.id.drawer_layout);
        myListView = findViewById(R.id.left_drawer);

        doNetCheck();

        GetSourcesRunnable sourcesRunnable = new GetSourcesRunnable(this);
        new Thread(sourcesRunnable).start();

        mainActivityHandler ();

        fragments = new ArrayList<>();

        pagerAdapter = new MyPageAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.viewpager);
        pager.setAdapter(pagerAdapter);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        myDrawerToggle.syncState(); // <== IMPORTANT
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        myDrawerToggle.onConfigurationChanged(newConfig); // <== IMPORTANT
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // Important!
        if (myDrawerToggle.onOptionsItemSelected(item)) {
            //Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }

        if (categories.contains(item.toString())){
            //Toast.makeText(this, "You just selected categories " + item.toString() , Toast.LENGTH_SHORT).show();
            categoryFilter = item.toString();
            sourcesForViewer.clear();
            //sourcesForViewer.add("All");
            for (int i = 0; i < sourcesList.size(); i++){
                if (sourcesList.get(i).getCategory().equals(categoryFilter)){

                    sourcesForViewer.add(sourcesList.get(i).getName());
                }
            }
            setTitle(String.format("News Gateway (%s)", sourcesForViewer.size()));
            mainActivityHandler();
        }

        if (languagesNew.contains(item.toString())){
            //Toast.makeText(this, "You just selected languages " + item.toString() , Toast.LENGTH_SHORT).show();
            languageFilterOriginal = item.toString();
            languageFilter = converter(getResources().openRawResource(R.raw.language_codes), item.toString()) ;
            sourcesForViewer.clear();
            //sourcesForViewer.add("All");
            for (int i = 0; i < sourcesList.size(); i++){
                if (sourcesList.get(i).getLanguage().equals(languageFilter)){
                    sourcesForViewer.add(sourcesList.get(i).getName());
                }
            }
            setTitle(String.format("News Gateway (%s)", sourcesForViewer.size()));
            mainActivityHandler();
        }

        if (countriesNew.contains(item.toString())){
            countryFilterOriginal = item.toString();
            countryFilter = converter(getResources().openRawResource(R.raw.country_codes), item.toString()) ;
            sourcesForViewer.clear();
            //sourcesForViewer.add("All");
            for (int i = 0; i < sourcesList.size(); i++){
                if (sourcesList.get(i).getLanguage().equals(countryFilter)){
                    sourcesForViewer.add(sourcesList.get(i).getName());
                }
            }
            setTitle(String.format("News Gateway (%s)", sourcesForViewer.size()));
            mainActivityHandler();
        }

        if (item.toString().equals("All Topics")){
            categoryFilter = "";
            sourcesForViewer.clear();
            for (int i = 0; i < sourcesList.size(); i++){
                sourcesForViewer.add(sourcesList.get(i).getName());
            }
            setTitle(String.format("News Gateway (%s)", sourcesForViewer.size()));
            mainActivityHandler();
        }

        if (item.toString().equals("All Languages")){
            languageFilterOriginal = "";
            languageFilter = "";
            sourcesForViewer.clear();
            for (int i = 0; i < sourcesList.size(); i++){
                sourcesForViewer.add(sourcesList.get(i).getName());
            }
            setTitle(String.format("News Gateway (%s)", sourcesForViewer.size()));
            mainActivityHandler();
        }

        if (item.toString().equals("All Countries")){
            countryFilterOriginal = "";
            countryFilter = "";
            sourcesForViewer.clear();
            for (int i = 0; i < sourcesList.size(); i++){
                sourcesForViewer.add(sourcesList.get(i).getName());
            }
            setTitle(String.format("News Gateway (%s)", sourcesForViewer.size()));
            mainActivityHandler();
        }

        if (!categoryFilter.equals("")){
            sourcesForViewer.clear();
            for (int i = 0; i < sourcesList.size(); i++){
                if (sourcesList.get(i).getCategory().equals(categoryFilter)){
                    sourcesForViewer.add(sourcesList.get(i).getName());
                }
            }
            setTitle(String.format("News Gateway (%s)", sourcesForViewer.size()));
            mainActivityHandler();
        }

        if (!languageFilter.equals("")){
            sourcesForViewer.clear();
            for (int i = 0; i < sourcesList.size(); i++){
                if (sourcesList.get(i).getLanguage().equals(languageFilter)){
                    sourcesForViewer.add(sourcesList.get(i).getName());
                }
            }
            setTitle(String.format("News Gateway (%s)", sourcesForViewer.size()));
            mainActivityHandler();
        }

        if (!countryFilter.equals("")){
            sourcesForViewer.clear();
            for (int i = 0; i < sourcesList.size(); i++){
                if (sourcesList.get(i).getCountry().equals(countryFilter)){
                    sourcesForViewer.add(sourcesList.get(i).getName());
                }
            }
            setTitle(String.format("News Gateway (%s)", sourcesForViewer.size()));
            mainActivityHandler();
        }

        if (!categoryFilter.equals("") && !languageFilter.equals("")){
            sourcesForViewer.clear();
            for (int i = 0; i < sourcesList.size(); i++){
                if (sourcesList.get(i).getCategory().equals(categoryFilter)){
                    if (sourcesList.get(i).getLanguage().equals(languageFilter))
                        sourcesForViewer.add(sourcesList.get(i).getName());
                }
            }
            setTitle(String.format("News Gateway (%s)", sourcesForViewer.size()));
            mainActivityHandler();
        }

        if (!categoryFilter.equals("") && !countryFilter.equals("")){
            sourcesForViewer.clear();
            for (int i = 0; i < sourcesList.size(); i++){
                if (sourcesList.get(i).getCategory().equals(categoryFilter)){
                    if (sourcesList.get(i).getCountry().equals(countryFilter))
                        sourcesForViewer.add(sourcesList.get(i).getName());
                }
            }
            setTitle(String.format("News Gateway (%s)", sourcesForViewer.size()));
            mainActivityHandler();
        }

        if (!languageFilter.equals("") && !countryFilter.equals("")){
            sourcesForViewer.clear();
            for (int i = 0; i < sourcesList.size(); i++){
                if (sourcesList.get(i).getLanguage().equals(languageFilter)){
                    if (sourcesList.get(i).getCountry().equals(countryFilter))
                        sourcesForViewer.add(sourcesList.get(i).getName());
                }
            }
            setTitle(String.format("News Gateway (%s)", sourcesForViewer.size()));
            mainActivityHandler();
        }

        if (!categoryFilter.equals("") && !countryFilter.equals("") && !languageFilter.equals("")){
            sourcesForViewer.clear();
            for (int i = 0; i < sourcesList.size(); i++){
                if (sourcesList.get(i).getCategory().equals(categoryFilter)){
                    if (sourcesList.get(i).getCountry().equals(countryFilter)){
                        if (sourcesList.get(i).getLanguage().equals(languageFilter))
                            sourcesForViewer.add(sourcesList.get(i).getName());
                    }
                }
            }
            setTitle(String.format("News Gateway (%s)", sourcesForViewer.size()));
            mainActivityHandler();
        }

        if(sourcesForViewer.isEmpty() && !item.toString().equals("Topics")
        && !item.toString().equals("Languages") && !item.toString().equals("Countries"))
            noMatchDialog(null);

        ((ArrayAdapter) myListView.getAdapter()).notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }

    public void noMatchDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(String.format("Cannot find any matches for: \nTopic: %s \nLanguage: %s \nCountry: %s",
                categoryFilter, languageFilterOriginal, countryFilterOriginal));
        builder.setTitle("No Match Found!");
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    public String converter(InputStream file, String oldString){
        String newString = "";

        try {
            InputStream is = file;

            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }

            String jsonString = writer.toString();
            JSONObject jObject = new JSONObject(jsonString);

            if (jObject.has("languages")){

                JSONArray jArray = jObject.getJSONArray("languages");

                    for (int j = 0 ; j < jArray.length(); j++){
                        JSONObject jo = jArray.getJSONObject(j);

                        if (oldString.equals(jo.getString("name"))){
                            newString = jo.getString("code").toLowerCase();
                            break;
                        }
                    }
            }
            if (jObject.has("countries")){
                JSONArray jArray = jObject.getJSONArray("countries");

                    for (int j = 0 ; j < jArray.length(); j++){
                        JSONObject jo = jArray.getJSONObject(j);

                        if (oldString.equals(jo.getString("name"))){
                            newString = jo.getString("code").toLowerCase();
                            break;
                        }
                    }
            }
        }

        catch (Exception e){
            e.printStackTrace();
        }
        return newString;
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){ //this is the only code we have for menues
        getMenuInflater().inflate(R.menu.options_menu, menu);

        topicsMenu = menu.addSubMenu("Topics");
        languagesMenu = menu.addSubMenu("Languages");
        countriesMenu = menu.addSubMenu("Countries");

        return true;
    }



    private void selectItem(int position) {
        pager.setBackground(null);
        setTitle(sourcesForViewer.get(position));
        String articleID = "";
        for (int i = 0; i < sourcesList.size(); i++){
            if (sourcesList.get(i).getName().equals(sourcesForViewer.get(position)))
                articleID = sourcesList.get(i).getId();
        }

        //Toast.makeText(this, sourcesForViewer.get(position), Toast.LENGTH_SHORT).show();
        GetArticlesRunnable GAR = new GetArticlesRunnable(this, articleID);
        new Thread(GAR).start();

        /*
        textView.setText(String.format(Locale.getDefault(),
                "You picked %s", items[position]));

         */
        myDrawerLayout.closeDrawer(myListView);
    }


    public void loadFile(InputStream currentList){

        try {
            InputStream is = currentList;

            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }

            String jsonString = writer.toString();
            JSONObject jObject = new JSONObject(jsonString);

            if (jObject.has("languages")){
                JSONArray jArray = jObject.getJSONArray("languages");
                //languagesNew.add("All");
                for (int i = 0; i < languagesOld.size() ; i++){

                    for (int j = 0 ; j < jArray.length(); j++){
                        JSONObject jo = jArray.getJSONObject(j);

                        if (languagesOld.get(i).toUpperCase().equals(jo.getString("code"))){
                            languagesNew.add(jo.getString("name"));
                            break;
                        }
                    }
                }
            }
            if (jObject.has("countries")){
                JSONArray jArray = jObject.getJSONArray("countries");
                //countriesNew.add("All");
                for (int i = 0; i < languagesOld.size() ; i++){

                    for (int j = 0 ; j < jArray.length(); j++){
                        JSONObject jo = jArray.getJSONObject(j);

                        if (countriesOld.get(i).toUpperCase().equals(jo.getString("code"))){
                            countriesNew.add(jo.getString("name"));
                            break;
                        }
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public void sourceThreadHandler (List<Sources> sourcesList, List<String> categories,
                                     List<String> languages, List<String> countries){
        this.sourcesList.addAll(sourcesList);
        this.categories.addAll(categories);
        this.languagesOld.addAll(languages);
        this.countriesOld.addAll(countries);

        Collections.sort(sourcesList);

        loadFile(getResources().openRawResource(R.raw.language_codes));
        loadFile(getResources().openRawResource(R.raw.country_codes));

        for (int i = 0; i < sourcesList.size(); i++){
            sourcesForViewer.add(sourcesList.get(i).getName());
        }


        mainActivityHandler ();
        setTitle(String.format("News Gateway (%s)", sourcesForViewer.size()));

        topicsMenu.add("All Topics");
        Collections.sort(categories);
        for (String s: categories)
            topicsMenu.add(s);

        languagesMenu.add("All Languages");
        Collections.sort(languagesNew);
        for (String s: languagesNew)
            languagesMenu.add(s);

        countriesMenu.add("All Countries");
        Collections.sort(countriesNew);
        for (String s: countriesNew)
            countriesMenu.add(s);

    }

    public void articleThreadHandler(List<Articles> articlesList){
        this.articlesList.addAll(articlesList);
        Collections.sort(articlesList);


        for (int i = 0; i < pagerAdapter.getCount(); i++)
            pagerAdapter.notifyChangeInPosition(i);
        fragments.clear();


        for (int i = 0; i < articlesList.size(); i++){
            fragments.add(ArticleFragment.newInstance(articlesList.get(i), i+1, articlesList.size()));
        }

        pagerAdapter.notifyDataSetChanged();
        pager.setCurrentItem(0);

    }

    public void articleThreadHandlerFAILED(){
        Toast.makeText(this, "This channel has no articles to show!", Toast.LENGTH_LONG).show();
        for (int i = 0; i < pagerAdapter.getCount(); i++)
            pagerAdapter.notifyChangeInPosition(i);
        fragments.clear();
        pagerAdapter.notifyDataSetChanged();
        //pager.setCurrentItem(0);
    }

    public void mainActivityHandler (){
        myListView.setAdapter(new ArrayAdapter<>(this,   // <== Important!
                R.layout.drawer_list_item, sourcesForViewer));

        myListView.setOnItemClickListener(   // <== Important!
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectItem(position);
                    }
                }
        );


        myDrawerToggle = new ActionBarDrawerToggle(   // <== Important!
                this,                /* host Activity */
                myDrawerLayout,             /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );

        if (getSupportActionBar() != null) {  // <== Important!
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }


    }


    private class MyPageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;


        MyPageAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return baseId + position;
        }

        /**
         * Notify that the position of a fragment has been changed.
         * Create a new ID for each position to force recreation of the fragment
         * @param n number of items which have been changed
         */
        void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() + n;
        }

    }


    private boolean doNetCheck() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }
    }






}