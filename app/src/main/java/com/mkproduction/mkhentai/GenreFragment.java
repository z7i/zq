package com.mkproduction.mkhentai;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class GenreFragment extends Fragment implements IFetchListData {
    private String home_url = "https://id.nhent.ai/tags/?page=";
    RecyclerView rcGenre;
    GenreRcAdapter adapter;
    List<Genre> genres = new ArrayList<>();
    public static GenreFragment genreFragment;
    int page = 1;

    public static GenreFragment newInstance() {
        if (genreFragment == null) {
            genreFragment = new GenreFragment();
        }
        return genreFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_genre, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //TODO : GET GENRES
        makeRequest(page++);
        //TODO : RCGENRES
        rcGenre = view.findViewById(R.id.rcGenre);
        adapter = new GenreRcAdapter(genres);
        rcGenre.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        rcGenre.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);

        rcGenre.setLayoutManager(gridLayoutManager);
        rcGenre.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int index, int totalItemsCount, RecyclerView view) {
                makeRequest(page++);
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void makeRequest(int page) {
        String url = home_url + page;
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        genres.addAll(parseResponse(response));
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });

        queue.add(stringRequest);
    }

    @Override
    public List<Genre> parseResponse(String html) {
        Document doc = Jsoup.parse(html);
        Elements gallery = doc.getElementById("tag-container").getElementsByTag("a");
        List<Genre> genres = new ArrayList<>();

        for (Element el : gallery) {
            String url = el.attr("href");
            String title = el.text();
            Genre genre = new Genre(title, url);
            genres.add(genre);
        }
        return genres;
    }
}

