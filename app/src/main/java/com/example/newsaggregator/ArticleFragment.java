package com.example.newsaggregator;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 * It doesn't do much, but this is just an example
 */
public class ArticleFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    //private static final String ARG_SECTION_NUMBER = "section_number";
    public ArticleFragment() {
        // Required empty public constructor
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number. The 'sectionNumber' parameter indicates what page to
     * display: 1, 2 , 3, etc.
     */
    public static ArticleFragment newInstance(Articles article, int index, int max) {

        ArticleFragment fragment = new ArticleFragment();

        Bundle args = new Bundle();
        //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putSerializable("ARTICLE_DATA", article);
        args.putSerializable("INDEX", index);
        args.putSerializable("TOTAL_COUNT", max);
        fragment.setArguments(args);

        return fragment;
    }


    // The onCreateView is like Activity's onCreate for a Fragment
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment_layout = inflater.inflate(R.layout.fragment_article, container, false);

        Bundle args = getArguments();
        if (args != null) {
            final Articles currentArticle = (Articles) args.getSerializable("ARTICLE_DATA");
            if (currentArticle == null) {
                return null;
            }
            int index = args.getInt("INDEX");
            int total = args.getInt("TOTAL_COUNT");

            TextView title = fragment_layout.findViewById(R.id.title_fragment);
            if (!currentArticle.getTitle().isEmpty() && currentArticle.getTitle() != null && !currentArticle.getTitle().equals("null")){
                title.setText(currentArticle.getTitle());
                title.setOnClickListener(v -> click(currentArticle.getUrl()));
            }
            else{
                title.setVisibility(View.INVISIBLE);
                title.setHeight(0);
            }


            TextView date = fragment_layout.findViewById(R.id.date_fragment);
            if (!currentArticle.getPublishedAt().isEmpty() && currentArticle.getPublishedAt() != null && !currentArticle.getPublishedAt().equals("null")){
                String[] date2 = currentArticle.getPublishedAt().split("T");
                String[] date3 = date2[0].split("-");
                String month = "";
                switch (date3[1]){
                    case "01":
                        month = "Jan";
                        break;
                    case "02":
                        month = "Feb";
                        break;
                    case "03":
                        month = "Mar";
                        break;
                    case "04":
                        month = "Apr";
                        break;
                    case "05":
                        month = "May";
                        break;
                    case "06":
                        month = "Jun";
                        break;
                    case "07":
                        month = "Jul";
                        break;
                    case "08":
                        month = "Aug";
                        break;
                    case "09":
                        month = "Sep";
                        break;
                    case "10":
                        month = "Oct";
                        break;
                    case "11":
                        month = "Nov";
                        break;
                    case "12":
                        month = "Dec";
                        break;
                    default:
                        month = date3[1];
                }
                String[] time = date2[1].split(":");

                date.setText(String.format(Locale.getDefault(),
                        "%s %s, %s %s:%s", month, date3[2],date3[0]
                        ,time[0], time[1]));
            }
            else{
                date.setVisibility(View.INVISIBLE);
                date.setHeight(0);
            }


            TextView author = fragment_layout.findViewById(R.id.author_fragment);
            if (!currentArticle.getAuthor().isEmpty() && currentArticle.getAuthor() != null && !currentArticle.getAuthor().equals("null")){
                author.setText(currentArticle.getAuthor());
            }
            else{
                author.setVisibility(View.INVISIBLE);
                author.setHeight(0);
            }


            //TextView image = fragment_layout.findViewById(R.id.population);
            //population.setText(String.format(Locale.US, "%,d", currentArticle.getPopulation()));

            TextView news = fragment_layout.findViewById(R.id.news_fragment);
            if (!currentArticle.getDescription().isEmpty() && currentArticle.getDescription() != null && !currentArticle.getDescription().equals("null")){
                news.setText(currentArticle.getDescription());
                news.setOnClickListener(v -> click(currentArticle.getUrl()));
            }
            else{
                news.setVisibility(View.INVISIBLE);
                news.setHeight(0);
            }


            TextView pageNum = fragment_layout.findViewById(R.id.count_fragment);
            pageNum.setText(String.format(Locale.US, "%d of %d", index, total));

            ImageView image = fragment_layout.findViewById(R.id.image_fragment);
            image.setLayerType(View.LAYER_TYPE_SOFTWARE, null);


            if (!currentArticle.getUrlToImage().isEmpty()) {
                Picasso.get().load(currentArticle.getUrlToImage())
                        .error(R.drawable.noimage)
                        .placeholder(R.drawable.loading)
                        .into(image);
            }

            //image.setImageDrawable(currentArticle.getDrawable());
            if (currentArticle.getUrl() != null && !currentArticle.getUrl().isEmpty() && !currentArticle.getUrl().equals("null"))
                image.setOnClickListener(v -> click(currentArticle.getUrl()));

            return fragment_layout;
        } else {
            return null;
        }
    }

    public void click(String name) {

        Uri mapUri = Uri.parse(name);
        Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);
        startActivity(intent);

    }


}